package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Service
@Slf4j
public class OrderReversalService extends BaseService {

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    GrLineV2Repository grLineV2Repository;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    public void pgiOrderReversal(InboundIntegrationHeader inbound) {

        /* PGI Reversal Reversing the status of,
           1. PreInbound Header
           2. PreInbound Line
           3. Staging Header
           4. Staging Line
           5. Gr Header
           6. Gr Line
           7. Inbound Header
           8. Inbound Line  */

        //Getting Reversal Status ====> 1L
        Long STATUS_ID = 1L;
        String reversalStatusDesc = stagingLineV2Repository.getReversalDesc(STATUS_ID);

        log.info("InboundIntegrationHeader data --------> {}", inbound);

        // Updating PreInboundLine and Header Started
        log.info("Updating PreInboundLine and Header Started....");
        AtomicInteger preInboundLineCount = new AtomicInteger(0);

        // Getting PreInboundLine
        List<PreInboundLineEntityV2> existedPreInboundLines = preInboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                inbound.getLanguageId(), inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getWarehouseID(), inbound.getRefDocumentNo(), 0L
        );
        log.info("Already Existed PreInboundLines ----------> {}", existedPreInboundLines);
        log.info("Existing preInboundLines count ----> {}", existedPreInboundLines.size());

        // Updating Staging and Header Started
        log.info("Updating Staging and Header Started....");
        AtomicInteger stagingLineCount = new AtomicInteger(0);

