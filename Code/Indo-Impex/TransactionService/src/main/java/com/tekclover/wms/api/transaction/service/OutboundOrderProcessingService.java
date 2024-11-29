package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.dto.BomHeader;
import com.tekclover.wms.api.transaction.model.dto.BomLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OutboundOrderProcessingService extends BaseService {

    @Autowired
    MastersService mastersService;

    @Autowired
    OrderService orderService;

    @Autowired
    PreOutboundHeaderService preOutboundHeaderService;

    @Autowired
    OutboundHeaderService outboundHeaderService;

    @Autowired
    OrderManagementLineService orderManagementLineService;

    //--------------------------------------------------------------------------------------------------------------
    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;

    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;

    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    WarehouseRepository warehouseRepository;

    //========================================================================V2====================================================================

    /**
     *
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public OutboundHeaderV2 processOutboundReceivedV4(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            /*
             * Checking whether received refDocNumber processed already.
             */
            log.info("Outbound Process Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + ", " + outboundIntegrationHeader.getOutboundOrderTypeID());
            if (outboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = outboundIntegrationHeader.getLoginUserId();
            }

            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
            String refDocNumber = outboundIntegrationHeader.getRefDocumentNo();

            Optional<PreOutboundHeaderV2> orderProcessedStatus =
                    preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                            refDocNumber, outboundIntegrationHeader.getOutboundOrderTypeID(), 0L);

            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + outboundIntegrationHeader.getRefDocumentNo() + " already processed. Reprocessing can't be allowed.");
            }

            String masterAuthToken = getMasterAuthToken();

            if (warehouseId == null) {
                try {
                    Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> warehouse =
                            warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), LANG_ID, 0L);
                    log.info("warehouse : " + warehouse);
                    if (warehouse != null && !warehouse.isEmpty()) {
                        log.info("warehouse : " + warehouse.get().getWarehouseId());
                        warehouseId = warehouse.get().getWarehouseId();
                    } else {
                        log.info("warehouse not found.");
                        throw new BadRequestException("Warehouse cannot be null.");
                    }
                } catch (Exception e) {
                    log.error("Warehouse fetch exception : " + e.toString());
                    throw e;
                }
            }

            if (outboundIntegrationHeader.getOutboundOrderTypeID() == 4 ||
                    (outboundIntegrationHeader.getReferenceDocumentType() != null && outboundIntegrationHeader.getReferenceDocumentType().equalsIgnoreCase("Sales Invoice"))) {
                OutboundHeaderV2 updateOutboundHeaderAndLine = outboundHeaderService.updateOutboundHeaderForSalesInvoice(outboundIntegrationHeader, warehouseId);
                log.info("SalesInvoice Updated in OutboundHeader and Line");
                if (updateOutboundHeaderAndLine == null) {
                    updateOutboundHeaderAndLine = new OutboundHeaderV2();
                }
                return updateOutboundHeaderAndLine;
            }

            // Getting PreOutboundNo from NumberRangeTable
            String preOutboundNo = preOutboundHeaderService.getPreOutboundNo(warehouseId, companyCodeId, plantId, languageId);
            String refField1ForOrderType = null;

            List<PreOutboundLineV2> overallCreatedPreoutboundLineList = new ArrayList<>();
            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                log.info("outboundIntegrationLine : " + outboundIntegrationLine);

                /*-------------Insertion of BOM item in preOutboundLine table---------------------------------------------------------*/
                /*
                 * Before Insertion into PREOUTBOUNDLINE table , validate the Below
                 * Pass the WH_ID/ITM_CODE as PAR_ITM_CODE into BOMHEADER table and validate the records,
                 * If record is not Null then fetch BOM_NO Pass BOM_NO in BOMITEM table and fetch CHL_ITM_CODE and
                 * CHL_ITM_QTY values and insert along with PAR_ITM_CODE in PREOUTBOUNDLINE table as below
                 * Insertion of CHL_ITM_CODE values
                 */
                BomHeader bomHeader = mastersService.getBomHeader(outboundIntegrationLine.getItemCode(), companyCodeId, plantId, languageId, warehouseId, masterAuthToken);
                if (bomHeader != null) {
                    BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), companyCodeId, plantId, languageId, warehouseId, masterAuthToken);
                    List<PreOutboundLineV2> toBeCreatedpreOutboundLineList = new ArrayList<>();
                    for (BomLine dbBomLine : bomLine) {
                        toBeCreatedpreOutboundLineList.add(preOutboundHeaderService.createPreOutboundLineBOMBasedV2(companyCodeId, plantId, languageId, preOutboundNo,
                                                                                                                    outboundIntegrationHeader, dbBomLine, outboundIntegrationLine, MW_AMS));
                    }

                    // Batch Insert - preOutboundLines
                    if (!toBeCreatedpreOutboundLineList.isEmpty()) {
                        List<PreOutboundLineV2> createdpreOutboundLine = preOutboundLineV2Repository.saveAll(toBeCreatedpreOutboundLineList);
                        log.info("createdpreOutboundLine [BOM] : " + createdpreOutboundLine);
                        overallCreatedPreoutboundLineList.addAll(createdpreOutboundLine);
                    }
                }
                refField1ForOrderType = outboundIntegrationLine.getRefField1ForOrderType();
            }

            /*
             * Inserting BOM Line records in OutboundLine and OrderManagementLine
             */
            if (!overallCreatedPreoutboundLineList.isEmpty()) {
                for (PreOutboundLineV2 preOutboundLine : overallCreatedPreoutboundLineList) {
//                 OrderManagementLine
                    OrderManagementLineV2 orderManagementLine = preOutboundHeaderService.createOrderManagementLineV2(companyCodeId, plantId, languageId,
                                                                                                                     preOutboundNo, outboundIntegrationHeader, preOutboundLine, MW_AMS);
                    log.info("orderManagementLine created---BOM---> : " + orderManagementLine);
                }

                /*------------------Record Insertion in OUTBOUNDLINE table--for BOM---------*/
                List<OutboundLineV2> createOutboundLineListForBOM = preOutboundHeaderService.createOutboundLineV2(overallCreatedPreoutboundLineList, outboundIntegrationHeader, MW_AMS);
                log.info("createOutboundLine created : " + createOutboundLineListForBOM);
            }

            /*
             * Append PREOUTBOUNDLINE table through below logic
             */
            List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                // PreOutboundLine
                try {
                    //=========================================================================================================//

                    PreOutboundLineV2 preOutboundLine = preOutboundHeaderService.createPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId,
                                                                                                         preOutboundNo, outboundIntegrationHeader, outboundIntegrationLine, MW_AMS);
                    PreOutboundLineV2 createdPreOutboundLine = preOutboundLineV2Repository.save(preOutboundLine);
                    log.info("preOutboundLine created---1---> : " + createdPreOutboundLine);
                    createdPreOutboundLineList.add(createdPreOutboundLine);

                } catch (Exception e) {
                    log.error("Error on processing PreOutboundLine : " + e.toString());
                    e.printStackTrace();
                }
            }

            preOutboundHeaderService.createOrderManagementLine(companyCodeId, plantId, languageId, preOutboundNo, outboundIntegrationHeader, createdPreOutboundLineList, MW_AMS);

            /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
            List<OutboundLineV2> createOutboundLineList = preOutboundHeaderService.createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader, MW_AMS);
            log.info("createOutboundLine created : " + createOutboundLineList);

            /*------------------Insert into PreOutboundHeader table-----------------------------*/
            PreOutboundHeaderV2 createdPreOutboundHeader = preOutboundHeaderService.createPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                                                                                                              preOutboundNo, outboundIntegrationHeader, refField1ForOrderType, MW_AMS);
            log.info("preOutboundHeader Created : " + createdPreOutboundHeader);

            /*------------------ORDERMANAGEMENTHEADER TABLE-------------------------------------*/
            OrderManagementHeaderV2 createdOrderManagementHeader = preOutboundHeaderService.createOrderManagementHeaderV2(createdPreOutboundHeader, MW_AMS);
            log.info("OrderMangementHeader Created : " + createdOrderManagementHeader);

            /*------------------Record Insertion in OUTBOUNDHEADER/OUTBOUNDLINE tables-----------*/
            OutboundHeaderV2 outboundHeader = preOutboundHeaderService.createOutboundHeaderV2(createdPreOutboundHeader, createdOrderManagementHeader.getStatusId(),
                                                                                              outboundIntegrationHeader, MW_AMS);

            //check the status of OrderManagementLine for NoStock update status of outbound header, preoutbound header, preoutboundline
            statusDescription = getStatusDescription(47L, languageId);
            orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 47L, statusDescription);
            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

            /*------------------------------------------------------------------------------------*/
            //Pickup Header Automation only for Picklist Header - OutboundOrderTypeId - 3L --> wh_id = 200 referenceDocumentType - Pick List
            //for wh_id = 200 ---> ob_type_id=3 && wh_id = 100 ---> ob_type_id=0,1,3
//            if ((warehouseId.equalsIgnoreCase("200") && (outboundIntegrationHeader.getOutboundOrderTypeID() == 3L ||
//                    outboundIntegrationHeader.getOutboundOrderTypeID().equals(OB_IPL_ORD_TYP_ID_SFG) || outboundIntegrationHeader.getOutboundOrderTypeID().equals(OB_IPL_ORD_TYP_ID_FG))) ||              //5L - IndusMegaFood
//                    (warehouseId.equalsIgnoreCase("100") && (outboundIntegrationHeader.getOutboundOrderTypeID() == 0L || outboundIntegrationHeader.getOutboundOrderTypeID() == 1L || outboundIntegrationHeader.getOutboundOrderTypeID() == 3L))) {
//                preOutboundHeaderService.createPickUpHeaderAssignPickerModified(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader,
//                                                       preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
//            }
            return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();

            // Updating the Processed Status
            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
            orderManagementLineService.rollback(outboundIntegrationHeader);
            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());

            throw e;
        }
    }

}