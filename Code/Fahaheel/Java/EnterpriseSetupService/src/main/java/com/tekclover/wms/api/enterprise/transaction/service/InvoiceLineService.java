package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.model.threepl.invoiceline.AddInvoiceLine;
import com.tekclover.wms.api.enterprise.transaction.model.threepl.invoiceline.InvoiceLine;
import com.tekclover.wms.api.enterprise.transaction.model.threepl.invoiceline.UpdateInvoiceLine;
import com.tekclover.wms.api.enterprise.transaction.repository.InvoiceLineRepository;
import com.tekclover.wms.api.enterprise.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
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
    public List<InvoiceLine> getInvoiceLines () {
        List<InvoiceLine> InvoiceLines =  invoiceLineRepository.findAll();
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
    public InvoiceLine getInvoiceLine (String companyCodeId, String plantId, String languageId, String warehouseId, String invoiceNumber, String partnerCode,Long lineNumber) {
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
                    "lineNumber-"+lineNumber+
                    " doesn't exist.");

        }
        return dbInvoiceLine.get();
    }

    /**
     * createInvoiceLine
     * @param newInvoiceLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InvoiceLine createInvoiceLine (AddInvoiceLine newInvoiceLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InvoiceLine dbInvoiceLine = new InvoiceLine();
        log.info("newInvoiceLine : " + newInvoiceLine);
        BeanUtils.copyProperties(newInvoiceLine, dbInvoiceLine, CommonUtils.getNullPropertyNames(newInvoiceLine));
        dbInvoiceLine.setCompanyCodeId(getCompanyCode());
        dbInvoiceLine.setPlantId(getPlantId());
        dbInvoiceLine.setDeletionIndicator(0L);
        dbInvoiceLine.setCreatedBy(loginUserID);
        dbInvoiceLine.setUpdatedBy(loginUserID);
        dbInvoiceLine.setCreatedOn(new Date());
        dbInvoiceLine.setUpdatedOn(new Date());
        return invoiceLineRepository.save(dbInvoiceLine);
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
    public InvoiceLine updateInvoiceLine (String companyCodeId, String plantId, String languageId, String warehouseId,
                                          String invoiceNumber, String partnerCode,Long lineNumber, String loginUserID,
                                      UpdateInvoiceLine updateInvoiceLine)
            throws IllegalAccessException, InvocationTargetException {
        InvoiceLine dbInvoiceLine = getInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode,lineNumber);
        BeanUtils.copyProperties(updateInvoiceLine, dbInvoiceLine, CommonUtils.getNullPropertyNames(updateInvoiceLine));
        dbInvoiceLine.setUpdatedBy(loginUserID);
        dbInvoiceLine.setUpdatedOn(new Date());
        return invoiceLineRepository.save(dbInvoiceLine);
    }

    /**
     * deleteInvoiceLine
     * @param loginUserID
     * @param invoiceNumber
     * @param partnerCode
     * @param lineNumber
     */
    public void deleteInvoiceLine (String companyCodeId, String plantId, String languageId, String warehouseId,
                                   String invoiceNumber, String partnerCode,Long lineNumber, String loginUserID) {
        InvoiceLine dbInvoiceLine = getInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode,lineNumber);
        if ( dbInvoiceLine != null) {
            dbInvoiceLine.setDeletionIndicator(1L);
            dbInvoiceLine.setUpdatedBy(loginUserID);
            invoiceLineRepository.save(dbInvoiceLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }
}