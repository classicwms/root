package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import com.tekclover.wms.api.idmaster.model.vertical.FindVertical;
import com.tekclover.wms.api.idmaster.repository.Specification.VerticalSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.vertical.AddVertical;
import com.tekclover.wms.api.idmaster.model.vertical.UpdateVertical;
import com.tekclover.wms.api.idmaster.model.vertical.Vertical;
import com.tekclover.wms.api.idmaster.repository.VerticalRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VerticalService {

	@Autowired
	private VerticalRepository verticalRepository;

	@Autowired
	private LanguageIdService languageIdService;

	/**
	 * getCompanies
	 * @return
	 */
	public List<Vertical> getCompanies () {
		return verticalRepository.findAll();
	}

	/**
	 * getVertical
	 * @param verticalId
	 * @return
	 */
	public Vertical getVertical (Long verticalId,String languageId) {
		log.info("vertical Id: " + verticalId);
		Vertical vertical = verticalRepository.findByVerticalIdAndLanguageId(verticalId,languageId).orElse(null);
		if (vertical == null) {
			throw new BadRequestException("The given ID doesn't exist : " + verticalId);
		}
		return vertical;
	}

	/**
	 * createVertical
	 * @param newVertical
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Vertical createVertical (AddVertical newVertical,String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Vertical dbVertical = new Vertical();
		Optional<Vertical> duplicateVertical=verticalRepository.findByVerticalIdAndLanguageId(newVertical.getVerticalId(), newVertical.getLanguageId());
		if(!duplicateVertical.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		}else{
			LanguageId languageId = languageIdService.getLanguageId(newVertical.getLanguageId());
			BeanUtils.copyProperties(newVertical, dbVertical, CommonUtils.getNullPropertyNames(newVertical));
			dbVertical.setDeletionIndicator(0L);
			dbVertical.setCreatedBy(loginUserID);
			dbVertical.setUpdatedBy(loginUserID);
			dbVertical.setCreatedOn(new Date());
			dbVertical.setUpdatedOn(new Date());
			verticalRepository.save(dbVertical);
		}
		return dbVertical;
	}

	/**
	 * updateVertical
	 * @param verticalId
	 * @param updateVertical
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Vertical updateVertical (Long verticalId,String languageId,String loginUserID,UpdateVertical updateVertical)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Vertical dbVertical = getVertical(verticalId,languageId);
		BeanUtils.copyProperties(updateVertical, dbVertical, CommonUtils.getNullPropertyNames(updateVertical));
		dbVertical.setUpdatedBy(loginUserID);
		dbVertical.setUpdatedOn(new Date());
		return verticalRepository.save(dbVertical);
	}

	/**
	 * deleteVertical
	 * @param verticalId
	 * @param languageId
	 */
	public void deleteVertical (Long verticalId,String languageId) {
		Vertical vertical = getVertical(verticalId,languageId);
		if ( vertical != null) {
			verticalRepository.delete(vertical);
		} else {
			throw new EntityNotFoundException("Error in deleting Vertical Id: " + verticalId);
		}
	}

	//Find Vertical
	public List<Vertical> findVertical(FindVertical findVertical) throws ParseException {

		VerticalSpecification spec = new VerticalSpecification(findVertical);
		List<Vertical> results = verticalRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
