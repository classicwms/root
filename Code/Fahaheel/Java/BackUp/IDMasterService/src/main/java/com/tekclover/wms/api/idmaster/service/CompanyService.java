package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.companyid.AddCompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.CompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.UpdateCompanyId;
import com.tekclover.wms.api.idmaster.repository.CompanyRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyService {
	
	@Autowired
	private CompanyRepository companyRepository;
	
	/**
	 * getCompanies
	 * @return
	 */
	public List<CompanyId> getCompanies () {
		return companyRepository.findAll();
	}
	
	/**
	 * getCompanyId
	 * @param companyId
	 * @return
	 */
	public CompanyId getCompanyId (String companyId) {
		log.info("company Id: " + companyId);
		CompanyId company = companyRepository.findByCompanyCodeId(companyId).orElse(null);
		if (company == null) {
			throw new BadRequestException("The given ID doesn't exist : " + companyId);
		}
		return company;
	}
	
	/**
	 * createCompanyId
	 * @param newCompanyId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CompanyId createCompanyId (AddCompanyId newCompanyId) 
			throws IllegalAccessException, InvocationTargetException {
		CompanyId dbCompanyId = new CompanyId();
		BeanUtils.copyProperties(newCompanyId, dbCompanyId, CommonUtils.getNullPropertyNames(newCompanyId));
		return companyRepository.save(dbCompanyId);
	}
	
	/**
	 * updateCompanyId
	 * @param companyId
	 * @param updateCompanyId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CompanyId updateCompanyId (String companyId, UpdateCompanyId updateCompanyId) 
			throws IllegalAccessException, InvocationTargetException {
		CompanyId dbCompanyId = getCompanyId(companyId);
		BeanUtils.copyProperties(updateCompanyId, dbCompanyId, CommonUtils.getNullPropertyNames(updateCompanyId));
		return companyRepository.save(dbCompanyId);
	}
	
	/**
	 * deleteCompanyId
	 * @param companyCode
	 */
	public void deleteCompanyId (String companyModuleId) {
		CompanyId company = getCompanyId(companyModuleId);
		if ( company != null) {
			companyRepository.delete(company);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + companyModuleId);
		}
	}
}
