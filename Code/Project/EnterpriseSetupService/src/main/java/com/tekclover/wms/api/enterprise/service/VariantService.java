package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.variant.AddVariant;
import com.tekclover.wms.api.enterprise.model.variant.SearchVariant;
import com.tekclover.wms.api.enterprise.model.variant.UpdateVariant;
import com.tekclover.wms.api.enterprise.model.variant.Variant;
import com.tekclover.wms.api.enterprise.repository.VariantRepository;
import com.tekclover.wms.api.enterprise.repository.specification.VariantSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VariantService extends BaseService {
	
	@Autowired
	private VariantRepository variantRepository;
	
	/**
	 * getVariants
	 * @return
	 */
	public List<Variant> getVariants () {
		List<Variant> variantList = variantRepository.findAll();
		log.info("variantList : " + variantList);
		variantList = variantList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return variantList;
	}
	
	/**
	 * getVariant
	 * @param variantCode
	 * @return
	 */
	public Variant getVariant (String variantId) {
		Variant variant = variantRepository.findByVariantId(variantId).orElse(null);
		if (variant != null && variant.getDeletionIndicator() != null && variant.getDeletionIndicator() == 0) {
			return variant;
		} else {
			throw new BadRequestException("The given Variant ID : " + variantId + " doesn't exist.");
		}
	}
	
	/**
	 * findVariant
	 * @param searchVariant
	 * @return
	 * @throws ParseException
	 */
	public List<Variant> findVariant(SearchVariant searchVariant) throws Exception {
		if (searchVariant.getStartCreatedOn() != null && searchVariant.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchVariant.getStartCreatedOn(), searchVariant.getEndCreatedOn());
			searchVariant.setStartCreatedOn(dates[0]);
			searchVariant.setEndCreatedOn(dates[1]);
		}
		
		VariantSpecification spec = new VariantSpecification(searchVariant);
		List<Variant> results = variantRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createVariant
	 * @param newVariant
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Variant createVariant (AddVariant newVariant, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Variant dbVariant = new Variant();
		BeanUtils.copyProperties(newVariant, dbVariant, CommonUtils.getNullPropertyNames(newVariant));
		dbVariant.setDeletionIndicator(0L);
		dbVariant.setCompanyId(getCompanyCode());
		dbVariant.setLanguageId(getLanguageId());
		dbVariant.setPlantId(getPlantId());
		
		dbVariant.setCreatedBy(loginUserID);
		dbVariant.setUpdatedBy(loginUserID);
		dbVariant.setCreatedOn(new Date());
		dbVariant.setUpdatedOn(new Date());
		return variantRepository.save(dbVariant);
	}
	
	/**
	 * updateVariant
	 * @param variantCode
	 * @param updateVariant
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Variant updateVariant (String variantCode, UpdateVariant updateVariant, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Variant dbVariant = getVariant(variantCode);
		BeanUtils.copyProperties(updateVariant, dbVariant, CommonUtils.getNullPropertyNames(updateVariant));
		dbVariant.setUpdatedBy(loginUserID);
		dbVariant.setUpdatedOn(new Date());
		return variantRepository.save(dbVariant);
	}
	
	/**
	 * deleteVariant
	 * @param variantCode
	 */
	public void deleteVariant (String variantCode, String loginUserID) {
		Variant variant = getVariant(variantCode);
		if ( variant != null) {
			variant.setDeletionIndicator (1L);
			variant.setUpdatedBy(loginUserID);
			variant.setUpdatedOn(new Date());
			variantRepository.save(variant);
		} else {
			throw new EntityNotFoundException(String.valueOf(variantCode));
		}
	}
}
