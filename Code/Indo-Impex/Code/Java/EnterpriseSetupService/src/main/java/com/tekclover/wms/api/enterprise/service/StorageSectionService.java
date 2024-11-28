package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.storagesection.AddStorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.SearchStorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.StorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.UpdateStorageSection;
import com.tekclover.wms.api.enterprise.repository.*;
import com.tekclover.wms.api.enterprise.repository.specification.StorageSectionSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StorageSectionService {
    @Autowired
    private FloorRepository floorRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private PlantRepository plantRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StorageSectionRepository storagesectionRepository;

    /**
     * getStorageSections
     *
     * @return
     */
    public List<StorageSection> getStorageSections() {
        try {
            List<StorageSection> storagesectionList = storagesectionRepository.findAll();
            log.info("storagesectionList : " + storagesectionList);
            storagesectionList = storagesectionList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<StorageSection> newStorageSectionId = new ArrayList<>();
            for (StorageSection dbStorageSectionId : storagesectionList) {
                if (dbStorageSectionId.getCompanyIdAndDescription() != null && dbStorageSectionId.getPlantIdAndDescription() != null && dbStorageSectionId.getWarehouseIdAndDescription() != null && dbStorageSectionId.getFloorIdAndDescription() != null) {
                    IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbStorageSectionId.getCompanyId(), dbStorageSectionId.getLanguageId());
                    IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbStorageSectionId.getPlantId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getCompanyId());
                    IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageSectionId.getWarehouseId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getCompanyId(), dbStorageSectionId.getPlantId());
                    IkeyValuePair iKeyValuePair3 = floorRepository.getFloorIdAndDescription(String.valueOf(dbStorageSectionId.getFloorId()), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getWarehouseId(), dbStorageSectionId.getPlantId(), dbStorageSectionId.getCompanyId());
                    IkeyValuePair ikeyValuePair4 = storagesectionRepository.getStorageSectionIdAndDescription(dbStorageSectionId.getStorageSectionId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getFloorId(), dbStorageSectionId.getCompanyId(), dbStorageSectionId.getPlantId(), dbStorageSectionId.getWarehouseId());
                    if (iKeyValuePair != null) {
                        dbStorageSectionId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                    }
                    if (iKeyValuePair1 != null) {
                        dbStorageSectionId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                    }
                    if (iKeyValuePair2 != null) {
                        dbStorageSectionId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                    }
                    if (iKeyValuePair3 != null) {
                        dbStorageSectionId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
                    }
                    if (ikeyValuePair4 != null) {
                        dbStorageSectionId.setDescription(ikeyValuePair4.getDescription());
                    }
                }
                newStorageSectionId.add(dbStorageSectionId);
            }
            return newStorageSectionId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getStorageSection
     *
     * @param warehouseId
     * @param floorId
     * @param storageSectionId
     * @return
     */
    public StorageSection getStorageSection(String warehouseId, Long floorId, String storageSectionId, String companyId, String languageId, String plantId) {
        try {
            Optional<StorageSection> storagesection =
                    storagesectionRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndDeletionIndicator(
                            languageId,
                            companyId,
                            plantId,
                            warehouseId,
                            floorId,
                            storageSectionId,
                            0L);
            if (storagesection.isEmpty()) {
                throw new BadRequestException("The given StorageSection Id : " + storageSectionId + " doesn't exist.");
            }
            StorageSection newStorageSection = new StorageSection();
            BeanUtils.copyProperties(storagesection.get(), newStorageSection, CommonUtils.getNullPropertyNames(storagesection));
            IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(companyId, languageId);
            IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(plantId, languageId, companyId);
            IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(warehouseId, languageId, companyId, plantId);
            IkeyValuePair iKeyValuePair3 = floorRepository.getFloorIdAndDescription(String.valueOf(floorId), languageId, warehouseId, plantId, companyId);
            IkeyValuePair ikeyValuePair4 = storagesectionRepository.getStorageSectionIdAndDescription(storageSectionId, languageId, floorId, companyId, plantId, warehouseId);
            if (iKeyValuePair != null) {
                newStorageSection.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
            }
            if (iKeyValuePair1 != null) {
                newStorageSection.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
            }
            if (iKeyValuePair2 != null) {
                newStorageSection.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
            }
            if (iKeyValuePair3 != null) {
                newStorageSection.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
            }
            if (ikeyValuePair4 != null) {
                newStorageSection.setDescription(ikeyValuePair4.getDescription());
            }
            return newStorageSection;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findStorageSection
     *
     * @param searchStorageSection
     * @return
     */
    public List<StorageSection> findStorageSection(SearchStorageSection searchStorageSection) {
        try {
            if (searchStorageSection.getStartCreatedOn() != null && searchStorageSection.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageSection.getStartCreatedOn(), searchStorageSection.getEndCreatedOn());
                searchStorageSection.setStartCreatedOn(dates[0]);
                searchStorageSection.setEndCreatedOn(dates[1]);
            }

            StorageSectionSpecification spec = new StorageSectionSpecification(searchStorageSection);
            List<StorageSection> results = storagesectionRepository.findAll(spec);
            log.info("results: " + results);
            results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<StorageSection> newStorageSectionId = new ArrayList<>();
            for (StorageSection dbStorageSectionId : results) {
                if (dbStorageSectionId.getCompanyIdAndDescription() != null && dbStorageSectionId.getPlantIdAndDescription() != null && dbStorageSectionId.getWarehouseIdAndDescription() != null && dbStorageSectionId.getFloorIdAndDescription() != null) {
                    IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbStorageSectionId.getCompanyId(), dbStorageSectionId.getLanguageId());
                    IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbStorageSectionId.getPlantId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getCompanyId());
                    IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageSectionId.getWarehouseId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getCompanyId(), dbStorageSectionId.getPlantId());
                    IkeyValuePair iKeyValuePair3 = floorRepository.getFloorIdAndDescription(String.valueOf(dbStorageSectionId.getFloorId()), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getWarehouseId(), dbStorageSectionId.getPlantId(), dbStorageSectionId.getCompanyId());
                    IkeyValuePair ikeyValuePair4 = storagesectionRepository.getStorageSectionIdAndDescription(dbStorageSectionId.getStorageSectionId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getFloorId(), dbStorageSectionId.getCompanyId(), dbStorageSectionId.getPlantId(), dbStorageSectionId.getWarehouseId());
                    if (iKeyValuePair != null) {
                        dbStorageSectionId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                    }
                    if (iKeyValuePair1 != null) {
                        dbStorageSectionId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                    }
                    if (iKeyValuePair2 != null) {
                        dbStorageSectionId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                    }
                    if (iKeyValuePair3 != null) {
                        dbStorageSectionId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
                    }
                    if (ikeyValuePair4 != null) {
                        dbStorageSectionId.setDescription(ikeyValuePair4.getDescription());
                    }
                }
                newStorageSectionId.add(dbStorageSectionId);
            }
            return newStorageSectionId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createStorageSection
     *
     * @param newStorageSection
     * @return
     */
    public StorageSection createStorageSection(AddStorageSection newStorageSection, String loginUserID) {
        try {
            Optional<StorageSection> optStorageSection =
                    storagesectionRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndDeletionIndicator(
                            newStorageSection.getLanguageId(),
                            newStorageSection.getCompanyId(),
                            newStorageSection.getPlantId(),
                            newStorageSection.getWarehouseId(),
                            newStorageSection.getFloorId(),
                            newStorageSection.getStorageSectionId(),
                            0L);
            if (!optStorageSection.isEmpty()) {
                throw new BadRequestException("The given values are getting duplicated.");
            }

            StorageSection dbStorageSection = new StorageSection();
            BeanUtils.copyProperties(newStorageSection, dbStorageSection, CommonUtils.getNullPropertyNames(newStorageSection));
            IkeyValuePair ikeyValuePair = companyRepository.getCompanyIdAndDescription(newStorageSection.getCompanyId(), newStorageSection.getLanguageId());
            IkeyValuePair ikeyValuePair1 = plantRepository.getPlantIdAndDescription(newStorageSection.getPlantId(), newStorageSection.getLanguageId(), newStorageSection.getCompanyId());
            IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(newStorageSection.getWarehouseId(), newStorageSection.getLanguageId(), newStorageSection.getCompanyId(), newStorageSection.getPlantId());
            IkeyValuePair ikeyValuePair3 = floorRepository.getFloorIdAndDescription(String.valueOf(newStorageSection.getFloorId()), newStorageSection.getLanguageId(), newStorageSection.getWarehouseId(), newStorageSection.getPlantId(), newStorageSection.getCompanyId());
            IkeyValuePair ikeyValuePair4 = storagesectionRepository.getStorageSectionIdAndDescription(newStorageSection.getStorageSectionId(), newStorageSection.getLanguageId(), newStorageSection.getFloorId(), newStorageSection.getCompanyId(), newStorageSection.getPlantId(), newStorageSection.getWarehouseId());

            if (ikeyValuePair != null && ikeyValuePair1 != null &&
                    ikeyValuePair2 != null && ikeyValuePair3 != null && ikeyValuePair4 != null) {
                dbStorageSection.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
                dbStorageSection.setPlantIdAndDescription(ikeyValuePair1.getPlantId() + "-" + ikeyValuePair1.getDescription());
                dbStorageSection.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
                dbStorageSection.setFloorIdAndDescription(ikeyValuePair3.getFloorId() + "-" + ikeyValuePair3.getDescription());
                dbStorageSection.setDescription(ikeyValuePair4.getDescription());
            } else {
                throw new BadRequestException("The given values of Company Id "
                        + newStorageSection.getCompanyId() + " Plant Id "
                        + newStorageSection.getPlantId() + " Warehouse Id "
                        + newStorageSection.getWarehouseId() + " Floor Id "
                        + newStorageSection.getFloorId() + " Storage Section Id "
                        + newStorageSection.getStorageSectionId() + " doesn't exist ");
            }
            dbStorageSection.setDeletionIndicator(0L);
            dbStorageSection.setCreatedBy(loginUserID);
            dbStorageSection.setUpdatedBy(loginUserID);
            dbStorageSection.setCreatedOn(new Date());
            dbStorageSection.setUpdatedOn(new Date());
            return storagesectionRepository.save(dbStorageSection);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateStorageSection
     *
     * @param storageSectionId
     * @param updateStorageSection
     * @return
     */
    public StorageSection updateStorageSection(String warehouseId, Long floorId, String companyId, String languageId, String plantId,
                                               String storageSectionId, UpdateStorageSection updateStorageSection, String loginUserID) {
        try {
            StorageSection dbStorageSection = getStorageSection(warehouseId, floorId, storageSectionId, companyId, languageId, plantId);
            BeanUtils.copyProperties(updateStorageSection, dbStorageSection, CommonUtils.getNullPropertyNames(updateStorageSection));
            dbStorageSection.setUpdatedBy(loginUserID);
            dbStorageSection.setUpdatedOn(new Date());
            return storagesectionRepository.save(dbStorageSection);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteStorageSection
     *
     * @param storageSectionId
     */
    public void deleteStorageSection(String warehouseId, Long floorId, String storageSectionId, String companyId,
                                     String plantId, String languageId, String loginUserID) {
        try {
            StorageSection storagesection = getStorageSection(warehouseId, floorId, storageSectionId, companyId, languageId, plantId);
            if (storagesection != null) {
                storagesection.setDeletionIndicator(1L);
                storagesection.setUpdatedBy(loginUserID);
                storagesection.setUpdatedOn(new Date());
                storagesectionRepository.save(storagesection);
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + storageSectionId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}