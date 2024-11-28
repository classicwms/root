package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.barcode.AddBarcode;
import com.tekclover.wms.api.enterprise.model.barcode.Barcode;
import com.tekclover.wms.api.enterprise.model.barcode.SearchBarcode;
import com.tekclover.wms.api.enterprise.model.barcode.UpdateBarcode;
import com.tekclover.wms.api.enterprise.repository.BarcodeRepository;
import com.tekclover.wms.api.enterprise.repository.specification.BarcodeSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BarcodeService extends BaseService {

    @Autowired
    private BarcodeRepository barcodeRepository;

    /**
     * getBarcodes
     *
     * @return
     */
    public List<Barcode> getBarcodes() {
        try {
            List<Barcode> barcodeList = barcodeRepository.findAll();
            log.info("barcodeList : " + barcodeList);
            barcodeList = barcodeList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return barcodeList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getBarcode
     *
     * @param warehouseId
     * @param method
     * @param barcodeTypeId
     * @param barcodeSubTypeId
     * @param levelId
     * @param levelReference
     * @param processId
     * @return
     */
    public Barcode getBarcode(String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId,
                              Long levelId, String levelReference, Long processId) {
        try {
            Optional<Barcode> barcode =
                    barcodeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndMethodAndBarcodeTypeIdAndBarcodeSubTypeIdAndLevelIdAndLevelReferenceAndProcessIdAndDeletionIndicator(
                            getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, method,
                            barcodeTypeId, barcodeSubTypeId, levelId, levelReference, processId, 0L);
            if (barcode.isEmpty()) {
                throw new BadRequestException("The given Barcode Id : " + barcodeTypeId + " doesn't exist.");
            }
            return barcode.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findBarcode
     *
     * @param searchBarcode
     * @return
     */
    public List<Barcode> findBarcode(SearchBarcode searchBarcode) {
        try {
            if (searchBarcode.getStartCreatedOn() != null && searchBarcode.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchBarcode.getStartCreatedOn(), searchBarcode.getEndCreatedOn());
                searchBarcode.setStartCreatedOn(dates[0]);
                searchBarcode.setEndCreatedOn(dates[1]);
            }

            BarcodeSpecification spec = new BarcodeSpecification(searchBarcode);
            List<Barcode> results = barcodeRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param newBarcode
     * @param loginUserID
     * @return
     */
    public Barcode createBarcode(AddBarcode newBarcode, String loginUserID) {
        try {
            Optional<Barcode> optBarcode =
                    barcodeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndMethodAndBarcodeTypeIdAndBarcodeSubTypeIdAndLevelIdAndLevelReferenceAndProcessIdAndDeletionIndicator(
                            getLanguageId(),
                            getCompanyCode(),
                            getPlantId(),
                            newBarcode.getWarehouseId(),
                            newBarcode.getMethod(),
                            newBarcode.getBarcodeTypeId(),
                            newBarcode.getBarcodeSubTypeId(),
                            newBarcode.getLevelId(),
                            newBarcode.getLevelReference(),
                            newBarcode.getProcessId(),
                            0L);
            if (!optBarcode.isEmpty()) {
                throw new BadRequestException("The given values are getting duplicated.");
            }

            Barcode dbBarcode = new Barcode();
            BeanUtils.copyProperties(newBarcode, dbBarcode, CommonUtils.getNullPropertyNames(newBarcode));

            dbBarcode.setLanguageId(getLanguageId());
            dbBarcode.setCompanyId(getCompanyCode());
            dbBarcode.setPlantId(getPlantId());
            dbBarcode.setDeletionIndicator(0L);
            dbBarcode.setCompanyId(getCompanyCode());
            dbBarcode.setCreatedBy(loginUserID);
            dbBarcode.setUpdatedBy(loginUserID);
            dbBarcode.setCreatedOn(new Date());
            dbBarcode.setUpdatedOn(new Date());
            return barcodeRepository.save(dbBarcode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param warehouseId
     * @param method
     * @param barcodeTypeId
     * @param barcodeSubTypeId
     * @param levelId
     * @param levelReference
     * @param processId
     * @param updateBarcode
     * @param loginUserID
     * @return
     */
    public Barcode updateBarcode(String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId,
                                 Long levelId, String levelReference, Long processId, UpdateBarcode updateBarcode, String loginUserID) {
        try {
            Barcode dbBarcode = getBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, levelId,
                    levelReference, processId);
            BeanUtils.copyProperties(updateBarcode, dbBarcode, CommonUtils.getNullPropertyNames(updateBarcode));
            dbBarcode.setUpdatedBy(loginUserID);
            dbBarcode.setUpdatedOn(new Date());
            return barcodeRepository.save(dbBarcode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param warehouseId
     * @param method
     * @param barcodeTypeId
     * @param barcodeSubTypeId
     * @param levelId
     * @param levelReference
     * @param processId
     * @param loginUserID
     */
    public void deleteBarcode(String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId,
                              Long levelId, String levelReference, Long processId, String loginUserID) {
        try {
            Barcode barcode = getBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, levelId,
                    levelReference, processId);
            if (barcode != null) {
                barcode.setDeletionIndicator(1L);
                barcode.setUpdatedBy(loginUserID);
                barcode.setUpdatedOn(new Date());
                barcodeRepository.save(barcode);
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + barcodeTypeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}