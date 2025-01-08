package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.imcapacity.ImCapacity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ImCapacityRepository extends JpaRepository<ImCapacity, String>, JpaSpecificationExecutor<ImCapacity> {

    Optional<ImCapacity> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, String itemCode, Long deletionIndicator);

}