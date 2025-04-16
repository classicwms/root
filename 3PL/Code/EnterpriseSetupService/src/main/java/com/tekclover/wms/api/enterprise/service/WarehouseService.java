package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.warehouse.AddWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.SearchWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.UpdateWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.Warehouse;
import com.tekclover.wms.api.enterprise.repository.CompanyRepository;
import com.tekclover.wms.api.enterprise.repository.PlantRepository;
import com.tekclover.wms.api.enterprise.repository.WarehouseRepository;
import com.tekclover.wms.api.enterprise.repository.specification.WarehouseSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;
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
public class WarehouseService {
    @Autowired
    private PlantRepository plantRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    /**
     * getWarehouses
     *
     * @return
     */
    public List<Warehouse> getWarehouses() {
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        log.info("warehouseList : " + warehouseList);
        warehouseList = warehouseList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
        List<Warehouse> newWarehouse = new ArrayList<>();
        for (Warehouse dbWarehouse : warehouseList) {
            if (dbWarehouse.getCompanyIdAndDescription() != null && dbWarehouse.getPlantIdAndDescription() != null && dbWarehouse.getWarehouseTypeIdAndDescription() != null) {
                IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbWarehouse.getCompanyId(), dbWarehouse.getLanguageId());
                IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbWarehouse.getPlantId(), dbWarehouse.getLanguageId(), dbWarehouse.getCompanyId());
                IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseTypeIdAndDescription(String.valueOf(dbWarehouse.getWarehouseTypeId()), dbWarehouse.getLanguageId(), dbWarehouse.getCompanyId(), dbWarehouse.getPlantId(), dbWarehouse.getWarehouseId());
                IkeyValuePair ikeyValuePair3 = warehouseRepository.getWarehouseIdAndDescription(dbWarehouse.getWarehouseId(), dbWarehouse.getLanguageId(), dbWarehouse.getCompanyId(), dbWarehouse.getPlantId());
                if (iKeyValuePair != null) {
                    dbWarehouse.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbWarehouse.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (ikeyValuePair2 != null) {
                    dbWarehouse.setWarehouseTypeIdAndDescription(ikeyValuePair2.getWarehouseTypeId() + "-" + ikeyValuePair2.getDescription());
                }
                if (ikeyValuePair3 != null) {
                    dbWarehouse.setDescription(ikeyValuePair3.getDescription());
                }
            }
            newWarehouse.add(dbWarehouse);
        }
        return newWarehouse;
    }

    /**
     * getWarehouse
     *
     * @param warehouseId
     * @param modeOfImplementation
     * @param warehouseTypeId
     * @return
     */
    public Warehouse getWarehouse(String warehouseId, String modeOfImplementation, Long warehouseTypeId, String companyId, String plantId, String languageId) {
        Optional<Warehouse> warehouse =
                warehouseRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndModeOfImplementationAndWarehouseTypeIdAndDeletionIndicator(
                        languageId,
                        companyId,
                        plantId,
                        warehouseId,
                        modeOfImplementation,
                        warehouseTypeId,
                        0L);
        if (warehouse.isEmpty()) {
            throw new BadRequestException("The given Warehouse ID : " + warehouseId + " doesn't exist.");

        }
        Warehouse newWarehouse = new Warehouse();
        BeanUtils.copyProperties(warehouse.get(), newWarehouse, CommonUtils.getNullPropertyNames(warehouse));
        IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(companyId, languageId);
        IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(plantId, languageId, companyId);
        IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseTypeIdAndDescription(String.valueOf(warehouseTypeId), languageId, companyId, plantId, warehouseId);
        IkeyValuePair ikeyValuePair3 = warehouseRepository.getWarehouseIdAndDescription(warehouseId, languageId, companyId, plantId);
        if (iKeyValuePair != null) {
            newWarehouse.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if (iKeyValuePair1 != null) {
            newWarehouse.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if (ikeyValuePair2 != null) {
            newWarehouse.setWarehouseTypeIdAndDescription(ikeyValuePair2.getWarehouseTypeId() + "-" + ikeyValuePair2.getDescription());
        }
        if (ikeyValuePair3 != null) {
            newWarehouse.setDescription(ikeyValuePair3.getDescription());
        }
        return newWarehouse;
    }


    /**
     * getWarehouse
     *
     * @param warehouseId
     * @return
     */
    public Warehouse getWarehouse(String warehouseId) {
        Warehouse warehouse = warehouseRepository.findByWarehouseId(warehouseId).orElse(null);
        if (warehouse != null && warehouse.getDeletionIndicator() != null && warehouse.getDeletionIndicator() == 0) {
            return warehouse;
        } else {
            throw new BadRequestException("The given Warehouse ID : " + warehouseId + " doesn't exist.");
        }
    }

    /**
     * findWarehouse
     *
     * @param searchWarehouse
     * @return
     * @throws java.text.ParseException
     */
    public List<Warehouse> findWarehouse(SearchWarehouse searchWarehouse) throws Exception {
        if (searchWarehouse.getStartCreatedOn() != null && searchWarehouse.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchWarehouse.getStartCreatedOn(), searchWarehouse.getEndCreatedOn());
            searchWarehouse.setStartCreatedOn(dates[0]);
            searchWarehouse.setEndCreatedOn(dates[1]);
        }

        WarehouseSpecification spec = new WarehouseSpecification(searchWarehouse);
        List<Warehouse> results = warehouseRepository.findAll(spec);
        log.info("results: " + results);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        List<Warehouse> newWarehouse = new ArrayList<>();
        for (Warehouse dbWarehouse : results) {
            if (dbWarehouse.getCompanyIdAndDescription() != null && dbWarehouse.getPlantIdAndDescription() != null && dbWarehouse.getWarehouseTypeIdAndDescription() != null) {
                IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbWarehouse.getCompanyId(), dbWarehouse.getLanguageId());
                IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbWarehouse.getPlantId(), dbWarehouse.getLanguageId(), dbWarehouse.getCompanyId());
                IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseTypeIdAndDescription(String.valueOf(dbWarehouse.getWarehouseTypeId()), dbWarehouse.getLanguageId(), dbWarehouse.getCompanyId(), dbWarehouse.getPlantId(), dbWarehouse.getWarehouseId());
                IkeyValuePair ikeyValuePair3 = warehouseRepository.getWarehouseIdAndDescription(dbWarehouse.getWarehouseId(), dbWarehouse.getLanguageId(), dbWarehouse.getCompanyId(), dbWarehouse.getPlantId());
                if (iKeyValuePair != null) {
                    dbWarehouse.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbWarehouse.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (ikeyValuePair2 != null) {
                    dbWarehouse.setWarehouseTypeIdAndDescription(ikeyValuePair2.getWarehouseTypeId() + "-" + ikeyValuePair2.getDescription());
                }
                if (ikeyValuePair3 != null) {
                    dbWarehouse.setDescription(ikeyValuePair3.getDescription());
                }
            }
            newWarehouse.add(dbWarehouse);
        }
        return newWarehouse;
    }

    /**
     *
     * @param addWarehouseList
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public List<Warehouse> createWarehouse(List<Warehouse> addWarehouseList, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<Warehouse> warehouseList = new ArrayList<>();
        try {
            for (Warehouse addWarehouse : addWarehouseList) {
                Optional<Warehouse> optWarehouse =
                        warehouseRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndModeOfImplementationAndWarehouseTypeIdAndDeletionIndicator(
                                addWarehouse.getLanguageId(),
                                addWarehouse.getCompanyId(),
                                addWarehouse.getPlantId(),
                                addWarehouse.getWarehouseId(),
                                addWarehouse.getModeOfImplementation(),
                                addWarehouse.getWarehouseTypeId(),
                                0L);
                if (!optWarehouse.isEmpty()) {
                    throw new BadRequestException("The given values are getting duplicated.");
                } else {
                    IkeyValuePair ikeyValuePair = companyRepository.getCompanyIdAndDescription(addWarehouse.getCompanyId(), addWarehouse.getLanguageId());
                    IkeyValuePair ikeyValuePair1 = plantRepository.getPlantIdAndDescription(addWarehouse.getPlantId(), addWarehouse.getLanguageId(), addWarehouse.getCompanyId());
                    IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseTypeIdAndDescription(String.valueOf(addWarehouse.getWarehouseTypeId()), addWarehouse.getLanguageId(), addWarehouse.getCompanyId(), addWarehouse.getPlantId(), addWarehouse.getWarehouseId());
                    IkeyValuePair ikeyValuePair3 = warehouseRepository.getWarehouseIdAndDescription(addWarehouse.getWarehouseId(), addWarehouse.getLanguageId(), addWarehouse.getCompanyId(), addWarehouse.getPlantId());
                    Warehouse dbWarehouse = new Warehouse();
                    BeanUtils.copyProperties(addWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(addWarehouse));
                    if (ikeyValuePair != null && ikeyValuePair1 != null &&
                            ikeyValuePair2 != null && ikeyValuePair3 != null) {
                        dbWarehouse.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
                        dbWarehouse.setPlantIdAndDescription(ikeyValuePair1.getPlantId() + "-" + ikeyValuePair1.getDescription());
                        dbWarehouse.setWarehouseTypeIdAndDescription(ikeyValuePair2.getWarehouseTypeId() + "-" + ikeyValuePair2.getDescription());
                        dbWarehouse.setDescription(ikeyValuePair3.getDescription());
                    } else {
                        throw new BadRequestException(" The given WarehouseType Id "
                                + addWarehouse.getWarehouseTypeId() + " CompanyId "
                                + addWarehouse.getCompanyId() + " PlantId "
                                + addWarehouse.getPlantId() + " Warehouse Id "
                                + addWarehouse.getWarehouseId() + " doesn't exist");
                    }
                    dbWarehouse.setDeletionIndicator(0L);
                    dbWarehouse.setCreatedBy(loginUserID);
                    dbWarehouse.setUpdatedBy(loginUserID);
                    dbWarehouse.setCreatedOn(new Date());
                    dbWarehouse.setUpdatedOn(new Date());
                    warehouseRepository.save(dbWarehouse);
                    warehouseList.add(dbWarehouse);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return warehouseList;
    }

    /**
     * updateWarehouse
     *
     * @param warehouseId
     * @param updateWarehouse
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Warehouse updateWarehouse(String warehouseId, String modeOfImplementation, Long warehouseTypeId, String companyId, String plantId, String languageId, UpdateWarehouse updateWarehouse, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Warehouse dbWarehouse = getWarehouse(warehouseId, modeOfImplementation, warehouseTypeId, companyId, plantId, languageId);
        BeanUtils.copyProperties(updateWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(updateWarehouse));
        dbWarehouse.setUpdatedBy(loginUserID);
        dbWarehouse.setUpdatedOn(new Date());
        return warehouseRepository.save(dbWarehouse);
    }

    /**
     * deleteWarehouse
     *
     * @param warehouseId
     */
    public void deleteWarehouse(String warehouseId, String modeOfImplementation, Long warehouseTypeId, String companyId, String plantId, String languageId, String loginUserID) throws ParseException {
        Warehouse warehouse = getWarehouse(warehouseId, modeOfImplementation, warehouseTypeId, companyId, plantId, languageId);
        if (warehouse != null) {
            warehouse.setDeletionIndicator(1L);
            warehouse.setUpdatedBy(loginUserID);
            warehouse.setUpdatedOn(new Date());
            warehouseRepository.save(warehouse);
        } else {
            throw new EntityNotFoundException(String.valueOf(warehouseId));
        }
    }
}
