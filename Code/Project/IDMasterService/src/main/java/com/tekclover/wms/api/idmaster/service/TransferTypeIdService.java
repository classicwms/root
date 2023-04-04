package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.transfertypeid.AddTransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.TransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.UpdateTransferTypeId;
import com.tekclover.wms.api.idmaster.repository.TransferTypeIdRepository;
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
public class TransferTypeIdService extends BaseService {
	
	@Autowired
	private TransferTypeIdRepository transferTypeIdRepository;
	
	/**
	 * getTransferTypeIds
	 * @return
	 */
	public List<TransferTypeId> getTransferTypeIds () {
		List<TransferTypeId> transferTypeIdList =  transferTypeIdRepository.findAll();
		transferTypeIdList = transferTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return transferTypeIdList;
	}
	
	/**
	 * getTransferTypeId
	 * @param transferTypeId
	 * @return
	 */
	public TransferTypeId getTransferTypeId (String warehouseId, String transferTypeId) {
		Optional<TransferTypeId> dbTransferTypeId = 
				transferTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								transferTypeId,
								getLanguageId(),
								0L
								);
		if (dbTransferTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"transferTypeId - " + transferTypeId +
						" doesn't exist.");
			
		} 
		return dbTransferTypeId.get();
	}
	
	/**
	 * createTransferTypeId
	 * @param newTransferTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public TransferTypeId createTransferTypeId (AddTransferTypeId newTransferTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		TransferTypeId dbTransferTypeId = new TransferTypeId();
		log.info("newTransferTypeId : " + newTransferTypeId);
		BeanUtils.copyProperties(newTransferTypeId, dbTransferTypeId, CommonUtils.getNullPropertyNames(newTransferTypeId));
		dbTransferTypeId.setCompanyCodeId(getCompanyCode());
		dbTransferTypeId.setPlantId(getPlantId());
		dbTransferTypeId.setDeletionIndicator(0L);
		dbTransferTypeId.setCreatedBy(loginUserID);
		dbTransferTypeId.setUpdatedBy(loginUserID);
		dbTransferTypeId.setCreatedOn(new Date());
		dbTransferTypeId.setUpdatedOn(new Date());
		return transferTypeIdRepository.save(dbTransferTypeId);
	}
	
	/**
	 * updateTransferTypeId
	 * @param loginUserID
	 * @param transferTypeId
	 * @param updateTransferTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public TransferTypeId updateTransferTypeId (String warehouseId, String transferTypeId, String loginUserID,
			UpdateTransferTypeId updateTransferTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		TransferTypeId dbTransferTypeId = getTransferTypeId( warehouseId, transferTypeId);
		BeanUtils.copyProperties(updateTransferTypeId, dbTransferTypeId, CommonUtils.getNullPropertyNames(updateTransferTypeId));
		dbTransferTypeId.setUpdatedBy(loginUserID);
		dbTransferTypeId.setUpdatedOn(new Date());
		return transferTypeIdRepository.save(dbTransferTypeId);
	}
	
	/**
	 * deleteTransferTypeId
	 * @param loginUserID 
	 * @param transferTypeId
	 */
	public void deleteTransferTypeId (String warehouseId, String transferTypeId, String loginUserID) {
		TransferTypeId dbTransferTypeId = getTransferTypeId( warehouseId, transferTypeId);
		if ( dbTransferTypeId != null) {
			dbTransferTypeId.setDeletionIndicator(1L);
			dbTransferTypeId.setUpdatedBy(loginUserID);
			transferTypeIdRepository.save(dbTransferTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + transferTypeId);
		}
	}
}
