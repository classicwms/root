//package com.tekclover.wms.api.masters.service;
//
//import com.tekclover.wms.api.masters.model.threepl.billing.Billing;
//import com.tekclover.wms.api.masters.model.threepl.invoice.proformainvoiceheader.ProformaInvoiceHeader;
//import com.tekclover.wms.api.masters.model.threepl.invoice.proformainvoiceline.ProformaInvoiceLine;
//import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
//import com.tekclover.wms.api.masters.model.threepl.pricelistassignment.PriceListAssignment;
//import com.tekclover.wms.api.masters.repository.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Slf4j
//@EnableScheduling
//public class ProformaInvoiceService {
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
//    @Autowired
//    private ProformaInvoiceLineRepository proformaInvoiceLineRepository;
//
//
//
//
//    @Transactional
////    @Scheduled(cron = "0 2 0 * * *")
//    public void generateProformaInvoices() {
//        List<Billing> billingRecords = billingRepository.findByBillGenerationIndicatorAndBillModeIdAndDeletionIndicator("Automatic", 1L, 0L);
//
//        for (Billing billing : billingRecords) {
//            String partnerCode = billing.getPartnerCode();
//            String modId = billing.getModuleId();
//            Long billFreqId = billing.getBillFrequencyId();
//
//            // Bill_Frequency_Id = 1
//            if (billFreqId == 1L) {
//                log.info("Bill_Frequency_Id is 1 so invoice generated stated in daily");
//                createProformaInvoice(billing, partnerCode, billFreqId);
//            } else if (billFreqId == 2L) {
//
//                String billGenDay = billingRepository.getDayBillDate(billing.getCompanyCodeId(), billing.getPlantId(), billing.getLanguageId(), billing.getWarehouseId(), billFreqId);
//                if (billGenDay != null) {
//                    // Convert fetched billGenDay (e.g., "Monday") to DayOfWeek
//                    DayOfWeek expectedDay = DayOfWeek.valueOf(billGenDay.toUpperCase());
//                    DayOfWeek today = LocalDate.now().getDayOfWeek();
//
//                    // Check if today is the scheduled BILL_GEN_DAY
//                    if (today.equals(expectedDay)) {
//                        log.info("Generating invoice for " + billGenDay);
//                        createProformaInvoice(billing, partnerCode, billFreqId);
//                    } else {
//                        log.info("Skipping invoice generation as today is not " + billGenDay);
//                    }
//                }
//            } else if (billFreqId == 3L) {
//
//                Long dateBillGen = billingRepository.getMonBillDate(billing.getCompanyCodeId(), billing.getPlantId(), billing.getLanguageId(), billing.getWarehouseId(), billFreqId);
//                int todayDate = LocalDate.now().getDayOfMonth();
//                if (dateBillGen != null && todayDate == dateBillGen) {
//                    log.info("Bill_Frequency_Id is 3 (Monthly) - Generating Invoice for day " + dateBillGen);
//                    createProformaInvoice(billing, partnerCode, billFreqId);
//                } else {
//                    log.info("Skipping Monthly Invoice - Today is not the scheduled date (" + dateBillGen + ")");
//                }
//            }
//        }
//    }
//
//    @Transactional
////    @Scheduled(cron = "0 */2 * * * *")//Runs at 6 AM Kuwait time
//    public void generatePeriodicProformaInvoices() {
//        List<Billing> billingRecords = billingRepository.findByBillGenerationIndicatorAndBillModeIdAndDeletionIndicator("Automatic", 2L, 0L);
//
//        for (Billing billing : billingRecords) {
//            String partnerCode = billing.getPartnerCode();
//            Long billFreqId = billing.getBillFrequencyId();
//
//            // BILL_FREQ_ID = 1 (Daily)
//            if (billFreqId == 1L) {
//                log.info("BILL_FREQ_ID = 1 (Daily) - Generating invoice for Partner: " + partnerCode);
//                createProformaForPeriodicBilling(billing, partnerCode, billFreqId);
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
//    /**
//     * @param billing
//     * @param partnerCode
//     * @param billFreqId
//     */
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
//    /**
//     * @param billing
//     * @param partnerCode
//     * @param billFreqId
//     */
//    public void createProformaForPeriodicBilling(Billing billing, String partnerCode, Long billFreqId) {
//
//        PriceListAssignment priceListAssignment = priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
//                billing.getCompanyCodeId(), billing.getPlantId(), billing.getWarehouseId(), billing.getPartnerCode(), 0L);
//
//        List<PriceList> priceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(), priceListAssignment.getPriceListId(), 1L, "98", 0L);
//
//        PriceList priceList = priceLists.get(0);
//        Double sumCbmValues = priceListRepository.getCbm(priceList.getCompanyCodeId(), priceList.getPlantId(), priceList.getLanguageId(), priceList.getWarehouseId(), partnerCode);
//        // Total_Amount
////        double totalAmount = priceLists.stream()
////                .mapToDouble(PriceList::getPricePerChargeUnit)
////                .sum();
//        ProformaInvoiceHeader header = new ProformaInvoiceHeader();
//        header.setLanguageId(priceListAssignment.getLanguageId());
//        header.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        header.setPlantId(priceListAssignment.getPlantId());
//        header.setWarehouseId(priceListAssignment.getWarehouseId());
//        header.setPartnerCode(partnerCode);
//        header.setProformaBillNo(generateBillNumber());
//        header.setProformaBillAmount(sumCbmValues);
//        header.setBillUnit("NO");
//        header.setBillQuantity(1D);
//        header.setPriceUnit(priceList.getPriceUnit());
//        header.setBillDateFrom(new Date());
//        header.setBillDateTo(calculateBillDateTo(billFreqId));
//        header.setProformaBillDate(new Date());
//        header.setStatusId(87L);
//        header.setDeletionIndicator(0L);
//        header.setCreatedBy("CWMS");
//        header.setCreatedOn(new Date());
//
//        proformaInvoiceHeaderRepository.save(header);
//
////        long lineNo = 1;
////        for (PriceList priceList : priceLists) {
//        ProformaInvoiceLine line = new ProformaInvoiceLine();
//        line.setLanguageId(priceListAssignment.getLanguageId());
//        line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        line.setPlantId(priceListAssignment.getPlantId());
//        line.setWarehouseId(priceListAssignment.getWarehouseId());
//        line.setPartnerCode(partnerCode);
//        line.setProformaBillNo(header.getProformaBillNo());
//        line.setLineNumber(1L);
//        line.setDescription(priceList.getDescription());
//        line.setProformaBillAmountLine(priceList.getPricePerChargeUnit());
//        line.setBillUnit("NO");
//        line.setBillQuantity(1D);
//        line.setPriceUnit(priceList.getPriceUnit());
//        line.setInvoiceDate(new Date());
//        line.setStatusId(87L);
//        line.setDeletionIndicator(0L);
//        line.setCreatedBy("CWMS");
//        line.setCreatedOn(new Date());
//        proformaInvoiceLineRepository.save(line);
//    }
//
//
//    //** ---------------------------------------- Storage CBM calculation Schedule--------------------------//*
//
//    @Transactional
////    @Scheduled(cron = "0 */2 * * * *")// Runs at 6 AM Kuwait time
//    public void generateStorageCBM() {
//        List<Billing> billingRecords = billingRepository.findByBillGenerationIndicatorAndBillModeIdAndDeletionIndicator("Automatic", 2L, 0L);
//
//        for (Billing billing : billingRecords) {
//            String partnerCode = billing.getPartnerCode();
//            Long billFreqId = billing.getBillFrequencyId();
//
//            // BILL_FREQ_ID = 1 (Daily)
//            if (billFreqId == 1L) {
//                log.info("BILL_FREQ_ID = 1 (Daily) - Generating invoice for Partner: " + partnerCode);
//                createProformaForStockMovement(billing, partnerCode);
//            }
//            // BILL_FREQ_ID = 2 (Weekly)
//            else if (billFreqId == 2L) {
//                String billGenDay = billingRepository.getDayBillDate(billing.getCompanyCodeId(), billing.getPlantId(), billing.getLanguageId(), billing.getWarehouseId(), billFreqId);
//                if (billGenDay != null) {
//                    DayOfWeek expectedDay = DayOfWeek.valueOf(billGenDay.toUpperCase());
////                    DayOfWeek today = LocalDate.now().getDayOfWeek();
//                    DayOfWeek today = LocalDate.now().getDayOfWeek();
//
//                    if (today.equals(expectedDay)) {
//                        log.info("BILL_FREQ_ID = 2 (Weekly) - Generating invoice for Partner: " + partnerCode + " on " + billGenDay);
//                        createProformaForStockMovement(billing, partnerCode);
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
//                    createProformaForStockMovement(billing, partnerCode);
//                } else {
//                    log.info("Skipping Monthly Invoice - Today is " + todayDate + ", expected date is " + dateBillGen);
//                }
//            }
//        }
//    }
//
//    //*-------------------------------------------  Storage CBM calculation creation---------------------------------*//
//
//    public void createProformaForStockMovement(Billing billing, String partnerCode) {
//
//        PriceListAssignment priceListAssignment = priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
//                billing.getCompanyCodeId(), billing.getPlantId(), billing.getWarehouseId(), billing.getPartnerCode(), 0L);
//
//        List<PriceList> priceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(),
//                priceListAssignment.getPriceListId(), 1L, "115", 0L);
//
//        if (priceLists.isEmpty()) {
//            throw new RuntimeException("No Price List found for MOD_ID 115");
//        }
//
//        PriceList priceList = priceLists.get(0);
//
//        // Calculate total CBM values based on chargeRangeIds
//        Double sumclosestockvalues = priceListRepository.getclosestock(priceList.getCompanyCodeId(),priceList.getPlantId(),priceList.getWarehouseId(), partnerCode);
//
//        // Creating Proforma Invoice Header
//        ProformaInvoiceHeader header = new ProformaInvoiceHeader();
//        header.setLanguageId(priceListAssignment.getLanguageId());
//        header.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        header.setPlantId(priceListAssignment.getPlantId());
//        header.setWarehouseId(priceListAssignment.getWarehouseId());
//        header.setPartnerCode(partnerCode);
//        header.setProformaBillNo(generateBillNumber());
//        header.setProformaBillAmount(sumclosestockvalues);
//        header.setBillUnit("NO");
//        header.setBillQuantity(1D);
//        header.setPriceUnit(priceList.getPriceUnit());
////        header.setBillDateFrom(startDate);
////        header.setBillDateTo(endDate);
//        header.setProformaBillDate(new Date());
//        header.setStatusId(87L);
//        header.setDeletionIndicator(0L);
//        header.setCreatedBy("CWMS");
//        header.setCreatedOn(new Date());
//
//        proformaInvoiceHeaderRepository.save(header);
//
//        // Creating Proforma Invoice Line
//        ProformaInvoiceLine line = new ProformaInvoiceLine();
//        line.setLanguageId(priceListAssignment.getLanguageId());
//        line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        line.setPlantId(priceListAssignment.getPlantId());
//        line.setWarehouseId(priceListAssignment.getWarehouseId());
//        line.setPartnerCode(partnerCode);
//        line.setProformaBillNo(header.getProformaBillNo());
//        line.setLineNumber(1L);
//        line.setDescription(priceList.getDescription());
//        line.setProformaBillAmountLine(priceList.getPricePerChargeUnit());
//        line.setBillUnit("NO");
//        line.setBillQuantity(1D);
//        line.setPriceUnit(priceList.getPriceUnit());
//        line.setInvoiceDate(new Date());
//        line.setStatusId(87L);
//        line.setDeletionIndicator(0L);
//        line.setCreatedBy("CWMS");
//        line.setCreatedOn(new Date());
//
//        proformaInvoiceLineRepository.save(line);
//    }
//
//    //*------------------------------------Picking CBM Calculation Schedule---------------------------------*//
//
//
//    @Transactional
////    @Scheduled(cron = "0 */2 * * * *")// Runs at 6 AM Kuwait time
//    public void generatePickingCBM() {
//        List<Billing> billingRecords = billingRepository.findByBillGenerationIndicatorAndBillModeIdAndDeletionIndicator("Automatic", 2L, 0L);
//
//        for (Billing billing : billingRecords) {
//            String partnerCode = billing.getPartnerCode();
//            Long billFreqId = billing.getBillFrequencyId();
//
//            // BILL_FREQ_ID = 1 (Daily)
//            if (billFreqId == 1L) {
//                log.info("BILL_FREQ_ID = 1 (Daily) - Generating invoice for Partner: " + partnerCode);
//                createProformaForPicking(billing, partnerCode);
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
//                        createProformaForPicking(billing, partnerCode);
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
//                    createProformaForPicking(billing, partnerCode);
//                } else {
//                    log.info("Skipping Monthly Invoice - Today is " + todayDate + ", expected date is " + dateBillGen);
//                }
//            }
//        }
//    }
//
//    //*------------------------------------------Picking CBM Calculation Creation-----------------------------------*//
//
//    public void createProformaForPicking(Billing billing, String partnerCode) {
//
//        PriceListAssignment priceListAssignment = priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
//                billing.getCompanyCodeId(), billing.getPlantId(), billing.getWarehouseId(), billing.getPartnerCode(), 0L);
//
//        List<PriceList> priceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(),
//                priceListAssignment.getPriceListId(), 1L, "108", 0L);
//
//        if (priceLists.isEmpty()) {
//            throw new RuntimeException("No Price List found for MOD_ID 108");
//        }
//
//        PriceList priceList = priceLists.get(0);
//
//        // Calculate total CBM values based on chargeRangeIds
//        Double getPickCbm = priceListRepository.getPickCbm(priceList.getCompanyCodeId(),priceList.getPlantId(),priceList.getWarehouseId(), partnerCode);
//
//        // Creating Proforma Invoice Header
//        ProformaInvoiceHeader header = new ProformaInvoiceHeader();
//        header.setLanguageId(priceListAssignment.getLanguageId());
//        header.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        header.setPlantId(priceListAssignment.getPlantId());
//        header.setWarehouseId(priceListAssignment.getWarehouseId());
//        header.setPartnerCode(partnerCode);
//        header.setProformaBillNo(generateBillNumber());
//        header.setProformaBillAmount(getPickCbm);
//        header.setBillUnit("NO");
//        header.setBillQuantity(1D);
//        header.setPriceUnit(priceList.getPriceUnit());
////        header.setBillDateFrom(startDate);
////        header.setBillDateTo(endDate);
//        header.setProformaBillDate(new Date());
//        header.setStatusId(87L);
//        header.setDeletionIndicator(0L);
//        header.setCreatedBy("CWMS");
//        header.setCreatedOn(new Date());
//
//        proformaInvoiceHeaderRepository.save(header);
//
//        // Creating Proforma Invoice Line
//        ProformaInvoiceLine line = new ProformaInvoiceLine();
//        line.setLanguageId(priceListAssignment.getLanguageId());
//        line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        line.setPlantId(priceListAssignment.getPlantId());
//        line.setWarehouseId(priceListAssignment.getWarehouseId());
//        line.setPartnerCode(partnerCode);
//        line.setProformaBillNo(header.getProformaBillNo());
//        line.setLineNumber(1L);
//        line.setDescription(priceList.getDescription());
//        line.setProformaBillAmountLine(priceList.getPricePerChargeUnit());
//        line.setBillUnit("NO");
//        line.setBillQuantity(1D);
//        line.setPriceUnit(priceList.getPriceUnit());
//        line.setInvoiceDate(new Date());
//        line.setStatusId(87L);
//        line.setDeletionIndicator(0L);
//        line.setCreatedBy("CWMS");
//        line.setCreatedOn(new Date());
//
//        proformaInvoiceLineRepository.save(line);
//    }
//
////**-----------------------------------------------Packing CBM Calculation Schedule---------------------------------------*//
//
//    @Transactional
////    @Scheduled(cron = "0 */2 * * * *")// Runs at 6 AM Kuwait time
//    public void generatePackingCBM() {
//        List<Billing> billingRecords = billingRepository.findByBillGenerationIndicatorAndBillModeIdAndDeletionIndicator("Automatic", 2L, 0L);
//
//        for (Billing billing : billingRecords) {
//            String partnerCode = billing.getPartnerCode();
//            Long billFreqId = billing.getBillFrequencyId();
//
//            // BILL_FREQ_ID = 1 (Daily)
//            if (billFreqId == 1L) {
//                log.info("BILL_FREQ_ID = 1 (Daily) - Generating invoice for Partner: " + partnerCode);
//                createProformaForPacking(billing, partnerCode);
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
//                        createProformaForPacking(billing, partnerCode);
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
//                    createProformaForPacking(billing, partnerCode);
//                } else {
//                    log.info("Skipping Monthly Invoice - Today is " + todayDate + ", expected date is " + dateBillGen);
//                }
//            }
//        }
//    }
//    //*------------------------------------------Packing CBM Calculation Creation-----------------------------------*//
//    public void createProformaForPacking(Billing billing, String partnerCode) {
//
//        PriceListAssignment priceListAssignment = priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
//                billing.getCompanyCodeId(), billing.getPlantId(), billing.getWarehouseId(), billing.getPartnerCode(), 0L);
//
//        List<PriceList> priceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
//                priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(),
//                priceListAssignment.getPriceListId(), 1L, "110", 0L);
//
//        if (priceLists.isEmpty()) {
//            throw new RuntimeException("No Price List found for MOD_ID 110");
//        }
//
//        PriceList priceList = priceLists.get(0);
//
//        // Calculate total CBM values based on chargeRangeIds
//        Double getPackCbm = priceListRepository.getPackCbm(priceList.getCompanyCodeId(),priceList.getPlantId(),priceList.getWarehouseId(), partnerCode);
//
//        // Creating Proforma Invoice Header
//        ProformaInvoiceHeader header = new ProformaInvoiceHeader();
//        header.setLanguageId(priceListAssignment.getLanguageId());
//        header.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        header.setPlantId(priceListAssignment.getPlantId());
//        header.setWarehouseId(priceListAssignment.getWarehouseId());
//        header.setPartnerCode(partnerCode);
//        header.setProformaBillNo(generateBillNumber());
//        header.setProformaBillAmount(getPackCbm);
//        header.setBillUnit("NO");
//        header.setBillQuantity(1D);
//        header.setPriceUnit(priceList.getPriceUnit());
////        header.setBillDateFrom(startDate);
////        header.setBillDateTo(endDate);
//        header.setProformaBillDate(new Date());
//        header.setStatusId(87L);
//        header.setDeletionIndicator(0L);
//        header.setCreatedBy("CWMS");
//        header.setCreatedOn(new Date());
//
//        proformaInvoiceHeaderRepository.save(header);
//
//        // Creating Proforma Invoice Line
//        ProformaInvoiceLine line = new ProformaInvoiceLine();
//        line.setLanguageId(priceListAssignment.getLanguageId());
//        line.setCompanyCodeId(priceListAssignment.getCompanyCodeId());
//        line.setPlantId(priceListAssignment.getPlantId());
//        line.setWarehouseId(priceListAssignment.getWarehouseId());
//        line.setPartnerCode(partnerCode);
//        line.setProformaBillNo(header.getProformaBillNo());
//        line.setLineNumber(1L);
//        line.setDescription(priceList.getDescription());
//        line.setProformaBillAmountLine(priceList.getPricePerChargeUnit());
//        line.setBillUnit("NO");
//        line.setBillQuantity(1D);
//        line.setPriceUnit(priceList.getPriceUnit());
//        line.setInvoiceDate(new Date());
//        line.setStatusId(87L);
//        line.setDeletionIndicator(0L);
//        line.setCreatedBy("CWMS");
//        line.setCreatedOn(new Date());
//
//        proformaInvoiceLineRepository.save(line);
//    }
//}
//
//
//
