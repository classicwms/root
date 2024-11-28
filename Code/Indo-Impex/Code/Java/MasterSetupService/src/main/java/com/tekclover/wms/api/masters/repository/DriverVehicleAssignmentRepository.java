package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.drivervehicleassignment.DriverVehicleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface DriverVehicleAssignmentRepository extends JpaRepository<DriverVehicleAssignment, String>, JpaSpecificationExecutor<DriverVehicleAssignment> {

    Optional<DriverVehicleAssignment> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndDriverIdAndVehicleNumberAndRouteIdAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, Long driverId, String vehicleNumber, Long routeId, Long deletionIndicator);
}