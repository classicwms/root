package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicLineV2;
import com.tekclover.wms.api.outbound.transaction.model.inventory.InventoryMovement;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.InventoryV2;
import com.tekclover.wms.api.outbound.transaction.repository.PickupLineRepository;
import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.PerpetualLineV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.stockadjustment.SearchStockAdjustment;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.stockadjustment.StockAdjustment;
import com.tekclover.wms.api.outbound.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.outbound.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.outbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.outbound.transaction.model.outbound.StorageBinPutAway;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.outbound.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.outbound.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.outbound.transaction.repository.StockAdjustmentRepository;
import com.tekclover.wms.api.outbound.transaction.repository.specification.StockAdjustmentSpecification;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.abs;

@Slf4j
@Service
public class StockAdjustmentService extends BaseService {
    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;
    @Autowired
    private PickupLineRepository pickupLineRepository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    StockAdjustmentRepository stockAdjustmentRepository;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    PerpetualLineService perpetualLineService;

    @Autowired
    MastersService mastersService;

    @Autowired
    InhouseTransferHeaderService inhouseTransferHeaderService;

    String statusDescription = null;
    String LANG_ID = "EN";


    /**
     * @param stockAdjustment
     * @return
     */
    @Transactional
    public WarehouseApiResponse processStockAdjustment(com.tekclover.wms.api.outbound.transaction.model.warehouse.stockAdjustment.StockAdjustment stockAdjustment) throws java.text.ParseException {

        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();

        //Code Changed to do stock Adjustment Automatically
//        StockAdjustment createStockAdjustment = autoUpdateStockAdjustment(stockAdjustment);
        StockAdjustment createStockAdjustment = autoUpdateStockAdjustmentNew(stockAdjustment);

//        StockAdjustment createStockAdjustment = null;
//        List<StockAdjustment> stockAdjustmentList = new ArrayList<>();
//
//        List<IInventoryImpl> inventoryList = inventoryService.getInventoryForStockAdjustment(stockAdjustment.getCompanyCode(),
//                stockAdjustment.getBranchCode(),
//                "EN",
//                stockAdjustment.getWarehouseId(),
//                stockAdjustment.getItemCode(),
//                stockAdjustment.getManufacturerName());
//        log.info("Inventory List: " + inventoryList);
//
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            warehouseApiResponse.setStatusCode("1400");
//            warehouseApiResponse.setMessage("No Inventory Found");
//            return warehouseApiResponse;
//        }
//        if (inventoryList != null) {
//            Long stockAdjustmentKey = System.currentTimeMillis();
//            for (IInventoryImpl dbInventory : inventoryList) {
//                StockAdjustment dbStockAdjustment = new StockAdjustment();
//                BeanUtils.copyProperties(dbInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(dbInventory));
//
//                dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
//                dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
//                dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());
//                if (dbInventory.getDescription() != null) {
//                    dbStockAdjustment.setItemDescription(dbInventory.getDescription());
//                }
//                if (dbInventory.getDescription() == null) {
//                    if (dbInventory.getReferenceField8() != null) {
//                        dbStockAdjustment.setItemDescription(dbInventory.getReferenceField8());
//                    }
//                }
//                dbStockAdjustment.setPackBarcodes(dbInventory.getPackBarcodes());
//                dbStockAdjustment.setBranchName(stockAdjustment.getBranchName());
//                dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
//                dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
//                dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
//                dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
//                dbStockAdjustment.setReferenceField1(stockAdjustment.getRefDocType());
//                dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
//                dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
//                dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
//                dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
//                dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
//                dbStockAdjustment.setMiddlewareId(stockAdjustment.getMiddlewareId());
//                dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
//                dbStockAdjustment.setSaUpdatedOn(stockAdjustment.getUpdatedOn());
//                dbStockAdjustment.setStockAdjustmentKey(stockAdjustmentKey);
//
//                dbStockAdjustment.setStatusId(87L);                 //Hard Code - StockAdjustment Created
//                statusDescription = stagingLineV2Repository.getStatusDescription(87L, dbInventory.getLanguageId());
//                dbStockAdjustment.setStatusDescription(statusDescription);
//
//                dbStockAdjustment.setDeletionIndicator(0L);
//                dbStockAdjustment.setCreatedOn(new Date());
//                dbStockAdjustment.setCreatedBy("MW_AMS");
//
//                createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
//                stockAdjustmentList.add(dbStockAdjustment);
//            }
//        }

        if (createStockAdjustment != null) {
            warehouseApiResponse.setStatusCode("200");
            warehouseApiResponse.setMessage("Success");
            return warehouseApiResponse;
        }
        return null;
    }

