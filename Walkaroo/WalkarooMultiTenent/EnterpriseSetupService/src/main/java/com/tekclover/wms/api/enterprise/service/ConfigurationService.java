package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.configuration.Configuration;
import com.tekclover.wms.api.enterprise.model.configuration.SearchConfiguration;
import com.tekclover.wms.api.enterprise.model.floor.Floor;
import com.tekclover.wms.api.enterprise.model.floor.SearchFloor;
import com.tekclover.wms.api.enterprise.repository.ConfigurationRepository;
import com.tekclover.wms.api.enterprise.repository.specification.ConfigurationSpecification;
import com.tekclover.wms.api.enterprise.repository.specification.FloorSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ConfigurationService extends BaseService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    /**
     * getConfiguration
     *
     * @param configurationId
     * @return
     */
    public Configuration getConfiguration(Long configurationId) {
        Optional<Configuration> configuration = configurationRepository.findByConfigurationIdAndDeletionIndicator(configurationId, 0L);
        if (configuration.isPresent()) {
            return configuration.get();
        } else {
            throw new BadRequestException("The given Configuration ID : " + configurationId + " doesn't exist.");
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public Configuration getConfiguration(String companyCodeId, String plantId, String languageId, String warehouseId) {
        Optional<Configuration> configuration = configurationRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, 0L);
        if (configuration.isPresent()) {
            return configuration.get();
        }
        return null;
    }

    /**
     * @param configurationList
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public List<Configuration> createConfiguration(List<Configuration> configurationList, String loginUserId) throws Exception {
        try {
            List<Configuration> createdConfigurationList = new ArrayList<>();
            if (configurationList != null && !configurationList.isEmpty()) {
                for (Configuration newConfiguration : configurationList) {
                    log.info("NewConfiguration : " + newConfiguration);
                    boolean duplicateRecord = configurationRepository.existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
                            newConfiguration.getCompanyCodeId(), newConfiguration.getPlantId(),
                            newConfiguration.getLanguageId(), newConfiguration.getWarehouseId(), 0L);
                    if (duplicateRecord) {
                        throw new BadRequestException("Record already exist");
                    }
                    Configuration dbConfiguration = new Configuration();
                    BeanUtils.copyProperties(newConfiguration, dbConfiguration, CommonUtils.getNullPropertyNames(newConfiguration));
                    dbConfiguration.setDeletionIndicator(0L);
                    dbConfiguration.setCreatedBy(loginUserId);
                    dbConfiguration.setCreatedOn(new Date());
                    Configuration configuration = configurationRepository.save(dbConfiguration);
                    log.info("Configuration Created: " + configuration);
                    createdConfigurationList.add(configuration);
                }
            }
            return createdConfigurationList;
        } catch (Exception e) {
            log.error("Exception Occured while creating configuration: " + e.toString());
            throw e;
        }
    }

    /**
     * @param updateConfigurationList
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public List<Configuration> updateConfiguration(List<Configuration> updateConfigurationList, String loginUserId) throws Exception {
        List<Configuration> updatedConfigurationList = new ArrayList<>();
        if (updateConfigurationList != null && !updateConfigurationList.isEmpty()) {
            for (Configuration updateConfiguration : updateConfigurationList) {
                log.info("Update Configuration : " + updateConfiguration);
                Configuration dbConfiguration = getConfiguration(updateConfiguration.getCompanyCodeId(), updateConfiguration.getPlantId(),
                        updateConfiguration.getLanguageId(), updateConfiguration.getWarehouseId());
                log.info("dbConfiguration : " + dbConfiguration);
                if (dbConfiguration != null) {
                    BeanUtils.copyProperties(updateConfiguration, dbConfiguration, CommonUtils.getNullPropertyNames(updateConfiguration));
                    dbConfiguration.setUpdatedBy(loginUserId);
                    dbConfiguration.setUpdatedOn(new Date());
                    Configuration configuration = configurationRepository.save(dbConfiguration);
                    log.info("Configuration updated: " + configuration);
                    updatedConfigurationList.add(configuration);
                }
            }
        }
        return updatedConfigurationList;
    }

    /**
     * @param configurationId
     * @param loginUserId
     * @throws Exception
     */
    public void deleteConfiguration(Long configurationId, String loginUserId) throws Exception {
        try {
            Configuration configuration = getConfiguration(configurationId);
            if (configuration != null) {
                configuration.setDeletionIndicator(1L);
                configuration.setUpdatedBy(loginUserId);
                configuration.setUpdatedOn(new Date());
                configurationRepository.save(configuration);
            } else {
                throw new BadRequestException("Configuration Id Not Exist : " + configurationId);
            }
        } catch (Exception e) {
            log.error("Exception while Configuration Delete : " + e.toString());
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @throws Exception
     */
    public void deleteConfiguration(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserId) throws Exception {
        try {
            Configuration configuration = getConfiguration(companyCodeId, plantId, languageId, warehouseId);
            if (configuration != null) {
                configuration.setDeletionIndicator(1L);
                configuration.setUpdatedBy(loginUserId);
                configuration.setUpdatedOn(new Date());
                configurationRepository.save(configuration);
            } else {
                throw new BadRequestException("Configuration Not Exist for : " + companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId);
            }
        } catch (Exception e) {
            log.error("Exception while deleting Configuration : " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param searchConfiguration
     * @return
     * @throws Exception
     */
    public Stream<Configuration> findConfiguration(SearchConfiguration searchConfiguration) throws Exception {
        if (searchConfiguration.getStartCreatedOn() != null && searchConfiguration.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchConfiguration.getStartCreatedOn(), searchConfiguration.getEndCreatedOn());
            searchConfiguration.setStartCreatedOn(dates[0]);
            searchConfiguration.setEndCreatedOn(dates[1]);
        }
        ConfigurationSpecification spec = new ConfigurationSpecification(searchConfiguration);
        return configurationRepository.stream(spec, Configuration.class);
    }
}
