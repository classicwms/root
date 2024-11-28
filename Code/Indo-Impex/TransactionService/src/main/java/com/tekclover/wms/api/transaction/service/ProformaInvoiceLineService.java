package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.AddProformaInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.FindProformaInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.UpdateProformaInvoiceLine;
import com.tekclover.wms.api.transaction.repository.ProformaInvoiceLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.ProformaInvoiceLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProformaInvoiceLineService extends BaseService {

    @Autowired
    private ProformaInvoiceLineRepository proformaInvoiceLineRepository;

    /**
     * getProformaInvoiceLines
     * @return
     */
    public List<ProformaInvoiceLine> getProformaInvoiceLines() {
        List<ProformaInvoiceLine> ProformaInvoiceLines = proformaInvoiceLineRepository.findAll();
        ProformaInvoiceLines = ProformaInvoiceLines.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return ProformaInvoiceLines;
    }

    /**
     * getProformaInvoiceLine
     * @param proformaBillNo
     * @param partnerCode
     * @param lineNumber
     * @return
     */
    public ProformaInvoiceLine getProformaInvoiceLine(String companyCodeId, String plantId, String languageId,
                                                      String warehouseId, Long proformaBillNo, String partnerCode, Long lineNumber) {
        Optional<ProformaInvoiceLine> dbProformaInvoiceLine =
                proformaInvoiceLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        proformaBillNo,
                        partnerCode,
                        lineNumber,
                        languageId,
                        0L
                );
        if (dbProformaInvoiceLine.isEmpty()) {
            throw new BadRequestException("The given values : " +
                                                  "warehouseId - " + warehouseId +
                                                  "proformaBillNo - " + proformaBillNo +
                                                  "partnerCode - " + partnerCode +
                                                  "lineNumber-" + lineNumber +
                                                  " doesn't exist.");

        }
        return dbProformaInvoiceLine.get();
    }


    /**
     * GetProformaInvoice
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param proformaBillNo
     * @param partnerCode
     * @return
     */
    public List<ProformaInvoiceLine> getProformaInvoice(String companyCodeId, String plantId, String languageId,
                                                        String warehouseId, Long proformaBillNo, String partnerCode) {
        List<ProformaInvoiceLine> invoiceLineList =
                proformaInvoiceLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        proformaBillNo,
                        partnerCode,
                        languageId,
                        0L);
        if (invoiceLineList.isEmpty()) {
            throw new BadRequestException("Given values doesn't exist");
        }
        return invoiceLineList;
    }


    /**
     * createProformaInvoiceLine
     * @param newProformaInvoiceLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<ProformaInvoiceLine> createProformaInvoiceLine(List<AddProformaInvoiceLine> newProformaInvoiceLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        List<ProformaInvoiceLine> proformaInvoiceLineList = new ArrayList<>();
        Long proformaNo = proformaInvoiceLineRepository.getProformaNo();
        if (proformaNo == null) {
            proformaNo = 1L;
        } else {
            proformaNo++;
        }
        for (AddProformaInvoiceLine newInvoice : newProformaInvoiceLine) {
            ProformaInvoiceLine dbProformaInvoiceLine = new ProformaInvoiceLine();
            Optional<ProformaInvoiceLine> duplicateCheck =
                    proformaInvoiceLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(
                            newInvoice.getCompanyCodeId(), newInvoice.getPlantId(),
                            newInvoice.getWarehouseId(), proformaNo,
                            newInvoice.getPartnerCode(), newInvoice.getLineNumber(),
                            newInvoice.getLanguageId(), 0L);
            if (duplicateCheck.isPresent()) {
                throw new BadRequestException("Given values CompanyId " + newInvoice.getCompanyCodeId() +
                                                      " PlantId " + newInvoice.getPlantId() + " LanguageId " + newInvoice.getLanguageId() +
                                                      " WarehouseId " + newInvoice.getWarehouseId() + " ProformaBillNo " + proformaNo +
                                                      " PartnerCode " + newInvoice.getPartnerCode() + " LineNumber " + newInvoice.getLineNumber() +
                                                      " Getting Duplicate ");
            }
            BeanUtils.copyProperties(newInvoice, dbProformaInvoiceLine, CommonUtils.getNullPropertyNames(newInvoice));
            if (newInvoice.getProformaHeaderId() == null) {
                throw new BadRequestException("Header Id is Must to create Line");
            }
            dbProformaInvoiceLine.setProformaHeaderId(newInvoice.getProformaHeaderId());
            dbProformaInvoiceLine.setProformaBillNo(proformaNo);
            dbProformaInvoiceLine.setDeletionIndicator(0L);
            dbProformaInvoiceLine.setCreatedBy(loginUserID);
            dbProformaInvoiceLine.setUpdatedBy(loginUserID);
            dbProformaInvoiceLine.setCreatedOn(new Date());
            dbProformaInvoiceLine.setUpdatedOn(new Date());
            proformaInvoiceLineList.add(proformaInvoiceLineRepository.save(dbProformaInvoiceLine));
        }
        return proformaInvoiceLineList;
    }

    /**
     * updateProformaInvoiceLine
     * @param loginUserID
     * @param proformaBillNo
     * @param partnerCode
     * @param lineNumber
     * @param updateProformaInvoiceLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ProformaInvoiceLine updateProformaInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId, Long proformaBillNo, String partnerCode, Long lineNumber, String loginUserID,
                                                         UpdateProformaInvoiceLine updateProformaInvoiceLine)
            throws IllegalAccessException, InvocationTargetException {
        ProformaInvoiceLine dbProformaInvoiceLine = getProformaInvoiceLine(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, lineNumber);
        BeanUtils.copyProperties(updateProformaInvoiceLine, dbProformaInvoiceLine, CommonUtils.getNullPropertyNames(updateProformaInvoiceLine));
        dbProformaInvoiceLine.setUpdatedBy(loginUserID);
        dbProformaInvoiceLine.setUpdatedOn(new Date());
        return proformaInvoiceLineRepository.save(dbProformaInvoiceLine);
    }

    /**
     * deleteProformaInvoiceLine
     * @param loginUserID
     * @param proformaBillNo
     * @param partnerCode
     * @param lineNumber
     */
    public void deleteProformaInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId, Long proformaBillNo, String partnerCode, Long lineNumber, String loginUserID) {
        ProformaInvoiceLine dbProformaInvoiceLine = getProformaInvoiceLine(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, lineNumber);
        if (dbProformaInvoiceLine != null) {
            dbProformaInvoiceLine.setDeletionIndicator(1L);
            dbProformaInvoiceLine.setUpdatedBy(loginUserID);
            proformaInvoiceLineRepository.save(dbProformaInvoiceLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     * @param findProformaInvoiceLine
     * @return
     */
    public List<ProformaInvoiceLine> findProformaInvoiceLine(FindProformaInvoiceLine findProformaInvoiceLine) {
        ProformaInvoiceLineSpecification spec = new ProformaInvoiceLineSpecification(findProformaInvoiceLine);
        List<ProformaInvoiceLine> results = proformaInvoiceLineRepository.findAll(spec);
        log.info("Found ProformaInvoiceLine --> " + results);
        return results;
    }
}