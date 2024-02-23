package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.handlingequipment.AddHandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.HandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.SearchHandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.UpdateHandlingEquipment;
import com.tekclover.wms.api.masters.repository.HandlingEquipmentRepository;
import com.tekclover.wms.api.masters.repository.specification.HandlingEquipmentSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HandlingEquipmentService {

    @Autowired
    private HandlingEquipmentRepository handlingequipmentRepository;

    /**
     * getHandlingEquipments
     *
     * @return
     */
    public List<HandlingEquipment> getHandlingEquipments() {
        List<HandlingEquipment> handlingequipmentList = handlingequipmentRepository.findAll();
        log.info("handlingequipmentList : " + handlingequipmentList);
        handlingequipmentList = handlingequipmentList.stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return handlingequipmentList;
    }

    /**
     * getHandlingEquipment
     *
     * @param handlingEquipmentId
     * @return
     */
    public HandlingEquipment getHandlingEquipment(String warehouseId, String handlingEquipmentId, String companyCodeId, String languageId, String plantId) {
        Optional<HandlingEquipment> dbHandlingEquipment =
                handlingequipmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingEquipmentIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        handlingEquipmentId,
                        languageId,
                        0L
                );
        if (dbHandlingEquipment.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "handlingEquipmentId - " + handlingEquipmentId +
                    "doesn't exist.");

        }
        return dbHandlingEquipment.get();
    }

    /**
     * @param warehouseId
     * @param heBarcode
     * @return
     */
    public HandlingEquipment getHandlingEquipment(String warehouseId, String heBarcode) {
        Optional<HandlingEquipment> handlingequipment =
                handlingequipmentRepository.findByHeBarcodeAndWarehouseIdAndDeletionIndicator(heBarcode, warehouseId, 0L);
        if (!handlingequipment.isEmpty()) {
            return handlingequipment.get();
        } else {
            throw new BadRequestException("The given values: warehouseId-" + warehouseId + ", heBarcode - " + heBarcode + " doesn't exist.");
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param heBarcode
     * @return
     */
    public HandlingEquipment getHandlingEquipmentV2(String companyCodeId, String plantId, String languageId, String warehouseId, String heBarcode) {
        Optional<HandlingEquipment> handlingequipment =
                handlingequipmentRepository.findByHeBarcodeAndCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
                        heBarcode, companyCodeId, plantId, languageId, warehouseId, 0L);
        if (!handlingequipment.isEmpty()) {
            return handlingequipment.get();
        } else {
            throw new BadRequestException("The given values: warehouseId-" + warehouseId + ", heBarcode - " + heBarcode + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param handlingEquipmentId
     * @return
     */
    public HandlingEquipment getHandlingEquipmentByWarehouseId(String warehouseId, String handlingEquipmentId) {
        Optional<HandlingEquipment> handlingequipment =
                handlingequipmentRepository.findByHandlingEquipmentIdAndWarehouseIdAndDeletionIndicator(handlingEquipmentId, warehouseId, 0L);
        if (!handlingequipment.isEmpty()) {
            return handlingequipment.get();
        } else {
            throw new BadRequestException("The given values: warehouseId-" + warehouseId + ", "
                    + "heBarcode - " + handlingEquipmentId + " doesn't exist.");
        }
    }

    /**
     * @param searchHandlingEquipment
     * @return
     * @throws Exception
     */
    public List<HandlingEquipment> findHandlingEquipment(SearchHandlingEquipment searchHandlingEquipment)
            throws Exception {
        HandlingEquipmentSpecification spec = new HandlingEquipmentSpecification(searchHandlingEquipment);
        List<HandlingEquipment> results = handlingequipmentRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * createHandlingEquipment
     *
     * @param newHandlingEquipment
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public HandlingEquipment createHandlingEquipment(AddHandlingEquipment newHandlingEquipment, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        HandlingEquipment dbHandlingEquipment = new HandlingEquipment();
        Optional<HandlingEquipment> duplicateHandlingEquipmentId = handlingequipmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingEquipmentIdAndLanguageIdAndDeletionIndicator(newHandlingEquipment.getCompanyCodeId(), newHandlingEquipment.getPlantId(), newHandlingEquipment.getWarehouseId(), newHandlingEquipment.getHandlingEquipmentId(), newHandlingEquipment.getLanguageId(), 0L);
        if (!duplicateHandlingEquipmentId.isEmpty()) {
            throw new BadRequestException("Record is Getting Duplicated");
        } else {
            BeanUtils.copyProperties(newHandlingEquipment, dbHandlingEquipment, CommonUtils.getNullPropertyNames(newHandlingEquipment));
            dbHandlingEquipment.setDeletionIndicator(0L);
            dbHandlingEquipment.setCreatedBy(loginUserID);
            dbHandlingEquipment.setUpdatedBy(loginUserID);
            dbHandlingEquipment.setCreatedOn(new Date());
            dbHandlingEquipment.setUpdatedOn(new Date());
            return handlingequipmentRepository.save(dbHandlingEquipment);
        }
    }

    /**
     * updateHandlingEquipment
     *
     * @param handlingEquipmentId
     * @param updateHandlingEquipment
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public HandlingEquipment updateHandlingEquipment(String handlingEquipmentId, String companyCodeId, String plantId, String warehouseId, String languageId, UpdateHandlingEquipment updateHandlingEquipment, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        HandlingEquipment dbHandlingEquipment = getHandlingEquipment(warehouseId, handlingEquipmentId, companyCodeId, languageId, plantId);
        BeanUtils.copyProperties(updateHandlingEquipment, dbHandlingEquipment, CommonUtils.getNullPropertyNames(updateHandlingEquipment));
        dbHandlingEquipment.setUpdatedBy(loginUserID);
        dbHandlingEquipment.setUpdatedOn(new Date());
        return handlingequipmentRepository.save(dbHandlingEquipment);
    }

    /**
     * deleteHandlingEquipment
     *
     * @param handlingEquipmentId
     */
    public void deleteHandlingEquipment(String handlingEquipmentId, String companyCodeId, String languageId, String plantId, String warehouseId, String loginUserID) throws ParseException {
        HandlingEquipment handlingequipment = getHandlingEquipment(warehouseId, handlingEquipmentId, companyCodeId, languageId, plantId);
        if (handlingequipment != null) {
            handlingequipment.setDeletionIndicator(1L);
            handlingequipment.setUpdatedBy(loginUserID);
            handlingequipment.setUpdatedOn(new Date());
            handlingequipmentRepository.save(handlingequipment);
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + handlingEquipmentId);
        }
    }
}
