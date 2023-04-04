package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.approvalid.*;
import com.tekclover.wms.api.idmaster.repository.ApprovalIdRepository;
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
public class ApprovalIdService extends BaseService {
	
	@Autowired
	private ApprovalIdRepository approvalIdRepository;
	
	/**
	 * getApprovalIds
	 * @return
	 */
	public List<ApprovalId> getApprovalIds () {
		List<ApprovalId> approvalIdList =  approvalIdRepository.findAll();
		approvalIdList = approvalIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return approvalIdList;
	}
	
	/**
	 * getApprovalId
	 * @param approvalId
	 * @return
	 */
	public ApprovalId getApprovalId (String warehouseId, String approvalId, String approvalLevel, String approverCode) {
		Optional<ApprovalId> dbApprovalId = 
				approvalIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndApprovalIdAndApprovalLevelAndApproverCodeAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								approvalId,
								approvalLevel,
								approverCode,
								getLanguageId(),
								0L
								);
		if (dbApprovalId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"approvalId - " + approvalId +
						"approvalLevel - " + approvalLevel +
						"approverCode - " + approverCode +
						" doesn't exist.");
			
		} 
		return dbApprovalId.get();
	}
	
	/**
	 * createApprovalId
	 * @param newApprovalId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ApprovalId createApprovalId (AddApprovalId newApprovalId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ApprovalId dbApprovalId = new ApprovalId();
		log.info("newApprovalId : " + newApprovalId);
		BeanUtils.copyProperties(newApprovalId, dbApprovalId, CommonUtils.getNullPropertyNames(newApprovalId));
		dbApprovalId.setCompanyCodeId(getCompanyCode());
		dbApprovalId.setPlantId(getPlantId());
		dbApprovalId.setDeletionIndicator(0L);
		dbApprovalId.setCreatedBy(loginUserID);
		dbApprovalId.setUpdatedBy(loginUserID);
		dbApprovalId.setCreatedOn(new Date());
		dbApprovalId.setUpdatedOn(new Date());
		return approvalIdRepository.save(dbApprovalId);
	}
	
	/**
	 * updateApprovalId
	 * @param loginUserID
	 * @param approvalId
	 * @param updateApprovalId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ApprovalId updateApprovalId (String warehouseId, String approvalId,String approvalLevel, String approverCode, String loginUserID,
			UpdateApprovalId updateApprovalId) 
			throws IllegalAccessException, InvocationTargetException {
		ApprovalId dbApprovalId = getApprovalId(warehouseId, approvalId, approvalLevel,approverCode);
		BeanUtils.copyProperties(updateApprovalId, dbApprovalId, CommonUtils.getNullPropertyNames(updateApprovalId));
		dbApprovalId.setUpdatedBy(loginUserID);
		dbApprovalId.setUpdatedOn(new Date());
		return approvalIdRepository.save(dbApprovalId);
	}
	
	/**
	 * deleteApprovalId
	 * @param loginUserID 
	 * @param approvalId
	 */
	public void deleteApprovalId (String warehouseId, String approvalId,String approvalLevel, String approverCode, String loginUserID) {
		ApprovalId dbApprovalId = getApprovalId(warehouseId, approvalId, approvalLevel,approverCode);
		if ( dbApprovalId != null) {
			dbApprovalId.setDeletionIndicator(1L);
			dbApprovalId.setUpdatedBy(loginUserID);
			approvalIdRepository.save(dbApprovalId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + approvalId);
		}
	}
}