    /**
     * @param searchStockAdjustment
     * @return
     * @throws ParseException
     */
    public List<StockAdjustment> findStockAdjustment(SearchStockAdjustment searchStockAdjustment)
            throws ParseException {
        StockAdjustmentSpecification spec = new StockAdjustmentSpecification(searchStockAdjustment);
                List<StockAdjustment> results = stockAdjustmentRepository.findAll(spec);
        return results;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockAdjustmentKey
     * @param updateStockAdjustmentList
     * @param loginUserId
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<StockAdjustment> updateStockAdjustment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                       String itemCode, String manufacturerName, Long stockAdjustmentKey,
                                                       List<StockAdjustment> updateStockAdjustmentList, String loginUserId) throws InvocationTargetException, IllegalAccessException, java.text.ParseException {

        List<StockAdjustment> updatedStockAdjustmentList = new ArrayList<>();
        if (updateStockAdjustmentList != null) {
            for (StockAdjustment updateStockAdjustment : updateStockAdjustmentList) {

                StockAdjustment dbStockAdjustment =
                        stockAdjustmentRepository.
                                findByLanguageIdAndCompanyCodeAndBranchCodeAndWarehouseIdAndItemCodeAndManufacturerNameAndStockAdjustmentKeyAndStockAdjustmentIdAndDeletionIndicator(
                                        languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, stockAdjustmentKey, updateStockAdjustment.getStockAdjustmentId(), 0L);

                BeanUtils.copyProperties(updateStockAdjustment, dbStockAdjustment, CommonUtils.getNullPropertyNames(updateStockAdjustment));
                dbStockAdjustment.setUpdatedBy(loginUserId);
                dbStockAdjustment.setUpdatedOn(new Date());

                statusDescription = pickupLineRepository.getStatusDescription(dbStockAdjustment.getStatusId(), dbStockAdjustment.getLanguageId());
                dbStockAdjustment.setStatusDescription(statusDescription);

                StockAdjustment updatedStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
                log.info("updatedStockAdjustment: " + updatedStockAdjustment);
                updatedStockAdjustmentList.add(updatedStockAdjustment);

                if (updatedStockAdjustment != null) {
                    if (updatedStockAdjustment.getAdjustmentQty() != null) {
                        List<InventoryV2> dbInventoryList =
                                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicatorOrderByInventoryIdDesc(
                                        languageId, companyCodeId, plantId,
                                        warehouseId, updatedStockAdjustment.getPackBarcodes(), itemCode, manufacturerName,
                                        updatedStockAdjustment.getStorageBin(), updatedStockAdjustment.getStockTypeId(), dbStockAdjustment.getSpecialStockIndicatorId(), 0L);

                        if (dbInventoryList != null) {
                            InventoryV2 dbInventoryV2 = dbInventoryList.get(0);
                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(dbInventoryV2, newInventoryV2, CommonUtils.getNullPropertyNames(dbInventoryV2));
                            newInventoryV2.setInventoryQuantity(updatedStockAdjustment.getInventoryQuantity() + updatedStockAdjustment.getAdjustmentQty());
                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + createdInventoryV2);
                        }

//                    InventoryV2 inventoryV2 = new InventoryV2();
//                    BeanUtils.copyProperties(updateStockAdjustment, inventoryV2, CommonUtils.getNullPropertyNames(updateStockAdjustment));
//                    InventoryV2 updateInventory = inventoryService.updateInventoryV2(
//                            companyCodeId, plantId, languageId, warehouseId, updatedStockAdjustment.getPackBarcodes(), itemCode,
//                            storageBin, updatedStockAdjustment.getStockTypeId(), updateStockAdjustment.getSpecialStockIndicatorId(),
//                            inventoryV2, loginUserId);
//                    log.info("Inventory Updated: " + updateInventory);
                    }
                }
            }
        }
        if (updateStockAdjustmentList == null || updateStockAdjustmentList.isEmpty()) {
            throw new BadRequestException("The Given StockAdjustmentId doesn't Exist: " + stockAdjustmentKey);
        }
        return updatedStockAdjustmentList;
    }

    /**
     * @param stockAdjustment
     * @return
     * @throws java.text.ParseException
     */
    public StockAdjustment autoUpdateStockAdjustmentNew(com.tekclover.wms.api.outbound.transaction.model.warehouse.stockAdjustment.StockAdjustment stockAdjustment) {
        String movementQtyValue = null;
        if (stockAdjustment.getIsCycleCount().equalsIgnoreCase("Y") && stockAdjustment.getIsDamage().equalsIgnoreCase("N")) {
            log.info("IsCycleCount: " + stockAdjustment.getIsCycleCount());
            PerpetualLineV2 perpetualLine = perpetualLineService.getPerpetualLineForStockAdjustmentV2(
                    stockAdjustment.getCompanyCode(),
                    stockAdjustment.getBranchCode(),
                    "EN",
                    stockAdjustment.getWarehouseId(),
                    stockAdjustment.getItemCode(),
                    stockAdjustment.getManufacturerName());
            log.info("Perpetual Line: " + perpetualLine);

            if (perpetualLine != null) {

                InventoryV2 dbInventory = inventoryService.getInventoryV2(
                        perpetualLine.getCompanyCodeId(),
                        perpetualLine.getPlantId(),
                        perpetualLine.getLanguageId(),
                        perpetualLine.getWarehouseId(),
                        perpetualLine.getPackBarcodes(),
                        perpetualLine.getItemCode(),
                        perpetualLine.getStorageBin(),
                        perpetualLine.getManufacturerName());
                log.info("Inventory: " + dbInventory);

                if (dbInventory != null) {
                    InventoryV2 newInventory = new InventoryV2();
                    BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
                    Double INV_QTY = dbInventory.getInventoryQuantity();
                    Double ADJ_QTY = stockAdjustment.getAdjustmentQty();
                    INV_QTY = INV_QTY + ADJ_QTY;
                    if(INV_QTY < 0){
                        INV_QTY = 0D;
                    }
                    newInventory.setInventoryQuantity(INV_QTY);
                    Double ALLOC_QTY = 0D;
                    if(dbInventory.getAllocatedQuantity() != null) {
                        ALLOC_QTY = dbInventory.getAllocatedQuantity();
                    }
                    Double TOT_QTY = INV_QTY + ALLOC_QTY;
                    newInventory.setReferenceField4(TOT_QTY);
                    inventoryV2Repository.save(newInventory);

                    //StockAdjustment Record Insert
                    StockAdjustment dbStockAdjustment = new StockAdjustment();
                    BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));

                    dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
                    dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
                    dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());

                    if (stockAdjustment.getItemDescription() != null) {
                        dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
                    }
                    if (stockAdjustment.getItemDescription() == null && dbInventory.getDescription() != null) {
                        dbStockAdjustment.setItemDescription(dbInventory.getDescription());
                    }
                    if (stockAdjustment.getItemDescription() == null && dbInventory.getDescription() == null) {
                        if (dbInventory.getReferenceField8() != null) {
                            dbStockAdjustment.setItemDescription(dbInventory.getReferenceField8());
                        }
                    }

                    dbStockAdjustment.setBeforeAdjustment(dbInventory.getReferenceField4());
                    dbStockAdjustment.setAfterAdjustment(newInventory.getReferenceField4());

                    dbStockAdjustment.setPackBarcodes(dbInventory.getPackBarcodes());
                    dbStockAdjustment.setBranchName(newInventory.getPlantDescription());
                    dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
                    dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
                    dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
                    dbStockAdjustment.setReferenceField1(stockAdjustment.getRefDocType());
                    dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
                    dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
                    dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
                    dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
                    dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
                    dbStockAdjustment.setMiddlewareId(stockAdjustment.getMiddlewareId());
                    dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
                    dbStockAdjustment.setSaUpdatedOn(stockAdjustment.getUpdatedOn());
                    dbStockAdjustment.setStockAdjustmentKey(newInventory.getInventoryId());

                    dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
                    statusDescription = pickupLineRepository.getStatusDescription(88L, dbInventory.getLanguageId());
                    dbStockAdjustment.setStatusDescription(statusDescription);

                    dbStockAdjustment.setDeletionIndicator(0L);
                    dbStockAdjustment.setCreatedOn(new Date());
                    dbStockAdjustment.setIsCompleted("Y");
                    dbStockAdjustment.setCreatedBy("MW_AMS");

                    StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
                    log.info("createdStockAdjustment: " + createStockAdjustment);
                    if(stockAdjustment.getAdjustmentQty() < 0) {
                        movementQtyValue = "N";
                    }
                    if(stockAdjustment.getAdjustmentQty() > 0) {
                        movementQtyValue = "P";
                    }
                    createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                    return createStockAdjustment;
                }
            }
            List<IInventoryImpl> inventoryList = null;
            if(perpetualLine == null) {
                inventoryList = inventoryService.getStockAdjustmentInventory(
                    stockAdjustment.getCompanyCode(),
                    stockAdjustment.getBranchCode(),
                        LANG_ID,
                    stockAdjustment.getWarehouseId(),
                    stockAdjustment.getItemCode(),
                        stockAdjustment.getManufacturerName(),
                        1L);
                log.info("Inventory-----> Perpetual Line for this item is not found; ----> BinClassId 1 ---> " + inventoryList);

                if (inventoryList != null && !inventoryList.isEmpty()) {
                    //Stock Adjustment - Positive Qty - Have to add Stock Adjustment Qty with Inventory
                    if(stockAdjustment.getAdjustmentQty() > 0) {
                        InventoryV2 stkInventory = new InventoryV2();
                        BeanUtils.copyProperties(inventoryList.get(0), stkInventory, CommonUtils.getNullPropertyNames(inventoryList.get(0)));
                        Double INV_QTY = 0D;
                        if(inventoryList.get(0).getInventoryQuantity() != null) {
                            INV_QTY = inventoryList.get(0).getInventoryQuantity();
                            INV_QTY = INV_QTY + stockAdjustment.getAdjustmentQty();
                        }
                        Double ALLOC_QTY = 0D;
                        if(inventoryList.get(0).getAllocatedQuantity() != null) {
                            ALLOC_QTY = inventoryList.get(0).getAllocatedQuantity();
                        }
                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
                        stkInventory.setInventoryQuantity(INV_QTY);
                        stkInventory.setAllocatedQuantity(ALLOC_QTY);
                        stkInventory.setReferenceField4(TOT_QTY);
                        stkInventory.setDeletionIndicator(0L);
                        stkInventory.setUpdatedOn(new Date());
                        inventoryV2Repository.save(stkInventory);

                        //Insert New Record in StockAdjustment
                        StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventoryList.get(0).getReferenceField4(), inventoryList.get(0).getReferenceField8(), stockAdjustment);
                        if(stockAdjustment.getAdjustmentQty() < 0) {
                            movementQtyValue = "N";
                        }
                        if(stockAdjustment.getAdjustmentQty() > 0) {
                            movementQtyValue = "P";
                        }
                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                        log.info("createdStockAdjustment: " + createStockAdjustment);
                        return createStockAdjustment;
                    }
                    //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
                    if(stockAdjustment.getAdjustmentQty() < 0) {
                        Double STK_ADJ_QTY = 0D;
                        Double INV_QTY = 0D;
                        Double STK_QTY = 0D;
                        Double ALLOC_QTY = 0D;
                        List<StockAdjustment> stockAdjustmentList = new ArrayList<>();
                        if(stockAdjustment.getAdjustmentQty() != null) {
                            STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
                        }
                    outerloop:
                    for (IInventoryImpl inventory : inventoryList) {
                        if (inventory.getInventoryQuantity() != null) {
                            INV_QTY = inventory.getInventoryQuantity();
                        }
                        if (inventory.getAllocatedQuantity() != null) {
                            ALLOC_QTY = inventory.getAllocatedQuantity();
                        }
                        if (STK_ADJ_QTY <= INV_QTY) {
                            STK_QTY = STK_ADJ_QTY;
                        } else if (STK_ADJ_QTY > INV_QTY) {
                            STK_QTY = INV_QTY;
                        } else if (INV_QTY == 0) {
                            STK_QTY = 0D;
                        }
                        log.info("STK_QTY -----1--->: " + STK_QTY);

                        InventoryV2 stkInventory = new InventoryV2();
                        BeanUtils.copyProperties(inventory, stkInventory, CommonUtils.getNullPropertyNames(inventory));
                        INV_QTY = INV_QTY - STK_QTY;
                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
                        stkInventory.setInventoryQuantity(INV_QTY);
                        stkInventory.setReferenceField4(TOT_QTY);
                        stkInventory.setDeletionIndicator(0L);
                        inventoryV2Repository.save(stkInventory);

                        //Insert New Record in StockAdjustment
                        StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
                        if (stockAdjustment.getAdjustmentQty() < 0) {
                            movementQtyValue = "N";
                        }
                        if (stockAdjustment.getAdjustmentQty() > 0) {
                            movementQtyValue = "P";
                        }
                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                        stockAdjustmentList.add(createStockAdjustment);

//                            if (STK_ADJ_QTY > STK_QTY) {
                        STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                        log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
//                            }
                        if (STK_ADJ_QTY < 0) {
                            STK_ADJ_QTY = 0D;
                        }
                        if (STK_ADJ_QTY == 0D) {
                            log.info("STK_ADJ_QTY fully adjusted: ");
                            break outerloop;
                        }
                    }
                    return stockAdjustmentList.get(0);
                    }
                }
                if (inventoryList == null || inventoryList.isEmpty()) {
                    throw new BadRequestException("No Stock found in the Bin Locations");
                }
            }
            if (perpetualLine == null && (inventoryList == null || inventoryList.isEmpty())) {
                throw new BadRequestException("No PerPetual Line & Inventory Found for given ItemCode, ManufacturerName : " + stockAdjustment.getItemCode() + ", " + stockAdjustment.getManufacturerName());
            }
        }

        //Stock Adjustment Damage Code
        if (stockAdjustment.getIsDamage().equalsIgnoreCase("Y") && stockAdjustment.getIsCycleCount().equalsIgnoreCase("N")) {
            log.info("IsDamage: " + stockAdjustment.getIsDamage());

            InventoryV2 dbInventory = inventoryService.getInventoryForStockAdjustmentDamageV2(
                    stockAdjustment.getCompanyCode(),
                    stockAdjustment.getBranchCode(),
                    "EN",
                    stockAdjustment.getWarehouseId(),
                    stockAdjustment.getItemCode(),
                    "99999",
                    7L,
                    stockAdjustment.getManufacturerName());
            log.info("Inventory: " + dbInventory);

            if (dbInventory != null) {
                if (dbInventory.getInventoryQuantity() > 0) {

                    //Stock Adjustment - Positive Qty - Have to add Stock Adjustment Qty with Inventory
//                    if(stockAdjustment.getAdjustmentQty() > 0) {
//                        InventoryV2 newInventory = new InventoryV2();
//                        log.info("Inventory : " + dbInventory);
//                        BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
//                        Double INV_QTY = dbInventory.getInventoryQuantity();
//                        Double ADJ_QTY = stockAdjustment.getAdjustmentQty();
////                        INV_QTY = INV_QTY + ADJ_QTY;
//                        INV_QTY = INV_QTY - ADJ_QTY;        //User requested to stick this logic(always subtract adjustment qty when declared as damage)
//                        if(INV_QTY < 0){
//                            INV_QTY = 0D;
//                        }
//                        newInventory.setInventoryQuantity(INV_QTY);
//                        Double ALLOC_QTY = 0D;
//                        if (dbInventory.getAllocatedQuantity() != null) {
//                            ALLOC_QTY = dbInventory.getAllocatedQuantity();
//                        }
//                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                        newInventory.setReferenceField4(TOT_QTY);
//
//                        newInventory.setUpdatedOn(new Date());
//                        inventoryV2Repository.save(newInventory);
//                        //Insert New Record in StockAdjustment
//                        StockAdjustment createStockAdjustment = createStockAdjustment(newInventory, dbInventory.getReferenceField4(), dbInventory.getReferenceField8(), stockAdjustment);
//                        log.info("createdStockAdjustment: " + createStockAdjustment);
//                        if(stockAdjustment.getAdjustmentQty() < 0) {
//                            movementQtyValue = "N";
//                        }
//                        if(stockAdjustment.getAdjustmentQty() > 0) {
//                            movementQtyValue = "P";
//                        }
//                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                        return createStockAdjustment;
//                    }
//                    if(stockAdjustment.getAdjustmentQty() < 0) {
                        Double STK_ADJ_QTY = 0D;
                        Double INV_QTY = 0D;
                        Double STK_QTY = 0D;
                        Double ALLOC_QTY = 0D;
                        List<StockAdjustment> stockAdjustmentList = new ArrayList<>();
                        if(stockAdjustment.getAdjustmentQty() != null) {
                            STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
                        }
                        if(dbInventory.getInventoryQuantity() != null){
                            INV_QTY = dbInventory.getInventoryQuantity();
                        }
                        if(dbInventory.getAllocatedQuantity() != null) {
                            ALLOC_QTY = dbInventory.getAllocatedQuantity();
                        }
                        if (STK_ADJ_QTY <= INV_QTY) {
                            STK_QTY = STK_ADJ_QTY;
                        } else if (STK_ADJ_QTY > INV_QTY) {
                            STK_QTY = INV_QTY;
                        } else if (INV_QTY == 0) {
                            STK_QTY = 0D;
                        }
                        log.info("STK_QTY -----1--->: " + STK_QTY);

                        InventoryV2 stkInventory = new InventoryV2();
                        BeanUtils.copyProperties(dbInventory, stkInventory, CommonUtils.getNullPropertyNames(dbInventory));
                        INV_QTY = INV_QTY - STK_QTY;
                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
                        stkInventory.setInventoryQuantity(INV_QTY);
                        stkInventory.setReferenceField4(TOT_QTY);
                        stkInventory.setDeletionIndicator(0L);
                        inventoryV2Repository.save(stkInventory);

                        //Insert New Record in StockAdjustment
                        StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, dbInventory.getReferenceField4(), dbInventory.getReferenceField8(), stockAdjustment);
                        if(stockAdjustment.getAdjustmentQty() < 0) {
                            movementQtyValue = "N";
                        }
                        if(stockAdjustment.getAdjustmentQty() > 0) {
                            movementQtyValue = "P";
                        }
                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                        stockAdjustmentList.add(createStockAdjustment);

                        STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                        log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);

                        if(STK_ADJ_QTY > 0) {
                            List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
                                    stockAdjustment.getCompanyCode(),
                                    stockAdjustment.getBranchCode(),
                                    LANG_ID,
                                    stockAdjustment.getWarehouseId(),
                                    stockAdjustment.getItemCode(),
                                    stockAdjustment.getManufacturerName(),
                                    1L);
                            log.info("Inventory-----> BinclassId 7 for this item has been emptied; ----> BinClassId 1 ---> " + inventoryList);
                            if(inventoryList != null && !inventoryList.isEmpty()){
                                //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
                                    outerloop:
                                    for(IInventoryImpl inventory : inventoryList) {
                                        if(inventory.getInventoryQuantity() != null){
                                            INV_QTY = inventory.getInventoryQuantity();
                                        }
                                        if(inventory.getAllocatedQuantity() != null) {
                                            ALLOC_QTY = inventory.getAllocatedQuantity();
                                        }
                                        if (STK_ADJ_QTY <= INV_QTY) {
                                            STK_QTY = STK_ADJ_QTY;
                                        } else if (STK_ADJ_QTY > INV_QTY) {
                                            STK_QTY = INV_QTY;
                                        } else if (INV_QTY == 0) {
                                            STK_QTY = 0D;
                                        }
                                        log.info("STK_QTY -----1--->: " + STK_QTY);

                                        InventoryV2 stkInventoryNew = new InventoryV2();
                                        BeanUtils.copyProperties(inventory, stkInventoryNew, CommonUtils.getNullPropertyNames(inventory));
                                        INV_QTY = INV_QTY - STK_QTY;
                                        TOT_QTY = INV_QTY + ALLOC_QTY;
                                        stkInventoryNew.setInventoryQuantity(INV_QTY);
                                        stkInventoryNew.setReferenceField4(TOT_QTY);
                                        stkInventoryNew.setDeletionIndicator(0L);
                                        inventoryV2Repository.save(stkInventoryNew);

                                        //Insert New Record in StockAdjustment
                                        StockAdjustment createStockAdjustmentNew = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
                                        if(stockAdjustment.getAdjustmentQty() < 0) {
                                            movementQtyValue = "N";
                                        }
                                        if(stockAdjustment.getAdjustmentQty() > 0) {
                                            movementQtyValue = "P";
                                        }
                                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                                        stockAdjustmentList.add(createStockAdjustmentNew);

                                        STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                                        log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);

                                        if(STK_ADJ_QTY < 0) {
                                            STK_ADJ_QTY = 0D;
                                        }
                                        if(STK_ADJ_QTY == 0D) {
                                            log.info("STK_ADJ_QTY fully adjusted: ");
                                            break outerloop;
                                        }
                                    }
                                    if(STK_ADJ_QTY > 0){
                                        throw new BadRequestException("Stock Adjustment Qty is greater than physically present in inventory");
                                    }
                                    return stockAdjustmentList.get(0);
                            }
                            if(inventoryList == null || inventoryList.isEmpty()){
                                throw new BadRequestException("No Stock found in the Bin Locations");
                            }
                        }
                        return stockAdjustmentList.get(0);
