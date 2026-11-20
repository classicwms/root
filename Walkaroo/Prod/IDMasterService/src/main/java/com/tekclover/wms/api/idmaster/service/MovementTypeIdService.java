package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.AddMovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.FindMovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.UpdateMovementTypeId;
import com.tekclover.wms.api.idmaster.model.uomid.AddUomId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.MovementTypeIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.MovementTypeIdSpecification;
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
public class MovementTypeIdService {

	@Autowired
	private MovementTypeIdRepository movementTypeIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getMovementTypeIds
	 * @return
	 */
	public List<MovementTypeId> getMovementTypeIds () {
		List<MovementTypeId> movementTypeIdList =  movementTypeIdRepository.findAll();
		movementTypeIdList = movementTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<MovementTypeId> newMovementTypeId=new ArrayList<>();
		for(MovementTypeId dbMovementTypeId:movementTypeIdList) {
			if (dbMovementTypeId.getCompanyIdAndDescription() != null&&dbMovementTypeId.getPlantIdAndDescription()!=null&&dbMovementTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbMovementTypeId.getCompanyCodeId(), dbMovementTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbMovementTypeId.getPlantId(), dbMovementTypeId.getLanguageId(), dbMovementTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbMovementTypeId.getWarehouseId(), dbMovementTypeId.getLanguageId(), dbMovementTypeId.getCompanyCodeId(), dbMovementTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbMovementTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbMovementTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbMovementTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newMovementTypeId.add(dbMovementTypeId);
		}
		return newMovementTypeId;
	}

	/**
	 * getMovementTypeId
	 * @param movementTypeId
	 * @return
	 */
	public MovementTypeId getMovementTypeId (String warehouseId, String movementTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<MovementTypeId> dbMovementTypeId =
				movementTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						movementTypeId,
						languageId,
						0L
				);
		if (dbMovementTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"movementTypeId - " + movementTypeId +
					" doesn't exist.");

		}
		MovementTypeId newMovementTypeId = new MovementTypeId();
		BeanUtils.copyProperties(dbMovementTypeId.get(),newMovementTypeId, CommonUtils.getNullPropertyNames(dbMovementTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newMovementTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newMovementTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newMovementTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newMovementTypeId;
	}

	/**
	 * createMovementTypeId
	 * @param newMovementTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public MovementTypeId createMovementTypeId (AddMovementTypeId newMovementTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		MovementTypeId dbMovementTypeId = new MovementTypeId();
		Optional<MovementTypeId> duplicateMovementTypeId = movementTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(newMovementTypeId.getCompanyCodeId(), newMovementTypeId.getPlantId(), newMovementTypeId.getWarehouseId(), newMovementTypeId.getMovementTypeId(), newMovementTypeId.getLanguageId(), 0L);
		if (!duplicateMovementTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newMovementTypeId.getWarehouseId(), newMovementTypeId.getCompanyCodeId(), newMovementTypeId.getPlantId(), newMovementTypeId.getLanguageId());
			log.info("newMovementTypeId : " + newMovementTypeId);
			BeanUtils.copyProperties(newMovementTypeId, dbMovementTypeId, CommonUtils.getNullPropertyNames(newMovementTypeId));
			dbMovementTypeId.setDeletionIndicator(0L);
			dbMovementTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbMovementTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbMovementTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbMovementTypeId.setCreatedBy(loginUserID);
			dbMovementTypeId.setUpdatedBy(loginUserID);
			dbMovementTypeId.setCreatedOn(new Date());
			dbMovementTypeId.setUpdatedOn(new Date());
			return movementTypeIdRepository.save(dbMovementTypeId);
		}
	}

	/**
	 * updateMovementTypeId
	 * @param loginUserID
	 * @param movementTypeId
	 * @param updateMovementTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public MovementTypeId updateMovementTypeId (String warehouseId, String movementTypeId,String companyCodeId,String languageId,String plantId, String loginUserID,
												UpdateMovementTypeId updateMovementTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		MovementTypeId dbMovementTypeId = getMovementTypeId( warehouseId,movementTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateMovementTypeId, dbMovementTypeId, CommonUtils.getNullPropertyNames(updateMovementTypeId));
		dbMovementTypeId.setUpdatedBy(loginUserID);
		dbMovementTypeId.setUpdatedOn(new Date());
		return movementTypeIdRepository.save(dbMovementTypeId);
	}

	/**
	 * deleteMovementTypeId
	 * @param loginUserID
	 * @param movementTypeId
	 */
	public void deleteMovementTypeId (String warehouseId, String movementTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		MovementTypeId dbMovementTypeId = getMovementTypeId( warehouseId, movementTypeId,companyCodeId,languageId,plantId);
		if ( dbMovementTypeId != null) {
			dbMovementTypeId.setDeletionIndicator(1L);
			dbMovementTypeId.setUpdatedBy(loginUserID);
			movementTypeIdRepository.save(dbMovementTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + movementTypeId);
		}
	}

	//Find MovementTypeId
	public List<MovementTypeId> findMovementTypeId(FindMovementTypeId findMovementTypeId) throws ParseException {

		MovementTypeIdSpecification spec = new MovementTypeIdSpecification(findMovementTypeId);
		List<MovementTypeId> results = movementTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<MovementTypeId> newMovementTypeId=new ArrayList<>();
		for(MovementTypeId dbMovementTypeId:results) {
			if (dbMovementTypeId.getCompanyIdAndDescription() != null&&dbMovementTypeId.getPlantIdAndDescription()!=null&&dbMovementTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbMovementTypeId.getCompanyCodeId(), dbMovementTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbMovementTypeId.getPlantId(), dbMovementTypeId.getLanguageId(), dbMovementTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbMovementTypeId.getWarehouseId(), dbMovementTypeId.getLanguageId(), dbMovementTypeId.getCompanyCodeId(), dbMovementTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbMovementTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbMovementTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbMovementTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newMovementTypeId.add(dbMovementTypeId);
		}
		return newMovementTypeId;

	}
}
