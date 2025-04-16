package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.threepl.invoiceline.FindInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.invoiceline.InvoiceLine;
import com.tekclover.wms.api.transaction.repository.InvoiceLineRepository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
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

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    /**
     * getInvoiceLines
     *
     * @return
     */
    public List<InvoiceLine> getInvoiceLines() {
        List<InvoiceLine> InvoiceLines = invoiceLineRepository.findAll();
        InvoiceLines = InvoiceLines.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return InvoiceLines;
    }

    /**
     * getInvoiceLine
     *
     * @param invoiceNumber
     * @param partnerCode
     * @param lineNumber
     * @return
     */
    public InvoiceLine getInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId, String invoiceNumber, String partnerCode, Long lineNumber) {
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
     * createInvoiceLine
     *
     * @param newInvoiceLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<InvoiceLine> createInvoiceLine(List<InvoiceLine> newInvoiceLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<InvoiceLine> newInvoiceLineList = new ArrayList<>();
        newInvoiceLine.stream().forEach(invoiceLine -> {
            InvoiceLine dbInvoiceLine = new InvoiceLine();
            log.info("newInvoiceLine : " + newInvoiceLine);
            BeanUtils.copyProperties(invoiceLine, dbInvoiceLine, CommonUtils.getNullPropertyNames(invoiceLine));
            IKeyValuePair description = stagingLineV2Repository.getDescription(dbInvoiceLine.getCompanyCodeId(),
                    dbInvoiceLine.getLanguageId(),
                    dbInvoiceLine.getPlantId(),
                    dbInvoiceLine.getWarehouseId());

            dbInvoiceLine.setCompanyDescription(description.getCompanyDesc());
            dbInvoiceLine.setPlantDescription(description.getPlantDesc());
            dbInvoiceLine.setWarehouseDescription(description.getWarehouseDesc());
            dbInvoiceLine.setDeletionIndicator(0L);
            dbInvoiceLine.setCreatedBy(loginUserID);
            dbInvoiceLine.setUpdatedBy(loginUserID);
            dbInvoiceLine.setCreatedOn(new Date());
            dbInvoiceLine.setUpdatedOn(new Date());
            invoiceLineRepository.save(dbInvoiceLine);
            newInvoiceLineList.add(dbInvoiceLine);
        });
        return newInvoiceLineList;
    }

//    /**
//     * updateInvoiceLine
//     * @param loginUserID
//     * @param invoiceNumber
//     * @param partnerCode
//     * @param lineNumber
//     * @param updateInvoiceLine
//     * @return
//     * @throws IllegalAccessException
//     * @throws InvocationTargetException
//     */
//    public InvoiceLine updateInvoiceLine (String companyCodeId, String plantId, String languageId, String warehouseId,
//                                          String invoiceNumber, String partnerCode,Long lineNumber, String loginUserID,
//                                      UpdateInvoiceLine updateInvoiceLine)
//            throws IllegalAccessException, InvocationTargetException {
//        InvoiceLine dbInvoiceLine = getInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode,lineNumber);
//        BeanUtils.copyProperties(updateInvoiceLine, dbInvoiceLine, CommonUtils.getNullPropertyNames(updateInvoiceLine));
//        dbInvoiceLine.setUpdatedBy(loginUserID);
//        dbInvoiceLine.setUpdatedOn(new Date());
//        return invoiceLineRepository.save(dbInvoiceLine);
//    }

    /**
     * deleteInvoiceLine
     *
     * @param loginUserID
     * @param invoiceNumber
     * @param partnerCode
     * @param lineNumber
     */
    public void deleteInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                  String invoiceNumber, String partnerCode, Long lineNumber, String loginUserID) {
        InvoiceLine dbInvoiceLine = getInvoiceLine(companyCodeId, plantId, languageId, warehouseId, invoiceNumber, partnerCode, lineNumber);
        if (dbInvoiceLine != null) {
            dbInvoiceLine.setDeletionIndicator(1L);
            dbInvoiceLine.setUpdatedBy(loginUserID);
            invoiceLineRepository.save(dbInvoiceLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    //Find InvoiceLine
    public List<InvoiceLine> findInvoiceLine(FindInvoiceLine findInvoiceLine)
            throws Exception {
        InvoiceLineSpecification spec = new InvoiceLineSpecification(findInvoiceLine);
        List<InvoiceLine> results = invoiceLineRepository.findAll(spec);
        log.info("results" + results);
        return results;
    }

    public List<InvoiceLine> updateInvoiceLine(List<InvoiceLine> updateInvoiceLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<InvoiceLine> invoiceLineList = new ArrayList<>();
        for (InvoiceLine updateLine : updateInvoiceLines) {
            invoiceLineRepository.deleteInvoiceLine(updateLine.getInvoiceNumber(), updateLine.getLanguageId(), updateLine.getCompanyCodeId(), updateLine.getPlantId(), updateLine.getWarehouseId(),
                    updateLine.getPartnerCode());
            log.info("Deleted Successfully: " + updateLine.getInvoiceNumber());
        }
        for (InvoiceLine updateInvoiceLine : updateInvoiceLines) {
            InvoiceLine newInvoiceLine = new InvoiceLine();
            BeanUtils.copyProperties(updateInvoiceLine, newInvoiceLine, CommonUtils.getNullPropertyNames(updateInvoiceLine));
            newInvoiceLine.setDeletionIndicator(0L);
            newInvoiceLine.setUpdatedOn(new Date());
            newInvoiceLine.setUpdatedBy(loginUserID);
            invoiceLineRepository.save(newInvoiceLine);
            invoiceLineList.add(newInvoiceLine);
        }
        return invoiceLineList;
    }
}