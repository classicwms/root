package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.PerpetualLineV2;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.PerpetualZeroStockLine;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.repository.*;
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
public class PerpetualZeroStkLineService extends BaseService{
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
    @Autowired
    private PerpetualZeroStkLineRepository perpetualZeroStkLineRepository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private PerpetualLineService perpetualLineService;

    public List<PerpetualLineV2> updatePerpetualZeroStkLine(List<PerpetualZeroStockLine> newPerpetualLines, String loginUserID) {
        List<PerpetualLineV2> newPerpetualLineList = new ArrayList<>();
        for (PerpetualZeroStockLine newPerpetualLine : newPerpetualLines) {
            PerpetualLineV2 dbPerpetualLine = new PerpetualLineV2();
            BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
            dbPerpetualLine.setStatusId(78L);
            dbPerpetualLine.setPackBarcodes("99999");
            newPerpetualLineList.add(dbPerpetualLine);

            try {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(newPerpetualLine, inventory, CommonUtils.getNullPropertyNames(newPerpetualLine));

                inventory.setCompanyCodeId(newPerpetualLine.getCompanyCodeId());

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);                // VAR_ID
                inventory.setVariantSubCode("1");            // VAR_SUB_ID
                inventory.setStorageMethod("1");            // STR_MTD
                inventory.setBatchSerialNumber("1");        // STR_NO
                inventory.setBatchSerialNumber(newPerpetualLine.getBatchSerialNumber());
                inventory.setStorageBin(newPerpetualLine.getStorageBin());
                inventory.setBarcodeId(newPerpetualLine.getBarcodeId());
                inventory.setManufacturerName(newPerpetualLine.getManufacturerName());

                    inventory.setReferenceField8(newPerpetualLine.getItemDesc());
                    inventory.setReferenceField9(newPerpetualLine.getManufacturerPartNo());
                    inventory.setManufacturerCode(newPerpetualLine.getManufacturerName());
                    inventory.setDescription(newPerpetualLine.getItemDesc());

                inventory.setBinClassId(Long.valueOf(newPerpetualLine.getReferenceField5()));
                inventory.setReferenceField5(newPerpetualLine.getReferenceField6());        //Aisle
                inventory.setReferenceField6(newPerpetualLine.getReferenceField7());        //shelf
                inventory.setReferenceField7(newPerpetualLine.getReferenceField8());        //row
                inventory.setReferenceField10(newPerpetualLine.getStorageSectionId());
                inventory.setLevelId(newPerpetualLine.getLevelId());

                inventory.setCompanyDescription(newPerpetualLine.getCompanyDescription());
                inventory.setPlantDescription(newPerpetualLine.getPlantDescription());
                inventory.setWarehouseDescription(newPerpetualLine.getWarehouseDescription());

                inventory.setPalletCode("99999");
                inventory.setCaseCode("99999");

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(newPerpetualLine.getCompanyCodeId(), newPerpetualLine.getPlantId(),
                        newPerpetualLine.getLanguageId(), newPerpetualLine.getWarehouseId(), 1L);
                inventory.setStockTypeDescription(stockTypeDesc);
                log.info("StockTypeDescription: " + stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

                inventory.setAllocatedQuantity(0D);
                // INV_QTY
                inventory.setInventoryQuantity(newPerpetualLine.getInventoryQuantity());
                inventory.setReferenceField4(newPerpetualLine.getInventoryQuantity());

                //packbarcode
                /*
                 * Hardcoding Packbarcode as 99999
                 */
                inventory.setPackBarcodes("99999");

                // INV_UOM
                inventory.setInventoryUom(newPerpetualLine.getInventoryUom());
                inventory.setCreatedBy(loginUserID);

                inventory.setReferenceDocumentNo(newPerpetualLine.getReferenceNo());
                inventory.setReferenceOrderNo(newPerpetualLine.getCycleCountNo());
                inventory.setDeletionIndicator(0L);
                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                 // inventory.Long.valueOf(System.currentTimeMillis() + "" + 5));
                InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
                log.info("created inventory : " + createdinventory);

            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("Error Creating Inventory");
            }

            //delete the line in zero stock
            newPerpetualLine.setDeletionIndicator(1L);
            newPerpetualLine.setConfirmedBy(loginUserID);
            newPerpetualLine.setConfirmedOn(new Date());
            perpetualZeroStkLineRepository.save(newPerpetualLine);
        }
        perpetualLineService.createPerpetualLineV2(newPerpetualLineList, loginUserID);
        log.info("New Inventory Created, PerpetualZeroStockLine deleted, New Perpetual Line Created");
        return newPerpetualLineList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param cycleCountNo
     * @return
     */
    public List<PerpetualZeroStockLine> getPerpetualZeroStockLine(String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo) {
        List<PerpetualZeroStockLine> perpetualZeroStockLineList = perpetualZeroStkLineRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, cycleCountNo, 0L);
        return perpetualZeroStockLineList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param cycleCountNo
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public PerpetualZeroStockLine getaPerpetualZeroStockLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                            String cycleCountNo, String itemCode, String manufacturerName) {
        PerpetualZeroStockLine perpetualZeroStockLine =
                perpetualZeroStkLineRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, cycleCountNo, itemCode, manufacturerName, 0L);
        if (perpetualZeroStockLine == null) {
            throw new BadRequestException("The given PerpetualZeroLine ID - "
                    + " warehouseId: " + warehouseId + ","
                    + "cycleCountNo: " + cycleCountNo + ","
                    + "itemCode: " + itemCode + ","
                    + "manufacturerName: " + manufacturerName + ","
                    + " doesn't exist.");
        }
        return perpetualZeroStockLine;
    }
}