package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.plant.AddPlant;
import com.tekclover.wms.api.enterprise.model.plant.Plant;
import com.tekclover.wms.api.enterprise.model.plant.SearchPlant;
import com.tekclover.wms.api.enterprise.model.plant.UpdatePlant;
import com.tekclover.wms.api.enterprise.repository.CompanyRepository;
import com.tekclover.wms.api.enterprise.repository.PlantRepository;
import com.tekclover.wms.api.enterprise.repository.specification.PlantSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlantService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PlantRepository plantRepository;

    /**
     * getPlants
     *
     * @return
     */
    public List<Plant> getPlants() {
        try {
            List<Plant> plantList = plantRepository.findAll();
            log.info("plantList : " + plantList);
            plantList = plantList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<Plant> newPlant = new ArrayList<>();
            for (Plant dbPlant : plantList) {
                if (dbPlant.getCompanyIdAndDescription() != null && dbPlant.getDescription() != null) {
                    IkeyValuePair ikeyValuePair1 = companyRepository.getCompanyIdAndDescription(dbPlant.getCompanyId(), dbPlant.getLanguageId());
                    IkeyValuePair ikeyValuePair2 = plantRepository.getPlantIdAndDescription(dbPlant.getPlantId(), dbPlant.getLanguageId(), dbPlant.getCompanyId());
                    if (ikeyValuePair1 != null) {
                        dbPlant.setCompanyIdAndDescription(ikeyValuePair1.getCompanyCodeId() + "-" + ikeyValuePair1.getDescription());
                    }
                    if (ikeyValuePair2 != null) {
                        dbPlant.setDescription(ikeyValuePair2.getDescription());
                    }
                }
                newPlant.add(dbPlant);
            }
            return newPlant;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getPlant
     *
     * @param plantId
     * @return
     */
    public Plant getPlant(String plantId, String companyId, String languageId) {
        try {
            Optional<Plant> plant =
                    plantRepository.findByLanguageIdAndCompanyIdAndPlantIdAndDeletionIndicator(
                            languageId, companyId, plantId, 0L);
            if (plant.isEmpty()) {
                throw new BadRequestException("The given Plant Id : " + plantId + " doesn't exist.");
            }
            Plant newPlant = new Plant();
            BeanUtils.copyProperties(plant.get(), newPlant, CommonUtils.getNullPropertyNames(plant));
            IkeyValuePair ikeyValuePair1 = companyRepository.getCompanyIdAndDescription(companyId, languageId);
            IkeyValuePair ikeyValuePair2 = plantRepository.getPlantIdAndDescription(plantId, languageId, companyId);
            if (ikeyValuePair1 != null) {
                newPlant.setCompanyIdAndDescription(ikeyValuePair1.getCompanyCodeId() + "-" + ikeyValuePair1.getDescription());
            }
            if (ikeyValuePair2 != null) {
                newPlant.setDescription(ikeyValuePair2.getDescription());
            }
            return newPlant;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findPlant
     *
     * @param searchPlant
     * @return
     */
    public List<Plant> findPlant(SearchPlant searchPlant) {
        try {
            if (searchPlant.getStartCreatedOn() != null && searchPlant.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchPlant.getStartCreatedOn(), searchPlant.getEndCreatedOn());
                searchPlant.setStartCreatedOn(dates[0]);
                searchPlant.setEndCreatedOn(dates[1]);
            }
            PlantSpecification spec = new PlantSpecification(searchPlant);
            List<Plant> results = plantRepository.findAll(spec);
            log.info("results: " + results);
            results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<Plant> newPlant = new ArrayList<>();
            for (Plant dbPlant : results) {
                if (dbPlant.getCompanyIdAndDescription() != null && dbPlant.getDescription() != null) {
                    IkeyValuePair ikeyValuePair1 = companyRepository.getCompanyIdAndDescription(dbPlant.getCompanyId(), dbPlant.getLanguageId());
                    IkeyValuePair ikeyValuePair2 = plantRepository.getPlantIdAndDescription(dbPlant.getPlantId(), dbPlant.getLanguageId(), dbPlant.getCompanyId());
                    if (ikeyValuePair1 != null) {
                        dbPlant.setCompanyIdAndDescription(ikeyValuePair1.getCompanyCodeId() + "-" + ikeyValuePair1.getDescription());
                    }
                    if (ikeyValuePair2 != null) {
                        dbPlant.setDescription(ikeyValuePair2.getDescription());
                    }
                }
                newPlant.add(dbPlant);
            }
            return newPlant;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createPlant
     *
     * @param newPlant
     * @return
     */
    public Plant createPlant(AddPlant newPlant, String loginUserID) {
        try {
            Optional<Plant> optPlant =
                    plantRepository.findByLanguageIdAndCompanyIdAndPlantIdAndDeletionIndicator(
                            newPlant.getLanguageId(),
                            newPlant.getCompanyId(),
                            newPlant.getPlantId(),
                            0L);
            if (!optPlant.isEmpty()) {
                throw new BadRequestException("The given values are getting duplicated.");
            }
            IkeyValuePair ikeyValuePair = companyRepository.getCompanyIdAndDescription(newPlant.getCompanyId(), newPlant.getLanguageId());
            IkeyValuePair ikeyValuePair1 = plantRepository.getPlantIdAndDescription(newPlant.getPlantId(), newPlant.getLanguageId(), newPlant.getCompanyId());
            Plant dbPlant = new Plant();
            BeanUtils.copyProperties(newPlant, dbPlant, CommonUtils.getNullPropertyNames(newPlant));

            if (ikeyValuePair != null && ikeyValuePair1 != null) {
                dbPlant.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
                dbPlant.setDescription(ikeyValuePair1.getDescription());
                dbPlant.setPlantId(ikeyValuePair1.getPlantId());
            } else {
                throw new BadRequestException("The given values of Company Id "
                        + newPlant.getCompanyId() + " plant Id "
                        + newPlant.getPlantId() + "doesn't exist ");
            }
            dbPlant.setDeletionIndicator(0L);
            dbPlant.setCreatedBy(loginUserID);
            dbPlant.setUpdatedBy(loginUserID);
            dbPlant.setCreatedOn(new Date());
            dbPlant.setUpdatedOn(new Date());
            return plantRepository.save(dbPlant);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updatePlant
     *
     * @param plantId
     * @param updatePlant
     * @return
     */
    public Plant updatePlant(String plantId, String companyId, String languageId, UpdatePlant updatePlant, String loginUserID) {
        try {
            Plant dbPlant = getPlant(plantId, companyId, languageId);
            BeanUtils.copyProperties(updatePlant, dbPlant, CommonUtils.getNullPropertyNames(updatePlant));
            dbPlant.setUpdatedBy(loginUserID);
            dbPlant.setUpdatedOn(new Date());
            return plantRepository.save(dbPlant);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deletePlant
     *
     * @param plantId
     */
    public void deletePlant(String plantId, String companyId, String languageId, String loginUserID) {
        try {
            Plant plant = getPlant(plantId, companyId, languageId);
            if (plant != null) {
                plant.setDeletionIndicator(1L);
                plant.setUpdatedBy(loginUserID);
                plant.setUpdatedOn(new Date());
                plantRepository.save(plant);
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + plantId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}