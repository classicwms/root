//package com.tekclover.wms.api.masters.service;
//
//import com.tekclover.wms.api.masters.model.IKeyValuePair;
//import com.tekclover.wms.api.masters.model.threepl.billing.Billing;
//import com.tekclover.wms.api.masters.model.threepl.invoice.proformainvoiceheader.ProformaInvoiceHeader;
//import com.tekclover.wms.api.masters.model.threepl.invoice.proformainvoiceline.ProformaInvoiceLine;
//import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
//import com.tekclover.wms.api.masters.model.threepl.pricelistassignment.PriceListAssignment;
//import com.tekclover.wms.api.masters.repository.*;
//import com.tekclover.wms.api.masters.util.DateUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.text.ParseException;
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Slf4j
//@EnableScheduling
//public class ProformaInvoiceService2 {
//
//    @Autowired
//    private BillingRepository billingRepository;
//
//    @Autowired
//    private PriceListAssignmentRepository priceListAssignmentRepository;
//
//    @Autowired
//    private PriceListRepository priceListRepository;
//
//    @Autowired
//    private ProformaInvoiceHeaderRepository proformaInvoiceHeaderRepository;
//
//
//    @Autowired
//    private ProformaInvoiceLineRepository proformaInvoiceLineRepository;
//
//
//    @Transactional
//    @Scheduled(cron = "0 */1 * * * *")//Runs at 6 AM Kuwait time
//    public void generatePeriodicProformaInvoices() throws ParseException {
//        List<Billing> billingRecords = billingRepository.findByBillGenerationIndicatorAndBillModeIdAndDeletionIndicator("Automatic", 2L, 0L);
//
//        for (Billing billing : billingRecords) {
//            String partnerCode = billing.getPartnerCode();
//            Long billFreqId = billing.getBillFrequencyId();
//
//            // BILL_FREQ_ID = 1 (Daily)
//            if (billFreqId == 1L) {
//                log.info("BILL_FREQ_ID = 1 (Daily) - Generating invoice for Partner: " + partnerCode);
//                LocalDate currentDate = LocalDate.now();
//                Date[] dates = DateUtils.addTimeToDatesForSearchminus1date(currentDate, currentDate);
//                Date fromDate = dates[0];
//                Date toDate = dates[1];
//                log.info("Create ProformaBilling Started -----------------> ");
//                createProformaForPeriodicBilling(billing, partnerCode, billFreqId, fromDate, toDate);
//            }
//            // BILL_FREQ_ID = 2 (Weekly)
//            else if (billFreqId == 2L) {
//                String billGenDay = billingRepository.getDayBillDate(billing.getCompanyCodeId(), billing.getPlantId(), billing.getLanguageId(), billing.getWarehouseId(), billFreqId);
//                if (billGenDay != null) {
//                    DayOfWeek expectedDay = DayOfWeek.valueOf(billGenDay.toUpperCase());
//                    DayOfWeek today = LocalDate.now().getDayOfWeek();
//
//                    if (today.equals(expectedDay)) {
//                        log.info("BILL_FREQ_ID = 2 (Weekly) - Generating invoice for Partner: " + partnerCode + " on " + billGenDay);
//                        createProformaInvoice(billing, partnerCode, billFreqId);
//                    } else {
//                        log.info("Skipping Weekly Invoice - Today is " + today + ", expected day is " + billGenDay);
//                    }
//                }
//            }
//            // BILL_FREQ_ID = 3 (Monthly)
//            else if (billFreqId == 3L) {
//                Long dateBillGen = billingRepository.getMonBillDate(billing.getCompanyCodeId(), billing.getPlantId(), billing.getLanguageId(), billing.getWarehouseId(), billFreqId);
//                int todayDate = LocalDate.now().getDayOfMonth();
//
//                if (dateBillGen != null && todayDate == dateBillGen) {
//                    log.info("BILL_FREQ_ID = 3 (Monthly) - Generating invoice for Partner: " + partnerCode + " on Day " + dateBillGen);
//                    createProformaInvoice(billing, partnerCode, billFreqId);
//                } else {
//                    log.info("Skipping Monthly Invoice - Today is " + todayDate + ", expected date is " + dateBillGen);
//                }
//            }
//        }
//    }
//
//    public void createProformaInvoice(Billing billing, String partnerCode, Long billFreqId) {
//
//        PriceListAssignment priceListAssignment = priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
//                billing.getCompanyCodeId(), billing.getPlantId(), billing.getWarehouseId(), billing.getPartnerCode(), 0L);
//
//        List<PriceList> priceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(), priceListAssignment.getPriceListId(), 0L);
//
//        // Total_Amount
//        double totalAmount = priceLists.stream()
//                .mapToDouble(PriceList::getPricePerChargeUnit)
//                .sum();
//        ProformaInvoiceHeader header = new ProformaInvoiceHeader();
//        header.setLanguageId(priceListAssignment.getLanguageId());
//        header.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        header.setPlantId(priceListAssignment.getPlantId());
//        header.setWarehouseId(priceListAssignment.getWarehouseId());
//        header.setPartnerCode(partnerCode);
//        header.setProformaBillNo(generateBillNumber());
//        header.setProformaBillAmount(totalAmount);
//        header.setBillUnit("NO");
//        header.setBillQuantity(1D);
//        header.setPriceUnit(priceLists.get(0).getPriceUnit());
//        header.setBillDateFrom(new Date());
//        header.setBillDateTo(calculateBillDateTo(billFreqId));
//        header.setProformaBillDate(new Date());
//        header.setStatusId(87L);
//        header.setDeletionIndicator(0L);
//        header.setCreatedBy("CWMS");
//        header.setCreatedOn(new Date());
//
//
//        proformaInvoiceHeaderRepository.save(header);
//
//        long lineNo = 1;
//        for (PriceList priceList : priceLists) {
//            ProformaInvoiceLine line = new ProformaInvoiceLine();
//            line.setLanguageId(priceListAssignment.getLanguageId());
//            line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//            line.setPlantId(priceListAssignment.getPlantId());
//            line.setWarehouseId(priceListAssignment.getWarehouseId());
//            line.setPartnerCode(partnerCode);
//            line.setProformaBillNo(header.getProformaBillNo());
//            line.setLineNumber(lineNo++);
//            line.setDescription(priceList.getDescription());
//            line.setProformaBillAmountLine(priceList.getPricePerChargeUnit());
//            line.setBillUnit("NO");
//            line.setBillQuantity(1D);
//            line.setPriceUnit(priceList.getPriceUnit());
//            line.setInvoiceDate(new Date());
//            line.setStatusId(87L);
//            line.setDeletionIndicator(0L);
//            line.setCreatedBy("CWMS");
//            line.setCreatedOn(new Date());
//            proformaInvoiceLineRepository.save(line);
//        }
//    }
//
//    //---------------------------------------------ProformaForPeriodicBilling----------------------------------------------------//
//
//    /**
//     * @param billing
//     * @param partnerCode
//     * @param billFreqId
//     * @param fromDate
//     * @param toDate
//     */
//    public void createProformaForPeriodicBilling(Billing billing, String partnerCode, Long billFreqId, Date fromDate, Date toDate) {
//
//        PriceListAssignment priceListAssignment = priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
//                billing.getCompanyCodeId(), billing.getPlantId(), billing.getWarehouseId(), billing.getPartnerCode(), 0L);
//
//        ProformaInvoiceHeader header = new ProformaInvoiceHeader();
//        header.setLanguageId(priceListAssignment.getLanguageId());
//        header.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        header.setPlantId(priceListAssignment.getPlantId());
//        header.setWarehouseId(priceListAssignment.getWarehouseId());
//        header.setPartnerCode(partnerCode);
//        header.setProformaBillNo(generateBillNumber());
//
//        header.setBillUnit("NO");
//        header.setBillQuantity(1D);
////        header.setPriceUnit(priceList.getPriceUnit());
//        header.setBillDateFrom(new Date());
//        header.setBillDateTo(calculateBillDateTo(billFreqId));
//        header.setProformaBillDate(new Date());
//        header.setStatusId(87L);
//        header.setDeletionIndicator(0L);
//        header.setCreatedBy("CWMS");
//        header.setCreatedOn(new Date());
//
//
//        Double totalAmount = 0D;
//        Double totalCbm = 0D;
//        ProformaInvoiceLine line = null;
//        // GR_LINE
//        List<PriceList> priceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(), priceListAssignment.getPriceListId(), 1L, "98", 0L);
//
//        PriceList priceList = priceLists.get(0);
//        IKeyValuePair sumCbmValues = priceListRepository.getCbm(priceList.getCompanyCodeId(), priceList.getPlantId(), priceList.getLanguageId(), priceList.getWarehouseId(), partnerCode, fromDate, toDate);
//        log.info("GrCharges Values -------> " + sumCbmValues);
//        if (sumCbmValues != null) {
//            line = new ProformaInvoiceLine();
//            line.setLanguageId(priceListAssignment.getLanguageId());
//            line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//            line.setPlantId(priceListAssignment.getPlantId());
//            line.setWarehouseId(priceListAssignment.getWarehouseId());
//            line.setPartnerCode(partnerCode);
//            line.setProformaBillNo(header.getProformaBillNo());
//            line.setLineNumber(1L);
//            line.setDescription("GR CHARGES");
//            line.setProformaBillAmountLine(sumCbmValues.getProformaBillAmountLine());
//            line.setBillUnit("NO");
//            line.setBillQuantity(1D);
//            line.setPriceUnit(priceList.getPriceUnit());
//            line.setInvoiceDate(new Date());
//            line.setStatusId(87L);
//            line.setDeletionIndicator(0L);
//            line.setCreatedBy("CWMS");
//            line.setCreatedOn(new Date());
//            line.setTotalCbm(sumCbmValues.getTotalCbm());
//            line.setCurrency(sumCbmValues.getCurrency());
//            proformaInvoiceLineRepository.save(line);
//
//            totalCbm += sumCbmValues.getTotalCbm() != null ? sumCbmValues.getTotalCbm() : 0;
//            totalAmount += sumCbmValues.getTotalRate() != null ? sumCbmValues.getTotalRate() : 0;
//
//            // Gr_Invoice Updated = 1
//            priceListRepository.updateGrInvoice(priceList.getCompanyCodeId(), priceList.getPlantId(), priceList.getLanguageId(), priceList.getWarehouseId(), partnerCode, fromDate, toDate);
//            log.info("GrInvoice Generated Successfully And GrInvoiceNo Updated");
//        }
//
//        // 3PL_StockMovement
//        List<PriceList> stockPriceList = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(),
//                priceListAssignment.getPriceListId(), 1L, "115", 0L);
//
//        if (stockPriceList.isEmpty()) {
//            throw new RuntimeException("No Price List found for MOD_ID 115");
//        }
//        PriceList stockPrice = stockPriceList.get(0);
//        Double sumclosestockvalues = priceListRepository.getclosestock(stockPrice.getCompanyCodeId(), stockPrice.getPlantId(), stockPrice.getWarehouseId(), partnerCode, fromDate, toDate);
//        log.info("StockMovement value ------->" + sumclosestockvalues);
//
//        if (sumclosestockvalues != null) {
//            line = new ProformaInvoiceLine();
//            line.setLanguageId(priceListAssignment.getLanguageId());
//            line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//            line.setPlantId(priceListAssignment.getPlantId());
//            line.setWarehouseId(priceListAssignment.getWarehouseId());
//            line.setPartnerCode(partnerCode);
//            line.setProformaBillNo(header.getProformaBillNo());
//            line.setLineNumber(1L);
//            line.setDescription("STOCK CHARGES");
//            line.setProformaBillAmountLine(sumclosestockvalues);
//            line.setBillUnit("NO");
//            line.setBillQuantity(1D);
//            line.setPriceUnit(priceList.getPriceUnit());
//            line.setInvoiceDate(new Date());
//            line.setStatusId(87L);
//            line.setDeletionIndicator(0L);
//            line.setCreatedBy("CWMS");
//            line.setCreatedOn(new Date());
//            line.setTotalCbm(sumCbmValues.getTotalCbm());
//            line.setCurrency(sumCbmValues.getCurrency());
//            proformaInvoiceLineRepository.save(line);
//            totalCbm += sumCbmValues.getTotalCbm() != null ? sumCbmValues.getTotalCbm() : 0;
//            totalAmount += sumCbmValues.getTotalRate() != null ? sumCbmValues.getTotalRate() : 0;
//
//            //Stock Movement update = 1
//            priceListRepository.updateCloseStock(stockPrice.getCompanyCodeId(), stockPrice.getPlantId(), stockPrice.getWarehouseId(), partnerCode, fromDate, toDate);
//            log.info("StockMovement Generated Successfully And StockMovementNo Updated");
//        }
//
//        //Picking
//        List<PriceList> pickingpriceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(),
//                priceListAssignment.getPriceListId(), 1L, "108", 0L);
//
//        if (pickingpriceLists.isEmpty()) {
//            throw new RuntimeException("No Price List found for MOD_ID 108");
//        }
//
//        PriceList pickingprice = priceLists.get(0);
//        Double sumpickingvalues = priceListRepository.getPickCbm(pickingprice.getCompanyCodeId(), priceList.getPlantId(), priceList.getWarehouseId(), partnerCode, fromDate, toDate);
//        log.info("Picking Invoice value -------> " + sumpickingvalues);
//        if (sumpickingvalues != null) {
//            line = new ProformaInvoiceLine();
//            line.setLanguageId(priceListAssignment.getLanguageId());
//            line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//            line.setPlantId(priceListAssignment.getPlantId());
//            line.setWarehouseId(priceListAssignment.getWarehouseId());
//            line.setPartnerCode(partnerCode);
//            line.setProformaBillNo(header.getProformaBillNo());
//            line.setLineNumber(1L);
//            line.setDescription("PICKING CHARGES");
//            line.setProformaBillAmountLine(sumpickingvalues);
//            line.setBillUnit("NO");
//            line.setBillQuantity(1D);
//            line.setPriceUnit(priceList.getPriceUnit());
//            line.setInvoiceDate(new Date());
//            line.setStatusId(87L);
//            line.setDeletionIndicator(0L);
//            line.setCreatedBy("CWMS");
//            line.setCreatedOn(new Date());line.setTotalCbm(sumCbmValues.getTotalCbm());
//            line.setCurrency(sumCbmValues.getCurrency());
//            proformaInvoiceLineRepository.save(line);
//            totalCbm += sumCbmValues.getTotalCbm() != null ? sumCbmValues.getTotalCbm() : 0;
//            totalAmount += sumCbmValues.getTotalRate() != null ? sumCbmValues.getTotalRate() : 0;
//
//            // Picking update = 1;
//            priceListRepository.updatePickCbm(pickingprice.getCompanyCodeId(), priceList.getPlantId(), priceList.getWarehouseId(), partnerCode, fromDate, toDate);
//            log.info("Picking Invoice Generated Successfully And PickingInvoiceNo Updated");
//        }
//
//        //PutAway
//
//        List<PriceList> putAwayPriceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(), priceListAssignment.getPriceListId(),
//                1L, "117", 0L);
//        if (putAwayPriceLists.isEmpty()) {
//            throw new RuntimeException("No Price List found for MOD_ID 117");
//        }
//
//        PriceList putAwayPrice = priceLists.get(0);
//
//        //Calculate total CBM Values based on chargeRangeIds
//        Double sumPutAwayValues = priceListRepository.getPutAwayCbm(putAwayPrice.getCompanyCodeId(), putAwayPrice.getPlantId(), putAwayPrice.getLanguageId(), putAwayPrice.getWarehouseId(), partnerCode, fromDate, toDate);
//        log.info("PutAway value ------->" + sumPutAwayValues);
//
//        if (sumPutAwayValues != null) {
//            line = new ProformaInvoiceLine();
//            line.setLanguageId(priceListAssignment.getLanguageId());
//            line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//            line.setPlantId(priceListAssignment.getPlantId());
//            line.setWarehouseId(priceListAssignment.getWarehouseId());
//            line.setPartnerCode(partnerCode);
//            line.setProformaBillNo(header.getProformaBillNo());
//            line.setLineNumber(1L);
//            line.setDescription("PUTAWAY CHARGES");
//            line.setProformaBillAmountLine(sumPutAwayValues);
//            line.setBillUnit("NO");
//            line.setBillQuantity(1D);
//            line.setPriceUnit(priceList.getPriceUnit());
//            line.setInvoiceDate(new Date());
//            line.setStatusId(87L);
//            line.setDeletionIndicator(0L);
//            line.setCreatedBy("CWMS");
//            line.setCreatedOn(new Date());
//            proformaInvoiceLineRepository.save(line);
//            line.setTotalCbm(sumCbmValues.getTotalCbm());
//            line.setCurrency(sumCbmValues.getCurrency());
//            proformaInvoiceLineRepository.save(line);
//            totalCbm += sumCbmValues.getTotalCbm() != null ? sumCbmValues.getTotalCbm() : 0;
//            totalAmount += sumCbmValues.getTotalRate() != null ? sumCbmValues.getTotalRate() : 0;
//
//            // PUTAWAY_Invoice Updated = 1
//            priceListRepository.updatePutAwayInvoice(priceList.getCompanyCodeId(), priceList.getPlantId(), priceList.getLanguageId(), priceList.getWarehouseId(), partnerCode, fromDate, toDate);
//            log.info("PutAwayInvoice Generated Successfully And PutAwayInvoiceNo Updated");
//
//        }
//
//        //Packing
//        List<PriceList> packingpriceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(),
//                priceListAssignment.getPriceListId(), 1L, "110", 0L);
//
//        if (packingpriceLists.isEmpty()) {
//            throw new RuntimeException("No Price List found for MOD_ID 110");
//        }
//
//        PriceList packingprice = priceLists.get(0);
//
//        // Calculate total CBM values based on chargeRangeIds
//        Double sumpackingvalues = priceListRepository.getPackCbm(packingprice.getCompanyCodeId(), priceList.getPlantId(), priceList.getWarehouseId(), partnerCode, fromDate, toDate);
//        log.info("Packing Invoice value -------> " + sumpackingvalues);
//
//        if (sumpackingvalues != null) {
//            line = new ProformaInvoiceLine();
//            line.setLanguageId(priceListAssignment.getLanguageId());
//            line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//            line.setPlantId(priceListAssignment.getPlantId());
//            line.setWarehouseId(priceListAssignment.getWarehouseId());
//            line.setPartnerCode(partnerCode);
//            line.setProformaBillNo(header.getProformaBillNo());
//            line.setLineNumber(1L);
//            line.setDescription("PACKING CHARGES");
//            line.setProformaBillAmountLine(sumpackingvalues);
//            line.setBillUnit("NO");
//            line.setBillQuantity(1D);
//            line.setPriceUnit(priceList.getPriceUnit());
//            line.setInvoiceDate(new Date());
//            line.setStatusId(87L);
//            line.setDeletionIndicator(0L);
//            line.setCreatedBy("CWMS");
//            line.setCreatedOn(new Date());
//            line.setTotalCbm(sumCbmValues.getTotalCbm());
//            line.setCurrency(sumCbmValues.getCurrency());
//            proformaInvoiceLineRepository.save(line);
//            totalCbm += sumCbmValues.getTotalCbm() != null ? sumCbmValues.getTotalCbm() : 0;
//            totalAmount += sumCbmValues.getTotalRate() != null ? sumCbmValues.getTotalRate() : 0;
//
//            //Packing Invoice updated = 1
//            priceListRepository.updatePackCbm(packingprice.getCompanyCodeId(), priceList.getPlantId(), priceList.getWarehouseId(), partnerCode, fromDate, toDate);
//            log.info("Packing Invoice Generated Successfully And PackingInvoiceNo Updated");
//        }
//
//        header.setProformaBillAmount(totalAmount);
//        header.setTotalCbm(totalCbm);
//        proformaInvoiceHeaderRepository.save(header);
//
//
//        // Total_Amount
////        double totalAmount = priceLists.stream()
////                .mapToDouble(PriceList::getPricePerChargeUnit)
////                .sum();
//
//
////        long lineNo = 1;
////        for (PriceList priceList : priceLists) {
//
//
//    }
//
//
//    private Date calculateBillDateTo(Long billFreqId) {
//        LocalDate today = LocalDate.now();
//
//        if (billFreqId == 1) {
//            return Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        } else if (billFreqId == 2) {
//            return Date.from(today.plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
//        } else if (billFreqId == 3) {
//            return Date.from(today.plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());
//        }
//
//        return Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
//    }
//
//    private String generateBillNumber() {
//        return "PF-" + System.currentTimeMillis();
//    }
//
//
//}