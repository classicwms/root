package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.driver.AddDriver;
import com.tekclover.wms.api.masters.model.driver.Driver;
import com.tekclover.wms.api.masters.model.driver.SearchDriver;
import com.tekclover.wms.api.masters.model.driver.UpdateDriver;
import com.tekclover.wms.api.masters.repository.DriverRepository;
import com.tekclover.wms.api.masters.repository.specification.DriverSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    /**
     * getAllDriver
     *
     * @return
     */
    public List<Driver> getAllDriver() {
        try {
            List<Driver> driverList = driverRepository.findAll();
            log.info("driverList : " + driverList);
            driverList = driverList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return driverList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getDriver
     *
     * @param driverId
     * @param companyCodeId
     * @return
     */
    public Driver getDriver(Long driverId, String companyCodeId, String plantId, String languageId, String warehouseId) {
        try {
            Optional<Driver> dbDriver = driverRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndDriverIdAndDeletionIndicator(
                    companyCodeId,
                    languageId,
                    plantId,
                    warehouseId,
                    driverId,
                    0L
            );
            if (dbDriver.isEmpty()) {
                throw new BadRequestException("The given Values : " +
                        "driverId - " + driverId +
                        "companyCodeId" + companyCodeId +
                        "plantId" + plantId +
                        "languageId" + languageId + " doesn't exist.");
            }
            return dbDriver.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * @param searchDriver
     * @return
     */
    public List<Driver> findDriver(SearchDriver searchDriver) {
        try {
            DriverSpecification spec = new DriverSpecification(searchDriver);
            List<Driver> results = driverRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createDriver
     *
     * @param newDriver
     * @return
     */
    public Driver createDriver(AddDriver newDriver, String loginUserID) {
        try {
            Driver dbDriver = new Driver();
            Optional<Driver> duplicateDriver = driverRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndDriverIdAndDeletionIndicator(
                    newDriver.getCompanyCodeId(),
                    newDriver.getLanguageId(),
                    newDriver.getPlantId(),
                    newDriver.getWarehouseId(),
                    newDriver.getDriverId(),
                    0L);
            if (!duplicateDriver.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newDriver, dbDriver, CommonUtils.getNullPropertyNames(newDriver));
                dbDriver.setDeletionIndicator(0L);
                dbDriver.setCreatedBy(loginUserID);
                dbDriver.setUpdatedBy(loginUserID);
                dbDriver.setCreatedOn(new Date());
                dbDriver.setUpdatedOn(new Date());
                return driverRepository.save(dbDriver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateDriver
     *
     * @param driverId
     * @param companyCodeId
     * @return
     */
    public Driver updateDriver(String companyCodeId, String plantId, String warehouseId, String languageId,
                               Long driverId, UpdateDriver updateDriver, String loginUserID) {
        try {
            Driver dbDriver = getDriver(driverId, companyCodeId, plantId, languageId, warehouseId);
            BeanUtils.copyProperties(updateDriver, dbDriver, CommonUtils.getNullPropertyNames(updateDriver));
            dbDriver.setUpdatedBy(loginUserID);
            dbDriver.setUpdatedOn(new Date());
            return driverRepository.save(dbDriver);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteDriver
     *
     * @param driverId
     * @param companyCodeId
     */
    public void deleteDriver(String companyCodeId, String languageId, String plantId, String warehouseId,
                             Long driverId, String loginUserID) {
        try {
            Driver driver = getDriver(driverId, companyCodeId, plantId, languageId, warehouseId);
            if (driver != null) {
                driver.setDeletionIndicator(1L);
                driver.setUpdatedBy(loginUserID);
                driver.setUpdatedOn(new Date());
                driverRepository.save(driver);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + driverId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}