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
import com.tekclover.wms.api.idmaster.model.binclassid.AddBinClassId;
import com.tekclover.wms.api.idmaster.model.binclassid.BinClassId;
import com.tekclover.wms.api.idmaster.model.binclassid.UpdateBinClassId;
import com.tekclover.wms.api.idmaster.repository.BinClassIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BinClassIdService extends BaseService {
	
	@Autowired
	private BinClassIdRepository binClassIdRepository;
	
	/**
	 * getBinClassIds
	 * @return
	 */
	public List<BinClassId> getBinClassIds () {
		List<BinClassId> BinClassIdList =  binClassIdRepository.findAll();
		BinClassIdList = BinClassIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return BinClassIdList;
	}
	
	/**
	 *
	 * @param warehouseId
	 * @param binClassId
	 * @return
	 */
	public BinClassId getBinClassId (String warehouseId, Long binClassId) {
		Optional<BinClassId> objBinClassId = 
				binClassIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinClassIdAndLanguageIdAndDeletionIndicator(
						getCompanyCode(), getPlantId(), warehouseId, binClassId, getLanguageId(), 0L);
		if (objBinClassId.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",binClassId: " + binClassId + 
					" doesn't exist.");
		} 
		return objBinClassId.get();
	}
	
	/**
	 * createBinClassId
	 * @param newBinClassId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BinClassId createBinClassId (AddBinClassId newBinClassId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<BinClassId> binClassId = 
				binClassIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinClassIdAndLanguageIdAndDeletionIndicator(
						getCompanyCode(), 
						getPlantId(),
						newBinClassId.getWarehouseId(),
						newBinClassId.getBinClassId(),
						getLanguageId(),
						0L);
		if (!binClassId.isEmpty()) {
			throw new BadRequestException("Record is getting duplicated with the given values");
		}
		BinClassId dbBinClassId = new BinClassId();
		log.info("newBinClassId : " + newBinClassId);
		BeanUtils.copyProperties(newBinClassId, dbBinClassId, CommonUtils.getNullPropertyNames(newBinClassId));
		dbBinClassId.setDeletionIndicator(0L);
		dbBinClassId.setCreatedBy(loginUserID);
		dbBinClassId.setUpdatedBy(loginUserID);
		dbBinClassId.setCreatedOn(new Date());
		dbBinClassId.setUpdatedOn(new Date());
		return binClassIdRepository.save(dbBinClassId);
	}
	
	/**
	 * updateBinClassId
	 * @param loginUserID
	 * @param binClassId
	 * @param updateBinClassId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BinClassId updateBinClassId (String warehouseId, Long binClassId, String loginUserID, 
			UpdateBinClassId updateBinClassId) throws IllegalAccessException, InvocationTargetException {
		BinClassId dbBinClassId = getBinClassId(warehouseId, binClassId);
		BeanUtils.copyProperties(updateBinClassId, dbBinClassId, CommonUtils.getNullPropertyNames(updateBinClassId));
		dbBinClassId.setUpdatedBy(loginUserID);
		dbBinClassId.setUpdatedOn(new Date());
		return binClassIdRepository.save(dbBinClassId);
	}
	
	/**
	 * deleteBinClassId
	 * @param loginUserID 
	 * @param binClassId
	 */
	public void deleteBinClassId (String warehouseId, Long binClassId, String loginUserID) {
		BinClassId dbBinClassId = getBinClassId(warehouseId, binClassId);
		if ( dbBinClassId != null) {
			dbBinClassId.setDeletionIndicator(1L);
			dbBinClassId.setUpdatedBy(loginUserID);
			binClassIdRepository.save(dbBinClassId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + dbBinClassId);
		}
	}
}
