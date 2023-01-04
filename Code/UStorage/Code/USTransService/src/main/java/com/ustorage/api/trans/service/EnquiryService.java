package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.model.enquiry.Enquiry;
import com.ustorage.api.trans.model.enquiry.FindEnquiry;
import com.ustorage.api.trans.repository.Specification.EnquirySpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.enquiry.AddEnquiry;
import com.ustorage.api.trans.model.enquiry.Enquiry;
import com.ustorage.api.trans.model.enquiry.UpdateEnquiry;
import com.ustorage.api.trans.repository.EnquiryRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnquiryService {
	
	@Autowired
	private EnquiryRepository enquiryRepository;
	
	public List<Enquiry> getEnquiry () {
		List<Enquiry> enquiryList =  enquiryRepository.findAll();
		enquiryList = enquiryList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return enquiryList;
	}
	
	/**
	 * getEnquiry
	 * @param enquiryId
	 * @return
	 */
	public Enquiry getEnquiry (String enquiryId) {
		Optional<Enquiry> enquiry = enquiryRepository.findByEnquiryIdAndDeletionIndicator(enquiryId, 0L);
		if (enquiry.isEmpty()) {
			return null;
		}
		return enquiry.get();
	}
	
	/**
	 * createEnquiry
	 * @param newEnquiry
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Enquiry createEnquiry (AddEnquiry newEnquiry, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Enquiry dbEnquiry = new Enquiry();
		BeanUtils.copyProperties(newEnquiry, dbEnquiry, CommonUtils.getNullPropertyNames(newEnquiry));
		dbEnquiry.setDeletionIndicator(0L);
		dbEnquiry.setCreatedBy(loginUserId);
		dbEnquiry.setUpdatedBy(loginUserId);
		dbEnquiry.setCreatedOn(new Date());
		dbEnquiry.setUpdatedOn(new Date());
		return enquiryRepository.save(dbEnquiry);
	}
	
	/**
	 * updateEnquiry
	 * @param enquiryId
	 * @param loginUserId 
	 * @param updateEnquiry
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Enquiry updateEnquiry (String enquiryId, String loginUserId, UpdateEnquiry updateEnquiry)
			throws IllegalAccessException, InvocationTargetException {
		Enquiry dbEnquiry = getEnquiry(enquiryId);
		BeanUtils.copyProperties(updateEnquiry, dbEnquiry, CommonUtils.getNullPropertyNames(updateEnquiry));
		dbEnquiry.setUpdatedBy(loginUserId);
		dbEnquiry.setUpdatedOn(new Date());
		return enquiryRepository.save(dbEnquiry);
	}
	
	/**
	 * deleteEnquiry
	 * @param loginUserID 
	 * @param enquiryModuleId
	 */
	public void deleteEnquiry (String enquiryModuleId, String loginUserID) {
		Enquiry enquiry = getEnquiry(enquiryModuleId);
		if (enquiry != null) {
			enquiry.setDeletionIndicator(1L);
			enquiry.setUpdatedBy(loginUserID);
			enquiry.setUpdatedOn(new Date());
			enquiryRepository.save(enquiry);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + enquiryModuleId);
		}
	}

	//find
	public List<Enquiry> findEnquiry(FindEnquiry findEnquiry) throws ParseException {

		EnquirySpecification spec = new EnquirySpecification(findEnquiry);
		List<Enquiry> results = enquiryRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
	
}
