package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.numberrangestoragebin.NumberRangeStorageBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface NumberRangeStorageBinRepository extends JpaRepository<NumberRangeStorageBin, Long>,
        JpaSpecificationExecutor<NumberRangeStorageBin> {

    Optional<NumberRangeStorageBin> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndRowIdAndAisleNumberAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, Long floorId, String storageSectionId,
            String rowId, String aisleNumber, Long deletionIndicator);
}