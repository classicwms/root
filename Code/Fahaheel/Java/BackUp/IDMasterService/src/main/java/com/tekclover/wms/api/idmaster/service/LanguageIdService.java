package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.languageid.AddLanguageId;
import com.tekclover.wms.api.idmaster.model.languageid.FindLanguageId;
import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import com.tekclover.wms.api.idmaster.model.languageid.UpdateLanguageId;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.LanguageIdSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LanguageIdService {

	@Autowired
	private LanguageIdRepository languageIdRepository;

	/**
	 * getLanguageIds
	 * @return
	 */
	public List<LanguageId> getLanguageIds () {
		List<LanguageId> languageIdList =  languageIdRepository.findAll();
		languageIdList = languageIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return languageIdList;
	}

	/**
	 * getLanguageId
	 * @param languageId
	 * @return
	 */
	public LanguageId getLanguageId (String languageId) {
		Optional<LanguageId> dbLanguageId =
				languageIdRepository.findByLanguageIdAndDeletionIndicator(
						languageId,
						0L
				);
		if (dbLanguageId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"languageId - " + languageId +
					" doesn't exist.");

		}
		return dbLanguageId.get();
	}

	/**
	 * createLanguageId
	 * @param newLanguageId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public LanguageId createLanguageId (AddLanguageId newLanguageId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		LanguageId dbLanguageId = new LanguageId();
		Optional<LanguageId> duplicateLanguageId = languageIdRepository.findByLanguageIdAndDeletionIndicator(newLanguageId.getLanguageId(), 0L);
		if (!duplicateLanguageId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			log.info("newLanguageId : " + newLanguageId);
			BeanUtils.copyProperties(newLanguageId, dbLanguageId, CommonUtils.getNullPropertyNames(newLanguageId));
			dbLanguageId.setDeletionIndicator(0L);
			dbLanguageId.setCreatedBy(loginUserID);
			dbLanguageId.setUpdatedBy(loginUserID);
			dbLanguageId.setCreatedOn(new Date());
			dbLanguageId.setUpdatedOn(new Date());
			return languageIdRepository.save(dbLanguageId);
		}
	}

	/**
	 * updateLanguageId
	 * @param loginUserID
	 * @param languageId
	 * @param updateLanguageId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public LanguageId updateLanguageId (String languageId, String loginUserID,
										UpdateLanguageId updateLanguageId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		LanguageId dbLanguageId = getLanguageId(languageId);
		BeanUtils.copyProperties(updateLanguageId, dbLanguageId, CommonUtils.getNullPropertyNames(updateLanguageId));
		dbLanguageId.setUpdatedBy(loginUserID);
		dbLanguageId.setUpdatedOn(new Date());
		return languageIdRepository.save(dbLanguageId);
	}

	/**
	 * deleteLanguageId
	 * @param loginUserID
	 * @param languageId
	 */
	public void deleteLanguageId (String languageId, String loginUserID) {
		LanguageId dbLanguageId = getLanguageId(languageId);
		if ( dbLanguageId != null) {
			dbLanguageId.setDeletionIndicator(1L);
			dbLanguageId.setUpdatedBy(loginUserID);
			languageIdRepository.save(dbLanguageId);
			languageIdRepository.delete(dbLanguageId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + languageId);
		}
	}

	//Find LanguageId
	public List<LanguageId> findLanguageId(FindLanguageId findLanguageId) throws ParseException {

		LanguageIdSpecification spec = new LanguageIdSpecification(findLanguageId);
		List<LanguageId> results = languageIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
