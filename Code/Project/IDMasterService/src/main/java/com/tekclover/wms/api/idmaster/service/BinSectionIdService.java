package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.binsectionid.AddBinSectionId;
import com.tekclover.wms.api.idmaster.model.binsectionid.BinSectionId;
import com.tekclover.wms.api.idmaster.model.binsectionid.UpdateBinSectionId;
import com.tekclover.wms.api.idmaster.repository.BinSectionIdRepository;
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
public class BinSectionIdService extends BaseService {
	
	@Autowired
	private BinSectionIdRepository binSectionIdRepository;
	
	/**
	 * getBinSectionIds
	 * @return
	 */
	public List<BinSectionId> getBinSectionIds () {
		List<BinSectionId> binSectionIdList =  binSectionIdRepository.findAll();
		binSectionIdList = binSectionIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return binSectionIdList;
	}
	
	/**
	 * getBinSectionId
	 * @param binSectionId
	 * @return
	 */
	public BinSectionId getBinSectionId (String warehouseId, String binSectionId) {
		Optional<BinSectionId> dbBinSectionId = 
				binSectionIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinSectionIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								binSectionId,
								getLanguageId(),
								0L
								);
		if (dbBinSectionId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"binSectionId - " + binSectionId +
						" doesn't exist.");
			
		} 
		return dbBinSectionId.get();
	}
	
	/**
	 * createBinSectionId
	 * @param newBinSectionId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BinSectionId createBinSectionId (AddBinSectionId newBinSectionId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		BinSectionId dbBinSectionId = new BinSectionId();
		log.info("newBinSectionId : " + newBinSectionId);
		BeanUtils.copyProperties(newBinSectionId, dbBinSectionId, CommonUtils.getNullPropertyNames(newBinSectionId));
		dbBinSectionId.setCompanyCodeId(getCompanyCode());
		dbBinSectionId.setPlantId(getPlantId());
		dbBinSectionId.setDeletionIndicator(0L);
		dbBinSectionId.setCreatedBy(loginUserID);
		dbBinSectionId.setUpdatedBy(loginUserID);
		dbBinSectionId.setCreatedOn(new Date());
		dbBinSectionId.setUpdatedOn(new Date());
		return binSectionIdRepository.save(dbBinSectionId);
	}
	
	/**
	 * updateBinSectionId
	 * @param loginUserID
	 * @param binSectionId
	 * @param updateBinSectionId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BinSectionId updateBinSectionId (String warehouseId, String binSectionId, String loginUserID,
			UpdateBinSectionId updateBinSectionId) 
			throws IllegalAccessException, InvocationTargetException {
		BinSectionId dbBinSectionId = getBinSectionId( warehouseId, binSectionId);
		BeanUtils.copyProperties(updateBinSectionId, dbBinSectionId, CommonUtils.getNullPropertyNames(updateBinSectionId));
		dbBinSectionId.setUpdatedBy(loginUserID);
		dbBinSectionId.setUpdatedOn(new Date());
		return binSectionIdRepository.save(dbBinSectionId);
	}
	
	/**
	 * deleteBinSectionId
	 * @param loginUserID 
	 * @param binSectionId
	 */
	public void deleteBinSectionId (String warehouseId, String binSectionId, String loginUserID) {
		BinSectionId dbBinSectionId = getBinSectionId( warehouseId, binSectionId);
		if ( dbBinSectionId != null) {
			dbBinSectionId.setDeletionIndicator(1L);
			dbBinSectionId.setUpdatedBy(loginUserID);
			binSectionIdRepository.save(dbBinSectionId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + binSectionId);
		}
	}
}
