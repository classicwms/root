package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.strategies.AddStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.SearchStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.Strategies;
import com.tekclover.wms.api.enterprise.repository.CompanyRepository;
import com.tekclover.wms.api.enterprise.repository.PlantRepository;
import com.tekclover.wms.api.enterprise.repository.StrategiesRepository;
import com.tekclover.wms.api.enterprise.repository.WarehouseRepository;
import com.tekclover.wms.api.enterprise.repository.specification.StrategiesSpecification;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class StrategiesService {
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private PlantRepository plantRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StrategiesRepository strategiesRepository;

    /**
     * getStrategiess
     *
     * @return
     */
    public List<Strategies> getStrategiess() {
        try {
            List<Strategies> strategiesList = strategiesRepository.findAll();
            log.info("strategiesList : " + strategiesList);
            strategiesList = strategiesList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<Strategies> newStrategies = new ArrayList<>();
            for (Strategies dbStrategyId : strategiesList) {
                if (dbStrategyId.getCompanyIdAndDescription() != null &&
                        dbStrategyId.getPlantIdAndDescription() != null &&
                        dbStrategyId.getWarehouseIdAndDescription() != null) {

                    IkeyValuePair iKeyValuePair = companyRepository.
                            getCompanyIdAndDescription(dbStrategyId.getCompanyId(), dbStrategyId.getLanguageId());

                    IkeyValuePair iKeyValuePair1 = plantRepository.
                            getPlantIdAndDescription(dbStrategyId.getPlantId(), dbStrategyId.getLanguageId(),
                                    dbStrategyId.getCompanyId());

                    IkeyValuePair iKeyValuePair2 = warehouseRepository.
                            getWarehouseIdAndDescription(dbStrategyId.getWarehouseId(), dbStrategyId.getLanguageId(),
                                    dbStrategyId.getCompanyId(), dbStrategyId.getPlantId());

                    List<IkeyValuePair> ikeyValuePair3 = strategiesRepository.
                            getStrategyIdAndDescription(dbStrategyId.getStrategyTypeId(),
                                    dbStrategyId.getLanguageId(), dbStrategyId.getCompanyId(), dbStrategyId.getPlantId(),
                                    dbStrategyId.getWarehouseId());

                    if (iKeyValuePair != null) {
                        dbStrategyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                    }
                    if (iKeyValuePair1 != null) {
                        dbStrategyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                    }
                    if (iKeyValuePair2 != null) {
                        dbStrategyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                    }
                    for (IkeyValuePair ikeyValuePair : ikeyValuePair3) {
                        if (ikeyValuePair != null) {
                            dbStrategyId.setDescription(ikeyValuePair.getDescription());
                        }
                    }
                }
                newStrategies.add(dbStrategyId);
            }
            return newStrategies;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @return
     */
    public List<Strategies> getStrategies(String companyId, String languageId, String plantId, String warehouseId) {
        try {
            List<Strategies> strategiesList = new ArrayList<>();
            List<Strategies> strategies =
                    strategiesRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
                            languageId,
                            companyId,
                            plantId,
                            warehouseId,
                            0L);
            if (strategies.isEmpty()) {
                throw new BadRequestException("The given company : " + companyId +
                        " plantId" + plantId + "warehouseId" + warehouseId + " doesn't exist.");
            }
            for (Strategies newStrategies : strategies) {
                Strategies newStrategyId = new Strategies();
                BeanUtils.copyProperties(newStrategies, newStrategyId, CommonUtils.getNullPropertyNames(newStrategies));

                IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(companyId, languageId);

                IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(plantId, languageId, companyId);

                IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(warehouseId, languageId, companyId, plantId);

                List<IkeyValuePair> ikeyValuePair3 = strategiesRepository.getStrategyIdAndDescription(newStrategies.getStrategyTypeId(), languageId,
                        companyId, plantId, warehouseId);

                if (iKeyValuePair != null) {
                    newStrategyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    newStrategyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    newStrategyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                for (IkeyValuePair ikeyValuePair : ikeyValuePair3) {
                    if (ikeyValuePair != null) {
                        newStrategyId.setDescription(ikeyValuePair.getDescription());
                    }
                }
                strategiesList.add(newStrategyId);
            }
            return strategiesList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchStrategies
     * @return
     * @throws Exception
     */
    public List<Strategies> findStrategies(SearchStrategies searchStrategies) {
        try {
            if (searchStrategies.getStartCreatedOn() != null && searchStrategies.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchStrategies.getStartCreatedOn(), searchStrategies.getEndCreatedOn());
                searchStrategies.setStartCreatedOn(dates[0]);
                searchStrategies.setEndCreatedOn(dates[1]);
            }

            StrategiesSpecification spec = new StrategiesSpecification(searchStrategies);
            List<Strategies> results = strategiesRepository.findAll(spec);
            log.info("results: " + results);
            results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<Strategies> newStrategies = new ArrayList<>();

            for (Strategies dbStrategyId : results) {
                if (dbStrategyId.getCompanyIdAndDescription() != null &&
                        dbStrategyId.getPlantIdAndDescription() != null &&
                        dbStrategyId.getWarehouseIdAndDescription() != null) {

                    IkeyValuePair iKeyValuePair = companyRepository.
                            getCompanyIdAndDescription(dbStrategyId.getCompanyId(), dbStrategyId.getLanguageId());

                    IkeyValuePair iKeyValuePair1 = plantRepository.
                            getPlantIdAndDescription(dbStrategyId.getPlantId(), dbStrategyId.getLanguageId(),
                                    dbStrategyId.getCompanyId());

                    IkeyValuePair iKeyValuePair2 = warehouseRepository.
                            getWarehouseIdAndDescription(dbStrategyId.getWarehouseId(), dbStrategyId.getLanguageId(),
                                    dbStrategyId.getCompanyId(), dbStrategyId.getPlantId());

                    List<IkeyValuePair> ikeyValuePair3 = strategiesRepository.
                            getStrategyIdAndDescription(dbStrategyId.getStrategyTypeId(),
                                    dbStrategyId.getLanguageId(), dbStrategyId.getCompanyId(), dbStrategyId.getPlantId(),
                                    dbStrategyId.getWarehouseId());

                    if (iKeyValuePair != null) {
                        dbStrategyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                    }
                    if (iKeyValuePair1 != null) {
                        dbStrategyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                    }
                    if (iKeyValuePair2 != null) {
                        dbStrategyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                    }
                    for (IkeyValuePair ikeyValuePair : ikeyValuePair3) {
                        if (ikeyValuePair != null) {
                            dbStrategyId.setDescription(ikeyValuePair.getDescription());
                        }
                    }
                }
                newStrategies.add(dbStrategyId);
            }
            return newStrategies;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param newStrategies
     * @param loginUserID
     * @return
     */
    public List<Strategies> createStrategies(List<AddStrategies> newStrategies, String loginUserID) {

        try {
            List<Strategies> strategiesList = new ArrayList<>();

            for (AddStrategies addStrategies : newStrategies) {
                List<Strategies> optStrategies =
                        strategiesRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndDeletionIndicator(
                                addStrategies.getLanguageId(),
                                addStrategies.getCompanyId(),
                                addStrategies.getPlantId(),
                                addStrategies.getWarehouseId(),
                                addStrategies.getStrategyTypeId(),
                                addStrategies.getSequenceIndicator(),
                                0L);
                if (!optStrategies.isEmpty()) {
                    throw new BadRequestException("The given values are getting duplicated.");
                }

                Strategies dbStrategies = new Strategies();
                BeanUtils.copyProperties(addStrategies, dbStrategies, CommonUtils.getNullPropertyNames(addStrategies));

                IkeyValuePair ikeyValuePair =
                        companyRepository.getCompanyIdAndDescription(addStrategies.getCompanyId(), addStrategies.getLanguageId());

                IkeyValuePair ikeyValuePair1 =
                        plantRepository.getPlantIdAndDescription(addStrategies.getPlantId(),
                                addStrategies.getLanguageId(), addStrategies.getCompanyId());

                IkeyValuePair ikeyValuePair2 =
                        warehouseRepository.getWarehouseIdAndDescription(addStrategies.getWarehouseId(),
                                addStrategies.getLanguageId(), addStrategies.getCompanyId(), addStrategies.getPlantId());

                List<IkeyValuePair> ikeyValuePair3 =
                        strategiesRepository.getStrategyIdAndDescription(addStrategies.getStrategyTypeId(), addStrategies.getLanguageId(), addStrategies.getCompanyId(),
                                addStrategies.getPlantId(), addStrategies.getWarehouseId());

                if (ikeyValuePair != null && ikeyValuePair1 != null &&
                        ikeyValuePair2 != null && ikeyValuePair3 != null) {
                    dbStrategies.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
                    dbStrategies.setPlantIdAndDescription(ikeyValuePair1.getPlantId() + "-" + ikeyValuePair1.getDescription());
                    dbStrategies.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
                    for (IkeyValuePair ikeyValuePair4 : ikeyValuePair3) {
                        dbStrategies.setDescription(ikeyValuePair4.getDescription());
                    }
                } else {
                    throw new BadRequestException("The given values of Company Id "
                            + addStrategies.getCompanyId() + " Plant Id "
                            + addStrategies.getPlantId() + " Warehouse Id "
                            + addStrategies.getWarehouseId() + " Strategy Type Id "
                            + addStrategies.getStrategyTypeId() + " doesn't exist ");
                }
                dbStrategies.setDeletionIndicator(0L);
                dbStrategies.setCreatedBy(loginUserID);
                dbStrategies.setUpdatedBy(loginUserID);
                dbStrategies.setCreatedOn(new Date());
                dbStrategies.setUpdatedOn(new Date());
                Strategies savedStrategies = strategiesRepository.save(dbStrategies);

                strategiesList.add(savedStrategies);
            }
            return strategiesList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * @param companyId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param updateStrategies
     * @param loginUserID
     * @return
     */
    public List<Strategies> updateStrategies(String companyId, String languageId, String plantId, String warehouseId,
                                             List<AddStrategies> updateStrategies, String loginUserID) {

        try {
            List<Strategies> dbStrategies = strategiesRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
                    languageId, companyId, plantId, warehouseId, 0L);

            if (dbStrategies != null) {
                for (Strategies strategies : dbStrategies) {
                    strategies.setDeletionIndicator(1L);
                    strategies.setUpdatedBy(loginUserID);
                    strategies.setUpdatedOn(new Date());
                    strategiesRepository.save(strategies);
                }
            } else {
                throw new EntityNotFoundException("The given values of companyId" + companyId +
                        "plantId" + plantId + " warehouseId " + warehouseId + "doesn't exists");
            }

            List<Strategies> createStrategies = createStrategies(updateStrategies, loginUserID);

            return createStrategies;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param loginUserID
     */
    public void deleteStrategies(String companyId, String languageId, String plantId, String warehouseId, String loginUserID) {

        try {
            List<Strategies> strategies = getStrategies(companyId, languageId, plantId, warehouseId);
            if (strategies != null) {
                for (Strategies dbStrategies : strategies) {
                    dbStrategies.setDeletionIndicator(1L);
                    dbStrategies.setUpdatedBy(loginUserID);
                    dbStrategies.setUpdatedOn(new Date());
                    strategiesRepository.save(dbStrategies);
                }
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + strategies);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}