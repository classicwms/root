package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.impartner.AddImPartner;
import com.tekclover.wms.api.masters.model.impartner.ImPartner;
import com.tekclover.wms.api.masters.model.impartner.UpdateImPartner;
import com.tekclover.wms.api.masters.repository.ImPartnerRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImPartnerService {
	
	@Autowired
	private ImPartnerRepository impartnerRepository;
	
	/**
	 * getImPartners
	 * @return
	 */
	public List<ImPartner> getImPartners () {
		List<ImPartner> impartnerList = impartnerRepository.findAll();
		log.info("impartnerList : " + impartnerList);
		impartnerList = impartnerList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return impartnerList;
	}
	
	/**
	 * getImPartner
	 * @param businessPartnerCode
	 * @return
	 */
	public ImPartner getImPartner (String businessPartnerCode) {
		ImPartner impartner = impartnerRepository.findByBusinessPartnerCode(businessPartnerCode).orElse(null);
		if (impartner != null && impartner.getDeletionIndicator() != null && impartner.getDeletionIndicator() == 0) {
			return impartner;
		} else {
			throw new BadRequestException("The given ImPartner ID : " + businessPartnerCode + " doesn't exist.");
		}
	}
	
	/**
	 * findImPartner
	 * @param searchImPartner
	 * @return
	 * @throws ParseException
	 */
//	public List<ImPartner> findImPartner(SearchImPartner searchImPartner) throws ParseException {
//		if (searchImPartner.getStartCreatedOn() != null && searchImPartner.getEndCreatedOn() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImPartner.getStartCreatedOn(), searchImPartner.getEndCreatedOn());
//			searchImPartner.setStartCreatedOn(dates[0]);
//			searchImPartner.setEndCreatedOn(dates[1]);
//		}
//		
//		ImPartnerSpecification spec = new ImPartnerSpecification(searchImPartner);
//		List<ImPartner> results = impartnerRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createImPartner
	 * @param newImPartner
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImPartner createImPartner (AddImPartner newImPartner, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImPartner dbImPartner = new ImPartner();
		BeanUtils.copyProperties(newImPartner, dbImPartner, CommonUtils.getNullPropertyNames(newImPartner));
		dbImPartner.setDeletionIndicator(0L);
		dbImPartner.setCreatedBy(loginUserID);
		dbImPartner.setUpdatedBy(loginUserID);
		dbImPartner.setCreatedOn(new Date());
		dbImPartner.setUpdatedOn(new Date());
		return impartnerRepository.save(dbImPartner);
	}
	
	/**
	 * updateImPartner
	 * @param impartner
	 * @param updateImPartner
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImPartner updateImPartner (String businessPartnerCode, UpdateImPartner updateImPartner, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImPartner dbImPartner = getImPartner(businessPartnerCode);
		BeanUtils.copyProperties(updateImPartner, dbImPartner, CommonUtils.getNullPropertyNames(updateImPartner));
		dbImPartner.setUpdatedBy(loginUserID);
		dbImPartner.setUpdatedOn(new Date());
		return impartnerRepository.save(dbImPartner);
	}
	
	/**
	 * deleteImPartner
	 * @param impartner
	 */
	public void deleteImPartner (String businessPartnerCode, String loginUserID) {
		ImPartner impartner = getImPartner(businessPartnerCode);
		if ( impartner != null) {
			impartner.setDeletionIndicator (1L);
			impartner.setUpdatedBy(loginUserID);
			impartner.setUpdatedOn(new Date());
			impartnerRepository.save(impartner);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + businessPartnerCode);
		}
	}
}
