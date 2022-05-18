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
import com.tekclover.wms.api.idmaster.model.rowid.AddRowId;
import com.tekclover.wms.api.idmaster.model.rowid.RowId;
import com.tekclover.wms.api.idmaster.model.rowid.UpdateRowId;
import com.tekclover.wms.api.idmaster.repository.RowIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RowIdService extends BaseService {
	
	@Autowired
	private RowIdRepository rowIdRepository;
	
	/**
	 * getRowIds
	 * @return
	 */
	public List<RowId> getRowIds () {
		List<RowId> rowIdList =  rowIdRepository.findAll();
		rowIdList = rowIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return rowIdList;
	}
	
	/**
	 * getRowId
	 * @param rowId
	 * @return
	 */
	public RowId getRowId (String warehouseId, Long floorId, Long storageSectionId, String rowId) {
		Optional<RowId> dbRowId = 
				rowIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndRowIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								floorId,
								storageSectionId,
								rowId,
								getLanguageId(),
								0L
								);
		if (dbRowId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"floorId - " + floorId +
						"storageSectionId - " + storageSectionId +
						"rowId - " + rowId +
						" doesn't exist.");
			
		} 
		return dbRowId.get();
	}
	
//	/**
//	 * 
//	 * @param searchRowId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<RowId> findRowId(SearchRowId searchRowId) 
//			throws ParseException {
//		RowIdSpecification spec = new RowIdSpecification(searchRowId);
//		List<RowId> results = rowIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createRowId
	 * @param newRowId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RowId createRowId (AddRowId newRowId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		RowId dbRowId = new RowId();
		log.info("newRowId : " + newRowId);
		BeanUtils.copyProperties(newRowId, dbRowId, CommonUtils.getNullPropertyNames(newRowId));
		dbRowId.setCompanyCodeId(getCompanyCode());
		dbRowId.setPlantId(getPlantId());
		dbRowId.setDeletionIndicator(0L);
		dbRowId.setCreatedBy(loginUserID);
		dbRowId.setUpdatedBy(loginUserID);
		dbRowId.setCreatedOn(new Date());
		dbRowId.setUpdatedOn(new Date());
		return rowIdRepository.save(dbRowId);
	}
	
	/**
	 * updateRowId
	 * @param loginUserId 
	 * @param rowId
	 * @param updateRowId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RowId updateRowId (String warehouseId, Long floorId, Long storageSectionId, String rowId, String loginUserID, 
			UpdateRowId updateRowId) throws IllegalAccessException, InvocationTargetException {
		RowId dbRowId = getRowId(warehouseId, floorId, storageSectionId, rowId);
		BeanUtils.copyProperties(updateRowId, dbRowId, CommonUtils.getNullPropertyNames(updateRowId));
		dbRowId.setUpdatedBy(loginUserID);
		dbRowId.setUpdatedOn(new Date());
		return rowIdRepository.save(dbRowId);
	}
	
	/**
	 * deleteRowId
	 * @param loginUserID 
	 * @param rowId
	 */
	public void deleteRowId (String warehouseId, Long floorId, Long storageSectionId, String rowId, String loginUserID) {
		RowId dbRowId = getRowId(warehouseId, floorId, storageSectionId, rowId);
		if ( dbRowId != null) {
			dbRowId.setDeletionIndicator(1L);
			dbRowId.setUpdatedBy(loginUserID);
			rowIdRepository.save(dbRowId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + rowId);
		}
	}
}
