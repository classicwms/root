package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.inboundorderstatusid.*;
import com.tekclover.wms.api.idmaster.repository.InboundOrderStatusIdRepository;
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
public class InboundOrderStatusIdService extends BaseService {
	
	@Autowired
	private InboundOrderStatusIdRepository inboundOrderStatusIdRepository;
	
	/**
	 * getInboundOrderStatusIds
	 * @return
	 */
	public List<InboundOrderStatusId> getInboundOrderStatusIds () {
		List<InboundOrderStatusId> inboundOrderStatusIdList =  inboundOrderStatusIdRepository.findAll();
		inboundOrderStatusIdList = inboundOrderStatusIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return inboundOrderStatusIdList;
	}
	
	/**
	 * getInboundOrderStatusId
	 * @param inboundOrderStatusId
	 * @return
	 */
	public InboundOrderStatusId getInboundOrderStatusId (String warehouseId, String inboundOrderStatusId) {
		Optional<InboundOrderStatusId> dbInboundOrderStatusId = 
				inboundOrderStatusIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInboundOrderStatusIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								inboundOrderStatusId,
								getLanguageId(),
								0L
								);
		if (dbInboundOrderStatusId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"inboundOrderStatusId - " + inboundOrderStatusId +
						" doesn't exist.");
			
		} 
		return dbInboundOrderStatusId.get();
	}
	
	/**
	 * createInboundOrderStatusId
	 * @param newInboundOrderStatusId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InboundOrderStatusId createInboundOrderStatusId (AddInboundOrderStatusId newInboundOrderStatusId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		InboundOrderStatusId dbInboundOrderStatusId = new InboundOrderStatusId();
		log.info("newInboundOrderStatusId : " + newInboundOrderStatusId);
		BeanUtils.copyProperties(newInboundOrderStatusId, dbInboundOrderStatusId, CommonUtils.getNullPropertyNames(newInboundOrderStatusId));
		dbInboundOrderStatusId.setCompanyCodeId(getCompanyCode());
		dbInboundOrderStatusId.setPlantId(getPlantId());
		dbInboundOrderStatusId.setDeletionIndicator(0L);
		dbInboundOrderStatusId.setCreatedBy(loginUserID);
		dbInboundOrderStatusId.setUpdatedBy(loginUserID);
		dbInboundOrderStatusId.setCreatedOn(new Date());
		dbInboundOrderStatusId.setUpdatedOn(new Date());
		return inboundOrderStatusIdRepository.save(dbInboundOrderStatusId);
	}
	
	/**
	 * updateInboundOrderStatusId
	 * @param loginUserID
	 * @param inboundOrderStatusId
	 * @param updateInboundOrderStatusId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InboundOrderStatusId updateInboundOrderStatusId (String warehouseId, String inboundOrderStatusId, String loginUserID,
			UpdateInboundOrderStatusId updateInboundOrderStatusId) 
			throws IllegalAccessException, InvocationTargetException {
		InboundOrderStatusId dbInboundOrderStatusId = getInboundOrderStatusId( warehouseId, inboundOrderStatusId);
		BeanUtils.copyProperties(updateInboundOrderStatusId, dbInboundOrderStatusId, CommonUtils.getNullPropertyNames(updateInboundOrderStatusId));
		dbInboundOrderStatusId.setUpdatedBy(loginUserID);
		dbInboundOrderStatusId.setUpdatedOn(new Date());
		return inboundOrderStatusIdRepository.save(dbInboundOrderStatusId);
	}
	
	/**
	 * deleteInboundOrderStatusId
	 * @param loginUserID 
	 * @param inboundOrderStatusId
	 */
	public void deleteInboundOrderStatusId (String warehouseId, String inboundOrderStatusId, String loginUserID) {
		InboundOrderStatusId dbInboundOrderStatusId = getInboundOrderStatusId( warehouseId, inboundOrderStatusId);
		if ( dbInboundOrderStatusId != null) {
			dbInboundOrderStatusId.setDeletionIndicator(1L);
			dbInboundOrderStatusId.setUpdatedBy(loginUserID);
			inboundOrderStatusIdRepository.save(dbInboundOrderStatusId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + inboundOrderStatusId);
		}
	}
}
