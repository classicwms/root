package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.approvalid.*;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.ApprovalIdSpecification;
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
public class ApprovalIdService {

	@Autowired
	private ApprovalIdRepository approvalIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private ApprovalProcessIdRepository approvalProcessIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private ApprovalProcessIdService approvalProcessIdService;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getApprovalIds
	 * @return
	 */
	public List<ApprovalId> getApprovalIds () {
		List<ApprovalId> approvalIdList =  approvalIdRepository.findAll();
		approvalIdList = approvalIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ApprovalId> newApprovalId=new ArrayList<>();
		for(ApprovalId dbApprovalId:approvalIdList) {
			if (dbApprovalId.getCompanyIdAndDescription() != null && dbApprovalId.getPlantIdAndDescription() != null && dbApprovalId.getWarehouseIdAndDescription() != null && dbApprovalId.getApprovalProcessIdAndDescription() != null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbApprovalId.getCompanyCodeId(), dbApprovalId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbApprovalId.getPlantId(), dbApprovalId.getLanguageId(), dbApprovalId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbApprovalId.getWarehouseId(), dbApprovalId.getLanguageId(), dbApprovalId.getCompanyCodeId(), dbApprovalId.getPlantId());
				IKeyValuePair iKeyValuePair3 = approvalProcessIdRepository.getApprovalProcessIdAndDescription(dbApprovalId.getApprovalProcessId(), dbApprovalId.getLanguageId(), dbApprovalId.getCompanyCodeId(), dbApprovalId.getPlantId(), dbApprovalId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbApprovalId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbApprovalId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbApprovalId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null){
					dbApprovalId.setApprovalProcessIdAndDescription(iKeyValuePair3.getApprovalProcessId() + "-" + iKeyValuePair3.getDescription());
			}
		}
			newApprovalId.add(dbApprovalId);
		}
		return newApprovalId;
	}

	/**
	 * getApprovalId
	 * @param approvalId
	 * @return
	 */
	public ApprovalId getApprovalId (String warehouseId, String approvalId, String approvalLevel,String approvalProcessId,String companyCodeId,String languageId,String plantId) {
		Optional<ApprovalId> dbApprovalId =
				approvalIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndApprovalIdAndApprovalLevelAndApprovalProcessIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						approvalId,
						approvalLevel,
						approvalProcessId,
						languageId,
						0L
				);
		if (dbApprovalId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"approvalId - " + approvalId +
					"approvalProcessId"+approvalProcessId+
					"approvalLevel - " + approvalLevel +
					" doesn't exist.");

		}
		ApprovalId newApprovalId = new ApprovalId();
		BeanUtils.copyProperties(dbApprovalId.get(),newApprovalId, CommonUtils.getNullPropertyNames(dbApprovalId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= approvalProcessIdRepository.getApprovalProcessIdAndDescription(approvalProcessId,languageId,companyCodeId,plantId,warehouseId);
		if(iKeyValuePair!=null) {
			newApprovalId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newApprovalId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newApprovalId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newApprovalId.setApprovalProcessIdAndDescription(iKeyValuePair3.getApprovalProcessId() + "-" + iKeyValuePair3.getDescription());
		}
		return newApprovalId;

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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ApprovalId dbApprovalId = new ApprovalId();
		Optional<ApprovalId> duplicateApprovalId = approvalIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndApprovalIdAndApprovalLevelAndApprovalProcessIdAndLanguageIdAndDeletionIndicator(newApprovalId.getCompanyCodeId(), newApprovalId.getPlantId(), newApprovalId.getWarehouseId(), newApprovalId.getApprovalId(), newApprovalId.getApprovalLevel(), newApprovalId.getApprovalProcessId(), newApprovalId.getLanguageId(), 0L);
		if (!duplicateApprovalId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			ApprovalProcessId dbApprovalProcessId=approvalProcessIdService.getApprovalProcessId(newApprovalId.getWarehouseId(), newApprovalId.getApprovalProcessId(), newApprovalId.getCompanyCodeId(), newApprovalId.getLanguageId(), newApprovalId.getPlantId());
			log.info("newApprovalId : " + newApprovalId);
			BeanUtils.copyProperties(newApprovalId, dbApprovalId, CommonUtils.getNullPropertyNames(newApprovalId));
			dbApprovalId.setCreatedBy(loginUserID);
			dbApprovalId.setCompanyIdAndDescription(dbApprovalProcessId.getCompanyIdAndDescription());
			dbApprovalId.setPlantIdAndDescription(dbApprovalProcessId.getPlantIdAndDescription());
			dbApprovalId.setApprovalProcessIdAndDescription(dbApprovalProcessId.getApprovalProcessId()+"-"+dbApprovalProcessId.getApprovalProcess());
			dbApprovalId.setWarehouseIdAndDescription(dbApprovalProcessId.getWarehouseIdAndDescription());
			dbApprovalId.setDeletionIndicator(0L);
			dbApprovalId.setUpdatedBy(loginUserID);
			dbApprovalId.setCreatedOn(new Date());
			dbApprovalId.setUpdatedOn(new Date());
			return approvalIdRepository.save(dbApprovalId);
		}
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
	public ApprovalId updateApprovalId (String warehouseId, String approvalId,String approvalLevel,String approvalProcessId,String companyCodeId,String languageId,String plantId,String loginUserID,
										UpdateApprovalId updateApprovalId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ApprovalId dbApprovalId = getApprovalId(warehouseId, approvalId, approvalLevel,approvalProcessId,companyCodeId,languageId,plantId);
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
	public void deleteApprovalId (String warehouseId, String approvalId,String approvalLevel,String approvalProcessId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		ApprovalId dbApprovalId = getApprovalId(warehouseId, approvalId, approvalLevel,approvalProcessId,companyCodeId,languageId,plantId);
		if ( dbApprovalId != null) {
			dbApprovalId.setDeletionIndicator(1L);
			dbApprovalId.setUpdatedBy(loginUserID);
			approvalIdRepository.save(dbApprovalId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + approvalId);
		}
	}
	//Find ApprovalId

	public List<ApprovalId> findApprovalId(FindApprovalId findApprovalId) throws ParseException {

		ApprovalIdSpecification spec = new ApprovalIdSpecification(findApprovalId);
		List<ApprovalId> results = approvalIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ApprovalId> newApprovalId=new ArrayList<>();
		for(ApprovalId dbApprovalId:results) {
			if (dbApprovalId.getCompanyIdAndDescription() != null&&dbApprovalId.getPlantIdAndDescription()!=null&&dbApprovalId.getWarehouseIdAndDescription()!=null&&dbApprovalId.getApprovalProcessIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbApprovalId.getCompanyCodeId(),dbApprovalId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbApprovalId.getPlantId(),dbApprovalId.getLanguageId(), dbApprovalId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbApprovalId.getWarehouseId(),dbApprovalId.getLanguageId(), dbApprovalId.getCompanyCodeId(), dbApprovalId.getPlantId());
				IKeyValuePair iKeyValuePair3=approvalProcessIdRepository.getApprovalProcessIdAndDescription(dbApprovalId.getApprovalProcessId(),dbApprovalId.getLanguageId(), dbApprovalId.getCompanyCodeId(), dbApprovalId.getPlantId(), dbApprovalId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbApprovalId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbApprovalId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbApprovalId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null){
					dbApprovalId.setApprovalProcessIdAndDescription(iKeyValuePair3.getApprovalProcessId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newApprovalId.add(dbApprovalId);
		}
		return newApprovalId;
	}
}
