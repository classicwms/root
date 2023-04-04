package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.stocktypeid.AddStockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.StockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.UpdateStockTypeId;
import com.tekclover.wms.api.idmaster.repository.StockTypeIdRepository;
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
public class StockTypeIdService extends BaseService {
	
	@Autowired
	private StockTypeIdRepository stockTypeIdRepository;
	
	/**
	 * getStockTypeIds
	 * @return
	 */
	public List<StockTypeId> getStockTypeIds () {
		List<StockTypeId> stockTypeIdList =  stockTypeIdRepository.findAll();
		stockTypeIdList = stockTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return stockTypeIdList;
	}
	
	/**
	 * getStockTypeId
	 * @param stockTypeId
	 * @return
	 */
	public StockTypeId getStockTypeId (String warehouseId, String stockTypeId) {
		Optional<StockTypeId> dbStockTypeId = 
				stockTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStockTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								stockTypeId,
								getLanguageId(),
								0L
								);
		if (dbStockTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"stockTypeId - " + stockTypeId +
						" doesn't exist.");
			
		} 
		return dbStockTypeId.get();
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
			throws IllegalAccessException, InvocationTargetException {
		StockTypeId dbStockTypeId = new StockTypeId();
		log.info("newStockTypeId : " + newStockTypeId);
		BeanUtils.copyProperties(newStockTypeId, dbStockTypeId, CommonUtils.getNullPropertyNames(newStockTypeId));
		dbStockTypeId.setCompanyCodeId(getCompanyCode());
		dbStockTypeId.setPlantId(getPlantId());
		dbStockTypeId.setDeletionIndicator(0L);
		dbStockTypeId.setCreatedBy(loginUserID);
		dbStockTypeId.setUpdatedBy(loginUserID);
		dbStockTypeId.setCreatedOn(new Date());
		dbStockTypeId.setUpdatedOn(new Date());
		return stockTypeIdRepository.save(dbStockTypeId);
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
	public StockTypeId updateStockTypeId (String warehouseId, String stockTypeId, String loginUserID,
			UpdateStockTypeId updateStockTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		StockTypeId dbStockTypeId = getStockTypeId( warehouseId, stockTypeId);
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
	public void deleteStockTypeId (String warehouseId, String stockTypeId, String loginUserID) {
		StockTypeId dbStockTypeId = getStockTypeId( warehouseId, stockTypeId);
		if ( dbStockTypeId != null) {
			dbStockTypeId.setDeletionIndicator(1L);
			dbStockTypeId.setUpdatedBy(loginUserID);
			stockTypeIdRepository.save(dbStockTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + stockTypeId);
		}
	}
}
