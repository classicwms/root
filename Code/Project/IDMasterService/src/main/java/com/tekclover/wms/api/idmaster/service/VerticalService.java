package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

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
	public Vertical getVertical (Long verticalId) {
		log.info("vertical Id: " + verticalId);
		Vertical vertical = verticalRepository.findByVerticalId(verticalId).orElse(null);
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
	public Vertical createVertical (AddVertical newVertical) 
			throws IllegalAccessException, InvocationTargetException {
		Vertical dbVertical = new Vertical();
		BeanUtils.copyProperties(newVertical, dbVertical, CommonUtils.getNullPropertyNames(newVertical));
		dbVertical.setDeletionIndicator(0L);
		return verticalRepository.save(dbVertical);
	}
	
	/**
	 * updateVertical
	 * @param verticalId
	 * @param updateVertical
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Vertical updateVertical (Long verticalId, UpdateVertical updateVertical) 
			throws IllegalAccessException, InvocationTargetException {
		Vertical dbVertical = getVertical(verticalId);
		BeanUtils.copyProperties(updateVertical, dbVertical, CommonUtils.getNullPropertyNames(updateVertical));
		return verticalRepository.save(dbVertical);
	}
	
	/**
	 * deleteVertical
	 * @param verticalCode
	 */
	public void deleteVertical (Long verticalId) {
		Vertical vertical = getVertical(verticalId);
		if ( vertical != null) {
			verticalRepository.delete(vertical);
		} else {
			throw new EntityNotFoundException("Error in deleting Vertical Id: " + verticalId);
		}
	}
}
