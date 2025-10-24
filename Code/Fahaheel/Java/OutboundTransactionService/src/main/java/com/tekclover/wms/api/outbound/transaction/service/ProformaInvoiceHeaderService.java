package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.threepl.proformainvoiceheader.AddProformaInvoiceHeader;
import com.tekclover.wms.api.outbound.transaction.model.threepl.proformainvoiceheader.ProformaInvoiceHeader;
import com.tekclover.wms.api.outbound.transaction.model.threepl.proformainvoiceheader.UpdateProformaInvoiceHeader;
import com.tekclover.wms.api.outbound.transaction.repository.ProformaInvoiceHeaderRepository;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
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
public class ProformaInvoiceHeaderService extends BaseService {

    @Autowired
    private ProformaInvoiceHeaderRepository proformaInvoiceHeaderRepository;

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
        dbProformaInvoiceHeader.setCompanyCodeId(getCompanyCode());
        dbProformaInvoiceHeader.setPlantId(getPlantId());
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
}
