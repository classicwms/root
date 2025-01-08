package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.threepl.invoiceline.AddInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.invoiceline.FindInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.invoiceline.InvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.invoiceline.UpdateInvoiceLine;
import com.tekclover.wms.api.transaction.repository.InvoiceLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.InvoiceLineSpecification;
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

@Service
@Slf4j
public class InvoiceLineService extends BaseService {

    @Autowired
    private InvoiceLineRepository invoiceLineRepository;

    /**
     * getInvoiceLines
     * @return
     */
    public List<InvoiceLine> getInvoiceLines() {
        List<InvoiceLine> InvoiceLines = invoiceLineRepository.findAll();
        InvoiceLines = InvoiceLines.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return InvoiceLines;
    }

    /**
     * getInvoiceLine
     * @param invoiceNumber
     * @param partnerCode
     * @param lineNumber
     * @return
     */
    public InvoiceLine getInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      Long invoiceNumber, String partnerCode, Long lineNumber) {
        Optional<InvoiceLine> dbInvoiceLine =
                invoiceLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        invoiceNumber,
                        partnerCode,
                        lineNumber,
                        languageId,
                        0L
                );
        if (dbInvoiceLine.isEmpty()) {
            throw new BadRequestException("The given values : " +
                                                  "warehouseId - " + warehouseId +
                                                  "invoiceNumber - " + invoiceNumber +
                                                  "partnerCode - " + partnerCode +
                                                  "lineNumber-" + lineNumber +
                                                  " doesn't exist.");

        }
        return dbInvoiceLine.get();
    }

    /**
     * GetInvoiceLine
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param invoiceNumber
     * @param partnerCode
     * @return
     */
    public List<InvoiceLine> getInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            Long invoiceNumber, String partnerCode) {
        List<InvoiceLine> dbInvoiceLine = invoiceLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLanguageIdAndDeletionIndicator(
                companyCodeId, plantId, warehouseId, invoiceNumber, partnerCode, languageId, 0L);

        if (dbInvoiceLine.isEmpty()) {
            throw new BadRequestException("Given Values Doesn't exist");
        }
        return dbInvoiceLine;
    }

    /**
     * createInvoiceLine
     * @param newInvoiceLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<InvoiceLine> createInvoiceLine(List<AddInvoiceLine> newInvoiceLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<InvoiceLine> invoiceLines = new ArrayList<>();

        Long invoiceNo = invoiceLineRepository.getInvoiceNo();
        if (invoiceNo == null) {
            invoiceNo = 1L;
        } else {
            invoiceNo++;
        }
        for (AddInvoiceLine oldInvoice : newInvoiceLine) {
            InvoiceLine dbInvoiceLine = new InvoiceLine();
            Optional<InvoiceLine> duplicateCheck = invoiceLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(
                    oldInvoice.getCompanyCodeId(), oldInvoice.getPlantId(),
                    oldInvoice.getWarehouseId(), invoiceNo,
                    oldInvoice.getPartnerCode(), oldInvoice.getLineNumber(),
                    oldInvoice.getLanguageId(), 0L);
            if (duplicateCheck.isPresent()) {
                throw new BadRequestException("Given Values CompanyCodeId " + oldInvoice.getCompanyCodeId() +
                                                      " PlantId " + oldInvoice.getPlantId() + " LanguageId " + oldInvoice.getLanguageId() +
                                                      " warehouseId " + oldInvoice.getWarehouseId() + " InvoiceNumber " + invoiceNo +
                                                      " LineNumber " + oldInvoice.getLineNumber() + " Getting Duplicated ");
            }
            BeanUtils.copyProperties(oldInvoice, dbInvoiceLine, CommonUtils.getNullPropertyNames(oldInvoice));
            if (oldInvoice.getInvoiceHeaderId() == null) {
                throw new BadRequestException("Header Id is Must to create Line");
            }
            dbInvoiceLine.setInvoiceHeaderId(oldInvoice.getInvoiceHeaderId());
            dbInvoiceLine.setInvoiceNumber(invoiceNo);
            dbInvoiceLine.setDeletionIndicator(0L);
            dbInvoiceLine.setCreatedBy(loginUserID);
            dbInvoiceLine.setUpdatedBy(loginUserID);
            dbInvoiceLine.setCreatedOn(new Date());
            dbInvoiceLine.setUpdatedOn(new Date());
            invoiceLines.add(invoiceLineRepository.save(dbInvoiceLine));
        }
        return invoiceLines;
    }

    /**
     * updateInvoiceLine
     * @param loginUserID
     * @param invoiceNumber
     * @param partnerCode
     * @param lineNumber
     * @param updateInvoiceLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InvoiceLine updateInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                         Long invoiceNumber, String partnerCode, Long lineNumber, String loginUserID,
                                         UpdateInvoiceLine updateInvoiceLine)
            throws IllegalAccessException, InvocationTargetException {
        InvoiceLine dbInvoiceLine = getInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode, lineNumber);
        BeanUtils.copyProperties(updateInvoiceLine, dbInvoiceLine, CommonUtils.getNullPropertyNames(updateInvoiceLine));
        dbInvoiceLine.setUpdatedBy(loginUserID);
        dbInvoiceLine.setUpdatedOn(new Date());
        return invoiceLineRepository.save(dbInvoiceLine);
    }

    /**
     * @param updateInvoiceLine
     * @param loginUserID
     * @return
     */
    public List<InvoiceLine> updateInvoiceLine(List<UpdateInvoiceLine> updateInvoiceLine, String loginUserID) {
        List<InvoiceLine> invoiceLineList = new ArrayList<>();
        for (UpdateInvoiceLine inputInvoice : updateInvoiceLine) {
            InvoiceLine dbInvoiceLine = getInvoiceLine(inputInvoice.getCompanyCodeId(), inputInvoice.getPlantId(),
                                                       inputInvoice.getLanguageId(), inputInvoice.getWarehouseId(), inputInvoice.getInvoiceNumber(),
                                                       inputInvoice.getPartnerCode(), inputInvoice.getLineNumber());

            BeanUtils.copyProperties(inputInvoice, dbInvoiceLine, CommonUtils.getNullPropertyNames(inputInvoice));
            dbInvoiceLine.setUpdatedBy(loginUserID);
            dbInvoiceLine.setUpdatedOn(new Date());
            invoiceLineList.add(invoiceLineRepository.save(dbInvoiceLine));
        }
        return invoiceLineList;
    }

    /**
     * deleteInvoiceLine
     * @param loginUserID
     * @param invoiceNumber
     * @param partnerCode
     * @param lineNumber
     */
    public void deleteInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                  Long invoiceNumber, String partnerCode, Long lineNumber, String loginUserID) {
        InvoiceLine dbInvoiceLine = getInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode, lineNumber);
        if (dbInvoiceLine != null) {
            dbInvoiceLine.setDeletionIndicator(1L);
            dbInvoiceLine.setUpdatedBy(loginUserID);
            invoiceLineRepository.save(dbInvoiceLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     * FindInvoiceLine
     * @param findInvoiceLine
     * @return
     */
    public List<InvoiceLine> findInvoiceLine(FindInvoiceLine findInvoiceLine) {

        InvoiceLineSpecification spec = new InvoiceLineSpecification(findInvoiceLine);
        List<InvoiceLine> result = invoiceLineRepository.findAll(spec);
        log.info("found InvoiceLine --> " + result);
        return result;
    }
}