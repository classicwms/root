package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceline.AddProformaInvoiceLine;
import com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceline.UpdateProformaInvoiceLine;
import com.tekclover.wms.api.enterprise.transaction.repository.ProformaInvoiceLineRepository;
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

@Slf4j
@Service
public class ProformaInvoiceLineService extends BaseService {

    @Autowired
    private ProformaInvoiceLineRepository proformaInvoiceLineRepository;

    /**
     * getProformaInvoiceLines
     *
     * @return
     */
    public List<ProformaInvoiceLine> getProformaInvoiceLines() {
        List<ProformaInvoiceLine> ProformaInvoiceLines = proformaInvoiceLineRepository.findAll();
        ProformaInvoiceLines = ProformaInvoiceLines.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return ProformaInvoiceLines;
    }

    /**
     * getProformaInvoiceLine
     *
     * @param proformaBillNo
     * @param partnerCode
     * @param lineNumber
     * @return
     */
    public ProformaInvoiceLine getProformaInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId, String proformaBillNo, String partnerCode, Long lineNumber) {
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
     * createProformaInvoiceLine
     *
     * @param newProformaInvoiceLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ProformaInvoiceLine createProformaInvoiceLine(AddProformaInvoiceLine newProformaInvoiceLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        ProformaInvoiceLine dbProformaInvoiceLine = new ProformaInvoiceLine();
        log.info("newProformaInvoiceLine : " + newProformaInvoiceLine);
        BeanUtils.copyProperties(newProformaInvoiceLine, dbProformaInvoiceLine, CommonUtils.getNullPropertyNames(newProformaInvoiceLine));
        dbProformaInvoiceLine.setCompanyCodeId(getCompanyCode());
        dbProformaInvoiceLine.setPlantId(getPlantId());
        dbProformaInvoiceLine.setDeletionIndicator(0L);
        dbProformaInvoiceLine.setCreatedBy(loginUserID);
        dbProformaInvoiceLine.setUpdatedBy(loginUserID);
        dbProformaInvoiceLine.setCreatedOn(new Date());
        dbProformaInvoiceLine.setUpdatedOn(new Date());
        return proformaInvoiceLineRepository.save(dbProformaInvoiceLine);
    }

    /**
     * updateProformaInvoiceLine
     *
     * @param loginUserID
     * @param proformaBillNo
     * @param partnerCode
     * @param lineNumber
     * @param updateProformaInvoiceLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ProformaInvoiceLine updateProformaInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId, String proformaBillNo, String partnerCode, Long lineNumber, String loginUserID,
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
     *
     * @param loginUserID
     * @param proformaBillNo
     * @param partnerCode
     * @param lineNumber
     */
    public void deleteProformaInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId, String proformaBillNo, String partnerCode, Long lineNumber, String loginUserID) {
        ProformaInvoiceLine dbProformaInvoiceLine = getProformaInvoiceLine(companyCodeId, plantId, languageId, warehouseId, proformaBillNo, partnerCode, lineNumber);
        if (dbProformaInvoiceLine != null) {
            dbProformaInvoiceLine.setDeletionIndicator(1L);
            dbProformaInvoiceLine.setUpdatedBy(loginUserID);
            proformaInvoiceLineRepository.save(dbProformaInvoiceLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }
}