//                    }
                }
                if (dbInventory.getInventoryQuantity() <= 0) {
                    List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
                            stockAdjustment.getCompanyCode(),
                            stockAdjustment.getBranchCode(),
                            LANG_ID,
                            stockAdjustment.getWarehouseId(),
                            stockAdjustment.getItemCode(),
                            stockAdjustment.getManufacturerName(),
                            1L);
                    log.info("Inventory-----> BinclassId 7 for this item is empty; ----> BinClassId 1 ---> " + inventoryList);

                    if(inventoryList != null && !inventoryList.isEmpty()){
                        //Stock Adjustment - Positive Qty - Have to add Stock Adjustment Qty with Inventory
//                        if(stockAdjustment.getAdjustmentQty() > 0){
//                            InventoryV2 stkInventory = new InventoryV2();
//                            BeanUtils.copyProperties(inventoryList.get(0), stkInventory, CommonUtils.getNullPropertyNames(inventoryList.get(0)));
//                            Double INV_QTY = 0D;
//                            if(inventoryList.get(0).getInventoryQuantity() != null) {
//                                INV_QTY = inventoryList.get(0).getInventoryQuantity();
//                                INV_QTY = INV_QTY + stockAdjustment.getAdjustmentQty();
//                            }
//                            Double ALLOC_QTY = 0D;
//                            if(inventoryList.get(0).getAllocatedQuantity() != null) {
//                                ALLOC_QTY = inventoryList.get(0).getAllocatedQuantity();
//                            }
//                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                            stkInventory.setInventoryQuantity(INV_QTY);
//                            stkInventory.setAllocatedQuantity(ALLOC_QTY);
//                            stkInventory.setReferenceField4(TOT_QTY);
//                            stkInventory.setDeletionIndicator(0L);
//                            stkInventory.setUpdatedOn(new Date());
//                            inventoryV2Repository.save(stkInventory);
//
//                            //Insert New Record in StockAdjustment
//                            StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventoryList.get(0).getReferenceField4(), inventoryList.get(0).getReferenceField8(), stockAdjustment);
//                            if(stockAdjustment.getAdjustmentQty() < 0) {
//                                movementQtyValue = "N";
//                            }
//                            if(stockAdjustment.getAdjustmentQty() > 0) {
//                                movementQtyValue = "P";
//                            }
//                            createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                            log.info("createdStockAdjustment: " + createStockAdjustment);
//                            return createStockAdjustment;
//                        }
                        //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
//                        if(stockAdjustment.getAdjustmentQty() < 0){
                            Double STK_ADJ_QTY = 0D;
                            Double INV_QTY = 0D;
                            Double STK_QTY = 0D;
                            Double ALLOC_QTY = 0D;
                            List<StockAdjustment> stockAdjustmentList = new ArrayList<>();
                            if(stockAdjustment.getAdjustmentQty() != null) {
                                STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
                            }
                            outerloop:
                            for(IInventoryImpl inventory : inventoryList) {
                                if(inventory.getInventoryQuantity() != null){
                                    INV_QTY = inventory.getInventoryQuantity();
                                }
                                if(inventory.getAllocatedQuantity() != null) {
                                    ALLOC_QTY = inventory.getAllocatedQuantity();
                                }
                                if (STK_ADJ_QTY <= INV_QTY) {
                                    STK_QTY = STK_ADJ_QTY;
                                } else if (STK_ADJ_QTY > INV_QTY) {
                                    STK_QTY = INV_QTY;
                                } else if (INV_QTY == 0) {
                                    STK_QTY = 0D;
                                }
                                log.info("STK_QTY -----1--->: " + STK_QTY);

                                InventoryV2 stkInventory = new InventoryV2();
                                BeanUtils.copyProperties(inventory, stkInventory, CommonUtils.getNullPropertyNames(inventory));
                                INV_QTY = INV_QTY - STK_QTY;
                                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                                stkInventory.setInventoryQuantity(INV_QTY);
                                stkInventory.setReferenceField4(TOT_QTY);
                                stkInventory.setDeletionIndicator(0L);
                                inventoryV2Repository.save(stkInventory);

                                //Insert New Record in StockAdjustment
                                StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
                                if(stockAdjustment.getAdjustmentQty() < 0) {
                                    movementQtyValue = "N";
                                }
                                if(stockAdjustment.getAdjustmentQty() > 0) {
                                    movementQtyValue = "P";
                                }
                                createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                                stockAdjustmentList.add(createStockAdjustment);

//                            if (STK_ADJ_QTY > STK_QTY) {
                                STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                                log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
//                            }
                                if(STK_ADJ_QTY < 0) {
                                    STK_ADJ_QTY = 0D;
                                }
                                if(STK_ADJ_QTY == 0D) {
                                    log.info("STK_ADJ_QTY fully adjusted: ");
                                    break outerloop;
                                }
                            }
                            return stockAdjustmentList.get(0);
//                        }
                    }
                    if(inventoryList == null || inventoryList.isEmpty()){
                        throw new BadRequestException("No Stock found in the Bin Locations");
                    }
                }
            }
            if (dbInventory == null) {
                List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
                        stockAdjustment.getCompanyCode(),
                        stockAdjustment.getBranchCode(),
                        LANG_ID,
                        stockAdjustment.getWarehouseId(),
                        stockAdjustment.getItemCode(),
                        stockAdjustment.getManufacturerName(),
                        1L);
                log.info("Inventory-----> BinclassId 7 for this item is empty; ----> BinClassId 1 ---> " + inventoryList);

                if(inventoryList != null && !inventoryList.isEmpty()){
                    //Stock Adjustment - Positive Qty - Have to add Stock Adjustment Qty with Inventory
//                    if(stockAdjustment.getAdjustmentQty() > 0){
//                        InventoryV2 stkInventory = new InventoryV2();
//                        BeanUtils.copyProperties(inventoryList.get(0), stkInventory, CommonUtils.getNullPropertyNames(inventoryList.get(0)));
//                        Double INV_QTY = 0D;
//                        if(inventoryList.get(0).getInventoryQuantity() != null) {
//                            INV_QTY = inventoryList.get(0).getInventoryQuantity();
//                            INV_QTY = INV_QTY + stockAdjustment.getAdjustmentQty();
//                        }
//                        Double ALLOC_QTY = 0D;
//                        if(inventoryList.get(0).getAllocatedQuantity() != null) {
//                            ALLOC_QTY = inventoryList.get(0).getAllocatedQuantity();
//                        }
//                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                        stkInventory.setInventoryQuantity(INV_QTY);
//                        stkInventory.setAllocatedQuantity(ALLOC_QTY);
//                        stkInventory.setReferenceField4(TOT_QTY);
//                        stkInventory.setDeletionIndicator(0L);
//                        stkInventory.setUpdatedOn(new Date());
//                        inventoryV2Repository.save(stkInventory);
//
//                        //Insert New Record in StockAdjustment
//                        StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventoryList.get(0).getReferenceField4(), inventoryList.get(0).getReferenceField8(), stockAdjustment);
//                        if(stockAdjustment.getAdjustmentQty() < 0) {
//                            movementQtyValue = "N";
//                        }
//                        if(stockAdjustment.getAdjustmentQty() > 0) {
//                            movementQtyValue = "P";
//                        }
//                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                        log.info("createdStockAdjustment: " + createStockAdjustment);
//                        return createStockAdjustment;
//                    }
                    //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
//                    if(stockAdjustment.getAdjustmentQty() < 0){
                        Double STK_ADJ_QTY = 0D;
                        Double INV_QTY = 0D;
                        Double STK_QTY = 0D;
                        Double ALLOC_QTY = 0D;
                        List<StockAdjustment> stockAdjustmentList = new ArrayList<>();
                        if(stockAdjustment.getAdjustmentQty() != null) {
                            STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
                        }
                        outerloop:
                        for(IInventoryImpl inventory : inventoryList) {
                            if(inventory.getInventoryQuantity() != null){
                                INV_QTY = inventory.getInventoryQuantity();
                            }
                            if(inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            if (STK_ADJ_QTY <= INV_QTY) {
                                STK_QTY = STK_ADJ_QTY;
                            } else if (STK_ADJ_QTY > INV_QTY) {
                                STK_QTY = INV_QTY;
                            } else if (INV_QTY == 0) {
                                STK_QTY = 0D;
                            }
                            log.info("STK_QTY -----1--->: " + STK_QTY);

                            InventoryV2 stkInventory = new InventoryV2();
                            BeanUtils.copyProperties(inventory, stkInventory, CommonUtils.getNullPropertyNames(inventory));
                            INV_QTY = INV_QTY - STK_QTY;
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            stkInventory.setInventoryQuantity(INV_QTY);
                            stkInventory.setReferenceField4(TOT_QTY);
                            stkInventory.setDeletionIndicator(0L);
                            inventoryV2Repository.save(stkInventory);

                            //Insert New Record in StockAdjustment
                            StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
                            if(stockAdjustment.getAdjustmentQty() < 0) {
                                movementQtyValue = "N";
                            }
                            if(stockAdjustment.getAdjustmentQty() > 0) {
                                movementQtyValue = "P";
                            }
                            createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                            stockAdjustmentList.add(createStockAdjustment);

//                            if (STK_ADJ_QTY > STK_QTY) {
                                STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                                log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
//                            }
                            if(STK_ADJ_QTY < 0) {
                                STK_ADJ_QTY = 0D;
                            }
                            if(STK_ADJ_QTY == 0D) {
                                log.info("STK_ADJ_QTY fully adjusted: ");
                                break outerloop;
                            }
                        }
                        return stockAdjustmentList.get(0);
//                    }
                }
                if(inventoryList == null || inventoryList.isEmpty()){
                    throw new BadRequestException("No Stock found in the Bin Locations");
                }
            }
        }
        return null;
    }

