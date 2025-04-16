package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.report.BillingTransactionReportImpl;
import com.tekclover.wms.api.transaction.model.threepl.invoiceheader.FindInvoice;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceheader.*;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.ProformaInvoiceHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProformaInvoiceHeaderService extends BaseService {

    @Autowired
    private ProformaInvoiceHeaderRepository proformaInvoiceHeaderRepository;

    @Autowired
    private ProformaInvoiceLineRepository proformaInvoiceLineRepository;

    @Autowired
    private GrLineRepository grLineRepository;

    @Autowired
    private PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    private PackingHeaderRepository packingHeaderRepository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;



    /**
     * getProformaInvoiceHeaders
     *
     * @return
     */
    public List<ProformaInvoiceHeader> getProformaInvoiceHeaders() {
        List<ProformaInvoiceHeader> ProformaInvoiceHeaderList = proformaInvoiceHeaderRepository.findAll();
        ProformaInvoiceHeaderList = ProformaInvoiceHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return ProformaInvoiceHeaderList;
    }

    /**
     * getProformaInvoiceHeader
     *
     * @param proformaBillNo
     * @param partnerCode
     * @return
     */
    public ProformaInvoiceHeader getProformaInvoiceHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String proformaBillNo, String partnerCode) {
        Optional<ProformaInvoiceHeader> dbProformaInvoiceHeader =
                proformaInvoiceHeaderRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        proformaBillNo,
                        partnerCode,
                        languageId,
                        0L
                );
        if (dbProformaInvoiceHeader.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "proformaBillNo - " + proformaBillNo +
                    "partnerCode - " + partnerCode +
                    " doesn't exist.");

        }
        return dbProformaInvoiceHeader.get();
    }

    /**
     * createProformaInvoiceHeader
     *
     * @param newProformaInvoiceHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ProformaInvoiceHeader createProformaInvoiceHeader(AddProformaInvoiceHeader newProformaInvoiceHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        ProformaInvoiceHeader dbProformaInvoiceHeader = new ProformaInvoiceHeader();
        log.info("newProformaInvoiceHeader : " + newProformaInvoiceHeader);
        BeanUtils.copyProperties(newProformaInvoiceHeader, dbProformaInvoiceHeader, CommonUtils.getNullPropertyNames(newProformaInvoiceHeader));
        dbProformaInvoiceHeader.setDeletionIndicator(0L);
        dbProformaInvoiceHeader.setCreatedBy(loginUserID);
        dbProformaInvoiceHeader.setUpdatedBy(loginUserID);
        dbProformaInvoiceHeader.setCreatedOn(new Date());
        dbProformaInvoiceHeader.setUpdatedOn(new Date());
        return proformaInvoiceHeaderRepository.save(dbProformaInvoiceHeader);
    }

    /**
     * updateProformaInvoiceHeader
     *
     * @param loginUserID
     * @param partnerCode
     * @param proformaBillNo
     * @param updateProformaInvoiceHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ProformaInvoiceHeader updateProformaInvoiceHeader(String companyCodeId, String plantId, String languageId,
                                                             String warehouseId, String proformaBillNo, String partnerCode, String loginUserID,
                                                             UpdateProformaInvoiceHeader updateProformaInvoiceHeader)
            throws IllegalAccessException, InvocationTargetException {
        ProformaInvoiceHeader dbProformaInvoiceHeader = getProformaInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode);
        BeanUtils.copyProperties(updateProformaInvoiceHeader, dbProformaInvoiceHeader, CommonUtils.getNullPropertyNames(updateProformaInvoiceHeader));
        dbProformaInvoiceHeader.setUpdatedBy(loginUserID);
        dbProformaInvoiceHeader.setUpdatedOn(new Date());
        return proformaInvoiceHeaderRepository.save(dbProformaInvoiceHeader);
    }

    /**
     * deleteProformaInvoiceHeader
     *
     * @param loginUserID
     * @param partnerCode
     * @param proformaBillNo
     */
    public void deleteProformaInvoiceHeader(String companyCodeId, String plantId, String languageId,
                                            String warehouseId, String proformaBillNo, String partnerCode, String loginUserID) {
        ProformaInvoiceHeader dbProformaInvoiceHeader = getProformaInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode);
        if (dbProformaInvoiceHeader != null) {
            dbProformaInvoiceHeader.setDeletionIndicator(1L);
            dbProformaInvoiceHeader.setUpdatedBy(loginUserID);
            proformaInvoiceHeaderRepository.save(dbProformaInvoiceHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + partnerCode);
        }
    }


    //Find ProformaInvoiceHeader
    public List<ProformaInvoiceHeader> findProformaInvoiceHeader(FindProformaInvoiceHeader searchProformaInvoiceHeader)
            throws Exception {
        ProformaInvoiceHeaderSpecification spec = new ProformaInvoiceHeaderSpecification(searchProformaInvoiceHeader);
        List<ProformaInvoiceHeader> results = proformaInvoiceHeaderRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    // CreateProforma
    public List<ProformaInvoice> createProformaInvoiceHeaderAndLine(List<ProformaInvoice> invoiceHeaderList, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<ProformaInvoice> proformaInvoiceList = new ArrayList<>();
        ProformaInvoice dbInvoice = new ProformaInvoice();
        List<ProformaInvoiceLine> line = new ArrayList<>();

        log.info("ProformaInvoice" + invoiceHeaderList.size());
        for (ProformaInvoice proformaInvoice : invoiceHeaderList) {
            ProformaInvoiceHeader invoiceHeader = proformaInvoice.getProformaInvoiceHeader();
            List<ProformaInvoiceLine> proformaInvoiceLines = proformaInvoice.getProformaInvoiceLineList();

            long NUM_RAN_CODE = 17;
            String PU_NO = getNextRangeNumber(NUM_RAN_CODE, invoiceHeader.getCompanyCodeId(), invoiceHeader.getPlantId(), invoiceHeader.getLanguageId(), invoiceHeader.getWarehouseId());
            log.info("PU_NO : " + PU_NO);

            ProformaInvoiceHeader newProformaInvoiceHeader = new ProformaInvoiceHeader();
            BeanUtils.copyProperties(invoiceHeader, newProformaInvoiceHeader, CommonUtils.getNullPropertyNames(invoiceHeader));
            newProformaInvoiceHeader.setDeletionIndicator(0L);
            newProformaInvoiceHeader.setProformaBillNo(PU_NO);
            newProformaInvoiceHeader.setCreatedOn(new Date());
            newProformaInvoiceHeader.setCreatedBy(loginUserID);
            proformaInvoiceHeaderRepository.save(newProformaInvoiceHeader);
            log.info("ProformaInvoice Header " + newProformaInvoiceHeader);

            log.info("ProformaInvoice  " + proformaInvoiceLines.size());
            Long lineNo = 1L;
            for (ProformaInvoiceLine proformaInvoiceLine : proformaInvoiceLines) {
                ProformaInvoiceLine newProformaInvoiceLine = new ProformaInvoiceLine();
                BeanUtils.copyProperties(proformaInvoiceLine, newProformaInvoiceLine, CommonUtils.getNullPropertyNames(proformaInvoiceLine));
                newProformaInvoiceLine.setProformaBillNo(PU_NO);
                newProformaInvoiceLine.setDeletionIndicator(0L);
                newProformaInvoiceLine.setCreatedOn(new Date());
                newProformaInvoiceLine.setCreatedBy(loginUserID);
                newProformaInvoiceLine.setLineNumber(lineNo);
                proformaInvoiceLineRepository.save(newProformaInvoiceLine);
                if(newProformaInvoiceLine.getServiceTypeId() == 1L ){
                    proformaInvoiceLineRepository.updateGr(newProformaInvoiceLine.getCompanyCodeId(), newProformaInvoiceLine.getLanguageId(),newProformaInvoiceLine.getPlantId(),newProformaInvoiceLine.getWarehouseId(),
                            newProformaInvoiceLine.getPartnerCode(), newProformaInvoiceLine.getFromDate(), newProformaInvoiceLine.getToDate(), PU_NO);
                log.info("GR Charges Invoice generated Succesfully ------- ProformaInvoiceNo -> " + PU_NO);
                }
                if(newProformaInvoiceLine.getServiceTypeId() == 2L ){
                    proformaInvoiceLineRepository.updatePutAway(newProformaInvoiceLine.getCompanyCodeId(), newProformaInvoiceLine.getLanguageId(),newProformaInvoiceLine.getPlantId(),newProformaInvoiceLine.getWarehouseId(),
                            newProformaInvoiceLine.getPartnerCode(), newProformaInvoiceLine.getFromDate(), newProformaInvoiceLine.getToDate(), PU_NO);
                    log.info("PutAway Charges Invoice generated Succesfully ------- ProformaInvoiceNo -> " + PU_NO);
                }
                if(newProformaInvoiceLine.getServiceTypeId() == 3L ){
                    proformaInvoiceLineRepository.updateStockmovement(newProformaInvoiceLine.getCompanyCodeId(), newProformaInvoiceLine.getLanguageId(),newProformaInvoiceLine.getPlantId(),newProformaInvoiceLine.getWarehouseId(),
                            newProformaInvoiceLine.getPartnerCode(), newProformaInvoiceLine.getFromDate(), newProformaInvoiceLine.getToDate(), PU_NO);
                    log.info("Storage Charges Invoice generated Succesfully ------- ProformaInvoiceNo -> " + PU_NO);
                }
                if(newProformaInvoiceLine.getServiceTypeId() == 4L ){
                    proformaInvoiceLineRepository.updatePickingLine(newProformaInvoiceLine.getCompanyCodeId(), newProformaInvoiceLine.getLanguageId(),newProformaInvoiceLine.getPlantId(),newProformaInvoiceLine.getWarehouseId(),
                            newProformaInvoiceLine.getPartnerCode(), newProformaInvoiceLine.getFromDate(), newProformaInvoiceLine.getToDate(), PU_NO);
                    log.info("Picking Charges Invoice generated Succesfully ------- ProformaInvoiceNo -> " + PU_NO);
                }
                if(newProformaInvoiceLine.getServiceTypeId() == 5L ){
                    proformaInvoiceLineRepository.updatePackingLine(newProformaInvoiceLine.getCompanyCodeId(), newProformaInvoiceLine.getLanguageId(),newProformaInvoiceLine.getPlantId(),newProformaInvoiceLine.getWarehouseId(),
                            newProformaInvoiceLine.getPartnerCode(), newProformaInvoiceLine.getFromDate(), newProformaInvoiceLine.getToDate(), PU_NO);
                    log.info("Packing Charges Invoice generated Succesfully ------- ProformaInvoiceNo -> " + PU_NO);
                }
                line.add(newProformaInvoiceLine);
                lineNo ++;
            }
            dbInvoice.setProformaInvoiceHeader(newProformaInvoiceHeader);
            dbInvoice.setProformaInvoiceLineList(line);
            proformaInvoiceList.add(dbInvoice);
            log.info("ProformaInvoice Created  " + proformaInvoiceList.size());
        }
        return proformaInvoiceList;
    }


    /**
     * @param findInvoice
     * @return
     */
    public List<ProformaInvoice> executeInvoice(FindInvoice findInvoice) throws ParseException {
        List<ProformaInvoice> invoiceList = new ArrayList<>();

        log.info("Find Invoice Input Values {}", findInvoice );

        if(findInvoice.getPartnerCode().isEmpty()) {
            throw new BadRequestException( "PartnerCode is " + findInvoice.getPartnerCode() + " PartnerCode is Mandatory ");
        }
        if (findInvoice.getFromDate() != null && findInvoice.getToDate() != null) {
            Date[] dates = DateUtils.addTimeToDate(findInvoice.getFromDate(), findInvoice.getToDate());
            findInvoice.setFromDate(dates[0]);
            findInvoice.setToDate(dates[1]);
        }

        List<BillingTransactionReportImpl> partnerList = grLineRepository.grBillingTransactionReport(
                findInvoice.getCompanyId(), findInvoice.getPlantId(), findInvoice.getLanguageId(),
                findInvoice.getWarehouseId(), findInvoice.getPartnerCode());
        log.info("PartnerId's List ----------------> " + partnerList);

        if (!partnerList.isEmpty()) {
            for (BillingTransactionReportImpl billingTransactionReport : partnerList) {
                log.info("ServiceTypeId's List ---------------> " + findInvoice.getServiceTypeId());

                // Reinitialize invoice and invoice line list for each iteration
                ProformaInvoice newInvoice = new ProformaInvoice();
                ProformaInvoiceHeader invoiceHeader = new ProformaInvoiceHeader();
                List<ProformaInvoiceLine> newInvoiceLineList = new ArrayList<>();

                ProformaInvoiceHeader invH = new ProformaInvoiceHeader();
                BeanUtils.copyProperties(billingTransactionReport, invH, CommonUtils.getNullPropertyNames(billingTransactionReport));
                invH.setCompanyCodeId(billingTransactionReport.getCompanyCodeId());
                invH.setPlantId(billingTransactionReport.getPlantId());
                invH.setWarehouseId(billingTransactionReport.getWarehouseId());
                invH.setPartnerCode(billingTransactionReport.getPartnerCode());
                invH.setFromDate(findInvoice.getFromDate());
                invH.setToDate(findInvoice.getToDate());

                IKeyValuePair description = stagingLineV2Repository.getDescription(
                        billingTransactionReport.getCompanyCodeId(), billingTransactionReport.getLanguageId(),
                        billingTransactionReport.getPlantId(), billingTransactionReport.getWarehouseId());

                if (description != null) {
                    invH.setCompanyDescription(description.getCompanyDesc());
                    invH.setPlantDescription(description.getPlantDesc());
                    invH.setWarehouseDescription(description.getWarehouseDesc());
                }

                Double totalCbm = 0D;
                Double totalAmount = 0D;

                if (findInvoice.getServiceTypeId() == null || findInvoice.getServiceTypeId().contains("1")) {
                    IKeyValuePair grIkey = grLineRepository.getCbm(
                            invH.getCompanyCodeId(), invH.getPlantId(), invH.getLanguageId(), invH.getWarehouseId(),
                            invH.getPartnerCode(), findInvoice.getFromDate(), findInvoice.getToDate());
                    log.info("------------------> Sum of Gr Charges <---------------------------" );

                    if (grIkey != null && grIkey.getTotalRate() != null && grIkey.getTotalRate() != 0) {
                        ProformaInvoiceLine invoiceLine = new ProformaInvoiceLine();
                        BeanUtils.copyProperties(invH, invoiceLine, CommonUtils.getNullPropertyNames(invH));
                        invoiceLine.setProformaBillAmountLine(grIkey.getTotalRate());
                        invoiceLine.setTotalCbm(grIkey.getTotalCbm());
                        invoiceLine.setCurrency(grIkey.getCurrency());
                        invoiceLine.setPriceUnit(grIkey.getTotalRate() / grIkey.getTotalCbm());
                        invoiceLine.setDescription("Gr Charges");
                        invoiceLine.setServiceTypeId(1L);
                        invoiceLine.setFromDate(findInvoice.getFromDate());
                        invoiceLine.setToDate(findInvoice.getToDate());
                        newInvoiceLineList.add(invoiceLine);
                        invH.setCurrency(grIkey.getCurrency());
                        totalCbm += grIkey.getTotalCbm() != null ? grIkey.getTotalCbm() : 0;
                        totalAmount += grIkey.getTotalRate() != null ? grIkey.getTotalRate() : 0;
                    }
                }

                if (findInvoice.getServiceTypeId() == null || findInvoice.getServiceTypeId().contains("2")) {
                    IKeyValuePair getPutAway = putAwayHeaderV2Repository.getCbm(
                            invH.getCompanyCodeId(), invH.getPlantId(), invH.getLanguageId(),
                            invH.getWarehouseId(), invH.getPartnerCode(), findInvoice.getFromDate(), findInvoice.getToDate());
                    log.info("--------------------------------> Sum of PutAwayCharges <---------------------------------");
                    if (getPutAway != null && getPutAway.getTotalRate() != null && getPutAway.getTotalRate() != 0) {
                        ProformaInvoiceLine invoiceLine = new ProformaInvoiceLine();
                        BeanUtils.copyProperties(invH, invoiceLine, CommonUtils.getNullPropertyNames(invH));
                        invoiceLine.setProformaBillAmountLine(getPutAway.getTotalRate());
                        invoiceLine.setDescription("Putaway Charges");
                        invoiceLine.setServiceTypeId(2L);
                        invoiceLine.setTotalCbm(getPutAway.getTotalCbm());
                        invoiceLine.setCurrency(getPutAway.getCurrency());
                        invoiceLine.setFromDate(findInvoice.getFromDate());
                        invoiceLine.setToDate(findInvoice.getToDate());
                        invoiceLine.setPriceUnit(getPutAway.getTotalRate() / getPutAway.getTotalCbm());
                        newInvoiceLineList.add(invoiceLine);

                        totalCbm += getPutAway.getTotalCbm() != null ? getPutAway.getTotalCbm() : 0;
                        totalAmount += getPutAway.getTotalRate() != null ? getPutAway.getTotalRate() : 0;
                    }
                }

                if (findInvoice.getServiceTypeId() == null || findInvoice.getServiceTypeId().contains("3")) {
                    IKeyValuePair getStock = stockMovementRepository.getCbm(
                            invH.getCompanyCodeId(), invH.getPlantId(), invH.getWarehouseId(), invH.getPartnerCode(),
                            findInvoice.getFromDate(), findInvoice.getToDate());
                    log.info("Sum of 3PL StockMovement Charges");
                    if (getStock != null && getStock.getTotalRate() != null && getStock.getTotalRate() != 0) {
                        ProformaInvoiceLine invoiceLine = new ProformaInvoiceLine();
                        BeanUtils.copyProperties(invH, invoiceLine, CommonUtils.getNullPropertyNames(invH));
                        invoiceLine.setProformaBillAmountLine(getStock.getTotalRate());
                        invoiceLine.setDescription("Storage Charges");
                        invoiceLine.setServiceTypeId(3L);
                        invoiceLine.setTotalCbm(getStock.getTotalCbm());
                        invoiceLine.setCurrency(getStock.getCurrency());
                        invoiceLine.setFromDate(findInvoice.getFromDate());
                        invoiceLine.setToDate(findInvoice.getToDate());
                        invoiceLine.setPriceUnit(getStock.getTotalRate() / getStock.getTotalCbm());
                        newInvoiceLineList.add(invoiceLine);

                        totalCbm += getStock.getTotalCbm() != null ? getStock.getTotalCbm() : 0;
                        totalAmount += getStock.getTotalRate() != null ? getStock.getTotalRate() : 0;
                    }
                }

                if (findInvoice.getServiceTypeId() == null || findInvoice.getServiceTypeId().contains("4")) {
                    IKeyValuePair getPicking = pickupLineV2Repository.getCbm(
                            invH.getCompanyCodeId(), invH.getPlantId(), invH.getWarehouseId(), invH.getPartnerCode(),
                            findInvoice.getFromDate(), findInvoice.getToDate());
                    log.info("----------------------> Sum of PickingCharges <---------------------------- ");
                    if (getPicking != null && getPicking.getTotalRate() != null && getPicking.getTotalRate() != 0) {
                        ProformaInvoiceLine invoiceLine = new ProformaInvoiceLine();
                        BeanUtils.copyProperties(invH, invoiceLine, CommonUtils.getNullPropertyNames(invH));
                        invoiceLine.setProformaBillAmountLine(getPicking.getTotalRate());
                        invoiceLine.setDescription("Picking Charges");
                        invoiceLine.setServiceTypeId(4L);
                        invoiceLine.setTotalCbm(getPicking.getTotalCbm() != null ? getPicking.getTotalCbm() : 0D);
                        invoiceLine.setPriceUnit(getPicking.getTotalRate() / getPicking.getTotalCbm());
                        invoiceLine.setCurrency(getPicking.getCurrency());
                        invoiceLine.setFromDate(findInvoice.getFromDate());
                        invoiceLine.setToDate(findInvoice.getToDate());
                        newInvoiceLineList.add(invoiceLine);
                        totalCbm += getPicking.getTotalCbm() != null ? getPicking.getTotalCbm() : 0;
                        totalAmount += getPicking.getTotalRate() != null ? getPicking.getTotalRate() : 0;
                    }
                }

                if (findInvoice.getServiceTypeId() == null || findInvoice.getServiceTypeId().contains("5")) {
                   IKeyValuePair getPacking = packingHeaderRepository.getCbm(
                            invH.getCompanyCodeId(), invH.getPlantId(), invH.getWarehouseId(), invH.getPartnerCode(),
                            findInvoice.getFromDate(), findInvoice.getToDate());
                    log.info("-------------------------------> Sum of Packing Charges <---------------------------------");
                    log.info("Get Packing TotalRate -------> {}", getPacking.getTotalRate());
                    if (getPacking != null && getPacking.getTotalRate() != null && getPacking.getTotalRate() != 0) {
                        ProformaInvoiceLine invoiceLine = new ProformaInvoiceLine();
                        BeanUtils.copyProperties(invH, invoiceLine, CommonUtils.getNullPropertyNames(invH));
                        invoiceLine.setDescription("Packing Charges");
                        invoiceLine.setServiceTypeId(5L);
                        invoiceLine.setProformaBillAmountLine(getPacking.getTotalRate());
                        invoiceLine.setTotalCbm(getPacking.getTotalCbm());
                        invoiceLine.setCurrency(getPacking.getCurrency());
                        invoiceLine.setFromDate(findInvoice.getFromDate());
                        invoiceLine.setToDate(findInvoice.getToDate());
                        invoiceLine.setPriceUnit(getPacking.getTotalRate() / getPacking.getTotalCbm());
                        newInvoiceLineList.add(invoiceLine);

                        totalCbm += getPacking.getTotalCbm() != null ? getPacking.getTotalCbm() : 0;
                        totalAmount += getPacking.getTotalRate() != null ? getPacking.getTotalRate() : 0;
                    }
                }
                if(newInvoiceLineList.isEmpty()) {
                    throw new RuntimeException("No Charges Found This PartnerCode - " + findInvoice.getPartnerCode());
                }

                invH.setProformaBillAmount(totalAmount);
                invH.setTotalCbm(totalCbm);
                BeanUtils.copyProperties(invH, invoiceHeader);
                newInvoice.setProformaInvoiceHeader(invoiceHeader);
                newInvoice.setProformaInvoiceLineList(newInvoiceLineList);
                invoiceList.add(newInvoice);
            }
        }
        return invoiceList;
    }


}
