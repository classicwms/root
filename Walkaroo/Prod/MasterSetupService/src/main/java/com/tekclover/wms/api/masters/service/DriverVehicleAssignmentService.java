package com.tekclover.wms.api.masters.service;


import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.AddDriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.DriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.SearchDriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.UpdateDriverVehicleAssignment;
import com.tekclover.wms.api.masters.repository.DriverVehicleAssignmentRepository;
import com.tekclover.wms.api.masters.repository.specification.DriverVehicleAssignmentSpecification;
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

@Service
@Slf4j
public class DriverVehicleAssignmentService {

    @Autowired
    private DriverVehicleAssignmentRepository driverVehicleAssignmentRepository;

    /**
     * getAllDriverVehicleAssignment
     * @return
     */
    public List<DriverVehicleAssignment> getAllDriverVehicleAssignment () {
        List<DriverVehicleAssignment> driverVehicleAssignmentList = driverVehicleAssignmentRepository.findAll();
        log.info("driverVehicleAssignmentList : " + driverVehicleAssignmentList);
        driverVehicleAssignmentList =
                driverVehicleAssignmentList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return driverVehicleAssignmentList;
    }

    /**
     *
     * @param driverId
     * @param vehicleNumber
     * @param routeId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public DriverVehicleAssignment getDriverVehicleAssignment (Long driverId,String vehicleNumber,Long routeId,
                                                               String companyCodeId, String plantId, String languageId,
                                                               String warehouseId) {
        Optional<DriverVehicleAssignment> dbDriverVehicleAssignment =
                driverVehicleAssignmentRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndDriverIdAndVehicleNumberAndRouteIdAndDeletionIndicator(
                companyCodeId,
                languageId,
                plantId,
                warehouseId,
                driverId,
                vehicleNumber,
                routeId,
                0L
        );
        if(dbDriverVehicleAssignment.isEmpty()) {
            throw new BadRequestException("The given Values : " +
                    "driverId - " + driverId +
                    "routeId - " + routeId +
                    "vehicleNumber -" +vehicleNumber +
                    "companyCodeId" +companyCodeId +
                    "plantId"+plantId+
                    "languageId"+languageId+ " doesn't exist.");
        }
        return dbDriverVehicleAssignment.get();
    }


    /**
     *
     * @param searchDriverVehicleAssignment
     * @return
     * @throws Exception
     */
    public List<DriverVehicleAssignment> findDriverVehicleAssignment(SearchDriverVehicleAssignment
                                                                             searchDriverVehicleAssignment) throws Exception {

        DriverVehicleAssignmentSpecification spec = new DriverVehicleAssignmentSpecification(searchDriverVehicleAssignment);
        List<DriverVehicleAssignment> results = driverVehicleAssignmentRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     *
     * @param newDriverVehicleAssignment
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public DriverVehicleAssignment createDriverVehicleAssignment (AddDriverVehicleAssignment newDriverVehicleAssignment, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        DriverVehicleAssignment dbDriverVehicleAssignment = new DriverVehicleAssignment();
        Optional<DriverVehicleAssignment> duplicateDriverVehicleAssignment=
                driverVehicleAssignmentRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndDriverIdAndVehicleNumberAndRouteIdAndDeletionIndicator(
                newDriverVehicleAssignment.getCompanyCodeId(),
                newDriverVehicleAssignment.getLanguageId(),
                newDriverVehicleAssignment.getPlantId(),
                newDriverVehicleAssignment.getWarehouseId(),
                newDriverVehicleAssignment.getDriverId(),
                newDriverVehicleAssignment.getVehicleNumber(),
                newDriverVehicleAssignment.getRouteId(),
                0L);
        if (!duplicateDriverVehicleAssignment.isEmpty()) {
            throw new BadRequestException("Record is Getting Duplicated");
        } else {
            BeanUtils.copyProperties(newDriverVehicleAssignment, dbDriverVehicleAssignment, CommonUtils.getNullPropertyNames(newDriverVehicleAssignment));
            dbDriverVehicleAssignment.setDeletionIndicator(0L);
            dbDriverVehicleAssignment.setCreatedBy(loginUserID);
            dbDriverVehicleAssignment.setUpdatedBy(loginUserID);
            dbDriverVehicleAssignment.setCreatedOn(new Date());
            dbDriverVehicleAssignment.setUpdatedOn(new Date());
            return driverVehicleAssignmentRepository.save(dbDriverVehicleAssignment);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param driverId
     * @param routeId
     * @param vehicleNumber
     * @param loginUserID
     * @param updateDriverVehicleAssignment
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public DriverVehicleAssignment updateDriverVehicleAssignment (String companyCodeId, String plantId, String warehouseId,
                                                                  String languageId, Long driverId,Long routeId,
                                                                  String vehicleNumber, String loginUserID,
                                                                  UpdateDriverVehicleAssignment updateDriverVehicleAssignment)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        DriverVehicleAssignment dbDriverVehicleAssignment =
                getDriverVehicleAssignment(driverId,vehicleNumber,routeId,companyCodeId,plantId,languageId,warehouseId);

        BeanUtils.copyProperties(updateDriverVehicleAssignment, dbDriverVehicleAssignment,
                CommonUtils.getNullPropertyNames(updateDriverVehicleAssignment));
        dbDriverVehicleAssignment.setUpdatedBy(loginUserID);
        dbDriverVehicleAssignment.setUpdatedOn(new Date());
        return driverVehicleAssignmentRepository.save(dbDriverVehicleAssignment);
    }

    /**
     *
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param driverId
     * @param vehicleNumber
     * @param routeId
     * @param loginUserID
     */
    public void deleteDriverVehicleAssignment (String companyCodeId,String languageId,String plantId,String warehouseId,
                                               Long driverId,String vehicleNumber,Long routeId,String loginUserID) throws ParseException {
        DriverVehicleAssignment driverVehicleAssignment =
                getDriverVehicleAssignment(driverId,vehicleNumber,routeId,companyCodeId,plantId,languageId,warehouseId);
        if ( driverVehicleAssignment != null) {
            driverVehicleAssignment.setDeletionIndicator (1L);
            driverVehicleAssignment.setUpdatedBy(loginUserID);
            driverVehicleAssignment.setUpdatedOn(new Date());
            driverVehicleAssignmentRepository.save(driverVehicleAssignment);
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + driverId);
        }
    }
}
