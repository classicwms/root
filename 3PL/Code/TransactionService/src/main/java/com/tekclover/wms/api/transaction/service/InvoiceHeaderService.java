package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.threepl.invoiceheader.*;
import com.tekclover.wms.api.transaction.repository.InvoiceHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.InvoiceHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
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

@Slf4j
@Service

public class InvoiceHeaderService extends BaseService {

    @Autowired
    private InvoiceHeaderRepository invoiceHeaderRepository;

    /**
     * getInvoiceHeaders
     *
     * @return
     */
    public List<InvoiceHeader> getInvoiceHeaders() {
        List<InvoiceHeader> InvoiceHeaders = invoiceHeaderRepository.findAll();
        InvoiceHeaders = InvoiceHeaders.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return InvoiceHeaders;
    }

    /**
     * getInvoiceHeader
     *
     * @param invoiceNumber
     * @param partnerCode
     * @return
     */
    public InvoiceHeader getInvoiceHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String invoiceNumber, String partnerCode) {
        Optional<InvoiceHeader> dbInvoiceHeader =
                invoiceHeaderRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        invoiceNumber,
                        partnerCode,
                        languageId,
                        0L
                );
        if (dbInvoiceHeader.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "invoiceNumber - " + invoiceNumber +
                    "partnerCode - " + partnerCode +
                    " doesn't exist.");

        }
        return dbInvoiceHeader.get();
    }

    /**
     * createInvoiceHeader
     *
     * @param newInvoiceHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InvoiceHeader createInvoiceHeader(AddInvoiceHeader newInvoiceHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InvoiceHeader dbInvoiceHeader = new InvoiceHeader();
        log.info("newInvoiceHeader : " + newInvoiceHeader);
        BeanUtils.copyProperties(newInvoiceHeader, dbInvoiceHeader, CommonUtils.getNullPropertyNames(newInvoiceHeader));
        dbInvoiceHeader.setDeletionIndicator(0L);
        dbInvoiceHeader.setCreatedBy(loginUserID);
        dbInvoiceHeader.setUpdatedBy(loginUserID);
        dbInvoiceHeader.setCreatedOn(new Date());
        dbInvoiceHeader.setUpdatedOn(new Date());
        return invoiceHeaderRepository.save(dbInvoiceHeader);
    }

    /**
     * updateInvoiceHeader
     *
     * @param loginUserID
     * @param invoiceNumber
     * @param partnerCode
     * @param updateInvoiceHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InvoiceHeader updateInvoiceHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
                                             String invoiceNumber, String partnerCode, String loginUserID,
                                             UpdateInvoiceHeader updateInvoiceHeader)
            throws IllegalAccessException, InvocationTargetException {
        InvoiceHeader dbInvoiceHeader = getInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode);
        BeanUtils.copyProperties(updateInvoiceHeader, dbInvoiceHeader, CommonUtils.getNullPropertyNames(updateInvoiceHeader));
        dbInvoiceHeader.setUpdatedBy(loginUserID);
        dbInvoiceHeader.setUpdatedOn(new Date());
        return invoiceHeaderRepository.save(dbInvoiceHeader);
    }

    /**
     * deleteInvoiceHeader
     *
     * @param loginUserID
     * @param invoiceNumber
     * @param partnerCode
     */
    public void deleteInvoiceHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String invoiceNumber, String partnerCode, String loginUserID) {
        InvoiceHeader dbInvoiceHeader = getInvoiceHeader(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode);
        if (dbInvoiceHeader != null) {
            dbInvoiceHeader.setDeletionIndicator(1L);
            dbInvoiceHeader.setUpdatedBy(loginUserID);
            invoiceHeaderRepository.save(dbInvoiceHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + invoiceNumber);
        }
    }

    /**
     * @param findInvoiceHeader
     * @return
     * @throws Exception
     */
    public List<InvoiceHeader> findInvoiceHeader(FindInvoiceHeader findInvoiceHeader)
            throws Exception {
        InvoiceHeaderSpecification spec = new InvoiceHeaderSpecification(findInvoiceHeader);
        List<InvoiceHeader> results = invoiceHeaderRepository.findAll(spec);
        log.info("results" + results);
        return results;
    }
}
