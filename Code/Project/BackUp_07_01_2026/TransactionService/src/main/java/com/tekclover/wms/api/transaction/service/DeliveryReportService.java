package com.tekclover.wms.api.transaction.service;


import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.BusinessPartner;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.report.ShipmentDeliverySummary;
import com.tekclover.wms.api.transaction.model.report.ShipmentDeliverySummaryReport;
import com.tekclover.wms.api.transaction.model.report.SummaryMetrics;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.ShipmentDeliverySummaryRepository;
import com.tekclover.wms.api.transaction.repository.ShipmentReportRepository;
import com.tekclover.wms.api.transaction.repository.SummaryMetricsRepository;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DeliveryReportService {

    @Autowired
    OutboundHeaderRepository outboundHeaderRepository;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    MastersService mastersService;

    @Autowired
    OutboundLineService outboundLineService;

    @Autowired
    PickupLineService pickupLineService;

    @Autowired
    ReportsService reportsService;

    @Autowired
    ShipmentDeliverySummaryRepository shipmentDeliverySummaryRepository;

    @Autowired
    SummaryMetricsRepository summaryMetricsRepository;

    @Autowired
    ShipmentReportRepository shipmentReportRepository;


    @Async("asyncTaskExecutor")
    public void getShipmentDeliverySummaryReportV3(String fromDeliveryDate, String toDeliveryDate,
                                                   List<String> customerCode, String warehouseIds, Long referenceId) throws ParseException, java.text.ParseException {
        /*
         * Pass the Input Parameters in Outbound Line table (From and TO date in
         * DLV_CNF_ON fields) and fetch the below Fields, If Customer Code is Selected
         * all, Pass all the values into OUTBOUNDLINE table
         */
        // Date range
        if (fromDeliveryDate == null || toDeliveryDate == null) {
            throw new BadRequestException("DeliveryDate can't be blank.");
        }

        Date fromDeliveryDate_d = null;
        Date toDeliveryDate_d = null;
        try {
            log.info("Input Date: " + fromDeliveryDate + "," + toDeliveryDate);
            if (fromDeliveryDate.indexOf("T") > 0) {
                fromDeliveryDate_d = DateUtils.convertStringToDateWithT(fromDeliveryDate);
                toDeliveryDate_d = DateUtils.convertStringToDateWithT(toDeliveryDate);
            } else {
                fromDeliveryDate_d = DateUtils.addTimeToDate(fromDeliveryDate, 14, 0, 0);
                toDeliveryDate_d = DateUtils.addTimeToDate(toDeliveryDate, 13, 59, 59);
            }
            log.info("Date: " + fromDeliveryDate_d + "," + toDeliveryDate_d);
        } catch (Exception e) {
            throw new BadRequestException("Date should be in yyyy-MM-dd format.");
        }

        List<OutboundHeader> outboundHeaderList = outboundHeaderRepository
                .findByWarehouseIdAndStatusIdAndDeliveryConfirmedOnBetween(warehouseIds, 59L, fromDeliveryDate_d, toDeliveryDate_d);
//        ShipmentDeliverySummaryReport shipmentDeliverySummaryReport = new ShipmentDeliverySummaryReport();
        List<ShipmentDeliverySummary> shipmentDeliverySummaryList = new ArrayList<>();
        String languageId = null;
        String companyCode = null;
        String plantId = null;
        String warehouseId = null;

        try {
            double sumOfLineItems = 0.0;
            Set<String> partnerCodes = new HashSet<>();
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            for (OutboundHeader outboundHeader : outboundHeaderList) {
                languageId = outboundHeader.getLanguageId();
                companyCode = outboundHeader.getCompanyCodeId();
                plantId = outboundHeader.getPlantId();
                warehouseId = outboundHeader.getWarehouseId();
                partnerCodes.add(outboundHeader.getPartnerCode());

                // Report Preparation
                ShipmentDeliverySummary shipmentDeliverySummary = new ShipmentDeliverySummary();

                shipmentDeliverySummary.setReferenceId(referenceId);
                shipmentDeliverySummary.setSo(outboundHeader.getRefDocNumber());                            // SO
                shipmentDeliverySummary.setExpectedDeliveryDate(outboundHeader.getRequiredDeliveryDate());    // DEL_DATE
                shipmentDeliverySummary.setDeliveryDateTime(outboundHeader.getDeliveryConfirmedOn());        // DLV_CNF_ON
                shipmentDeliverySummary.setBranchCode(outboundHeader.getPartnerCode());                    // PARTNER_CODE/PARTNER_NM
                BusinessPartner dbBusinessPartner = mastersService.getBusinessPartner(outboundHeader.getPartnerCode(),
                        authTokenForMastersService.getAccess_token());
                shipmentDeliverySummary.setBranchDesc(dbBusinessPartner.getPartnerName());

                shipmentDeliverySummary.setOrderType(outboundHeader.getReferenceField1());

                // Line Ordered
                List<Long> countOfOrderedLines = outboundLineService.getCountofOrderedLines(
                        outboundHeader.getWarehouseId(), outboundHeader.getPreOutboundNo(),
                        outboundHeader.getRefDocNumber());

                // Line Shipped
                List<Long> deliveryLines = outboundLineService.getDeliveryLines(outboundHeader.getWarehouseId(),
                        outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());

                // Ordered Qty
                List<Long> sumOfOrderedQty = outboundLineService.getSumOfOrderedQty(outboundHeader.getWarehouseId(),
                        outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());

                // Shipped Qty
                List<Long> sumOfDeliveryQtyList = outboundLineService.getDeliveryQty(outboundHeader.getWarehouseId(),
                        outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());

                double pickupLineCount = pickupLineService.getPickupLineCount(outboundHeader.getWarehouseId(), outboundHeader.getPreOutboundNo(),
                        Arrays.asList(outboundHeader.getRefDocNumber()));
                double countOfOrderedLinesvalue = countOfOrderedLines.stream().mapToLong(Long::longValue).sum();
                double deliveryLinesCount = deliveryLines.stream().mapToLong(Long::longValue).sum();
                double sumOfOrderedQtyValue = sumOfOrderedQty.stream().mapToLong(Long::longValue).sum();
                double sumOfDeliveryQty = sumOfDeliveryQtyList.stream().mapToLong(Long::longValue).sum();

                sumOfLineItems += countOfOrderedLinesvalue;
                shipmentDeliverySummary.setLineOrdered(countOfOrderedLinesvalue);
                shipmentDeliverySummary.setLineShipped(deliveryLinesCount);
                shipmentDeliverySummary.setOrderedQty(sumOfOrderedQtyValue);
                shipmentDeliverySummary.setShippedQty(sumOfDeliveryQty);
                shipmentDeliverySummary.setPickedQty(pickupLineCount);

                // % Shipped - Divide (Shipped lines/Ordered Lines)*100
                double percShipped = Math.round((deliveryLinesCount / countOfOrderedLinesvalue) * 100);
                shipmentDeliverySummary.setPercentageShipped(percShipped);
                log.info("shipmentDeliverySummary : " + shipmentDeliverySummary);

                shipmentDeliverySummaryList.add(shipmentDeliverySummary);
            }

            // --------------------------------------------------------------------------------------------------------------------------------
            /*
             * Partner Code : 101, 102, 103, 107, 109, 111, 113 - Normal
             */
            //List<String> partnerCodes = Arrays.asList("101", "102", "103", "107", "109", "111", "112", "113");
            List<SummaryMetrics> summaryMetricsList = new ArrayList<>();
            for (String pCode : partnerCodes) {
                SummaryMetrics partnerCode_N = reportsService.getMetricsDetails(languageId, companyCode, plantId, "N", warehouseId, pCode, "N", fromDeliveryDate_d, toDeliveryDate_d);
                SummaryMetrics partnerCode_S = reportsService.getMetricsDetails(languageId, companyCode, plantId, "S", warehouseId, pCode, "S", fromDeliveryDate_d, toDeliveryDate_d);

                if (partnerCode_N != null) {
                    partnerCode_N.getMetricsSummary().setReferenceId(referenceId);
                    partnerCode_N.setReferenceId(referenceId);
                    summaryMetricsList.add(partnerCode_N);
                }

                if (partnerCode_S != null) {
                    partnerCode_S.getMetricsSummary().setReferenceId(referenceId);
                    partnerCode_S.setReferenceId(referenceId);
                    summaryMetricsList.add(partnerCode_S);
                }
            }

//            shipmentDeliverySummaryRepository.deleteAll();
//            summaryMetricsRepository.deleteAll();
//            metricsSummaryRepository.deleteAll();

            shipmentDeliverySummaryRepository.saveAll(shipmentDeliverySummaryList);
            summaryMetricsRepository.saveAll(summaryMetricsList);

            shipmentReportRepository.updateStatus(1L, referenceId);
            log.info("Report Generated --------------> Flag Updated Reference Id is {}", referenceId);
//            shipmentDeliverySummaryReport.setShipmentDeliverySummary(shipmentDeliverySummaryList);
//            shipmentDeliverySummaryReport.setSummaryMetrics(summaryMetricsList);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        return shipmentDeliverySummaryReport;
    }


}
