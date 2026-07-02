package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.AddTransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.FindTransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.TransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.UpdateTransferTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.TransferTypeIdSpecification;
import com.tekclover.wms.api.idmaster.repository.TransferTypeIdRepository;
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
public class TransferTypeIdService  {
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private TransferTypeIdRepository transferTypeIdRepository;

	/**
	 * getTransferTypeIds
	 * @return
	 */
	public List<TransferTypeId> getTransferTypeIds () {
		List<TransferTypeId> transferTypeIdList =  transferTypeIdRepository.findAll();
		transferTypeIdList = transferTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<TransferTypeId> newTransferTypeId=new ArrayList<>();
		for(TransferTypeId dbTransferTypeId:transferTypeIdList) {
			if (dbTransferTypeId.getCompanyIdAndDescription() != null&&dbTransferTypeId.getPlantIdAndDescription()!=null&&dbTransferTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbTransferTypeId.getCompanyCodeId(), dbTransferTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbTransferTypeId.getPlantId(), dbTransferTypeId.getLanguageId(), dbTransferTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbTransferTypeId.getWarehouseId(), dbTransferTypeId.getLanguageId(), dbTransferTypeId.getCompanyCodeId(), dbTransferTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbTransferTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbTransferTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbTransferTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newTransferTypeId.add(dbTransferTypeId);
		}
		return newTransferTypeId;
	}

	/**
	 * getTransferTypeId
	 * @param transferTypeId
	 * @return
	 */
	public TransferTypeId getTransferTypeId (String warehouseId, String transferTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<TransferTypeId> dbTransferTypeId =
				transferTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						transferTypeId,
						languageId,
						0L
				);
		if (dbTransferTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"transferTypeId - " + transferTypeId +
					" doesn't exist.");

		}
		TransferTypeId newTransferTypeId = new TransferTypeId();
		BeanUtils.copyProperties(dbTransferTypeId.get(),newTransferTypeId, CommonUtils.getNullPropertyNames(dbTransferTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newTransferTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newTransferTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newTransferTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newTransferTypeId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		TransferTypeId dbTransferTypeId = new TransferTypeId();
		Optional<TransferTypeId> duplicateTransferTypeId = transferTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferTypeIdAndLanguageIdAndDeletionIndicator(newTransferTypeId.getCompanyCodeId(), newTransferTypeId.getPlantId(), newTransferTypeId.getWarehouseId(), newTransferTypeId.getTransferTypeId(), newTransferTypeId.getLanguageId(), 0L);
		if (!duplicateTransferTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newTransferTypeId.getWarehouseId(), newTransferTypeId.getCompanyCodeId(), newTransferTypeId.getPlantId(), newTransferTypeId.getLanguageId());
			log.info("newTransferTypeId : " + newTransferTypeId);
			BeanUtils.copyProperties(newTransferTypeId, dbTransferTypeId, CommonUtils.getNullPropertyNames(newTransferTypeId));
			dbTransferTypeId.setDeletionIndicator(0L);
			dbTransferTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbTransferTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbTransferTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbTransferTypeId.setCreatedBy(loginUserID);
			dbTransferTypeId.setUpdatedBy(loginUserID);
			dbTransferTypeId.setCreatedOn(new Date());
			dbTransferTypeId.setUpdatedOn(new Date());
			return transferTypeIdRepository.save(dbTransferTypeId);
		}
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
	public TransferTypeId updateTransferTypeId (String warehouseId, String transferTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
												UpdateTransferTypeId updateTransferTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		TransferTypeId dbTransferTypeId = getTransferTypeId( warehouseId,transferTypeId,companyCodeId,languageId,plantId);
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
	public void deleteTransferTypeId (String warehouseId, String transferTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		TransferTypeId dbTransferTypeId = getTransferTypeId( warehouseId,transferTypeId,companyCodeId,languageId,plantId);
		if ( dbTransferTypeId != null) {
			dbTransferTypeId.setDeletionIndicator(1L);
			dbTransferTypeId.setUpdatedBy(loginUserID);
			transferTypeIdRepository.save(dbTransferTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + transferTypeId);
		}
	}

	//Find TransferTypeId
	public List<TransferTypeId> findTransferTypeId(FindTransferTypeId findTransferTypeId) throws ParseException {

		TransferTypeIdSpecification spec = new TransferTypeIdSpecification(findTransferTypeId);
		List<TransferTypeId> results = transferTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<TransferTypeId> newTransferTypeId=new ArrayList<>();
		for(TransferTypeId dbTransferTypeId:results) {
			if (dbTransferTypeId.getCompanyIdAndDescription() != null&&dbTransferTypeId.getPlantIdAndDescription()!=null&&dbTransferTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbTransferTypeId.getCompanyCodeId(), dbTransferTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbTransferTypeId.getPlantId(), dbTransferTypeId.getLanguageId(), dbTransferTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbTransferTypeId.getWarehouseId(), dbTransferTypeId.getLanguageId(), dbTransferTypeId.getCompanyCodeId(), dbTransferTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbTransferTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbTransferTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbTransferTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newTransferTypeId.add(dbTransferTypeId);
		}
		return newTransferTypeId;
	}
}
