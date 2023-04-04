package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.outboundorderstatusid.*;
import com.tekclover.wms.api.idmaster.repository.OutboundOrderStatusIdRepository;
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
public class OutboundOrderStatusIdService extends BaseService {
	
	@Autowired
	private OutboundOrderStatusIdRepository outboundOrderStatusIdRepository;
	
	/**
	 * getOutboundOrderStatusIds
	 * @return
	 */
	public List<OutboundOrderStatusId> getOutboundOrderStatusIds () {
		List<OutboundOrderStatusId> outboundOrderStatusIdList =  outboundOrderStatusIdRepository.findAll();
		outboundOrderStatusIdList = outboundOrderStatusIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return outboundOrderStatusIdList;
	}
	
	/**
	 * getOutboundOrderStatusId
	 * @param outboundOrderStatusId
	 * @return
	 */
	public OutboundOrderStatusId getOutboundOrderStatusId (String warehouseId, String outboundOrderStatusId) {
		Optional<OutboundOrderStatusId> dbOutboundOrderStatusId = 
				outboundOrderStatusIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndOutboundOrderStatusIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								outboundOrderStatusId,
								getLanguageId(),
								0L
								);
		if (dbOutboundOrderStatusId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"outboundOrderStatusId - " + outboundOrderStatusId +
						" doesn't exist.");
			
		} 
		return dbOutboundOrderStatusId.get();
	}
	
	/**
	 * createOutboundOrderStatusId
	 * @param newOutboundOrderStatusId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundOrderStatusId createOutboundOrderStatusId (AddOutboundOrderStatusId newOutboundOrderStatusId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		OutboundOrderStatusId dbOutboundOrderStatusId = new OutboundOrderStatusId();
		log.info("newOutboundOrderStatusId : " + newOutboundOrderStatusId);
		BeanUtils.copyProperties(newOutboundOrderStatusId, dbOutboundOrderStatusId, CommonUtils.getNullPropertyNames(newOutboundOrderStatusId));
		dbOutboundOrderStatusId.setCompanyCodeId(getCompanyCode());
		dbOutboundOrderStatusId.setPlantId(getPlantId());
		dbOutboundOrderStatusId.setDeletionIndicator(0L);
		dbOutboundOrderStatusId.setCreatedBy(loginUserID);
		dbOutboundOrderStatusId.setUpdatedBy(loginUserID);
		dbOutboundOrderStatusId.setCreatedOn(new Date());
		dbOutboundOrderStatusId.setUpdatedOn(new Date());
		return outboundOrderStatusIdRepository.save(dbOutboundOrderStatusId);
	}
	
	/**
	 * updateOutboundOrderStatusId
	 * @param loginUserID
	 * @param outboundOrderStatusId
	 * @param updateOutboundOrderStatusId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundOrderStatusId updateOutboundOrderStatusId (String warehouseId, String outboundOrderStatusId, String loginUserID,
			UpdateOutboundOrderStatusId updateOutboundOrderStatusId) 
			throws IllegalAccessException, InvocationTargetException {
		OutboundOrderStatusId dbOutboundOrderStatusId = getOutboundOrderStatusId( warehouseId, outboundOrderStatusId);
		BeanUtils.copyProperties(updateOutboundOrderStatusId, dbOutboundOrderStatusId, CommonUtils.getNullPropertyNames(updateOutboundOrderStatusId));
		dbOutboundOrderStatusId.setUpdatedBy(loginUserID);
		dbOutboundOrderStatusId.setUpdatedOn(new Date());
		return outboundOrderStatusIdRepository.save(dbOutboundOrderStatusId);
	}
	
	/**
	 * deleteOutboundOrderStatusId
	 * @param loginUserID 
	 * @param outboundOrderStatusId
	 */
	public void deleteOutboundOrderStatusId (String warehouseId, String outboundOrderStatusId, String loginUserID) {
		OutboundOrderStatusId dbOutboundOrderStatusId = getOutboundOrderStatusId( warehouseId, outboundOrderStatusId);
		if ( dbOutboundOrderStatusId != null) {
			dbOutboundOrderStatusId.setDeletionIndicator(1L);
			dbOutboundOrderStatusId.setUpdatedBy(loginUserID);
			outboundOrderStatusIdRepository.save(dbOutboundOrderStatusId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + outboundOrderStatusId);
		}
	}
}
