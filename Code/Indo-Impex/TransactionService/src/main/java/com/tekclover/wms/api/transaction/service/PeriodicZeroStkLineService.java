package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicLineV2;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicZeroStockLine;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.repository.PeriodicZeroStkLineRepository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PeriodicZeroStkLineService extends BaseService {

    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    private PeriodicZeroStkLineRepository periodicZeroStkLineRepository;

    @Autowired
    private PeriodicLineService periodicLineService;

    /**
     *
     * @param periodicZeroStockLines
     * @param loginUserID
     * @return
     */
    public List<PeriodicLineV2> updatePeriodicZeroStkLine(List<PeriodicZeroStockLine> periodicZeroStockLines, String loginUserID) {
        List<PeriodicLineV2> newPeriodicLineList = new ArrayList<>();
        for (PeriodicZeroStockLine newPeriodicLine : periodicZeroStockLines) {
            PeriodicLineV2 dbPeriodicLine = new PeriodicLineV2();
            BeanUtils.copyProperties(newPeriodicLine, dbPeriodicLine, CommonUtils.getNullPropertyNames(newPeriodicLine));
            dbPeriodicLine.setStatusId(78L);
            newPeriodicLineList.add(dbPeriodicLine);

            try {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(newPeriodicLine, inventory, CommonUtils.getNullPropertyNames(newPeriodicLine));

                inventory.setCompanyCodeId(newPeriodicLine.getCompanyCode());

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);                // VAR_ID
                inventory.setVariantSubCode("1");            // VAR_SUB_ID
                inventory.setStorageMethod("1");            // STR_MTD
                inventory.setBatchSerialNumber("1");        // STR_NO
                inventory.setBatchSerialNumber(newPeriodicLine.getBatchSerialNumber());
                inventory.setStorageBin(newPeriodicLine.getStorageBin());
                inventory.setBarcodeId(newPeriodicLine.getBarcodeId());
                inventory.setManufacturerName(newPeriodicLine.getManufacturerName());

                inventory.setReferenceField8(newPeriodicLine.getItemDesc());
                inventory.setReferenceField9(newPeriodicLine.getManufacturerPartNo());
                inventory.setManufacturerCode(newPeriodicLine.getManufacturerName());
                inventory.setDescription(newPeriodicLine.getItemDesc());

                inventory.setBinClassId(Long.valueOf(newPeriodicLine.getReferenceField5()));
                inventory.setReferenceField5(newPeriodicLine.getReferenceField6());        //Aisle
                inventory.setReferenceField6(newPeriodicLine.getReferenceField7());        //shelf
                inventory.setReferenceField7(newPeriodicLine.getReferenceField8());        //row
                inventory.setReferenceField10(newPeriodicLine.getStorageSectionId());
                inventory.setLevelId(newPeriodicLine.getLevelId());

                inventory.setCompanyDescription(newPeriodicLine.getCompanyDescription());
                inventory.setPlantDescription(newPeriodicLine.getPlantDescription());
                inventory.setWarehouseDescription(newPeriodicLine.getWarehouseDescription());

                inventory.setPalletCode("99999");
                inventory.setCaseCode("99999");

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(newPeriodicLine.getCompanyCode(), newPeriodicLine.getPlantId(),
                                                        newPeriodicLine.getLanguageId(), newPeriodicLine.getWarehouseId(), 1L);
                inventory.setStockTypeDescription(stockTypeDesc);
                log.info("StockTypeDescription: " + stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

                inventory.setAllocatedQuantity(0D);
                // INV_QTY
                inventory.setInventoryQuantity(round(newPeriodicLine.getInventoryQuantity()));
                inventory.setReferenceField4(round(newPeriodicLine.getInventoryQuantity()));

                //packbarcode
                /*
                 * Hardcoding Packbarcode as 99999
                 */
                inventory.setPackBarcodes("99999");

                // INV_UOM
                inventory.setInventoryUom(newPeriodicLine.getInventoryUom());
                inventory.setCreatedBy(loginUserID);

                inventory.setReferenceDocumentNo(newPeriodicLine.getReferenceNo());
                inventory.setReferenceOrderNo(newPeriodicLine.getCycleCountNo());
                inventory.setDeletionIndicator(0L);
                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
//                inventory.setInventoryId(System.currentTimeMillis());
                InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
                log.info("created inventory : " + createdinventory);

            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("Error Creating Inventory");
            }

            //delete the line in zero stock
            newPeriodicLine.setDeletionIndicator(1L);
            newPeriodicLine.setConfirmedBy(loginUserID);
            newPeriodicLine.setConfirmedOn(new Date());
            periodicZeroStkLineRepository.save(newPeriodicLine);
        }
        periodicLineService.createPeriodicLineV2(newPeriodicLineList, loginUserID);
        log.info("New Inventory Created, PeriodicZeroStockLine deleted, New Periodic Line Created");
        return newPeriodicLineList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param cycleCountNo
     * @return
     */
    public List<PeriodicZeroStockLine> getPeriodicZeroStockLine(String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo) {
        List<PeriodicZeroStockLine> periodicZeroStockLineList = periodicZeroStkLineRepository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, cycleCountNo, 0L);
        return periodicZeroStockLineList;
    }
}