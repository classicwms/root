package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.impalletization.AddImPalletization;
import com.tekclover.wms.api.masters.model.impalletization.ImPalletization;
import com.tekclover.wms.api.masters.model.impalletization.SearchImPalletization;
import com.tekclover.wms.api.masters.model.impalletization.UpdateImPalletization;
import com.tekclover.wms.api.masters.model.impartner.ImPartner;
import com.tekclover.wms.api.masters.repository.ImPalletizationRepository;
import com.tekclover.wms.api.masters.repository.specification.ImPalletizationSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
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
public class ImPalletizationService {

    @Autowired
    private ImPalletizationRepository imPalletizationRepository;

    /**
     * ImBatchSerial
     * @return
     */
    public List<ImPalletization> getAllImPalletization () {
        List<ImPalletization> imPalletizationList = imPalletizationRepository.findAll();
        log.info("imPalletization : " + imPalletizationList);
        imPalletizationList = imPalletizationList.stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return imPalletizationList;
    }

    /**
     * getImPalletization
     * @param companyCodeId
     * @param itemCode
     * @return
     */
    public List<ImPalletization> getImPalletization (String warehouseId, String companyCodeId, String languageId,
                                                     String plantId,String itemCode) {
        List<ImPalletization> dbImPalletization =
                imPalletizationRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        languageId,
                        warehouseId,
                        itemCode,
                        0L
                );
        if (dbImPalletization.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "companyCodeId - "+companyCodeId+
                    "plantId - " +plantId+
                    "itemCode - " +itemCode+
                    "doesn't exist.");

        }
        return dbImPalletization;
    }



    /**
     *
     * @param searchImPalletization
     * @return
     * @throws Exception
     */
    public List<ImPalletization> findImPalletization(SearchImPalletization searchImPalletization)
            throws Exception {
        ImPalletizationSpecification spec = new ImPalletizationSpecification(searchImPalletization);
        List<ImPalletization> results = imPalletizationRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * createImPalletization
     * @param newImPalletization
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<ImPalletization> createImPalletization (List<AddImPalletization> newImPalletization, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        List<ImPalletization>imPalletizationList = new ArrayList<>();

        for(AddImPalletization addImPalletization:newImPalletization) {
            List<ImPalletization> duplicateImPalletization = imPalletizationRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndPalletizationLevelAndDeletionIndicator(
                    addImPalletization.getCompanyCodeId(),
                    addImPalletization.getPlantId(),
                    addImPalletization.getLanguageId(),
                    addImPalletization.getWarehouseId(),
                    addImPalletization.getItemCode(),
                    addImPalletization.getPalletizationLevel(),
                    0L);
            if (!duplicateImPalletization.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                ImPalletization dbImPalletization = new ImPalletization();
                BeanUtils.copyProperties(addImPalletization, dbImPalletization, CommonUtils.getNullPropertyNames(addImPalletization));
                dbImPalletization.setDeletionIndicator(0L);
                dbImPalletization.setCreatedBy(loginUserID);
                dbImPalletization.setUpdatedBy(loginUserID);
                dbImPalletization.setCreatedOn(new Date());
                dbImPalletization.setUpdatedOn(new Date());
                ImPalletization savedImPalletization = imPalletizationRepository.save(dbImPalletization);
                imPalletizationList.add(savedImPalletization);
            }
        }
        return imPalletizationList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param itemCode
     * @param updateImPalletization
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<ImPalletization> updateImPalletization (String companyCodeId, String plantId, String warehouseId,String languageId,
                                                        String itemCode, List<AddImPalletization> updateImPalletization, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<ImPalletization> palletizationList = imPalletizationRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                companyCodeId,
                plantId,
                languageId,
                warehouseId,
                itemCode,
                0L
        );
        if (palletizationList !=null) {
            for (ImPalletization newImPalletization : palletizationList) {
                newImPalletization.setUpdatedBy(loginUserID);
                newImPalletization.setUpdatedOn(new Date());
                newImPalletization.setDeletionIndicator(1L);
                imPalletizationRepository.save(newImPalletization);
            }
        } else {
            throw new EntityNotFoundException("The given values companyId " + companyCodeId +
                    " plantId " + plantId + " warehouseId " + warehouseId +
                    " itemCode " + itemCode +  "doesn't exists");
        }
        List<ImPalletization>createImPalletization = createImPalletization(updateImPalletization,loginUserID);
        return createImPalletization;
    }

    /**
     *
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param itemCode
     * @param loginUserID
     */
    public void deleteImPalletization (String companyCodeId,String languageId,String plantId,String warehouseId,String itemCode,String loginUserID) throws ParseException {
        List<ImPalletization> imPalletization = getImPalletization(warehouseId,companyCodeId,languageId,plantId,itemCode);
        if ( imPalletization != null) {
            for(ImPalletization newImPalletization : imPalletization) {
                newImPalletization.setDeletionIndicator(1L);
                newImPalletization.setUpdatedBy(loginUserID);
                newImPalletization.setUpdatedOn(new Date());
                imPalletizationRepository.save(newImPalletization);
            }
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + imPalletization);
        }
    }
}
