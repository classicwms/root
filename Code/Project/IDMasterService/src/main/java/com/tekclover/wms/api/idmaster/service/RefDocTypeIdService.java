package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.refdoctypeid.*;
import com.tekclover.wms.api.idmaster.repository.RefDocTypeIdRepository;
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
public class RefDocTypeIdService extends BaseService {
	
	@Autowired
	private RefDocTypeIdRepository refDocTypeIdRepository;
	
	/**
	 * getRefDocTypeIds
	 * @return
	 */
	public List<RefDocTypeId> getRefDocTypeIds () {
		List<RefDocTypeId> refDocTypeIdList =  refDocTypeIdRepository.findAll();
		refDocTypeIdList = refDocTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return refDocTypeIdList;
	}
	
	/**
	 * getRefDocTypeId
	 * @param referenceDocumentTypeId
	 * @return
	 */
	public RefDocTypeId getRefDocTypeId (String warehouseId, String referenceDocumentTypeId) {
		Optional<RefDocTypeId> dbRefDocTypeId = 
				refDocTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								referenceDocumentTypeId,
								getLanguageId(),
								0L
								);
		if (dbRefDocTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"refDocTypeId - " + referenceDocumentTypeId +
						"doesn't exist.");
			
		} 
		return dbRefDocTypeId.get();
	}
	
	/**
	 * createRefDocTypeId
	 * @param newRefDocTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RefDocTypeId createRefDocTypeId (AddRefDocTypeId newRefDocTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		RefDocTypeId dbRefDocTypeId = new RefDocTypeId();
		log.info("newRefDocTypeId : " + newRefDocTypeId);
		BeanUtils.copyProperties(newRefDocTypeId, dbRefDocTypeId, CommonUtils.getNullPropertyNames(newRefDocTypeId));
		dbRefDocTypeId.setCompanyCodeId(getCompanyCode());
		dbRefDocTypeId.setPlantId(getPlantId());
		dbRefDocTypeId.setDeletionIndicator(0L);
		dbRefDocTypeId.setCreatedBy(loginUserID);
		dbRefDocTypeId.setUpdatedBy(loginUserID);
		dbRefDocTypeId.setCreatedOn(new Date());
		dbRefDocTypeId.setUpdatedOn(new Date());
		return refDocTypeIdRepository.save(dbRefDocTypeId);
	}
	
	/**
	 * updateRefDocTypeId
	 * @param loginUserID
	 * @param referenceDocumentTypeId
	 * @param updateRefDocTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RefDocTypeId updateRefDocTypeId (String warehouseId, String referenceDocumentTypeId, String loginUserID,
			UpdateRefDocTypeId updateRefDocTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		RefDocTypeId dbRefDocTypeId = getRefDocTypeId( warehouseId, referenceDocumentTypeId);
		BeanUtils.copyProperties(updateRefDocTypeId, dbRefDocTypeId, CommonUtils.getNullPropertyNames(updateRefDocTypeId));
		dbRefDocTypeId.setUpdatedBy(loginUserID);
		dbRefDocTypeId.setUpdatedOn(new Date());
		return refDocTypeIdRepository.save(dbRefDocTypeId);
	}
	
	/**
	 * deleteRefDocTypeId
	 * @param loginUserID 
	 * @param referenceDocumentTypeId
	 */
	public void deleteRefDocTypeId (String warehouseId, String referenceDocumentTypeId, String loginUserID) {
		RefDocTypeId dbRefDocTypeId = getRefDocTypeId( warehouseId, referenceDocumentTypeId);
		if ( dbRefDocTypeId != null) {
			dbRefDocTypeId.setDeletionIndicator(1L);
			dbRefDocTypeId.setUpdatedBy(loginUserID);
			refDocTypeIdRepository.save(dbRefDocTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + referenceDocumentTypeId);
		}
	}
}
