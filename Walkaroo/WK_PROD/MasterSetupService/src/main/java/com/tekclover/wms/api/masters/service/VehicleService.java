package com.tekclover.wms.api.masters.service;


import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.driver.AddDriver;
import com.tekclover.wms.api.masters.model.driver.Driver;
import com.tekclover.wms.api.masters.model.driver.SearchDriver;
import com.tekclover.wms.api.masters.model.driver.UpdateDriver;
import com.tekclover.wms.api.masters.model.vehicle.AddVehicle;
import com.tekclover.wms.api.masters.model.vehicle.SearchVehicle;
import com.tekclover.wms.api.masters.model.vehicle.UpdateVehicle;
import com.tekclover.wms.api.masters.model.vehicle.Vehicle;
import com.tekclover.wms.api.masters.repository.DriverRepository;
import com.tekclover.wms.api.masters.repository.VehicleRepository;
import com.tekclover.wms.api.masters.repository.specification.DriverSpecification;
import com.tekclover.wms.api.masters.repository.specification.VehicleSpecification;
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
import java.util.Vector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * getAllVehicle
     * @return
     */
    public List<Vehicle> getAllVehicle () {
        List<Vehicle> vehicleList = vehicleRepository.findAll();
//        log.info("vehicleList : " + vehicleList);
        vehicleList = vehicleList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return vehicleList;
    }

    /**
     * getVehicle
     * @return
     */
    public Vehicle getVehicle (String vehicleNumber, String companyCodeId, String plantId, String languageId, String warehouseId) {
        Optional<Vehicle> dbVehicle = vehicleRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndVehicleNumberAndDeletionIndicator(
               companyCodeId,
                plantId,
                warehouseId,
                languageId,
                vehicleNumber,
                0L
        );
        if(dbVehicle.isEmpty()) {
            throw new BadRequestException("The given Values : " +
                    "vehicleNumber - " + vehicleNumber +
                    "companyCodeId" +companyCodeId+
                    "plantId"+plantId+
                    "languageId"+languageId+ " doesn't exist.");
        }
        return dbVehicle.get();
    }


    /**
     *
     * @param searchVehicle
     * @return
     * @throws Exception
     */
    public List<Vehicle> findVehicle(SearchVehicle searchVehicle)
            throws Exception {
        VehicleSpecification spec = new VehicleSpecification(searchVehicle);
        List<Vehicle> results = vehicleRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * createVehicle
     * @param newVehicle
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Vehicle createVehicle (AddVehicle newVehicle, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Vehicle dbVehicle = new Vehicle();
        Optional<Vehicle> duplicateVehicle = vehicleRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndVehicleNumberAndDeletionIndicator(
                newVehicle.getCompanyCodeId(),
                newVehicle.getPlantId(),
                newVehicle.getWarehouseId(),
                newVehicle.getLanguageId(),
                newVehicle.getVehicleNumber(),
                0L);
        if (!duplicateVehicle.isEmpty()) {
            throw new BadRequestException("Record is Getting Duplicated");
        } else {
            BeanUtils.copyProperties(newVehicle, dbVehicle, CommonUtils.getNullPropertyNames(newVehicle));
            dbVehicle.setDeletionIndicator(0L);
            dbVehicle.setCreatedBy(loginUserID);
            dbVehicle.setUpdatedBy(loginUserID);
            dbVehicle.setCreatedOn(new Date());
            dbVehicle.setUpdatedOn(new Date());
            return vehicleRepository.save(dbVehicle);
        }
    }

    /**
     * updateVehicle
     * @param vehicleNumber
     * @param companyCodeId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Vehicle updateVehicle (String companyCodeId, String plantId, String warehouseId, String languageId, String vehicleNumber, UpdateVehicle updateVehicle, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Vehicle dbVehicle = getVehicle(vehicleNumber,companyCodeId,plantId,languageId,warehouseId);
        BeanUtils.copyProperties(updateVehicle, dbVehicle, CommonUtils.getNullPropertyNames(updateVehicle));
        dbVehicle.setUpdatedBy(loginUserID);
        dbVehicle.setUpdatedOn(new Date());
        return vehicleRepository.save(dbVehicle);
    }

    /**
     * deleteVehicle
     * @param vehicleNumber
     * @param companyCodeId
     */
    public void deleteVehicle (String companyCodeId,String languageId,String plantId,String warehouseId,String vehicleNumber,String loginUserID) throws ParseException {
        Vehicle dbVehicle = getVehicle(vehicleNumber,companyCodeId,plantId,languageId,warehouseId);
        if ( dbVehicle != null) {
            dbVehicle.setDeletionIndicator (1L);
            dbVehicle.setUpdatedBy(loginUserID);
            dbVehicle.setUpdatedOn(new Date());
            vehicleRepository.save(dbVehicle);
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + vehicleNumber);
        }
    }
}
