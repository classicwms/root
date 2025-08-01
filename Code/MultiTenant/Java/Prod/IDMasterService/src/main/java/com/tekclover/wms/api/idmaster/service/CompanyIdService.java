package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.companyid.FindCompanyId;
import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.CompanyIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.companyid.AddCompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.CompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.UpdateCompanyId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyIdService{
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private LanguageIdRepository languageIdRepository;

	@Autowired
	private LanguageIdService languageIdService;

	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private RoleAccessRepository roleAccessRepository;
	@Autowired
	private ModuleIdRepository moduleIdRepository;

	@Autowired
	DbConfigRepository dbConfigRepository;

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
	public CompanyId getCompanyId (String companyCodeId,String languageId) {
		Optional<CompanyId> dbCompanyId = companyIdRepository.findByCompanyCodeIdAndLanguageIdAndDeletionIndicator(
				companyCodeId, languageId, 0L );
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		CompanyId dbCompanyId = new CompanyId();
		Optional<CompanyId> duplicateCompanyId=companyIdRepository.findByCompanyCodeIdAndLanguageIdAndDeletionIndicator(newCompanyId.getCompanyCodeId(), newCompanyId.getLanguageId(),0L);

		if(!duplicateCompanyId.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {

			LanguageId dbLanguageId = languageIdService.getLanguageId(newCompanyId.getLanguageId());
			log.info("newCompanyId : " + newCompanyId);
			BeanUtils.copyProperties(newCompanyId, dbCompanyId, CommonUtils.getNullPropertyNames(newCompanyId));
			dbCompanyId.setDeletionIndicator(0L);
			dbCompanyId.setCreatedBy(loginUserID);
			dbCompanyId.setUpdatedBy(loginUserID);
			dbCompanyId.setCreatedOn(new Date());
			dbCompanyId.setUpdatedOn(new Date());
			log.info("Saving in Common DB");
			companyIdRepository.save(dbCompanyId);

			String routingDb = dbConfigRepository.getDbNameByCompany(newCompanyId.getCompanyCodeId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			log.info("Saving in FAHAHEEL DB");
			companyIdRepository.save(dbCompanyId);
		}
		return dbCompanyId;
	}

	/**
	 * updateCompanyId
	 * @param loginUserID
	 * @param companyCodeId
	 * @param updateCompanyId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CompanyId updateCompanyId (String companyCodeId,String languageId,String loginUserID,
									  UpdateCompanyId updateCompanyId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		CompanyId dbCompanyId = getCompanyId(companyCodeId,languageId);
		BeanUtils.copyProperties(updateCompanyId, dbCompanyId, CommonUtils.getNullPropertyNames(updateCompanyId));
		dbCompanyId.setUpdatedBy(loginUserID);
		dbCompanyId.setUpdatedOn(new Date());

		updateRoleAccess(companyCodeId, updateCompanyId.getDescription());									//Update Company Description
		updateModuleId(companyCodeId, updateCompanyId.getDescription());									//Update Company Description

		return companyIdRepository.save(dbCompanyId);
	}

	/**
	 * deleteCompanyId
	 * @param loginUserID
	 * @param companyCodeId
	 */
	public void deleteCompanyId (String companyCodeId,String languageId,String loginUserID) {
		CompanyId dbCompanyId = getCompanyId(companyCodeId,languageId);
		if ( dbCompanyId != null) {
			dbCompanyId.setDeletionIndicator(1L);
			dbCompanyId.setUpdatedBy(loginUserID);
			companyIdRepository.save(dbCompanyId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + companyCodeId);
		}
	}

	//Find CompanyId
	public List<CompanyId> findCompanyId(FindCompanyId findCompanyId) throws ParseException {

		CompanyIdSpecification spec = new CompanyIdSpecification(findCompanyId);
		List<CompanyId> results = companyIdRepository.findAll(spec);
//		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}

	//update Description
	public void updateRoleAccess(String companyCodeId, String description) {
		List<RoleAccess> roleAccessList = roleAccessRepository.findByCompanyCodeIdAndDeletionIndicator(companyCodeId, 0L);
		if (roleAccessList != null) {
			roleAccessList.stream().forEach(n -> n.setCompanyIdAndDescription(companyCodeId + "-" + description));
		}
	}
	public void updateModuleId(String companyCodeId, String description) {
		List<ModuleId> moduleIdList = moduleIdRepository.findByCompanyCodeIdAndDeletionIndicator(companyCodeId, 0L);
		if (moduleIdList != null) {
			moduleIdList.stream().forEach(n -> n.setCompanyIdAndDescription(companyCodeId + "-" + description));
		}
	}
}
