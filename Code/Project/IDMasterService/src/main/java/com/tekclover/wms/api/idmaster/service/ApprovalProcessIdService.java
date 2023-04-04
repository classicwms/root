package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.AddApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.UpdateApprovalProcessId;
import com.tekclover.wms.api.idmaster.repository.ApprovalProcessIdRepository;
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
public class ApprovalProcessIdService extends BaseService {
	
	@Autowired
	private ApprovalProcessIdRepository approvalProcessIdRepository;
	
	/**
	 * getApprovalProcessIds
	 * @return
	 */
	public List<ApprovalProcessId> getApprovalProcessIds () {
		List<ApprovalProcessId> approvalProcessIdList =  approvalProcessIdRepository.findAll();
		approvalProcessIdList = approvalProcessIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return approvalProcessIdList;
	}
	
	/**
	 * getApprovalProcessId
	 * @param approvalProcessId
	 * @return
	 */
	public ApprovalProcessId getApprovalProcessId (String warehouseId, String approvalProcessId) {
		Optional<ApprovalProcessId> dbApprovalProcessId = 
				approvalProcessIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndApprovalProcessIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								approvalProcessId,
								getLanguageId(),
								0L
								);
		if (dbApprovalProcessId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"approvalProcessId - " + approvalProcessId +
						" doesn't exist.");
			
		} 
		return dbApprovalProcessId.get();
	}
	
	/**
	 * createApprovalProcessId
	 * @param newApprovalProcessId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ApprovalProcessId createApprovalProcessId (AddApprovalProcessId newApprovalProcessId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ApprovalProcessId dbApprovalProcessId = new ApprovalProcessId();
		log.info("newApprovalProcessId : " + newApprovalProcessId);
		BeanUtils.copyProperties(newApprovalProcessId, dbApprovalProcessId, CommonUtils.getNullPropertyNames(newApprovalProcessId));
		dbApprovalProcessId.setCompanyCodeId(getCompanyCode());
		dbApprovalProcessId.setPlantId(getPlantId());
		dbApprovalProcessId.setDeletionIndicator(0L);
		dbApprovalProcessId.setCreatedBy(loginUserID);
		dbApprovalProcessId.setUpdatedBy(loginUserID);
		dbApprovalProcessId.setCreatedOn(new Date());
		dbApprovalProcessId.setUpdatedOn(new Date());
		return approvalProcessIdRepository.save(dbApprovalProcessId);
	}
	
	/**
	 * updateApprovalProcessId
	 * @param loginUserID
	 * @param approvalProcessId
	 * @param updateApprovalProcessId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ApprovalProcessId updateApprovalProcessId (String warehouseId, String approvalProcessId, String loginUserID,
			UpdateApprovalProcessId updateApprovalProcessId) 
			throws IllegalAccessException, InvocationTargetException {
		ApprovalProcessId dbApprovalProcessId = getApprovalProcessId( warehouseId, approvalProcessId);
		BeanUtils.copyProperties(updateApprovalProcessId, dbApprovalProcessId, CommonUtils.getNullPropertyNames(updateApprovalProcessId));
		dbApprovalProcessId.setUpdatedBy(loginUserID);
		dbApprovalProcessId.setUpdatedOn(new Date());
		return approvalProcessIdRepository.save(dbApprovalProcessId);
	}
	
	/**
	 * deleteApprovalProcessId
	 * @param loginUserID 
	 * @param approvalProcessId
	 */
	public void deleteApprovalProcessId (String warehouseId, String approvalProcessId, String loginUserID) {
		ApprovalProcessId dbApprovalProcessId = getApprovalProcessId( warehouseId, approvalProcessId);
		if ( dbApprovalProcessId != null) {
			dbApprovalProcessId.setDeletionIndicator(1L);
			dbApprovalProcessId.setUpdatedBy(loginUserID);
			approvalProcessIdRepository.save(dbApprovalProcessId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + approvalProcessId);
		}
	}
}
