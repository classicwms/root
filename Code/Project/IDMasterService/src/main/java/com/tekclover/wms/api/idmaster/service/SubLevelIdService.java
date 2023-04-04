package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.sublevelid.AddSubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.SubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.UpdateSubLevelId;
import com.tekclover.wms.api.idmaster.repository.SubLevelIdRepository;
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
public class SubLevelIdService extends BaseService {
	
	@Autowired
	private SubLevelIdRepository subLevelIdRepository;
	
	/**
	 * getSubLevelIds
	 * @return
	 */
	public List<SubLevelId> getSubLevelIds () {
		List<SubLevelId> subLevelIdList =  subLevelIdRepository.findAll();
		subLevelIdList = subLevelIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return subLevelIdList;
	}
	
	/**
	 * getSubLevelId
	 * @param subLevelId
	 * @return
	 */
	public SubLevelId getSubLevelId (String warehouseId, String subLevelId, String levelId) {
		Optional<SubLevelId> dbSubLevelId = 
				subLevelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubLevelIdAndLevelIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								subLevelId,
								levelId,
								getLanguageId(),
								0L
								);
		if (dbSubLevelId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"subLevelId - " + subLevelId +
						"approvalLevel - " + levelId +
						" doesn't exist.");
			
		} 
		return dbSubLevelId.get();
	}
	
	/**
	 * createSubLevelId
	 * @param newSubLevelId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubLevelId createSubLevelId (AddSubLevelId newSubLevelId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		SubLevelId dbSubLevelId = new SubLevelId();
		log.info("newSubLevelId : " + newSubLevelId);
		BeanUtils.copyProperties(newSubLevelId, dbSubLevelId, CommonUtils.getNullPropertyNames(newSubLevelId));
		dbSubLevelId.setCompanyCodeId(getCompanyCode());
		dbSubLevelId.setPlantId(getPlantId());
		dbSubLevelId.setDeletionIndicator(0L);
		dbSubLevelId.setCreatedBy(loginUserID);
		dbSubLevelId.setUpdatedBy(loginUserID);
		dbSubLevelId.setCreatedOn(new Date());
		dbSubLevelId.setUpdatedOn(new Date());
		return subLevelIdRepository.save(dbSubLevelId);
	}
	
	/**
	 * updateSubLevelId
	 * @param loginUserID
	 * @param subLevelId
	 * @param updateSubLevelId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubLevelId updateSubLevelId (String warehouseId, String subLevelId,String levelId, String loginUserID,
			UpdateSubLevelId updateSubLevelId) 
			throws IllegalAccessException, InvocationTargetException {
		SubLevelId dbSubLevelId = getSubLevelId(warehouseId, subLevelId, levelId);
		BeanUtils.copyProperties(updateSubLevelId, dbSubLevelId, CommonUtils.getNullPropertyNames(updateSubLevelId));
		dbSubLevelId.setUpdatedBy(loginUserID);
		dbSubLevelId.setUpdatedOn(new Date());
		return subLevelIdRepository.save(dbSubLevelId);
	}
	
	/**
	 * deleteSubLevelId
	 * @param loginUserID 
	 * @param subLevelId
	 */
	public void deleteSubLevelId (String warehouseId, String subLevelId,String levelId,  String loginUserID) {
		SubLevelId dbSubLevelId = getSubLevelId(warehouseId, subLevelId, levelId);
		if ( dbSubLevelId != null) {
			dbSubLevelId.setDeletionIndicator(1L);
			dbSubLevelId.setUpdatedBy(loginUserID);
			subLevelIdRepository.save(dbSubLevelId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + subLevelId);
		}
	}
}
