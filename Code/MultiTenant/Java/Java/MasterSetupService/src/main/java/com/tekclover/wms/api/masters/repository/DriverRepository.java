package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.driver.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface DriverRepository extends JpaRepository<Driver,Long>, JpaSpecificationExecutor<Driver> {

    Optional<Driver>findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndDriverIdAndDeletionIndicator(
            String companyCodeId,String languageId,String plantId,String warehouseId,Long driverId,Long deletionIndicator);
}
