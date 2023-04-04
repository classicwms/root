package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.AddCycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.CycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.UpdateCycleCountTypeId;
import com.tekclover.wms.api.idmaster.repository.CycleCountTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CycleCountTypeIdService extends BaseService {
	
	@Autowired
	private CycleCountTypeIdRepository cycleCountTypeIdRepository;
	
	/**
	 * getCycleCountTypeIds
	 * @return
	 */
	public List<CycleCountTypeId> getCycleCountTypeIds () {
		List<CycleCountTypeId> cycleCountTypeIdList =  cycleCountTypeIdRepository.findAll();
		cycleCountTypeIdList = cycleCountTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return cycleCountTypeIdList;
	}
	
	/**
	 * getCycleCountTypeId
	 * @param cycleCountTypeId
	 * @return
	 */
	public CycleCountTypeId getCycleCountTypeId (String warehouseId, String cycleCountTypeId) {
		Optional<CycleCountTypeId> dbCycleCountTypeId = 
				cycleCountTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								cycleCountTypeId,
								getLanguageId(),
								0L
								);
		if (dbCycleCountTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"cycleCountTypeId - " + cycleCountTypeId +
						" doesn't exist.");
			
		} 
		return dbCycleCountTypeId.get();
	}
	
	/**
	 * createCycleCountTypeId
	 * @param newCycleCountTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CycleCountTypeId createCycleCountTypeId (AddCycleCountTypeId newCycleCountTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		CycleCountTypeId dbCycleCountTypeId = new CycleCountTypeId();
		log.info("newCycleCountTypeId : " + newCycleCountTypeId);
		BeanUtils.copyProperties(newCycleCountTypeId, dbCycleCountTypeId, CommonUtils.getNullPropertyNames(newCycleCountTypeId));
		dbCycleCountTypeId.setCompanyCodeId(getCompanyCode());
		dbCycleCountTypeId.setPlantId(getPlantId());
		dbCycleCountTypeId.setDeletionIndicator(0L);
		dbCycleCountTypeId.setCreatedBy(loginUserID);
		dbCycleCountTypeId.setUpdatedBy(loginUserID);
		dbCycleCountTypeId.setCreatedOn(new Date());
		dbCycleCountTypeId.setUpdatedOn(new Date());
		return cycleCountTypeIdRepository.save(dbCycleCountTypeId);
	}
	
	/**
	 * updateCycleCountTypeId
	 * @param loginUserID
	 * @param cycleCountTypeId
	 * @param updateCycleCountTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CycleCountTypeId updateCycleCountTypeId (String warehouseId, String cycleCountTypeId, String loginUserID,
			UpdateCycleCountTypeId updateCycleCountTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		CycleCountTypeId dbCycleCountTypeId = getCycleCountTypeId( warehouseId, cycleCountTypeId);
		BeanUtils.copyProperties(updateCycleCountTypeId, dbCycleCountTypeId, CommonUtils.getNullPropertyNames(updateCycleCountTypeId));
		dbCycleCountTypeId.setUpdatedBy(loginUserID);
		dbCycleCountTypeId.setUpdatedOn(new Date());
		return cycleCountTypeIdRepository.save(dbCycleCountTypeId);
	}
	
	/**
	 * deleteCycleCountTypeId
	 * @param loginUserID 
	 * @param cycleCountTypeId
	 */
	public void deleteCycleCountTypeId (String warehouseId, String cycleCountTypeId, String loginUserID) {
		CycleCountTypeId dbCycleCountTypeId = getCycleCountTypeId( warehouseId, cycleCountTypeId);
		if ( dbCycleCountTypeId != null) {
			dbCycleCountTypeId.setDeletionIndicator(1L);
			dbCycleCountTypeId.setUpdatedBy(loginUserID);
			cycleCountTypeIdRepository.save(dbCycleCountTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + cycleCountTypeId);
		}
	}
}
