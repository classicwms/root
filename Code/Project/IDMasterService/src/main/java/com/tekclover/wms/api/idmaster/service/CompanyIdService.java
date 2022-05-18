package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.companyid.AddCompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.CompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.UpdateCompanyId;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyIdService extends BaseService {
	
	@Autowired
	private CompanyIdRepository companyIdRepository;
	
	/**
	 * getCompanyIds
	 * @return
	 */
	public List<CompanyId> getCompanyIds () {
		List<CompanyId> companyIdList =  companyIdRepository.findAll();
		companyIdList = companyIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return companyIdList;
	}
	
	/**
	 * getCompanyId
	 * @param companyCodeId
	 * @return
	 */
	public CompanyId getCompanyId (String companyCodeId) {
		Optional<CompanyId> dbCompanyId = companyIdRepository.findByCompanyCodeIdAndLanguageIdAndDeletionIndicator(
				companyCodeId, getLanguageId(), 0L );
		if (dbCompanyId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"companyCodeId - " + companyCodeId +
						" doesn't exist.");
			
		} 
		return dbCompanyId.get();
	}
	
//	/**
//	 * 
//	 * @param searchCompanyId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<CompanyId> findCompanyId(SearchCompanyId searchCompanyId) 
//			throws ParseException {
//		CompanyIdSpecification spec = new CompanyIdSpecification(searchCompanyId);
//		List<CompanyId> results = companyIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createCompanyId
	 * @param newCompanyId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CompanyId createCompanyId (AddCompanyId newCompanyId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		CompanyId dbCompanyId = new CompanyId();
		log.info("newCompanyId : " + newCompanyId);
		BeanUtils.copyProperties(newCompanyId, dbCompanyId, CommonUtils.getNullPropertyNames(newCompanyId));
		dbCompanyId.setDeletionIndicator(0L);
		dbCompanyId.setCreatedBy(loginUserID);
		dbCompanyId.setUpdatedBy(loginUserID);
		dbCompanyId.setCreatedOn(new Date());
		dbCompanyId.setUpdatedOn(new Date());
		return companyIdRepository.save(dbCompanyId);
	}
	
	/**
	 * updateCompanyId
	 * @param loginUserId 
	 * @param companyCodeId
	 * @param updateCompanyId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CompanyId updateCompanyId (String companyCodeId, String loginUserID, 
			UpdateCompanyId updateCompanyId) 
			throws IllegalAccessException, InvocationTargetException {
		CompanyId dbCompanyId = getCompanyId(companyCodeId);
		BeanUtils.copyProperties(updateCompanyId, dbCompanyId, CommonUtils.getNullPropertyNames(updateCompanyId));
		dbCompanyId.setUpdatedBy(loginUserID);
		dbCompanyId.setUpdatedOn(new Date());
		return companyIdRepository.save(dbCompanyId);
	}
	
	/**
	 * deleteCompanyId
	 * @param loginUserID 
	 * @param companyCodeId
	 */
	public void deleteCompanyId (String companyCodeId, String loginUserID) {
		CompanyId dbCompanyId = getCompanyId(companyCodeId);
		if ( dbCompanyId != null) {
			dbCompanyId.setDeletionIndicator(1L);
			dbCompanyId.setUpdatedBy(loginUserID);
			companyIdRepository.save(dbCompanyId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + companyCodeId);
		}
	}
}
