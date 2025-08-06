package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
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
public class CompanyService {

	@Autowired
	private CompanyRepository companyRepository;


	/**
	 * getCompanys
	 * @return
	 */
	public List<Company> getCompanys () {
		List<Company> companyList = companyRepository.findAll();
		log.info("companyList : " + companyList);
		companyList = companyList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<Company>newCompany=new ArrayList<>();
		for(Company dbCompany:companyList){
			if(dbCompany.getDescription()!=null&&dbCompany.getVerticalIdAndDescription()!=null) {
				IkeyValuePair ikeyValuePair1 = companyRepository.getCompanyIdAndDescription(dbCompany.getCompanyId(), dbCompany.getLanguageId());
				IkeyValuePair ikeyValuePair2 = companyRepository.getVerticalIdAndDescription(dbCompany.getVerticalId(), dbCompany.getLanguageId());
				if(ikeyValuePair2!=null) {
					dbCompany.setVerticalIdAndDescription(ikeyValuePair2.getVerticalId() + "-" + ikeyValuePair2.getDescription());
				}
				if (ikeyValuePair1 != null) {
					dbCompany.setDescription(ikeyValuePair1.getDescription());
				}

			}
			newCompany.add(dbCompany);
		}
		return newCompany;
	}

	/**
	 * getCompany
	 * @param companyId
	 * @return
	 */
	public Company getCompany (String companyId,String languageId) {
		Optional<Company> company =
				companyRepository.findByLanguageIdAndCompanyIdAndDeletionIndicator(
						languageId,
						companyId,
						0L);
		if (company.isEmpty()) {
			throw new BadRequestException("The given Company Id : " + companyId +
					"languageId" + languageId + " doesn't exist.");
		}
		Company newCompany=new Company();
		BeanUtils.copyProperties(company.get(),newCompany, CommonUtils.getNullPropertyNames(company));
		IkeyValuePair ikeyValuePair1= companyRepository.getCompanyIdAndDescription(companyId,languageId);
		IkeyValuePair ikeyValuePair2=companyRepository.getVerticalIdAndDescription(newCompany.getVerticalId(), newCompany.getLanguageId());
		if(ikeyValuePair1!=null) {
			newCompany.setDescription(ikeyValuePair1.getDescription());
		}
		if(ikeyValuePair2!=null) {
			newCompany.setVerticalIdAndDescription(ikeyValuePair2.getVerticalId() + "-" + ikeyValuePair2.getDescription());
		}
		return newCompany;
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
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<Company> newCompany=new ArrayList<>();
		for(Company dbCompany:results){
			if(dbCompany.getDescription()!=null){
				IkeyValuePair ikeyValuePair1= companyRepository.getCompanyIdAndDescription(dbCompany.getCompanyId(), dbCompany.getLanguageId());
				IkeyValuePair ikeyValuePair2=companyRepository.getVerticalIdAndDescription(dbCompany.getVerticalId(), dbCompany.getLanguageId());
				if(ikeyValuePair2!=null) {
					dbCompany.setVerticalIdAndDescription(ikeyValuePair2.getVerticalId() + "-" + ikeyValuePair2.getDescription());
				}
				if (ikeyValuePair1 != null) {
					dbCompany.setDescription(ikeyValuePair1.getDescription());
				}
			}
			newCompany.add(dbCompany);
		}
		return newCompany;
	}

	/**
	 * createCompany
	 * @param newCompany
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Company createCompany (AddCompany newCompany, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Optional<Company> optCompany =
				companyRepository.findByLanguageIdAndCompanyIdAndDeletionIndicator(
						newCompany.getLanguageId(),
						newCompany.getCompanyId(),
						0L);
		if (!optCompany.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}


		IkeyValuePair ikeyValuePair1= companyRepository.getCompanyIdAndDescription(newCompany.getCompanyId(), newCompany.getLanguageId());
		IkeyValuePair ikeyValuePair2=companyRepository.getVerticalIdAndDescription(newCompany.getVerticalId(), newCompany.getLanguageId());
		Company dbCompany = new Company();
		BeanUtils.copyProperties(newCompany, dbCompany, CommonUtils.getNullPropertyNames(newCompany));

		if(ikeyValuePair1 != null && ikeyValuePair2 != null) {
			dbCompany.setCompanyId(ikeyValuePair1.getCompanyCodeId());
			dbCompany.setDescription(ikeyValuePair1.getDescription());
			dbCompany.setVerticalIdAndDescription(ikeyValuePair2.getVerticalId() + "-" + ikeyValuePair2.getDescription());
		}
		else {
			throw new BadRequestException("The given values of Company Id "
					+ newCompany.getCompanyId() + " Vertical Id "
					+ newCompany.getVerticalId() + " doesn't exist");
		}
		dbCompany.setDeletionIndicator(0L);
		dbCompany.setCreatedBy(loginUserID);
		dbCompany.setUpdatedBy(loginUserID);
		dbCompany.setCreatedOn(new Date());
		dbCompany.setUpdatedOn(new Date());
		return companyRepository.save(dbCompany);
	}

	/**
	 * updateCompany
	 * @param companyId
	 * @param updateCompany
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Company updateCompany (String companyId,String languageId, UpdateCompany updateCompany, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Company dbCompany = getCompany(companyId,languageId);
		log.info("updateCompany : " + updateCompany);
		log.info("dbCompany : " + dbCompany);
		BeanUtils.copyProperties(updateCompany, dbCompany, CommonUtils.getNullPropertyNames(updateCompany));
		dbCompany.setUpdatedBy(loginUserID);
		dbCompany.setUpdatedOn(new Date());
		return companyRepository.save(dbCompany);
	}

	/**
	 * deleteCompany
	 * @param companyId
	 */
	public void deleteCompany (String companyId,String languageId,String loginUserID) throws ParseException {
		Company company = getCompany(companyId,languageId);
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