//    public StockAdjustment autoUpdateStockAdjustmentNew(com.tekclover.wms.api.transaction.model.warehouse.stockAdjustment.StockAdjustment stockAdjustment) {
//        String movementQtyValue = null;
//        if (stockAdjustment.getIsCycleCount().equalsIgnoreCase("Y") && stockAdjustment.getIsDamage().equalsIgnoreCase("N")) {
//            log.info("IsCycleCount: " + stockAdjustment.getIsCycleCount());
//            PerpetualLineV2 perpetualLine = perpetualLineService.getPerpetualLineForStockAdjustmentV2(
//                    stockAdjustment.getCompanyCode(),
//                    stockAdjustment.getBranchCode(),
//                    "EN",
//                    stockAdjustment.getWarehouseId(),
//                    stockAdjustment.getItemCode(),
//                    stockAdjustment.getManufacturerName());
//            log.info("Perpetual Line: " + perpetualLine);
//
//            if (perpetualLine != null) {
//
//                InventoryV2 dbInventory = inventoryService.getInventoryV2(
//                        perpetualLine.getCompanyCodeId(),
//                        perpetualLine.getPlantId(),
//                        perpetualLine.getLanguageId(),
//                        perpetualLine.getWarehouseId(),
//                        perpetualLine.getPackBarcodes(),
//                        perpetualLine.getItemCode(),
//                        perpetualLine.getStorageBin(),
//                        perpetualLine.getManufacturerName());
//                log.info("Inventory: " + dbInventory);
//
//                if (dbInventory != null) {
//                    InventoryV2 newInventory = new InventoryV2();
//                    BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
//                    Double INV_QTY = dbInventory.getInventoryQuantity();
//                    Double ADJ_QTY = stockAdjustment.getAdjustmentQty();
//                    INV_QTY = INV_QTY + ADJ_QTY;
//                    if(INV_QTY < 0){
//                        INV_QTY = 0D;
//                    }
//                    newInventory.setInventoryQuantity(INV_QTY);
//                    Double ALLOC_QTY = 0D;
//                    if(dbInventory.getAllocatedQuantity() != null) {
//                        ALLOC_QTY = dbInventory.getAllocatedQuantity();
//                    }
//                    Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                    newInventory.setReferenceField4(TOT_QTY);
//                    newInventory.setInventoryId(System.currentTimeMillis());
//                    inventoryV2Repository.save(newInventory);
//
//                    //StockAdjustment Record Insert
//                    StockAdjustment dbStockAdjustment = new StockAdjustment();
//                    BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));
//
//                    dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
//                    dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
//                    dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());
//
//                    if (stockAdjustment.getItemDescription() != null) {
//                        dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
//                    }
//                    if (stockAdjustment.getItemDescription() == null && dbInventory.getDescription() != null) {
//                        dbStockAdjustment.setItemDescription(dbInventory.getDescription());
//                    }
//                    if (stockAdjustment.getItemDescription() == null && dbInventory.getDescription() == null) {
//                        if (dbInventory.getReferenceField8() != null) {
//                            dbStockAdjustment.setItemDescription(dbInventory.getReferenceField8());
//                        }
//                    }
//
//                    dbStockAdjustment.setBeforeAdjustment(dbInventory.getReferenceField4());
//                    dbStockAdjustment.setAfterAdjustment(newInventory.getReferenceField4());
//
//                    dbStockAdjustment.setPackBarcodes(dbInventory.getPackBarcodes());
//                    dbStockAdjustment.setBranchName(newInventory.getPlantDescription());
//                    dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
//                    dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
//                    dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
//                    dbStockAdjustment.setReferenceField1(stockAdjustment.getRefDocType());
//                    dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
//                    dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
//                    dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
//                    dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
//                    dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
//                    dbStockAdjustment.setMiddlewareId(stockAdjustment.getMiddlewareId());
//                    dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
//                    dbStockAdjustment.setSaUpdatedOn(stockAdjustment.getUpdatedOn());
//                    dbStockAdjustment.setStockAdjustmentKey(newInventory.getInventoryId());
//
//                    dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
//                    statusDescription = stagingLineV2Repository.getStatusDescription(88L, dbInventory.getLanguageId());
//                    dbStockAdjustment.setStatusDescription(statusDescription);
//
//                    dbStockAdjustment.setDeletionIndicator(0L);
//                    dbStockAdjustment.setCreatedOn(new Date());
//                    dbStockAdjustment.setIsCompleted("Y");
//                    dbStockAdjustment.setCreatedBy("MW_AMS");
//
//                    StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
//                    log.info("createdStockAdjustment: " + createStockAdjustment);
//                    if(stockAdjustment.getAdjustmentQty() < 0) {
//                        movementQtyValue = "N";
//                    }
//                    if(stockAdjustment.getAdjustmentQty() > 0) {
//                        movementQtyValue = "P";
//                    }
//                    createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                    return createStockAdjustment;
//                }
//            }
//            if(perpetualLine == null) {
//                throw new BadRequestException("No PerPetual Line Found for given ItemCode, ManufacturerName : " + stockAdjustment.getItemCode() + ", " + stockAdjustment.getManufacturerName());
//            }
//        }
//
//        //Stock Adjustment Damage Code
//        if (stockAdjustment.getIsDamage().equalsIgnoreCase("Y") && stockAdjustment.getIsCycleCount().equalsIgnoreCase("N")) {
//            log.info("IsDamage: " + stockAdjustment.getIsDamage());
//
//            InventoryV2 dbInventory = inventoryService.getInventoryForStockAdjustmentDamageV2(
//                    stockAdjustment.getCompanyCode(),
//                    stockAdjustment.getBranchCode(),
//                    "EN",
//                    stockAdjustment.getWarehouseId(),
//                    stockAdjustment.getItemCode(),
//                    "99999",
//                    7L,
//                    stockAdjustment.getManufacturerName());
//            log.info("Inventory: " + dbInventory);
//
//            if (dbInventory != null) {
//                if (dbInventory.getInventoryQuantity() > 0) {
//
//                    //Stock Adjustment - Positive Qty - Have to add Stock Adjustment Qty with Inventory
//                    if(stockAdjustment.getAdjustmentQty() > 0) {
//                        InventoryV2 newInventory = new InventoryV2();
//                        log.info("Inventory : " + dbInventory);
//                        BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
//                        Double INV_QTY = dbInventory.getInventoryQuantity();
//                        Double ADJ_QTY = stockAdjustment.getAdjustmentQty();
//                        INV_QTY = INV_QTY + ADJ_QTY;
//                        if(INV_QTY < 0){
//                            INV_QTY = 0D;
//                        }
//                        newInventory.setInventoryQuantity(INV_QTY);
//                        Double ALLOC_QTY = 0D;
//                        if (dbInventory.getAllocatedQuantity() != null) {
//                            ALLOC_QTY = dbInventory.getAllocatedQuantity();
//                        }
//                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                        newInventory.setReferenceField4(TOT_QTY);
//
//                        newInventory.setUpdatedOn(new Date());
//                        newInventory.setInventoryId(System.currentTimeMillis());
//                        inventoryV2Repository.save(newInventory);
//                        //Insert New Record in StockAdjustment
//                        StockAdjustment createStockAdjustment = createStockAdjustment(newInventory, dbInventory.getReferenceField4(), dbInventory.getReferenceField8(), stockAdjustment);
//                        log.info("createdStockAdjustment: " + createStockAdjustment);
//                        if(stockAdjustment.getAdjustmentQty() < 0) {
//                            movementQtyValue = "N";
//                        }
//                        if(stockAdjustment.getAdjustmentQty() > 0) {
//                            movementQtyValue = "P";
//                        }
//                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                        return createStockAdjustment;
//                    }
//                    if(stockAdjustment.getAdjustmentQty() < 0) {
//                        Double STK_ADJ_QTY = 0D;
//                        Double INV_QTY = 0D;
//                        Double STK_QTY = 0D;
//                        Double ALLOC_QTY = 0D;
//                        List<StockAdjustment> stockAdjustmentList = new ArrayList<>();
//                        if(stockAdjustment.getAdjustmentQty() != null) {
//                            STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
//                        }
//                        if(dbInventory.getInventoryQuantity() != null){
//                            INV_QTY = dbInventory.getInventoryQuantity();
//                        }
//                        if(dbInventory.getAllocatedQuantity() != null) {
//                            ALLOC_QTY = dbInventory.getAllocatedQuantity();
//                        }
//                        if (STK_ADJ_QTY <= INV_QTY) {
//                            STK_QTY = STK_ADJ_QTY;
//                        } else if (STK_ADJ_QTY > INV_QTY) {
//                            STK_QTY = INV_QTY;
//                        } else if (INV_QTY == 0) {
//                            STK_QTY = 0D;
//                        }
//                        log.info("STK_QTY -----1--->: " + STK_QTY);
//
//                        InventoryV2 stkInventory = new InventoryV2();
//                        BeanUtils.copyProperties(dbInventory, stkInventory, CommonUtils.getNullPropertyNames(dbInventory));
//                        INV_QTY = INV_QTY - STK_QTY;
//                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                        stkInventory.setInventoryQuantity(INV_QTY);
//                        stkInventory.setReferenceField4(TOT_QTY);
//                        stkInventory.setDeletionIndicator(0L);
//                        stkInventory.setInventoryId(System.currentTimeMillis());
//                        inventoryV2Repository.save(stkInventory);
//
//                        //Insert New Record in StockAdjustment
//                        StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, dbInventory.getReferenceField4(), dbInventory.getReferenceField8(), stockAdjustment);
//                        if(stockAdjustment.getAdjustmentQty() < 0) {
//                            movementQtyValue = "N";
//                        }
//                        if(stockAdjustment.getAdjustmentQty() > 0) {
//                            movementQtyValue = "P";
//                        }
//                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                        stockAdjustmentList.add(createStockAdjustment);
//
//                        STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
//                        log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
//
//                        if(STK_ADJ_QTY > 0) {
//                            List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
//                                    stockAdjustment.getCompanyCode(),
//                                    stockAdjustment.getBranchCode(),
//                                    LANG_ID,
//                                    stockAdjustment.getWarehouseId(),
//                                    stockAdjustment.getItemCode(),
//                                    stockAdjustment.getManufacturerName(),
//                                    1L);
//                            log.info("Inventory-----> BinclassId 7 for this item has been emptied; ----> BinClassId 1 ---> " + inventoryList);
//                            if(inventoryList != null && !inventoryList.isEmpty()){
//                                //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
//                                outerloop:
//                                for(IInventoryImpl inventory : inventoryList) {
//                                    if(inventory.getInventoryQuantity() != null){
//                                        INV_QTY = inventory.getInventoryQuantity();
//                                    }
//                                    if(inventory.getAllocatedQuantity() != null) {
//                                        ALLOC_QTY = inventory.getAllocatedQuantity();
//                                    }
//                                    if (STK_ADJ_QTY <= INV_QTY) {
//                                        STK_QTY = STK_ADJ_QTY;
//                                    } else if (STK_ADJ_QTY > INV_QTY) {
//                                        STK_QTY = INV_QTY;
//                                    } else if (INV_QTY == 0) {
//                                        STK_QTY = 0D;
//                                    }
//                                    log.info("STK_QTY -----1--->: " + STK_QTY);
//
//                                    InventoryV2 stkInventoryNew = new InventoryV2();
//                                    BeanUtils.copyProperties(inventory, stkInventoryNew, CommonUtils.getNullPropertyNames(inventory));
//                                    INV_QTY = INV_QTY - STK_QTY;
//                                    TOT_QTY = INV_QTY + ALLOC_QTY;
//                                    stkInventoryNew.setInventoryQuantity(INV_QTY);
//                                    stkInventoryNew.setReferenceField4(TOT_QTY);
//                                    stkInventoryNew.setDeletionIndicator(0L);
//                                    stkInventoryNew.setInventoryId(System.currentTimeMillis());
//                                    inventoryV2Repository.save(stkInventoryNew);
//
//                                    //Insert New Record in StockAdjustment
//                                    StockAdjustment createStockAdjustmentNew = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
//                                    if(stockAdjustment.getAdjustmentQty() < 0) {
//                                        movementQtyValue = "N";
//                                    }
//                                    if(stockAdjustment.getAdjustmentQty() > 0) {
//                                        movementQtyValue = "P";
//                                    }
//                                    createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                                    stockAdjustmentList.add(createStockAdjustmentNew);
//
//                                    STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
//                                    log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
//
//                                    if(STK_ADJ_QTY < 0) {
//                                        STK_ADJ_QTY = 0D;
//                                    }
//                                    if(STK_ADJ_QTY == 0D) {
//                                        log.info("STK_ADJ_QTY fully adjusted: ");
//                                        break outerloop;
//                                    }
//                                }
//                                if(STK_ADJ_QTY > 0){
//                                    throw new BadRequestException("Stock Adjustment Qty is greater than physically present in inventory");
//                                }
//                                return stockAdjustmentList.get(0);
//                            }
//                            if(inventoryList == null || inventoryList.isEmpty()){
//                                throw new BadRequestException("No Stock found in the Bin Locations");
//                            }
//                        }
//                        return stockAdjustmentList.get(0);
//                    }
//                }
//                if (dbInventory.getInventoryQuantity() <= 0) {
//                    List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
//                            stockAdjustment.getCompanyCode(),
//                            stockAdjustment.getBranchCode(),
//                            LANG_ID,
//                            stockAdjustment.getWarehouseId(),
//                            stockAdjustment.getItemCode(),
//                            stockAdjustment.getManufacturerName(),
//                            1L);
//                    log.info("Inventory-----> BinclassId 7 for this item is empty; ----> BinClassId 1 ---> " + inventoryList);
//
//                    if(inventoryList != null && !inventoryList.isEmpty()){
//                        //Stock Adjustment - Positive Qty - Have to add Stock Adjustment Qty with Inventory
//                        if(stockAdjustment.getAdjustmentQty() > 0){
//                            InventoryV2 stkInventory = new InventoryV2();
//                            BeanUtils.copyProperties(inventoryList.get(0), stkInventory, CommonUtils.getNullPropertyNames(inventoryList.get(0)));
//                            Double INV_QTY = 0D;
//                            if(inventoryList.get(0).getInventoryQuantity() != null) {
//                                INV_QTY = inventoryList.get(0).getInventoryQuantity();
//                                INV_QTY = INV_QTY + stockAdjustment.getAdjustmentQty();
//                            }
//                            Double ALLOC_QTY = 0D;
//                            if(inventoryList.get(0).getAllocatedQuantity() != null) {
//                                ALLOC_QTY = inventoryList.get(0).getAllocatedQuantity();
//                            }
//                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                            stkInventory.setInventoryQuantity(INV_QTY);
//                            stkInventory.setAllocatedQuantity(ALLOC_QTY);
//                            stkInventory.setReferenceField4(TOT_QTY);
//                            stkInventory.setDeletionIndicator(0L);
//                            stkInventory.setUpdatedOn(new Date());
//                            stkInventory.setInventoryId(System.currentTimeMillis());
//                            inventoryV2Repository.save(stkInventory);
//
//                            //Insert New Record in StockAdjustment
//                            StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventoryList.get(0).getReferenceField4(), inventoryList.get(0).getReferenceField8(), stockAdjustment);
//                            if(stockAdjustment.getAdjustmentQty() < 0) {
//                                movementQtyValue = "N";
//                            }
//                            if(stockAdjustment.getAdjustmentQty() > 0) {
//                                movementQtyValue = "P";
//                            }
//                            createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                            log.info("createdStockAdjustment: " + createStockAdjustment);
//                            return createStockAdjustment;
//                        }
//                        //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
//                        if(stockAdjustment.getAdjustmentQty() < 0){
//                            Double STK_ADJ_QTY = 0D;
//                            Double INV_QTY = 0D;
//                            Double STK_QTY = 0D;
//                            Double ALLOC_QTY = 0D;
//                            List<StockAdjustment> stockAdjustmentList = new ArrayList<>();
//                            if(stockAdjustment.getAdjustmentQty() != null) {
//                                STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
//                            }
//                            outerloop:
//                            for(IInventoryImpl inventory : inventoryList) {
//                                if(inventory.getInventoryQuantity() != null){
//                                    INV_QTY = inventory.getInventoryQuantity();
//                                }
//                                if(inventory.getAllocatedQuantity() != null) {
//                                    ALLOC_QTY = inventory.getAllocatedQuantity();
//                                }
//                                if (STK_ADJ_QTY <= INV_QTY) {
//                                    STK_QTY = STK_ADJ_QTY;
//                                } else if (STK_ADJ_QTY > INV_QTY) {
//                                    STK_QTY = INV_QTY;
//                                } else if (INV_QTY == 0) {
//                                    STK_QTY = 0D;
//                                }
//                                log.info("STK_QTY -----1--->: " + STK_QTY);
//
//                                InventoryV2 stkInventory = new InventoryV2();
//                                BeanUtils.copyProperties(inventory, stkInventory, CommonUtils.getNullPropertyNames(inventory));
//                                INV_QTY = INV_QTY - STK_QTY;
//                                Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                                stkInventory.setInventoryQuantity(INV_QTY);
//                                stkInventory.setReferenceField4(TOT_QTY);
//                                stkInventory.setDeletionIndicator(0L);
//                                stkInventory.setInventoryId(System.currentTimeMillis());
//                                inventoryV2Repository.save(stkInventory);
//
//                                //Insert New Record in StockAdjustment
//                                StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
//                                if(stockAdjustment.getAdjustmentQty() < 0) {
//                                    movementQtyValue = "N";
//                                }
//                                if(stockAdjustment.getAdjustmentQty() > 0) {
//                                    movementQtyValue = "P";
//                                }
//                                createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                                stockAdjustmentList.add(createStockAdjustment);
//
////                            if (STK_ADJ_QTY > STK_QTY) {
//                                STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
//                                log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
////                            }
//                                if(STK_ADJ_QTY < 0) {
//                                    STK_ADJ_QTY = 0D;
//                                }
//                                if(STK_ADJ_QTY == 0D) {
//                                    log.info("STK_ADJ_QTY fully adjusted: ");
//                                    break outerloop;
//                                }
//                            }
//                            return stockAdjustmentList.get(0);
//                        }
//                    }
//                    if(inventoryList == null || inventoryList.isEmpty()){
//                        throw new BadRequestException("No Stock found in the Bin Locations");
//                    }
//                }
//            }
//            if (dbInventory == null) {
//                List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
//                        stockAdjustment.getCompanyCode(),
//                        stockAdjustment.getBranchCode(),
//                        LANG_ID,
//                        stockAdjustment.getWarehouseId(),
//                        stockAdjustment.getItemCode(),
//                        stockAdjustment.getManufacturerName(),
//                        1L);
//                log.info("Inventory-----> BinclassId 7 for this item is empty; ----> BinClassId 1 ---> " + inventoryList);
//
//                if(inventoryList != null && !inventoryList.isEmpty()){
//                    //Stock Adjustment - Positive Qty - Have to add Stock Adjustment Qty with Inventory
//                    if(stockAdjustment.getAdjustmentQty() > 0){
//                        InventoryV2 stkInventory = new InventoryV2();
//                        BeanUtils.copyProperties(inventoryList.get(0), stkInventory, CommonUtils.getNullPropertyNames(inventoryList.get(0)));
//                        Double INV_QTY = 0D;
//                        if(inventoryList.get(0).getInventoryQuantity() != null) {
//                            INV_QTY = inventoryList.get(0).getInventoryQuantity();
//                            INV_QTY = INV_QTY + stockAdjustment.getAdjustmentQty();
//                        }
//                        Double ALLOC_QTY = 0D;
//                        if(inventoryList.get(0).getAllocatedQuantity() != null) {
//                            ALLOC_QTY = inventoryList.get(0).getAllocatedQuantity();
//                        }
//                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                        stkInventory.setInventoryQuantity(INV_QTY);
//                        stkInventory.setAllocatedQuantity(ALLOC_QTY);
//                        stkInventory.setReferenceField4(TOT_QTY);
//                        stkInventory.setDeletionIndicator(0L);
//                        stkInventory.setUpdatedOn(new Date());
//                        stkInventory.setInventoryId(System.currentTimeMillis());
//                        inventoryV2Repository.save(stkInventory);
//
//                        //Insert New Record in StockAdjustment
//                        StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventoryList.get(0).getReferenceField4(), inventoryList.get(0).getReferenceField8(), stockAdjustment);
//                        if(stockAdjustment.getAdjustmentQty() < 0) {
//                            movementQtyValue = "N";
//                        }
//                        if(stockAdjustment.getAdjustmentQty() > 0) {
//                            movementQtyValue = "P";
//                        }
//                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                        log.info("createdStockAdjustment: " + createStockAdjustment);
//                        return createStockAdjustment;
//                    }
//                    //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
//                    if(stockAdjustment.getAdjustmentQty() < 0){
//                        Double STK_ADJ_QTY = 0D;
//                        Double INV_QTY = 0D;
//                        Double STK_QTY = 0D;
//                        Double ALLOC_QTY = 0D;
//                        List<StockAdjustment> stockAdjustmentList = new ArrayList<>();
//                        if(stockAdjustment.getAdjustmentQty() != null) {
//                            STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
//                        }
//                        outerloop:
//                        for(IInventoryImpl inventory : inventoryList) {
//                            if(inventory.getInventoryQuantity() != null){
//                                INV_QTY = inventory.getInventoryQuantity();
//                            }
//                            if(inventory.getAllocatedQuantity() != null) {
//                                ALLOC_QTY = inventory.getAllocatedQuantity();
//                            }
//                            if (STK_ADJ_QTY <= INV_QTY) {
//                                STK_QTY = STK_ADJ_QTY;
//                            } else if (STK_ADJ_QTY > INV_QTY) {
//                                STK_QTY = INV_QTY;
//                            } else if (INV_QTY == 0) {
//                                STK_QTY = 0D;
//                            }
//                            log.info("STK_QTY -----1--->: " + STK_QTY);
//
//                            InventoryV2 stkInventory = new InventoryV2();
//                            BeanUtils.copyProperties(inventory, stkInventory, CommonUtils.getNullPropertyNames(inventory));
//                            INV_QTY = INV_QTY - STK_QTY;
//                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
//                            stkInventory.setInventoryQuantity(INV_QTY);
//                            stkInventory.setReferenceField4(TOT_QTY);
//                            stkInventory.setDeletionIndicator(0L);
//                            stkInventory.setInventoryId(System.currentTimeMillis());
//                            inventoryV2Repository.save(stkInventory);
//
//                            //Insert New Record in StockAdjustment
//                            StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
//                            if(stockAdjustment.getAdjustmentQty() < 0) {
//                                movementQtyValue = "N";
//                            }
//                            if(stockAdjustment.getAdjustmentQty() > 0) {
//                                movementQtyValue = "P";
//                            }
//                            createInventoryMovementV2(createStockAdjustment, movementQtyValue);
//                            stockAdjustmentList.add(createStockAdjustment);
//
////                            if (STK_ADJ_QTY > STK_QTY) {
//                            STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
//                            log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
////                            }
//                            if(STK_ADJ_QTY < 0) {
//                                STK_ADJ_QTY = 0D;
//                            }
//                            if(STK_ADJ_QTY == 0D) {
//                                log.info("STK_ADJ_QTY fully adjusted: ");
//                                break outerloop;
//                            }
//                        }
//                        return stockAdjustmentList.get(0);
//                    }
//                }
//                if(inventoryList == null || inventoryList.isEmpty()){
//                    throw new BadRequestException("No Stock found in the Bin Locations");
//                }
//            }
//        }
//        return null;
//    }

    /**
     *
     * @param newInventory
     * @param inventoryQty
     * @param itemDesc
     * @param stockAdjustment
     * @return
     */
    public StockAdjustment createStockAdjustment(InventoryV2 newInventory,Double inventoryQty, String itemDesc,
                                                 com.tekclover.wms.api.outbound.transaction.model.warehouse.stockAdjustment.StockAdjustment stockAdjustment){
        //StockAdjustment Record Insert
        StockAdjustment dbStockAdjustment = new StockAdjustment();
        BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));

        dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
        dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
        dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());

        if (stockAdjustment.getItemDescription() != null) {
            dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
        }
        if (stockAdjustment.getItemDescription() == null && newInventory.getDescription() != null) {
            dbStockAdjustment.setItemDescription(newInventory.getDescription());
        }
        if (stockAdjustment.getItemDescription() == null && newInventory.getDescription() == null) {
            if (itemDesc != null) {
                dbStockAdjustment.setItemDescription(itemDesc);
            }
        }

        if (inventoryQty != null) {
            dbStockAdjustment.setBeforeAdjustment(inventoryQty);
        }
        if (inventoryQty == null) {
            dbStockAdjustment.setBeforeAdjustment(0D);
        }
        dbStockAdjustment.setAfterAdjustment(newInventory.getInventoryQuantity());

        dbStockAdjustment.setPackBarcodes(newInventory.getPackBarcodes());
        dbStockAdjustment.setBranchName(newInventory.getPlantDescription());
        dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
        dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
        dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
        dbStockAdjustment.setReferenceField1(stockAdjustment.getRefDocType());
        dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
        dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
        dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
        dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
        dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
        dbStockAdjustment.setMiddlewareId(stockAdjustment.getMiddlewareId());
        dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
        dbStockAdjustment.setSaUpdatedOn(stockAdjustment.getUpdatedOn());
        dbStockAdjustment.setStockAdjustmentKey(System.currentTimeMillis());

        dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
        statusDescription = pickupLineRepository.getStatusDescription(88L, newInventory.getLanguageId());
        dbStockAdjustment.setStatusDescription(statusDescription);

        dbStockAdjustment.setDeletionIndicator(0L);
        dbStockAdjustment.setIsCompleted("Y");
        dbStockAdjustment.setCreatedOn(new Date());
        dbStockAdjustment.setCreatedBy("MW_AMS");

        StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
        log.info("createdStockAdjustment: " + createStockAdjustment);

        return createStockAdjustment;
    }
    public StockAdjustment autoUpdateStockAdjustment(com.tekclover.wms.api.outbound.transaction.model.warehouse.stockAdjustment.StockAdjustment stockAdjustment) throws java.text.ParseException {

        if (stockAdjustment.getIsCycleCount().equalsIgnoreCase("Y") && stockAdjustment.getIsDamage().equalsIgnoreCase("N")) {
            log.info("IsCycleCount: " + stockAdjustment.getIsCycleCount());
            PerpetualLineV2 perpetualLine = perpetualLineService.getPerpetualLineForStockAdjustmentV2(
                    stockAdjustment.getCompanyCode(),
                    stockAdjustment.getBranchCode(),
                    "EN",
                    stockAdjustment.getWarehouseId(),
                    stockAdjustment.getItemCode(),
                    stockAdjustment.getManufacturerName());
            log.info("Perpetual Line: " + perpetualLine);

            if (perpetualLine != null) {

                InventoryV2 dbInventory = inventoryService.getInventoryV2(
                        perpetualLine.getCompanyCodeId(),
                        perpetualLine.getPlantId(),
                        perpetualLine.getLanguageId(),
                        perpetualLine.getWarehouseId(),
                        perpetualLine.getPackBarcodes(),
                        perpetualLine.getItemCode(),
                        perpetualLine.getStorageBin(),
                        perpetualLine.getManufacturerName());
                log.info("Inventory: " + dbInventory);

                if (dbInventory != null) {
                    InventoryV2 newInventory = new InventoryV2();
                    BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
                    newInventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + stockAdjustment.getAdjustmentQty());
                    Double ALLOC_QTY = 0D;
                    if(dbInventory.getAllocatedQuantity() != null) {
                        ALLOC_QTY = dbInventory.getAllocatedQuantity();
                    }
                    newInventory.setReferenceField4(dbInventory.getInventoryQuantity() + ALLOC_QTY);
                    newInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
                    newInventory.setUpdatedOn(new Date());
                    inventoryV2Repository.save(newInventory);

                    //StockAdjustment Record Insert
                    StockAdjustment dbStockAdjustment = new StockAdjustment();
                    BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));

                    dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
                    dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
                    dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());

                    if (stockAdjustment.getItemDescription() != null) {
                        dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
                    }
                    if (stockAdjustment.getItemDescription() == null && dbInventory.getDescription() != null) {
                        dbStockAdjustment.setItemDescription(dbInventory.getDescription());
                    }
                    if (stockAdjustment.getItemDescription() == null && dbInventory.getDescription() == null) {
                        if (dbInventory.getReferenceField8() != null) {
                            dbStockAdjustment.setItemDescription(dbInventory.getReferenceField8());
                        }
                    }

                    dbStockAdjustment.setBeforeAdjustment(dbInventory.getInventoryQuantity());
                    dbStockAdjustment.setAfterAdjustment(newInventory.getInventoryQuantity());

                    dbStockAdjustment.setPackBarcodes(dbInventory.getPackBarcodes());
                    dbStockAdjustment.setBranchName(stockAdjustment.getBranchName());
                    dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
                    dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
                    dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
                    dbStockAdjustment.setReferenceField1(stockAdjustment.getRefDocType());
                    dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
                    dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
                    dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
                    dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
                    dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
                    dbStockAdjustment.setMiddlewareId(stockAdjustment.getMiddlewareId());
                    dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
                    dbStockAdjustment.setSaUpdatedOn(stockAdjustment.getUpdatedOn());
                    dbStockAdjustment.setStockAdjustmentKey(newInventory.getInventoryId());

                    dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
                    statusDescription = pickupLineRepository.getStatusDescription(88L, dbInventory.getLanguageId());
                    dbStockAdjustment.setStatusDescription(statusDescription);

                    dbStockAdjustment.setDeletionIndicator(0L);
                    dbStockAdjustment.setCreatedOn(new Date());
                    dbStockAdjustment.setIsCompleted("Y");
                    dbStockAdjustment.setCreatedBy("MW_AMS");

                    StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
                    log.info("createdStockAdjustment: " + createStockAdjustment);
                    return createStockAdjustment;
                }
            }
            if(perpetualLine == null) {
                throw new BadRequestException("No PerPetual Line Found for given ItemCode, ManufacturerName : " + stockAdjustment.getItemCode() + ", " + stockAdjustment.getManufacturerName());
            }
        }

        //Stock Adjustment Damage Code
        if (stockAdjustment.getIsDamage().equalsIgnoreCase("Y") && stockAdjustment.getIsCycleCount().equalsIgnoreCase("N")) {
            log.info("IsDamage: " + stockAdjustment.getIsDamage());

            InventoryV2 dbInventory = inventoryService.getInventoryForStockAdjustmentDamageV2(
                    stockAdjustment.getCompanyCode(),
                    stockAdjustment.getBranchCode(),
                    "EN",
                    stockAdjustment.getWarehouseId(),
                    stockAdjustment.getItemCode(),
                    "99999",
                    7L,
                    stockAdjustment.getManufacturerName());
            log.info("Inventory: " + dbInventory);

            InventoryV2 newInventory = new InventoryV2();

            if (dbInventory != null) {
                log.info("Inventory : " + dbInventory);
                BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
                newInventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + stockAdjustment.getAdjustmentQty());
                Double ALLOC_QTY = 0D;
                if(dbInventory.getAllocatedQuantity() != null) {
                    ALLOC_QTY = dbInventory.getAllocatedQuantity();
                }
                newInventory.setReferenceField4(dbInventory.getInventoryQuantity() + ALLOC_QTY);
            }
            if (dbInventory == null) {
                log.info("New Inventory-----> BinclassId 7 for this item is empty");
                newInventory.setCompanyCodeId(stockAdjustment.getCompanyCode());
                newInventory.setPlantId(stockAdjustment.getBranchCode());
                newInventory.setWarehouseId(stockAdjustment.getWarehouseId());
                newInventory.setLanguageId(LANG_ID);
                newInventory.setVariantCode(1L);                // VAR_ID
                newInventory.setVariantSubCode("1");            // VAR_SUB_ID
                newInventory.setStorageMethod("1");            // STR_MTD
                newInventory.setBatchSerialNumber("1");        // STR_NO
                newInventory.setDeletionIndicator(0L);
                newInventory.setBinClassId(7L);
                newInventory.setPackBarcodes("99999");

                newInventory.setItemCode(stockAdjustment.getItemCode());
                newInventory.setManufacturerCode(stockAdjustment.getManufacturerName());
                newInventory.setManufacturerName(stockAdjustment.getManufacturerName());

                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                storageBinPutAway.setCompanyCodeId(stockAdjustment.getCompanyCode());
                storageBinPutAway.setPlantId(stockAdjustment.getBranchCode());
                storageBinPutAway.setLanguageId(newInventory.getLanguageId());
                storageBinPutAway.setWarehouseId(stockAdjustment.getWarehouseId());
                storageBinPutAway.setBinClassId(7L);

                StorageBinV2 storageBin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                log.info("storageBin: " + storageBin);

                if (storageBin != null) {
                    newInventory.setReferenceField10(storageBin.getStorageSectionId());
                    newInventory.setReferenceField5(storageBin.getAisleNumber());
                    newInventory.setReferenceField6(storageBin.getShelfId());
                    newInventory.setReferenceField7(storageBin.getRowId());
                    newInventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                    newInventory.setStorageBin(storageBin.getStorageBin());
                }

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(stockAdjustment.getCompanyCode());
                imBasicData.setPlantId(stockAdjustment.getBranchCode());
                imBasicData.setLanguageId(newInventory.getLanguageId());
                imBasicData.setWarehouseId(stockAdjustment.getWarehouseId());
                imBasicData.setItemCode(stockAdjustment.getItemCode());
                imBasicData.setManufacturerName(stockAdjustment.getManufacturerName());
                ImBasicData1 dbItemCode = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + dbItemCode);

                if (dbItemCode != null) {
                    newInventory.setReferenceField8(dbItemCode.getDescription());
                    newInventory.setReferenceField9(dbItemCode.getManufacturerPartNo());
                    newInventory.setManufacturerCode(dbItemCode.getManufacturerPartNo());
                    newInventory.setDescription(dbItemCode.getDescription());
                }

                // STCK_TYP_ID
                newInventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(),
                        newInventory.getLanguageId(), stockAdjustment.getWarehouseId(), 1L);
                newInventory.setStockTypeDescription(stockTypeDesc);

                // SP_ST_IND_ID
                newInventory.setSpecialStockIndicatorId(1L);
                Double INV_QTY = 0D;
                if (stockAdjustment.getAdjustmentQty() >= 0) {
                    newInventory.setInventoryQuantity(stockAdjustment.getAdjustmentQty());
                    INV_QTY = stockAdjustment.getAdjustmentQty();
                }
                if (stockAdjustment.getAdjustmentQty() < 0) {
                    newInventory.setInventoryQuantity(0D);
                    INV_QTY = 0D;
                }

                Double ALLOC_QTY = 0D;
                newInventory.setAllocatedQuantity(ALLOC_QTY);
                newInventory.setReferenceField4(INV_QTY + ALLOC_QTY);
                newInventory.setCompanyDescription(stockAdjustment.getCompanyDescription());
                newInventory.setPlantDescription(stockAdjustment.getPlantDescription());
                newInventory.setWarehouseDescription(stockAdjustment.getWarehouseDescription());

                List<String> barcode = pickupLineRepository.getPartnerItemBarcode(stockAdjustment.getItemCode(),
                        stockAdjustment.getCompanyCode(),
                        stockAdjustment.getBranchCode(),
                        stockAdjustment.getWarehouseId(),
                        stockAdjustment.getManufacturerName(),
                        newInventory.getLanguageId());
                log.info("Barcode : " + barcode);

                if (barcode != null && !barcode.isEmpty())  {
                    newInventory.setBarcodeId(barcode.get(0));
                }

                newInventory.setCreatedOn(new Date());
            }

            newInventory.setUpdatedOn(new Date());
            newInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
            inventoryV2Repository.save(newInventory);

            //StockAdjustment Record Insert
            StockAdjustment dbStockAdjustment = new StockAdjustment();
            BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));

            dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
            dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
            dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());

            if (stockAdjustment.getItemDescription() != null) {
                dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
            }
            if (stockAdjustment.getItemDescription() == null && newInventory.getDescription() != null) {
                dbStockAdjustment.setItemDescription(newInventory.getDescription());
            }
            if (stockAdjustment.getItemDescription() == null && newInventory.getDescription() == null) {
                if (dbInventory.getReferenceField8() != null) {
                    dbStockAdjustment.setItemDescription(newInventory.getReferenceField8());
                }
            }

            if (dbInventory != null) {
                dbStockAdjustment.setBeforeAdjustment(dbInventory.getInventoryQuantity());
            }
            if (dbInventory == null) {
                dbStockAdjustment.setBeforeAdjustment(0D);
            }
            dbStockAdjustment.setAfterAdjustment(newInventory.getInventoryQuantity());

            dbStockAdjustment.setPackBarcodes(newInventory.getPackBarcodes());
            dbStockAdjustment.setBranchName(stockAdjustment.getBranchName());
            dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
            dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
            dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
            dbStockAdjustment.setReferenceField1(stockAdjustment.getRefDocType());
            dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
            dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
            dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
            dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
            dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
            dbStockAdjustment.setMiddlewareId(stockAdjustment.getMiddlewareId());
            dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
            dbStockAdjustment.setSaUpdatedOn(stockAdjustment.getUpdatedOn());
            dbStockAdjustment.setStockAdjustmentKey(System.currentTimeMillis());

            dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
            statusDescription = pickupLineRepository.getStatusDescription(88L, newInventory.getLanguageId());
            dbStockAdjustment.setStatusDescription(statusDescription);

            dbStockAdjustment.setDeletionIndicator(0L);
            dbStockAdjustment.setIsCompleted("Y");
            dbStockAdjustment.setCreatedOn(new Date());
            dbStockAdjustment.setCreatedBy("MW_AMS");

            StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
            log.info("createdStockAdjustment: " + createStockAdjustment);

            return createStockAdjustment;
        }
        throw new BadRequestException("Either IS_DAMAGE should be 'Y' or IS_CYCLE_COUNT should be 'Y'");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockAdjustmentKey
     * @param storageBin
     * @return
     */
    public List<StockAdjustment> getStockAdjustment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String itemCode, String manufacturerName, Long stockAdjustmentKey, String storageBin) {
        List<StockAdjustment> dbStockAdjustment =
                stockAdjustmentRepository.
                        findByLanguageIdAndCompanyCodeAndBranchCodeAndWarehouseIdAndItemCodeAndManufacturerNameAndStorageBinAndStockAdjustmentKeyAndDeletionIndicator(
                                languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, storageBin, stockAdjustmentKey, 0L);

        if (dbStockAdjustment != null) {

            log.info("StockAdjustment: " + dbStockAdjustment);

            return dbStockAdjustment;
        }
        throw new BadRequestException("The Given StockAdjustmentKey doesn't Exist: " + stockAdjustmentKey);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param itemCode
     * @param manufacturerName
     * @param stockAdjustmentKey
     * @param storageBin
     */
    public void deleteStockAdjustment(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserId,
                                      String itemCode, String manufacturerName, Long stockAdjustmentKey, String storageBin) {
        List<StockAdjustment> dbStockAdjustmentList =
                stockAdjustmentRepository.
                        findByLanguageIdAndCompanyCodeAndBranchCodeAndWarehouseIdAndItemCodeAndManufacturerNameAndStorageBinAndStockAdjustmentKeyAndDeletionIndicator(
                                languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, storageBin, stockAdjustmentKey, 0L);

        if (dbStockAdjustmentList != null) {

            for (StockAdjustment dbStockAdjustment : dbStockAdjustmentList) {

                log.info("StockAdjustment: " + dbStockAdjustment);

                dbStockAdjustment.setDeletionIndicator(1L);
                dbStockAdjustment.setUpdatedBy(loginUserId);
                dbStockAdjustment.setUpdatedOn(new Date());
                stockAdjustmentRepository.save(dbStockAdjustment);

            }
        }
        throw new BadRequestException("The Given StockAdjustmentId doesn't Exist: " + stockAdjustmentKey);
    }

    /**
     *
     * @param stockAdjustment
     * @param movementQtyValue
     */
    private void createInventoryMovementV2(StockAdjustment stockAdjustment, String movementQtyValue) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(stockAdjustment, inventoryMovement, CommonUtils.getNullPropertyNames(stockAdjustment));
        inventoryMovement.setCompanyCodeId(stockAdjustment.getCompanyCode());
        inventoryMovement.setPlantId(stockAdjustment.getBranchCode());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(4L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(1L);

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        inventoryMovement.setManufacturerName(stockAdjustment.getManufacturerName());
        inventoryMovement.setRefDocNumber(stockAdjustment.getAmsReferenceNo());
        inventoryMovement.setCompanyDescription(stockAdjustment.getCompanyDescription());
        inventoryMovement.setPlantDescription(stockAdjustment.getBranchName());
        inventoryMovement.setWarehouseDescription(stockAdjustment.getWarehouseDescription());
        inventoryMovement.setBarcodeId(stockAdjustment.getBarcodeId());
        inventoryMovement.setDescription(stockAdjustment.getItemDescription());

        // MVT_DOC_NO
//        inventoryMovement.setMovementDocumentNo(String.valueOf(stockAdjustment.getStockAdjustmentKey()));
        inventoryMovement.setReferenceField10(String.valueOf(stockAdjustment.getStockAdjustmentKey()));

        // ST_BIN
        inventoryMovement.setStorageBin(stockAdjustment.getStorageBin());

        // MVT_QTY
        inventoryMovement.setMovementQty(stockAdjustment.getAdjustmentQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue(movementQtyValue);

        // MVT_UOM
        inventoryMovement.setInventoryUom(stockAdjustment.getInventoryUom());

        // BAL_OH_QTY
        Double sumOfInvQty = inventoryService.getInventoryQtyCountForInvMmt(
                stockAdjustment.getCompanyCode(),
                stockAdjustment.getBranchCode(),
                stockAdjustment.getLanguageId(),
                stockAdjustment.getWarehouseId(),
                stockAdjustment.getManufacturerName(),
                stockAdjustment.getItemCode());
        log.info("BalanceOhQty: " + sumOfInvQty);
        if(sumOfInvQty != null) {
        inventoryMovement.setBalanceOHQty(sumOfInvQty);
            Double openQty = 0D;
            if(movementQtyValue.equalsIgnoreCase("P")) {
                openQty = sumOfInvQty - stockAdjustment.getAdjustmentQty();
            }
            if(movementQtyValue.equalsIgnoreCase("N")) {
                openQty = sumOfInvQty + stockAdjustment.getAdjustmentQty();
            }
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
        }
        if(sumOfInvQty == null) {
            inventoryMovement.setBalanceOHQty(0D);
            Double openQty = 0D;
            sumOfInvQty = 0D;
            if(movementQtyValue.equalsIgnoreCase("P")) {
                openQty = sumOfInvQty - stockAdjustment.getAdjustmentQty();
                if(openQty < 0){
                    openQty = 0D;
                }
            }
            if(movementQtyValue.equalsIgnoreCase("N")) {
                openQty = sumOfInvQty + stockAdjustment.getAdjustmentQty();
            }
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
        }

        inventoryMovement.setVariantCode(1L);
        inventoryMovement.setVariantSubCode("1");

        inventoryMovement.setPackBarcodes(stockAdjustment.getPackBarcodes());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(stockAdjustment.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(new Date());
        inventoryMovement.setMovementDocumentNo(String.valueOf(System.currentTimeMillis()));
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("inventoryMovement : " + inventoryMovement);
    }

    /**
     * This method is for Knowell and Namratha
     *
     * @param periodicLines
     * @param loginUserId
     */
    public void createStockAdjustmentV4(List<PeriodicLineV2> periodicLines, String loginUserId) throws Exception {
        try {
            String movementQtyValue = null;
            if (periodicLines != null && !periodicLines.isEmpty()) {
                AuthToken idMasterServiceAuthToken = authTokenService.getIDMasterServiceAuthToken();
                for (PeriodicLineV2 periodicLine : periodicLines) {
                    if (periodicLine.getVarianceQty() != 0) {
                        InventoryV2 dbInventory = inventoryService.getOutboundInventoryV4(
                                periodicLine.getCompanyCode(), periodicLine.getPlantId(), periodicLine.getLanguageId(),
                                periodicLine.getWarehouseId(), periodicLine.getItemCode(), periodicLine.getManufacturerName(),
                                periodicLine.getBarcodeId(), periodicLine.getStorageBin(), periodicLine.getAlternateUom());
                        log.info("Stock Adjustment Inventory: " + dbInventory);

                        if (dbInventory != null) {
                            InventoryV2 newInventory = new InventoryV2();
                            BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));

                            double[] inventoryQty = calculateStockAdjustmentInventory(periodicLine.getVarianceQty(), newInventory.getBagSize(),
                                    newInventory.getInventoryQuantity(), newInventory.getAllocatedQuantity());
                            if (inventoryQty != null && inventoryQty.length > 3) {
                                newInventory.setInventoryQuantity(inventoryQty[0]);
                                newInventory.setAllocatedQuantity(inventoryQty[1]);
                                newInventory.setReferenceField4(inventoryQty[2]);
                                newInventory.setNoBags(inventoryQty[3]);
                            }

                            inventoryV2Repository.save(newInventory);

                            //StockAdjustment Record Insert
                            StockAdjustment dbStockAdjustment = new StockAdjustment();
                            BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));

                            String lineNo = idmasterService.getNextNumberRange(28L, periodicLine.getWarehouseId(),
                                    periodicLine.getCompanyCode(), periodicLine.getPlantId(), periodicLine.getLanguageId(), idMasterServiceAuthToken.getAccess_token());

                            dbStockAdjustment.setStockAdjustmentId(Long.valueOf(lineNo));
                            dbStockAdjustment.setAdjustmentQty(periodicLine.getVarianceQty());
                            dbStockAdjustment.setBranchCode(periodicLine.getPlantId());
                            dbStockAdjustment.setItemDescription(periodicLine.getItemDesc());
                            dbStockAdjustment.setBeforeAdjustment(dbInventory.getReferenceField4());
                            dbStockAdjustment.setAfterAdjustment(newInventory.getReferenceField4());
                            dbStockAdjustment.setBranchName(newInventory.getPlantDescription());
                            dbStockAdjustment.setDateOfAdjustment(new Date());
                            dbStockAdjustment.setUnitOfMeasure(periodicLine.getInventoryUom());
                            dbStockAdjustment.setStockAdjustmentKey(newInventory.getInventoryId());

                            dbStockAdjustment.setCompanyCode(periodicLine.getCompanyCode());
                            dbStockAdjustment.setBranchCode(periodicLine.getPlantId());
                            dbStockAdjustment.setWarehouseId(periodicLine.getWarehouseId());

                            dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
                            statusDescription = getStatusDescription(88L, dbInventory.getLanguageId());
                            dbStockAdjustment.setStatusDescription(statusDescription);

                            dbStockAdjustment.setDeletionIndicator(0L);
                            dbStockAdjustment.setCreatedOn(new Date());
                            dbStockAdjustment.setIsCompleted("Y");
                            dbStockAdjustment.setCreatedBy(loginUserId);

                            StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
                            log.info("createdStockAdjustment: " + createStockAdjustment);
                            if (dbStockAdjustment.getAdjustmentQty() < 0) {
                                movementQtyValue = "N";
                            }
                            if (dbStockAdjustment.getAdjustmentQty() > 0) {
                                movementQtyValue = "P";
                            }
                            createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception while creating Stock Adjustment Create : " + e.toString());
            throw e;
        }
    }

    /**
     * This method is for Reefron
     *
     * @param periodicLines
     * @param loginUserId
     */
    public void createStockAdjustmentV5(List<PeriodicLineV2> periodicLines, String loginUserId) throws Exception {
        try {
            String movementQtyValue = null;
            if (periodicLines != null && !periodicLines.isEmpty()) {
                AuthToken idMasterServiceAuthToken = authTokenService.getIDMasterServiceAuthToken();
                for (PeriodicLineV2 periodicLine : periodicLines) {
                    if (periodicLine.getVarianceQty() != 0) {
                        InventoryV2 dbInventory = inventoryService.getOutboundInventoryV5(
                                periodicLine.getCompanyCode(), periodicLine.getPlantId(), periodicLine.getLanguageId(),
                                periodicLine.getWarehouseId(), periodicLine.getItemCode(), periodicLine.getManufacturerName(),
                                periodicLine.getBarcodeId(), periodicLine.getStorageBin(), periodicLine.getAlternateUom());
                        log.info("Stock Adjustment Inventory: " + dbInventory);

                        if (dbInventory != null) {
                            InventoryV2 newInventory = new InventoryV2();
                            BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));

                            double[] inventoryQty = calculateStockAdjustmentInventory(periodicLine.getVarianceQty(), newInventory.getBagSize(),
                                    newInventory.getInventoryQuantity(), newInventory.getAllocatedQuantity());
                            if (inventoryQty != null && inventoryQty.length > 3) {
                                newInventory.setInventoryQuantity(inventoryQty[0]);
                                newInventory.setAllocatedQuantity(inventoryQty[1]);
                                newInventory.setReferenceField4(inventoryQty[2]);
                                newInventory.setNoBags(inventoryQty[3]);
                            }

                            InventoryV2 inventorySource = inhouseTransferHeaderService.setAlternateUomQuantities(newInventory);
                            newInventory.setQtyInCase(inventorySource.getQtyInCase());
                            newInventory.setQtyInPiece(inventorySource.getQtyInPiece());
                            newInventory.setQtyInCreate(inventorySource.getQtyInCreate());
                            newInventory.setManufacturerCode(dbInventory.getManufacturerCode());
                            newInventory.setSelfLife(dbInventory.getSelfLife());
                            newInventory.setRemainingDays(dbInventory.getRemainingDays());
                            newInventory.setManufacturerDate(dbInventory.getManufacturerDate());
                            newInventory.setExpiryDate(dbInventory.getExpiryDate());
                            newInventory.setRemainingSelfLifePercentage(dbInventory.getRemainingSelfLifePercentage());

                            inventoryV2Repository.save(newInventory);

                            //StockAdjustment Record Insert
                            StockAdjustment dbStockAdjustment = new StockAdjustment();
                            BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));

                            String lineNo = idmasterService.getNextNumberRange(28L, periodicLine.getWarehouseId(),
                                    periodicLine.getCompanyCode(), periodicLine.getPlantId(), periodicLine.getLanguageId(), idMasterServiceAuthToken.getAccess_token());

                            dbStockAdjustment.setStockAdjustmentId(Long.valueOf(lineNo));
                            dbStockAdjustment.setAdjustmentQty(periodicLine.getVarianceQty());
                            dbStockAdjustment.setBranchCode(periodicLine.getPlantId());
                            dbStockAdjustment.setItemDescription(periodicLine.getItemDesc());
                            dbStockAdjustment.setBeforeAdjustment(dbInventory.getReferenceField4());
                            dbStockAdjustment.setAfterAdjustment(newInventory.getReferenceField4());
                            dbStockAdjustment.setBranchName(newInventory.getPlantDescription());
                            dbStockAdjustment.setDateOfAdjustment(new Date());
                            dbStockAdjustment.setUnitOfMeasure(periodicLine.getInventoryUom());
                            dbStockAdjustment.setStockAdjustmentKey(newInventory.getInventoryId());

                            dbStockAdjustment.setCompanyCode(periodicLine.getCompanyCode());
                            dbStockAdjustment.setBranchCode(periodicLine.getPlantId());
                            dbStockAdjustment.setWarehouseId(periodicLine.getWarehouseId());

                            dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
                            statusDescription = getStatusDescription(88L, dbInventory.getLanguageId());
                            dbStockAdjustment.setStatusDescription(statusDescription);

                            dbStockAdjustment.setDeletionIndicator(0L);
                            dbStockAdjustment.setCreatedOn(new Date());
                            dbStockAdjustment.setIsCompleted("Y");
                            dbStockAdjustment.setCreatedBy(loginUserId);

                            StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
                            log.info("createdStockAdjustment: " + createStockAdjustment);
                            if (dbStockAdjustment.getAdjustmentQty() < 0) {
                                movementQtyValue = "N";
                            }
                            if (dbStockAdjustment.getAdjustmentQty() > 0) {
                                movementQtyValue = "P";
                            }
                            createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception while creating Stock Adjustment Create : " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param varianceQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateStockAdjustmentInventory (Double varianceQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, TFR_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + varianceQty);
        log.info("BAG_SIZE : " + bagSize);
        bagSize = bagSize != null ? bagSize : 0;
        //double actualVarianceQty = getQuantity((varianceQty != null ? varianceQty : 0), bagSize);
        double actualVarianceQty = varianceQty != null ? varianceQty : 0.0;

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) + actualVarianceQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        //double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;
        double NO_OF_BAGS = 1.0;

        log.info("Tfr INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

}