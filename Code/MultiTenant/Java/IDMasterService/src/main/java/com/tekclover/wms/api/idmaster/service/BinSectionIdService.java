package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.binclassid.BinClassId;
import com.tekclover.wms.api.idmaster.model.binsectionid.AddBinSectionId;
import com.tekclover.wms.api.idmaster.model.binsectionid.BinSectionId;
import com.tekclover.wms.api.idmaster.model.binsectionid.FindBinSectionId;
import com.tekclover.wms.api.idmaster.model.binsectionid.UpdateBinSectionId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.BinSectionIdRepository;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.BinSectionIdSpecification;
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
public class BinSectionIdService {
	@Autowired
	private CompanyIdRepository companyIdRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private BinSectionIdRepository binSectionIdRepository;

	/**
	 * getBinSectionIds
	 * @return
	 */
	public List<BinSectionId> getBinSectionIds () {
		List<BinSectionId> binSectionIdList =  binSectionIdRepository.findAll();
		binSectionIdList = binSectionIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<BinSectionId> newBinSectionId=new ArrayList<>();
		for(BinSectionId dbBinSectionId:binSectionIdList) {
			if (dbBinSectionId.getCompanyIdAndDescription() != null&&dbBinSectionId.getPlantIdAndDescription()!=null&&dbBinSectionId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbBinSectionId.getCompanyCodeId(), dbBinSectionId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbBinSectionId.getPlantId(), dbBinSectionId.getLanguageId(), dbBinSectionId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbBinSectionId.getWarehouseId(), dbBinSectionId.getLanguageId(), dbBinSectionId.getCompanyCodeId(), dbBinSectionId.getPlantId());
				if (iKeyValuePair != null) {
					dbBinSectionId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbBinSectionId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbBinSectionId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newBinSectionId.add(dbBinSectionId);
		}
		return newBinSectionId;
	}

	/**
	 * getBinSectionId
	 * @param binSectionId
	 * @return
	 */
	public BinSectionId getBinSectionId (String warehouseId,String binSectionId,String companyCodeId,String languageId,String plantId) {
		Optional<BinSectionId> dbBinSectionId =
				binSectionIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinSectionIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						binSectionId,
						languageId,
						0L
				);
		if (dbBinSectionId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"binSectionId - " + binSectionId +
					" doesn't exist.");

		}
		BinSectionId newBinSectionId = new BinSectionId();
		BeanUtils.copyProperties(dbBinSectionId.get(),newBinSectionId, CommonUtils.getNullPropertyNames(dbBinSectionId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newBinSectionId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newBinSectionId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newBinSectionId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newBinSectionId;
	}

	/**
	 * createBinSectionId
	 * @param newBinSectionId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BinSectionId createBinSectionId (AddBinSectionId newBinSectionId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		BinSectionId dbBinSectionId = new BinSectionId();
		Optional<BinSectionId> duplicateBinSectionId = binSectionIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinSectionIdAndLanguageIdAndDeletionIndicator(newBinSectionId.getCompanyCodeId(), newBinSectionId.getPlantId(), newBinSectionId.getWarehouseId(), newBinSectionId.getBinSectionId(), newBinSectionId.getLanguageId(), 0L);
		if (!duplicateBinSectionId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newBinSectionId.getWarehouseId(), newBinSectionId.getCompanyCodeId(), newBinSectionId.getPlantId(), newBinSectionId.getLanguageId());
			log.info("newBinSectionId : " + newBinSectionId);
			BeanUtils.copyProperties(newBinSectionId, dbBinSectionId, CommonUtils.getNullPropertyNames(newBinSectionId));
			dbBinSectionId.setDeletionIndicator(0L);
			dbBinSectionId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbBinSectionId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbBinSectionId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbBinSectionId.setCreatedBy(loginUserID);
			dbBinSectionId.setUpdatedBy(loginUserID);
			dbBinSectionId.setCreatedOn(new Date());
			dbBinSectionId.setUpdatedOn(new Date());
			return binSectionIdRepository.save(dbBinSectionId);
		}
	}

	/**
	 * updateBinSectionId
	 * @param loginUserID
	 * @param binSectionId
	 * @param updateBinSectionId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BinSectionId updateBinSectionId (String warehouseId, String binSectionId,String companyCodeId,String languageId,String plantId,String loginUserID,
											UpdateBinSectionId updateBinSectionId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		BinSectionId dbBinSectionId = getBinSectionId( warehouseId,binSectionId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateBinSectionId, dbBinSectionId, CommonUtils.getNullPropertyNames(updateBinSectionId));
		dbBinSectionId.setUpdatedBy(loginUserID);
		dbBinSectionId.setUpdatedOn(new Date());
		return binSectionIdRepository.save(dbBinSectionId);
	}

	/**
	 * deleteBinSectionId
	 * @param loginUserID
	 * @param binSectionId
	 */
	public void deleteBinSectionId (String warehouseId, String binSectionId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		BinSectionId dbBinSectionId = getBinSectionId( warehouseId,binSectionId,companyCodeId,languageId,plantId);
		if ( dbBinSectionId != null) {
			dbBinSectionId.setDeletionIndicator(1L);
			dbBinSectionId.setUpdatedBy(loginUserID);
			binSectionIdRepository.save(dbBinSectionId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + binSectionId);
		}
	}

	//Find BinSectionId
	public List<BinSectionId> findBinSectionId(FindBinSectionId findBinSectionId) throws ParseException {

		BinSectionIdSpecification spec = new BinSectionIdSpecification(findBinSectionId);
		List<BinSectionId> results = binSectionIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<BinSectionId> newBinSectionId=new ArrayList<>();
		for(BinSectionId dbBinSectionId:results) {
			if (dbBinSectionId.getCompanyIdAndDescription() != null&&dbBinSectionId.getPlantIdAndDescription()!=null&&dbBinSectionId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbBinSectionId.getCompanyCodeId(), dbBinSectionId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbBinSectionId.getPlantId(), dbBinSectionId.getLanguageId(), dbBinSectionId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbBinSectionId.getWarehouseId(), dbBinSectionId.getLanguageId(), dbBinSectionId.getCompanyCodeId(), dbBinSectionId.getPlantId());
				if (iKeyValuePair != null) {
					dbBinSectionId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbBinSectionId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbBinSectionId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newBinSectionId.add(dbBinSectionId);
		}
		return newBinSectionId;
	}
}
