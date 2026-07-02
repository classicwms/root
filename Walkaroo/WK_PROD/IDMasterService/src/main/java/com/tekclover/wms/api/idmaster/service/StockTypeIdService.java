package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.binsectionid.BinSectionId;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.AddStockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.FindStockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.StockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.UpdateStockTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.StockTypeIdSpecification;
import com.tekclover.wms.api.idmaster.repository.StockTypeIdRepository;
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
public class StockTypeIdService{

	@Autowired
	private StockTypeIdRepository stockTypeIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getStockTypeIds
	 * @return
	 */
	public List<StockTypeId> getStockTypeIds () {
		List<StockTypeId> stockTypeIdList =  stockTypeIdRepository.findAll();
		stockTypeIdList = stockTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StockTypeId> newStockTypeId=new ArrayList<>();
		for(StockTypeId dbStockTypeId:stockTypeIdList) {
			if (dbStockTypeId.getCompanyIdAndDescription() != null&&dbStockTypeId.getPlantIdAndDescription()!=null&&dbStockTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbStockTypeId.getCompanyCodeId(), dbStockTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbStockTypeId.getPlantId(), dbStockTypeId.getLanguageId(), dbStockTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStockTypeId.getWarehouseId(), dbStockTypeId.getLanguageId(), dbStockTypeId.getCompanyCodeId(), dbStockTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbStockTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStockTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStockTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newStockTypeId.add(dbStockTypeId);
		}
		return newStockTypeId;
	}

	/**
	 * getStockTypeId
	 * @param stockTypeId
	 * @return
	 */
	public StockTypeId getStockTypeId (String warehouseId,String stockTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<StockTypeId> dbStockTypeId =
				stockTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStockTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						stockTypeId,
						languageId,
						0L
				);
		if (dbStockTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"stockTypeId - " + stockTypeId +
					" doesn't exist.");

		}
		StockTypeId newStockTypeId = new StockTypeId();
		BeanUtils.copyProperties(dbStockTypeId.get(),newStockTypeId, CommonUtils.getNullPropertyNames(dbStockTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newStockTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newStockTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newStockTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newStockTypeId;
	}

	/**
	 * createStockTypeId
	 * @param newStockTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StockTypeId createStockTypeId (AddStockTypeId newStockTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StockTypeId dbStockTypeId = new StockTypeId();
		Optional<StockTypeId> duplicateStockTypeId = stockTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStockTypeIdAndLanguageIdAndDeletionIndicator(newStockTypeId.getCompanyCodeId(), newStockTypeId.getPlantId(), newStockTypeId.getWarehouseId(), newStockTypeId.getStockTypeId(), newStockTypeId.getLanguageId(), 0L);
		if (!duplicateStockTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newStockTypeId.getWarehouseId(), newStockTypeId.getCompanyCodeId(), newStockTypeId.getPlantId(), newStockTypeId.getLanguageId());
			log.info("newStockTypeId : " + newStockTypeId);
			BeanUtils.copyProperties(newStockTypeId, dbStockTypeId, CommonUtils.getNullPropertyNames(newStockTypeId));
			dbStockTypeId.setDeletionIndicator(0L);
			dbStockTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbStockTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbStockTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbStockTypeId.setCreatedBy(loginUserID);
			dbStockTypeId.setUpdatedBy(loginUserID);
			dbStockTypeId.setCreatedOn(new Date());
			dbStockTypeId.setUpdatedOn(new Date());
			return stockTypeIdRepository.save(dbStockTypeId);
		}
	}

	/**
	 * updateStockTypeId
	 * @param loginUserID
	 * @param stockTypeId
	 * @param updateStockTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StockTypeId updateStockTypeId (String warehouseId, String stockTypeId,String companyCodeId,String languageId,String plantId, String loginUserID,
										  UpdateStockTypeId updateStockTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StockTypeId dbStockTypeId = getStockTypeId( warehouseId,stockTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateStockTypeId, dbStockTypeId, CommonUtils.getNullPropertyNames(updateStockTypeId));
		dbStockTypeId.setUpdatedBy(loginUserID);
		dbStockTypeId.setUpdatedOn(new Date());
		return stockTypeIdRepository.save(dbStockTypeId);
	}

	/**
	 * deleteStockTypeId
	 * @param loginUserID
	 * @param stockTypeId
	 */
	public void deleteStockTypeId (String warehouseId, String stockTypeId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		StockTypeId dbStockTypeId = getStockTypeId( warehouseId,stockTypeId,companyCodeId,languageId,plantId);
		if ( dbStockTypeId != null) {
			dbStockTypeId.setDeletionIndicator(1L);
			dbStockTypeId.setUpdatedBy(loginUserID);
			stockTypeIdRepository.save(dbStockTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + stockTypeId);
		}
	}

	//Find StockTypeId
	public List<StockTypeId> findStockTypeId(FindStockTypeId findStockTypeId) throws ParseException {

		StockTypeIdSpecification spec = new StockTypeIdSpecification(findStockTypeId);
		List<StockTypeId> results = stockTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<StockTypeId> newStockTypeId=new ArrayList<>();
		for(StockTypeId dbStockTypeId:results) {
			if (dbStockTypeId.getCompanyIdAndDescription() != null&&dbStockTypeId.getPlantIdAndDescription()!=null&&dbStockTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbStockTypeId.getCompanyCodeId(), dbStockTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbStockTypeId.getPlantId(), dbStockTypeId.getLanguageId(), dbStockTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStockTypeId.getWarehouseId(), dbStockTypeId.getLanguageId(), dbStockTypeId.getCompanyCodeId(), dbStockTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbStockTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStockTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStockTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newStockTypeId.add(dbStockTypeId);
		}
		return newStockTypeId;
	}
}