        //Getting Staging
        List<StagingLineEntityV2> existedStagingLines = stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                inbound.getLanguageId(), inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getWarehouseID(), inbound.getRefDocumentNo(), 0L
        );
        log.info("Already Existed StagingLines ----------> {}", existedStagingLines);
        log.info("Existing StagingLines count ----> {}", existedStagingLines.size());

        // Updating InboundLine and Header Started
        log.info("Updating InboundLine and Header Started....");
        AtomicInteger inboundLineCount = new AtomicInteger(0);

        //Getting InboundLines
        List<InboundLineV2> existedInbound = inboundLineV2Repository.findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                inbound.getCompanyCode(), inbound.getLanguageId(), inbound.getBranchCode(), inbound.getWarehouseID(), inbound.getRefDocumentNo(), 0L
        );
        log.info("Already Existed InboundLines ------> {}", existedInbound);
        log.info("Existing InboundLines count ------> {}", existedInbound.size());

        //Updating Reverse Status for PreInboundLine Table
        inbound.getInboundIntegrationLine().stream().forEach(ibLine -> {
//            PreInboundLineEntityV2 dbPreInboundEntity = preInboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndLineNoAndDeletionIndicator(
//                    inbound.getLanguageId(), inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getWarehouseID(), inbound.getRefDocumentNo(), inbound.getPreInboundNo(), ibLine.getItemCode(), ibLine.getLineReference(), 0L
//            );

            log.info("Line Inputs: companyCodeId -----> " + inbound.getCompanyCode() + ", plantId -----> " + inbound.getBranchCode() + "," +
                    " warehouseId ------> " + inbound.getWarehouseID() + ", refDocumentNo ------> " + inbound.getRefDocumentNo() +
                    ", itemCode ------> " + ibLine.getItemCode() + ", lineReference -------> " + ibLine.getLineReference() + "," +
                    " statusId ------> " + STATUS_ID + ", statusDescription -------> " + reversalStatusDesc);

            log.info("PreInboundLine PGI Reversal update Started...");
            preInboundLineV2Repository.updatePreInboundLine(inbound.getCompanyCode(), inbound.getBranchCode(),
                    inbound.getWarehouseID(), inbound.getRefDocumentNo(), ibLine.getItemCode(),
                    ibLine.getLineReference(), STATUS_ID, reversalStatusDesc);
            log.info("PreInboundLine PGI Reversal update Completed...");
            preInboundLineCount.incrementAndGet();

//            StagingLineEntityV2 dbStagingLine = stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndLineNoAndDeletionIndicator(
//                    inbound.getLanguageId(), inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getWarehouseID(), inbound.getRefDocumentNo(), inbound.getPreInboundNo(), ibLine.getItemCode(), ibLine.getLineReference(), 0L
//            );

            log.info("Staging Line PGI Reversal update Started...");
            stagingLineV2Repository.updateStagingLine(inbound.getCompanyCode(), inbound.getBranchCode(),
                    inbound.getWarehouseID(), inbound.getRefDocumentNo(), ibLine.getItemCode(),
                    ibLine.getLineReference(), STATUS_ID, reversalStatusDesc);
            log.info("StagingLine PGI Reversal update Completed...");
            stagingLineCount.incrementAndGet();


            log.info("InboundLine PGI Reversal update started....");
            inboundLineV2Repository.updateInboundLine(inbound.getCompanyCode(), inbound.getBranchCode(),
                    inbound.getWarehouseID(), inbound.getRefDocumentNo(), ibLine.getItemCode(),
                    ibLine.getLineReference(), STATUS_ID, reversalStatusDesc);
            log.info("InboundLine PGI Reversal update Completed...");
            inboundLineCount.incrementAndGet();

            // Updating Inventory
//            log.info("Inventory update for Reversal process started...");
//            inventoryV2Repository.updateInventoryForPGI(inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getWarehouseID(),
//                    ibLine.getItemCode());
//            log.info("Inventory update for Reversal process completed...");
        });
        log.info("Total updated preInboundLine count -----> {}", preInboundLineCount);
        log.info("Total updated stagingLine count ------> {}", stagingLineCount);
        log.info("Total updated inbound count -------> {}", inboundLineCount);

        if (preInboundLineCount.get() == existedPreInboundLines.size()) {
            log.info("PreInboundHeader Reversal update condition satisfied...");
            log.info("PreInboundHeader Reversal update Started.......");
            preInboundHeaderV2Repository.updatePreInboundHeaderEntityStatus(inbound.getWarehouseID(), inbound.getCompanyCode(), inbound.getBranchCode(),
                    inbound.getLanguageId(), inbound.getRefDocumentNo(), STATUS_ID, reversalStatusDesc);
            log.info("PreInboundHeader Reversal update Completed.......");

            log.info("All PreInbound tables updated to Reversal status.....");
        } else {
            log.info("PreInboundLines partial Updated to Reversal status coz existing PreinboundLines count is not equal to incoming lines count");
        }

        if (inboundLineCount.get() == existedInbound.size()) {
            log.info("InboundHeader Reversal update condition satisfied...");
            log.info("InboundHeader Reversal update Started.......");
            inboundHeaderV2Repository.updateInboundHeaderStatusPGI(inbound.getWarehouseID(), inbound.getCompanyCode(), inbound.getBranchCode(),
                    inbound.getLanguageId(), inbound.getRefDocumentNo(), STATUS_ID, reversalStatusDesc);
            log.info("InboundHeader Reversal update Completed.......");

            log.info("All Inbound tables updated to Reversal status.....");
        } else {
            log.info("InboundLines partial Updated to Reversal status coz existing InboundLines count is not equal to incoming lines count");
        }

        if (stagingLineCount.get() == existedStagingLines.size()) {
            log.info("StagingHeader and GrHeader Reversal update conditon satisfied...");
            log.info("StagingHeader and GrHeader Reversal update Started......");
            stagingHeaderV2Repository.updateStagingHeaderStatus(inbound.getWarehouseID(), inbound.getCompanyCode(), inbound.getBranchCode(),
                    inbound.getLanguageId(), inbound.getRefDocumentNo(), STATUS_ID, reversalStatusDesc);
            grHeaderV2Repository.updateGrHeaderStatus(inbound.getWarehouseID(), inbound.getCompanyCode(), inbound.getBranchCode(),
                    inbound.getLanguageId(), inbound.getRefDocumentNo(), STATUS_ID, reversalStatusDesc);
            log.info("StagingHeader and GrHeader Reversal update Completed.....");

            log.info("All Staging tables updated to Reversal status.....");
        } else {
            log.info("StagingLines partial Updated to Reversal status coz existing StagingLines count is not equal to incoming lines count");
        }

        log.info("Deleting IbOrder and Line tabel for reversal flag!!");
        inboundLineV2Repository.deleteByOrderIdAndReversalFlag(inbound.getRefDocumentNo());
        inboundLineV2Repository.deleteByRefDocumentNoAndReversalFlag(inbound.getRefDocumentNo());
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param loginUserId
     * @return
     */
    public boolean deleteInventoryForGrReversal(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String itemCode, String manufacturerName, Long binClassId, String loginUserId) {
        try {
            List<InventoryV2> inventoryList = inventoryV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, 0L);
            log.info("inventoryList for Delete: " + inventoryList);
            if (inventoryList != null) {
                for (InventoryV2 inventory : inventoryList) {
                    inventory.setDeletionIndicator(1L);
                    inventory.setUpdatedOn(new Date());
                    inventory.setUpdatedBy(loginUserId);
                    inventoryV2Repository.save(inventory);
                    log.info("inventory deleted.");
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new EntityNotFoundException("Error in deleting Id: " + e.toString());
        }
    }
}
