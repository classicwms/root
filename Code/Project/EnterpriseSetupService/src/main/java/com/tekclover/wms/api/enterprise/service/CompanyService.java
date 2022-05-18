package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.company.AddCompany;
import com.tekclover.wms.api.enterprise.model.company.Company;
import com.tekclover.wms.api.enterprise.model.company.SearchCompany;
import com.tekclover.wms.api.enterprise.model.company.UpdateCompany;
import com.tekclover.wms.api.enterprise.repository.CompanyRepository;
import com.tekclover.wms.api.enterprise.repository.specification.CompanySpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyService extends BaseService {
	
	@Autowired
	private CompanyRepository companyRepository;
	
	/**
	 * getCompanys
	 * @return
	 */
	public List<Company> getCompanys () {
		List<Company> companyList = companyRepository.findAll();
		log.info("companyList : " + companyList);
		companyList = companyList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return companyList;
	}
	
	/**
	 * getCompany
	 * @param companyId
	 * @return
	 */
	public Company getCompany (String companyId) {
		Optional<Company> company = 
				companyRepository.findByLanguageIdAndCompanyIdAndDeletionIndicator(
						getLanguageId(), companyId, 0L);
		if (company.isEmpty()) {
			throw new BadRequestException("The given Company Id : " + companyId + " doesn't exist.");
		} 
		return company.get();
	}
	
	/**
	 * findCompany
	 * @param searchCompany
	 * @return
	 * @throws ParseException
	 */
	public List<Company> findCompany(SearchCompany searchCompany) throws Exception {
		if (searchCompany.getStartCreatedOn() != null && searchCompany.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchCompany.getStartCreatedOn(), searchCompany.getEndCreatedOn());
			searchCompany.setStartCreatedOn(dates[0]);
			searchCompany.setEndCreatedOn(dates[1]);
		}
		
		CompanySpecification spec = new CompanySpecification(searchCompany);
		List<Company> results = companyRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createCompany
	 * @param newCompany
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Company createCompany (AddCompany newCompany, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<Company> optCompany = 
				companyRepository.findByLanguageIdAndCompanyIdAndDeletionIndicator(
						newCompany.getLanguageId(),
						newCompany.getCompanyId(),
						0L);
		if (!optCompany.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		Company dbCompany = new Company();
		BeanUtils.copyProperties(newCompany, dbCompany, CommonUtils.getNullPropertyNames(newCompany));
		dbCompany.setDeletionIndicator(0L);
		dbCompany.setCompanyId(getCompanyCode());
		dbCompany.setLanguageId(getLanguageId());
		dbCompany.setCreatedBy(loginUserID);
		dbCompany.setUpdatedBy(loginUserID);
		dbCompany.setCreatedOn(new Date());
		dbCompany.setUpdatedOn(new Date());
		return companyRepository.save(dbCompany);
	}
	
	/**
	 * updateCompany
	 * @param companyCode
	 * @param updateCompany
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Company updateCompany (String companyId, UpdateCompany updateCompany, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Company dbCompany = getCompany(companyId);
		log.info("updateCompany : " + updateCompany);
		log.info("dbCompany : " + dbCompany);
		BeanUtils.copyProperties(updateCompany, dbCompany, CommonUtils.getNullPropertyNames(updateCompany));
		dbCompany.setUpdatedBy(loginUserID);
		dbCompany.setUpdatedOn(new Date());
		return companyRepository.save(dbCompany);
	}
	
	/**
	 * deleteCompany
	 * @param companyCode
	 */
	public void deleteCompany (String companyId, String loginUserID) {
		Company company = getCompany(companyId);
		if ( company != null) {
			company.setDeletionIndicator (1L);
			company.setUpdatedBy(loginUserID);
			company.setUpdatedOn(new Date());
			companyRepository.save(company);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + companyId);
		}
	}
}
