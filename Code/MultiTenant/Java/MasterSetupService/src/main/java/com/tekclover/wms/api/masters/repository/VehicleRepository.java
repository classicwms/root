package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface VehicleRepository extends JpaRepository<Vehicle,String>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle>findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndVehicleNumberAndDeletionIndicator(String companyCodeId,String plantId,String warehouseId,String languageId,String vehicleNumber,Long deletionIndicator);
}
