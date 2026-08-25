package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.AddApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.FindApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.UpdateApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.CycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.ApprovalProcessIdRepository;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.ApprovalProcessIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApprovalProcessIdService {

	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private ApprovalProcessIdRepository approvalProcessIdRepository;

	/**
	 * getApprovalProcessIds
	 * @return
	 */
	public List<ApprovalProcessId> getApprovalProcessIds () {
		List<ApprovalProcessId> approvalProcessIdList =  approvalProcessIdRepository.findAll();
		approvalProcessIdList = approvalProcessIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ApprovalProcessId> newApprovalProcessId=new ArrayList<>();
		for(ApprovalProcessId dbApprovalProcessId:approvalProcessIdList) {
			if (dbApprovalProcessId.getCompanyIdAndDescription() != null&&dbApprovalProcessId.getPlantIdAndDescription()!=null&&dbApprovalProcessId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbApprovalProcessId.getCompanyCodeId(), dbApprovalProcessId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbApprovalProcessId.getPlantId(), dbApprovalProcessId.getLanguageId(), dbApprovalProcessId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbApprovalProcessId.getWarehouseId(), dbApprovalProcessId.getLanguageId(), dbApprovalProcessId.getCompanyCodeId(), dbApprovalProcessId.getPlantId());
				if (iKeyValuePair != null) {
					dbApprovalProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbApprovalProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbApprovalProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newApprovalProcessId.add(dbApprovalProcessId);
		}
		return newApprovalProcessId;
	}

	/**
	 * getApprovalProcessId
	 * @param approvalProcessId
	 * @return
	 */
	public ApprovalProcessId getApprovalProcessId (String warehouseId,String approvalProcessId,String companyCodeId,String languageId,String plantId) {
		Optional<ApprovalProcessId> dbApprovalProcessId =
				approvalProcessIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndApprovalProcessIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						approvalProcessId,
						languageId,
						0L
				);
		if (dbApprovalProcessId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"approvalProcessId - " + approvalProcessId +
					" doesn't exist.");

		}
		ApprovalProcessId newApprovalProcessId = new ApprovalProcessId();
		BeanUtils.copyProperties(dbApprovalProcessId.get(),newApprovalProcessId, CommonUtils.getNullPropertyNames(dbApprovalProcessId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newApprovalProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newApprovalProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newApprovalProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newApprovalProcessId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ApprovalProcessId dbApprovalProcessId = new ApprovalProcessId();
		Optional<ApprovalProcessId>duplicateApprovalProcessId=approvalProcessIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndApprovalProcessIdAndLanguageIdAndDeletionIndicator(newApprovalProcessId.getCompanyCodeId(), newApprovalProcessId.getPlantId(), newApprovalProcessId.getWarehouseId(), newApprovalProcessId.getApprovalProcessId(), newApprovalProcessId.getLanguageId(),0L);
		if(!duplicateApprovalProcessId.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newApprovalProcessId.getWarehouseId(), newApprovalProcessId.getCompanyCodeId(), newApprovalProcessId.getPlantId(), newApprovalProcessId.getLanguageId());
			log.info("newApprovalProcessId : " + newApprovalProcessId);
			BeanUtils.copyProperties(newApprovalProcessId, dbApprovalProcessId, CommonUtils.getNullPropertyNames(newApprovalProcessId));
			dbApprovalProcessId.setDeletionIndicator(0L);
			dbApprovalProcessId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbApprovalProcessId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbApprovalProcessId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbApprovalProcessId.setCreatedBy(loginUserID);
			dbApprovalProcessId.setUpdatedBy(loginUserID);
			dbApprovalProcessId.setCreatedOn(new Date());
			dbApprovalProcessId.setUpdatedOn(new Date());
			return approvalProcessIdRepository.save(dbApprovalProcessId);
		}
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
	public ApprovalProcessId updateApprovalProcessId (String warehouseId, String approvalProcessId,String companyCodeId,String languageId,String plantId, String loginUserID,
													  UpdateApprovalProcessId updateApprovalProcessId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ApprovalProcessId dbApprovalProcessId = getApprovalProcessId( warehouseId,approvalProcessId,companyCodeId,languageId,plantId);
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
	public void deleteApprovalProcessId (String warehouseId, String approvalProcessId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		ApprovalProcessId dbApprovalProcessId = getApprovalProcessId( warehouseId, approvalProcessId,companyCodeId,languageId,plantId);
		if ( dbApprovalProcessId != null) {
			dbApprovalProcessId.setDeletionIndicator(1L);
			dbApprovalProcessId.setUpdatedBy(loginUserID);
			approvalProcessIdRepository.save(dbApprovalProcessId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + approvalProcessId);
		}
	}

	//Find ApprovalProcessId
	public List<ApprovalProcessId> findApprovalProcessId (FindApprovalProcessId findApprovalProcessId) throws ParseException {

		ApprovalProcessIdSpecification spec = new ApprovalProcessIdSpecification(findApprovalProcessId);
		List<ApprovalProcessId> results = approvalProcessIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ApprovalProcessId> newApprovalProcessId=new ArrayList<>();
		for(ApprovalProcessId dbApprovalProcessId:results) {
			if (dbApprovalProcessId.getCompanyIdAndDescription() != null&&dbApprovalProcessId.getPlantIdAndDescription()!=null&&dbApprovalProcessId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbApprovalProcessId.getCompanyCodeId(),dbApprovalProcessId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbApprovalProcessId.getPlantId(), dbApprovalProcessId.getLanguageId(),dbApprovalProcessId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbApprovalProcessId.getWarehouseId(), dbApprovalProcessId.getLanguageId(), dbApprovalProcessId.getCompanyCodeId(), dbApprovalProcessId.getPlantId());
				if (iKeyValuePair != null) {
					dbApprovalProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbApprovalProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbApprovalProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}	}
			newApprovalProcessId.add(dbApprovalProcessId);
		}
		return newApprovalProcessId;
	}
}
