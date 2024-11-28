package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.floor.AddFloor;
import com.tekclover.wms.api.enterprise.model.floor.Floor;
import com.tekclover.wms.api.enterprise.model.floor.SearchFloor;
import com.tekclover.wms.api.enterprise.model.floor.UpdateFloor;
import com.tekclover.wms.api.enterprise.repository.CompanyRepository;
import com.tekclover.wms.api.enterprise.repository.FloorRepository;
import com.tekclover.wms.api.enterprise.repository.PlantRepository;
import com.tekclover.wms.api.enterprise.repository.WarehouseRepository;
import com.tekclover.wms.api.enterprise.repository.specification.FloorSpecification;
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
public class FloorService {
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private PlantRepository plantRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private FloorRepository floorRepository;

    /**
     * getFloors
     *
     * @return
     */
    public List<Floor> getFloors() {
        try {
            List<Floor> floorList = floorRepository.findAll();
            log.info("floorList : " + floorList);
            floorList = floorList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<Floor> newFloorId = new ArrayList<>();
            for (Floor dbFloorId : floorList) {
                if (dbFloorId.getCompanyIdAndDescription() != null && dbFloorId.getPlantIdAndDescription() != null && dbFloorId.getWarehouseIdAndDescription() != null) {
                    IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbFloorId.getCompanyId(), dbFloorId.getLanguageId());
                    IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbFloorId.getPlantId(), dbFloorId.getLanguageId(), dbFloorId.getLanguageId());
                    IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbFloorId.getWarehouseId(), dbFloorId.getLanguageId(), dbFloorId.getCompanyId(), dbFloorId.getPlantId());
                    IkeyValuePair ikeyValuePair3 = floorRepository.getFloorIdAndDescription(String.valueOf(dbFloorId.getFloorId()), dbFloorId.getLanguageId(), dbFloorId.getWarehouseId(), dbFloorId.getPlantId(), dbFloorId.getCompanyId());
                    if (iKeyValuePair != null) {
                        dbFloorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                    }
                    if (iKeyValuePair1 != null) {
                        dbFloorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                    }
                    if (iKeyValuePair2 != null) {
                        dbFloorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                    }
                    if (ikeyValuePair3 != null)
                        dbFloorId.setDescription(ikeyValuePair3.getDescription());
                }
                newFloorId.add(dbFloorId);
            }
            return newFloorId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getFloor
     *
     * @param warehouseId
     * @param floorId
     * @return
     */
    public Floor getFloor(String warehouseId, String companyId, String plantId, String languageId, Long floorId) {
        try {
            Optional<Floor> floor =
                    floorRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndDeletionIndicator(
                            languageId,
                            companyId,
                            plantId,
                            warehouseId,
                            floorId,
                            0L);
            if (floor.isEmpty()) {
                throw new BadRequestException("The given Floor Id : " + floorId + " doesn't exist.");
            }
            Floor newFloorId = new Floor();
            BeanUtils.copyProperties(floor.get(), newFloorId, CommonUtils.getNullPropertyNames(floor));
            IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(companyId, languageId);
            IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(plantId, languageId, companyId);
            IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(warehouseId, languageId, companyId, plantId);
            IkeyValuePair ikeyValuePair3 = floorRepository.getFloorIdAndDescription(String.valueOf(floorId), languageId, warehouseId, plantId, companyId);
            if (iKeyValuePair != null) {
                newFloorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
            }
            if (iKeyValuePair1 != null) {
                newFloorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
            }
            if (iKeyValuePair2 != null) {
                newFloorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
            }
            if (ikeyValuePair3 != null) {
                newFloorId.setDescription(ikeyValuePair3.getDescription());
            }
            return newFloorId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findFloor
     *
     * @param searchFloor
     * @return
     */
    public List<Floor> findFloor(SearchFloor searchFloor) {
        try {
            if (searchFloor.getStartCreatedOn() != null && searchFloor.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchFloor.getStartCreatedOn(), searchFloor.getEndCreatedOn());
                searchFloor.setStartCreatedOn(dates[0]);
                searchFloor.setEndCreatedOn(dates[1]);
            }

            FloorSpecification spec = new FloorSpecification(searchFloor);
            List<Floor> results = floorRepository.findAll(spec);
            log.info("results: " + results);
            results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<Floor> newFloorId = new ArrayList<>();
            for (Floor dbFloorId : results) {
                if (dbFloorId.getCompanyIdAndDescription() != null && dbFloorId.getPlantIdAndDescription() != null && dbFloorId.getWarehouseIdAndDescription() != null) {
                    IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbFloorId.getCompanyId(), dbFloorId.getLanguageId());
                    IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbFloorId.getPlantId(), dbFloorId.getLanguageId(), dbFloorId.getLanguageId());
                    IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbFloorId.getWarehouseId(), dbFloorId.getLanguageId(), dbFloorId.getCompanyId(), dbFloorId.getPlantId());
                    IkeyValuePair ikeyValuePair3 = floorRepository.getFloorIdAndDescription(String.valueOf(dbFloorId.getFloorId()), dbFloorId.getLanguageId(), dbFloorId.getWarehouseId(), dbFloorId.getPlantId(), dbFloorId.getCompanyId());
                    if (iKeyValuePair != null) {
                        dbFloorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                    }
                    if (iKeyValuePair1 != null) {
                        dbFloorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                    }
                    if (iKeyValuePair2 != null) {
                        dbFloorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                    }
                    if (ikeyValuePair3 != null)
                        dbFloorId.setDescription(ikeyValuePair3.getDescription());
                }

                newFloorId.add(dbFloorId);
            }
            return newFloorId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createFloor
     *
     * @param newFloor
     * @return
     */
    public Floor createFloor(AddFloor newFloor, String loginUserID) {
        try {
            Optional<Floor> optFloor =
                    floorRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndDeletionIndicator(
                            newFloor.getLanguageId(),
                            newFloor.getCompanyId(),
                            newFloor.getPlantId(),
                            newFloor.getWarehouseId(),
                            newFloor.getFloorId(),
                            0L);
            if (!optFloor.isEmpty()) {
                throw new BadRequestException("The given values are getting duplicated.");
            }
            Floor dbFloor = new Floor();
            IkeyValuePair ikeyValuePair = companyRepository.getCompanyIdAndDescription(newFloor.getCompanyId(), newFloor.getLanguageId());
            IkeyValuePair ikeyValuePair1 = plantRepository.getPlantIdAndDescription(newFloor.getPlantId(), newFloor.getLanguageId(), newFloor.getCompanyId());
            IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(newFloor.getWarehouseId(), newFloor.getLanguageId(), newFloor.getCompanyId(), newFloor.getPlantId());
            IkeyValuePair ikeyValuePair3 = floorRepository.getFloorIdAndDescription(String.valueOf(newFloor.getFloorId()), newFloor.getLanguageId(), newFloor.getWarehouseId(), newFloor.getPlantId(), newFloor.getCompanyId());
            BeanUtils.copyProperties(newFloor, dbFloor, CommonUtils.getNullPropertyNames(newFloor));

            if (ikeyValuePair != null && ikeyValuePair1 != null &&
                    ikeyValuePair2 != null && ikeyValuePair3 != null) {
                dbFloor.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
                dbFloor.setPlantIdAndDescription(ikeyValuePair1.getPlantId() + "-" + ikeyValuePair1.getDescription());
                dbFloor.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
                dbFloor.setDescription(ikeyValuePair3.getDescription());
            } else {
                throw new BadRequestException("The given values of Company Id "
                        + newFloor.getCompanyId() + " Plant Id "
                        + newFloor.getPlantId() + " Warehouse Id "
                        + newFloor.getWarehouseId() + " Floor Id "
                        + newFloor.getFloorId() + " doesn't exist");
            }
            dbFloor.setDeletionIndicator(0L);
            dbFloor.setCreatedBy(loginUserID);
            dbFloor.setUpdatedBy(loginUserID);
            dbFloor.setCreatedOn(new Date());
            dbFloor.setUpdatedOn(new Date());
            return floorRepository.save(dbFloor);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateFloor
     *
     * @param floorId
     * @param updateFloor
     * @return
     */
    public Floor updateFloor(String warehouseId, String companyId, String plantId, String languageId, Long floorId, UpdateFloor updateFloor, String loginUserID) {
        try {
            Floor dbFloor = getFloor(warehouseId, companyId, plantId, languageId, floorId);
            BeanUtils.copyProperties(updateFloor, dbFloor, CommonUtils.getNullPropertyNames(updateFloor));
            dbFloor.setUpdatedBy(loginUserID);
            dbFloor.setUpdatedOn(new Date());
            return floorRepository.save(dbFloor);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteFloor
     *
     * @param floorId
     */
    public void deleteFloor(String warehouseId, String companyId, String plantId, String languageId, Long floorId, String loginUserID) {
        try {
            Floor floor = getFloor(warehouseId, companyId, plantId, languageId, floorId);
            if (floor != null) {
                floor.setDeletionIndicator(1L);
                floor.setUpdatedBy(loginUserID);
                floor.setUpdatedOn(new Date());
                floorRepository.save(floor);
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + floorId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}