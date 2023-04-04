package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.palletizationlevelid.*;
import com.tekclover.wms.api.idmaster.repository.PalletizationLevelIdRepository;
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
public class PalletizationLevelIdService extends BaseService {
	
	@Autowired
	private PalletizationLevelIdRepository palletizationLevelIdRepository;
	
	/**
	 * getPalletizationLevelIds
	 * @return
	 */
	public List<PalletizationLevelId> getPalletizationLevelIds () {
		List<PalletizationLevelId> palletizationLevelIdList =  palletizationLevelIdRepository.findAll();
		palletizationLevelIdList = palletizationLevelIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return palletizationLevelIdList;
	}
	
	/**
	 * getPalletizationLevelId
	 * @param palletizationLevelId
	 * @return
	 */
	public PalletizationLevelId getPalletizationLevelId (String warehouseId, String palletizationLevelId, String palletizationLevel) {
		Optional<PalletizationLevelId> dbPalletizationLevelId = 
				palletizationLevelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPalletizationLevelIdAndPalletizationLevelAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								palletizationLevelId,
								palletizationLevel,
								getLanguageId(),
								0L
								);
		if (dbPalletizationLevelId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"palletizationLevelId - " + palletizationLevelId +
						"palletizationLevel - " + palletizationLevel +
						" doesn't exist.");
			
		} 
		return dbPalletizationLevelId.get();
	}
	
	/**
	 * createPalletizationLevelId
	 * @param newPalletizationLevelId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PalletizationLevelId createPalletizationLevelId (AddPalletizationLevelId newPalletizationLevelId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		PalletizationLevelId dbPalletizationLevelId = new PalletizationLevelId();
		log.info("newPalletizationLevelId : " + newPalletizationLevelId);
		BeanUtils.copyProperties(newPalletizationLevelId, dbPalletizationLevelId, CommonUtils.getNullPropertyNames(newPalletizationLevelId));
		dbPalletizationLevelId.setCompanyCodeId(getCompanyCode());
		dbPalletizationLevelId.setPlantId(getPlantId());
		dbPalletizationLevelId.setDeletionIndicator(0L);
		dbPalletizationLevelId.setCreatedBy(loginUserID);
		dbPalletizationLevelId.setUpdatedBy(loginUserID);
		dbPalletizationLevelId.setCreatedOn(new Date());
		dbPalletizationLevelId.setUpdatedOn(new Date());
		return palletizationLevelIdRepository.save(dbPalletizationLevelId);
	}
	
	/**
	 * updatePalletizationLevelId
	 * @param loginUserID
	 * @param palletizationLevelId
	 * @param updatePalletizationLevelId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PalletizationLevelId updatePalletizationLevelId (String warehouseId, String palletizationLevelId, String palletizationLevel, String loginUserID,
			UpdatePalletizationLevelId updatePalletizationLevelId) 
			throws IllegalAccessException, InvocationTargetException {
		PalletizationLevelId dbPalletizationLevelId = getPalletizationLevelId( warehouseId, palletizationLevelId, palletizationLevel);
		BeanUtils.copyProperties(updatePalletizationLevelId, dbPalletizationLevelId, CommonUtils.getNullPropertyNames(updatePalletizationLevelId));
		dbPalletizationLevelId.setUpdatedBy(loginUserID);
		dbPalletizationLevelId.setUpdatedOn(new Date());
		return palletizationLevelIdRepository.save(dbPalletizationLevelId);
	}
	
	/**
	 * deletePalletizationLevelId
	 * @param loginUserID 
	 * @param palletizationLevelId
	 */
	public void deletePalletizationLevelId (String warehouseId, String palletizationLevelId, String palletizationLevel, String loginUserID) {
		PalletizationLevelId dbPalletizationLevelId = getPalletizationLevelId( warehouseId, palletizationLevelId, palletizationLevel);
		if ( dbPalletizationLevelId != null) {
			dbPalletizationLevelId.setDeletionIndicator(1L);
			dbPalletizationLevelId.setUpdatedBy(loginUserID);
			palletizationLevelIdRepository.save(dbPalletizationLevelId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + palletizationLevelId);
		}
	}
}
