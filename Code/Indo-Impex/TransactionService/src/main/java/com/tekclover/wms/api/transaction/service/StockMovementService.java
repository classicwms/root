package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.threepl.stockmovement.FindStockMovement;
import com.tekclover.wms.api.transaction.model.threepl.stockmovement.StockMovement;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.StockMovementRepository;
import com.tekclover.wms.api.transaction.repository.specification.StockMovementSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockMovementService extends BaseService {

    @Autowired
    StockMovementRepository stockMovementRepository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    /**
     * getAllStockMovement
     *
     * @return
     */
    public List<StockMovement> getAllStockMovement() {
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        stockMovementList = stockMovementList.stream().filter(n -> n.getDeletionIndicator() != null &&
                n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return stockMovementList;
    }


    /**
     * @param movementDocNo
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param itemCode
     * @return
     */
    public List<StockMovement> getStockMovement(Long movementDocNo, String languageId, String companyCodeId,
                                                String plantId, String warehouseId, String itemCode) {
        List<StockMovement> stockMovementList =
                stockMovementRepository.findByMovementDocNoAndLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                        movementDocNo, languageId, companyCodeId, plantId, warehouseId, itemCode, 0L);

        if (stockMovementList.isEmpty()) {
            throw new BadRequestException("The given values: movementDocNo:" + movementDocNo +
                    ",languageId: " + languageId + "," +
                    ",companyCodeId: " + companyCodeId + "," +
                    ",plantId: " + plantId +
                    ", warehouseId: " + warehouseId +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }
        return stockMovementList;
    }


    /**
     * @param newStockMovement
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<StockMovement> createStockMovement(List<StockMovement> newStockMovement, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        List<StockMovement> stockMovementList = new ArrayList<>();

        for (StockMovement stockMovement : newStockMovement) {
            Optional<StockMovement> duplicateStockMovement =
                    stockMovementRepository.findByLanguageIdAndMovementDocNoAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                            stockMovement.getLanguageId(), stockMovement.getMovementDocNo(), stockMovement.getCompanyCodeId(),
                            stockMovement.getPlantId(), stockMovement.getWarehouseId(), stockMovement.getItemCode(), 0L);

            if (!duplicateStockMovement.isEmpty()) {
                throw new BadRequestException("Record is getting duplicated with the given values");
            } else {
                StockMovement dbStockMovement = new StockMovement();
                BeanUtils.copyProperties(newStockMovement, dbStockMovement, CommonUtils.getNullPropertyNames(newStockMovement));

                //V2 Code
                IKeyValuePair description = stagingLineV2Repository.getDescription(dbStockMovement.getCompanyCodeId(),
                        dbStockMovement.getLanguageId(),
                        dbStockMovement.getPlantId(),
                        dbStockMovement.getWarehouseId());

                dbStockMovement.setCompanyDescription(description.getCompanyDesc());
                dbStockMovement.setPlantDescription(description.getPlantDesc());
                dbStockMovement.setWarehouseDescription(description.getWarehouseDesc());

                if (dbStockMovement.getStatusId() != null) {
                    statusDescription = stagingLineV2Repository.getStatusDescription(dbStockMovement.getStatusId(), dbStockMovement.getLanguageId());
                    dbStockMovement.setStatusDescription(statusDescription);
                }
                dbStockMovement.setDeletionIndicator(0L);
                dbStockMovement.setCreatedBy(loginUserID);
                dbStockMovement.setUpdatedBy(loginUserID);
                dbStockMovement.setCreatedOn(new Date());
                dbStockMovement.setUpdatedOn(new Date());
                StockMovement savedDeliveryLine = stockMovementRepository.save(dbStockMovement);
                stockMovementList.add(savedDeliveryLine);
            }
        }
        return stockMovementList;
    }


    /**
     * @param movementDocNo
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param itemCode
     * @param updateStockMovement
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<StockMovement> updateStockMovement(Long movementDocNo, String languageId, String companyCodeId,
                                                   String plantId, String warehouseId, String itemCode, List<StockMovement> updateStockMovement, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        List<StockMovement> stockMovementList = new ArrayList<>();

        for (StockMovement stockMovement : updateStockMovement) {
            StockMovement dbStockMovement = stockMovementRepository.findByCompanyCodeIdAndLanguageIdAndMovementDocNoAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                    companyCodeId, languageId, movementDocNo, plantId, warehouseId, itemCode, 0L);

            BeanUtils.copyProperties(stockMovement, dbStockMovement, CommonUtils.getNullPropertyNames(stockMovement));
            dbStockMovement.setUpdatedBy(loginUserID);
            dbStockMovement.setUpdatedOn(new Date());
            stockMovementRepository.save(dbStockMovement);
            stockMovementList.add(dbStockMovement);
        }
        return stockMovementList;
    }


    //Delete
    public void deleteStockMovement(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode,
                                    Long movementDocNo, String loginUserID) {
        StockMovement dbStockMovement = stockMovementRepository.findByCompanyCodeIdAndLanguageIdAndMovementDocNoAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                companyCodeId, languageId, movementDocNo, plantId, warehouseId, itemCode, 0L);
        if (dbStockMovement != null) {
            dbStockMovement.setDeletionIndicator(1L);
            dbStockMovement.setUpdatedBy(loginUserID);
            stockMovementRepository.save(dbStockMovement);
        } else {
            throw new BadRequestException("Error in deleting Id:  warehouseId:" + warehouseId +
                    ", companyCodeId :" + companyCodeId +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param loginUserID
     */
    public void deleteStockMovement(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String refDocNumber, String itemCode, String loginUserID) {

        List<StockMovement> dbStockMovement = stockMovementRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndRefDocNumberAndDeletionIndicator(
                companyCodeId, languageId, plantId, warehouseId, itemCode, refDocNumber, 0L);

        if (dbStockMovement != null) {
            for (StockMovement stockMovement : dbStockMovement) {
                stockMovement.setDeletionIndicator(1L);
                stockMovement.setUpdatedBy(loginUserID);
                stockMovementRepository.save(stockMovement);
            }
        } else {
            log.info("Error in deleting Id:  warehouseId:" + warehouseId +
                    ", companyCodeId :" + companyCodeId +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }
    }

    /**
     * @param findStockMovement
     * @return
     * @throws Exception
     */
    public List<StockMovement> findStockMovement(FindStockMovement findStockMovement) throws Exception {

        StockMovementSpecification spec = new StockMovementSpecification(findStockMovement);
        List<StockMovement> results = stockMovementRepository.findAll(spec);
        return results;
    }
}