package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundOrderProcess;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class OrderProcessingService extends BaseService {

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    OrderService orderService;
    //--------------------------------------------------------------------------------------------------------------
    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    InboundIntegrationLogRepository inboundIntegrationLogRepository;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    //========================================================================V2====================================================================

    /**
     *
     * @param inboundOrderProcess
     * @return
     * @throws Exception
     */
    public InboundHeaderV2 postInboundReceived(InboundOrderProcess inboundOrderProcess) throws Exception {

        String refDocNumber = inboundOrderProcess.getRefDocNumber();
        Long inboundOrderTypeId = inboundOrderProcess.getInboundOrderTypeId();
        String companyCodeId = inboundOrderProcess.getCompanyCodeId();
        String plantId = inboundOrderProcess.getPlantId();
        String languageId = inboundOrderProcess.getLanguageId() != null ? inboundOrderProcess.getLanguageId() : LANG_ID;
        String warehouseId = inboundOrderProcess.getWarehouseId();
        InboundHeaderV2 createdInboundHeader = null;
        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);
        log.info("Inbound Order Save Process Initiated ------> " + refDocNumber + ", " + inboundOrderTypeId);

        try {

            //Lines
            if(inboundOrderProcess.getInboundLines() != null && !inboundOrderProcess.getInboundLines().isEmpty()) {
                inboundLineV2Repository.saveAll(inboundOrderProcess.getInboundLines());
            }
            if(inboundOrderProcess.getPreInboundLines() != null && !inboundOrderProcess.getPreInboundLines().isEmpty()) {
                preInboundLineV2Repository.saveAll(inboundOrderProcess.getPreInboundLines());
            }
            if(inboundOrderProcess.getStagingLines() != null && !inboundOrderProcess.getStagingLines().isEmpty()) {
                stagingLineV2Repository.saveAll(inboundOrderProcess.getStagingLines());
            }

            //Header
            if(inboundOrderProcess.getStagingHeader() != null) {
                stagingHeaderV2Repository.save(inboundOrderProcess.getStagingHeader());
            }
            if(inboundOrderProcess.getGrHeader() != null) {
                grHeaderV2Repository.save(inboundOrderProcess.getGrHeader());
            }
            if(inboundOrderProcess.getInboundHeader() != null) {
                createdInboundHeader = inboundHeaderV2Repository.save(inboundOrderProcess.getInboundHeader());
            }
            if(inboundOrderProcess.getPreInboundHeader() != null) {
                preInboundHeaderV2Repository.save(inboundOrderProcess.getPreInboundHeader());
            }
            if(inboundOrderProcess.getInboundIntegrationLog() != null) {
                inboundIntegrationLogRepository.save(inboundOrderProcess.getInboundIntegrationLog());
            }
            log.info("Inbound Order Save Process Completed ------> " + refDocNumber + ", " + inboundOrderTypeId);

            return createdInboundHeader;

        } catch (Exception e) {
            createInboundIntegrationLogV2(inboundOrderProcess.getInboundIntegrationHeader(), inboundOrderProcess.getLoginUserId());
            log.error("Inbound Order Processing Exception ----> ");
            e.printStackTrace();

            // Updating the Processed Status
            log.info("Inbound Rollback Initiated...!" + refDocNumber);

            preInboundHeaderService.initiateInboundRollBack(companyCodeId, plantId, languageId, warehouseId, refDocNumber, inboundOrderTypeId);
            orderService.updateProcessedIbOrderV2(refDocNumber, inboundOrderTypeId);
            log.info("Inbound Rollback Finished...!" + refDocNumber);
            throw e;
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param inboundOrderTypeId
     * @param inboundOrderProcess
     * @return
     * @throws Exception
     */
    public InboundHeaderV2 postInboundReceived(String companyCodeId, String plantId, String languageId, String warehouseId,
                                               String refDocNumber, String preInboundNo, Long inboundOrderTypeId,
                                               InboundOrderProcess inboundOrderProcess) throws Exception {

        InboundHeaderV2 createdInboundHeader = new InboundHeaderV2();
        log.info("Inbound Order Save Process Initiated ------> " + refDocNumber + "|" + inboundOrderTypeId + "|" + companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId);

        try {
             //Checking whether received refDocNumber processed already.
            boolean orderProcessedStatus = preInboundHeaderV2Repository.existsByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundOrderTypeId, 0L);
            if (!orderProcessedStatus) {
                //Lines
                if (inboundOrderProcess.getInboundLines() != null && !inboundOrderProcess.getInboundLines().isEmpty()) {
                    inboundLineV2Repository.saveAll(inboundOrderProcess.getInboundLines());
                }
                if (inboundOrderProcess.getPreInboundLines() != null && !inboundOrderProcess.getPreInboundLines().isEmpty()) {
                    preInboundLineV2Repository.saveAll(inboundOrderProcess.getPreInboundLines());
                }
                if (inboundOrderProcess.getStagingLines() != null && !inboundOrderProcess.getStagingLines().isEmpty()) {
                    stagingLineV2Repository.saveAll(inboundOrderProcess.getStagingLines());
                }

                //Header
                if (inboundOrderProcess.getStagingHeader() != null) {
                    stagingHeaderV2Repository.save(inboundOrderProcess.getStagingHeader());
                }
                if (inboundOrderProcess.getGrHeader() != null) {
                    grHeaderV2Repository.save(inboundOrderProcess.getGrHeader());
                }
                if (inboundOrderProcess.getInboundHeader() != null) {
                    createdInboundHeader = inboundHeaderV2Repository.save(inboundOrderProcess.getInboundHeader());
                }
                if (inboundOrderProcess.getPreInboundHeader() != null) {
                    preInboundHeaderV2Repository.save(inboundOrderProcess.getPreInboundHeader());
                }
                if (inboundOrderProcess.getInboundIntegrationLog() != null) {
                    inboundIntegrationLogRepository.save(inboundOrderProcess.getInboundIntegrationLog());
                }

                log.info("Inbound Order Save Process Completed ------> " + refDocNumber + ", " + inboundOrderTypeId);
                stagingLineV2Repository.updateStagingLineInvQtyUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
                log.info("StagingLine InvQty Update using Stored Procedure Finished");
            }
            return createdInboundHeader;

        } catch (Exception e) {
            createInboundIntegrationLogV2(inboundOrderProcess.getInboundIntegrationHeader(), inboundOrderProcess.getLoginUserId());
            log.error("Inbound Order Processing Exception ----> ");
            e.printStackTrace();

            // Updating the Processed Status
            log.info("Inbound Rollback Initiated...!" + refDocNumber);
            preInboundHeaderService.initiateInboundRollBack(companyCodeId, plantId, languageId, warehouseId, refDocNumber, inboundOrderTypeId);
            orderService.updateProcessedIbOrderV2(refDocNumber, inboundOrderTypeId);
            log.info("Inbound Rollback Finished...!" + refDocNumber);
            throw e;
        }
    }

    /**
     * @param inbound
     * @param MW_AMS
     * @return
     * @throws Exception
     */
    public void createInboundIntegrationLogV2(InboundIntegrationHeader inbound, String MW_AMS) throws Exception {
        try {
            InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
            BeanUtils.copyProperties(inbound, dbInboundIntegrationLog, CommonUtils.getNullPropertyNames(inbound));
            dbInboundIntegrationLog.setCompanyCodeId(inbound.getCompanyCode());
            dbInboundIntegrationLog.setPlantId(inbound.getBranchCode());
            dbInboundIntegrationLog.setLanguageId(inbound.getLanguageId());
            dbInboundIntegrationLog.setWarehouseId(inbound.getWarehouseID());
            dbInboundIntegrationLog.setIntegrationLogNumber(inbound.getRefDocumentNo());
            dbInboundIntegrationLog.setRefDocNumber(inbound.getRefDocumentNo());
            dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
            dbInboundIntegrationLog.setIntegrationStatus("FAILED");
            dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
            dbInboundIntegrationLog.setDeletionIndicator(0L);
            dbInboundIntegrationLog.setCreatedBy(MW_AMS);

            dbInboundIntegrationLog.setCreatedOn(new Date());
            dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
            log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
        } catch (Exception e) {
            log.error("Inbound Integration Log Create Exception : " + e.toString());
            throw e;
        }
    }
}