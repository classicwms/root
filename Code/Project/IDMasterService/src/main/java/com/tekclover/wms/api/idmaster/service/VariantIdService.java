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
import com.tekclover.wms.api.idmaster.model.varientid.AddVariantId;
import com.tekclover.wms.api.idmaster.model.varientid.UpdateVariantId;
import com.tekclover.wms.api.idmaster.model.varientid.VariantId;
import com.tekclover.wms.api.idmaster.repository.VariantIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VariantIdService extends BaseService {
	
	@Autowired
	private VariantIdRepository variantIdRepository;
	
	/**
	 * getVariantIds
	 * @return
	 */
	public List<VariantId> getVariantIds () {
		List<VariantId> variantIdList =  variantIdRepository.findAll();
		variantIdList = variantIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return variantIdList;
	}
	
	/**
	 * getVariantId
	 * @param variantCode
	 * @return
	 */
	public VariantId getVariantId ( String warehouseId, String variantCode, String variantType, String variantSubCode) {
		Optional<VariantId> dbVariantId = 
				variantIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndVariantCodeAndVariantTypeAndVariantSubCodeAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								variantCode,
								variantType,
								variantSubCode,
								0L
								);
		if (dbVariantId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"variantCode - " + variantCode +
						"variantType - " + variantType +
						"variantSubCode - " + variantSubCode +
						" doesn't exist.");
			
		} 
		return dbVariantId.get();
	}
	
//	/**
//	 * 
//	 * @param searchVariantId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<VariantId> findVariantId(SearchVariantId searchVariantId) 
//			throws ParseException {
//		VariantIdSpecification spec = new VariantIdSpecification(searchVariantId);
//		List<VariantId> results = variantIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createVariantId
	 * @param newVariantId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public VariantId createVariantId (AddVariantId newVariantId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		VariantId dbVariantId = new VariantId();
		log.info("newVariantId : " + newVariantId);
		BeanUtils.copyProperties(newVariantId, dbVariantId, CommonUtils.getNullPropertyNames(newVariantId));
		dbVariantId.setCompanyCodeId(getCompanyCode());
		dbVariantId.setPlantId(getPlantId());
		dbVariantId.setDeletionIndicator(0L);
		dbVariantId.setCreatedBy(loginUserID);
		dbVariantId.setUpdatedBy(loginUserID);
		dbVariantId.setCreatedOn(new Date());
		dbVariantId.setUpdatedOn(new Date());
		return variantIdRepository.save(dbVariantId);
	}
	
	/**
	 * updateVariantId
	 * @param loginUserId 
	 * @param variantCode
	 * @param updateVariantId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public VariantId updateVariantId ( String warehouseId, String variantCode, String variantType, String variantSubCode, String loginUserID, 
			UpdateVariantId updateVariantId) 
			throws IllegalAccessException, InvocationTargetException {
		VariantId dbVariantId = getVariantId(warehouseId, variantCode, variantType, variantSubCode);
		BeanUtils.copyProperties(updateVariantId, dbVariantId, CommonUtils.getNullPropertyNames(updateVariantId));
		dbVariantId.setUpdatedBy(loginUserID);
		dbVariantId.setUpdatedOn(new Date());
		return variantIdRepository.save(dbVariantId);
	}
	
	/**
	 * deleteVariantId
	 * @param loginUserID 
	 * @param variantCode
	 */
	public void deleteVariantId ( String warehouseId, String variantCode, String variantType, String variantSubCode, 
			String loginUserID) {
		VariantId dbVariantId = getVariantId(warehouseId, variantCode, variantType, variantSubCode);
		if ( dbVariantId != null) {
			dbVariantId.setDeletionIndicator(1L);
			dbVariantId.setUpdatedBy(loginUserID);
			variantIdRepository.save(dbVariantId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + variantCode);
		}
	}
}
