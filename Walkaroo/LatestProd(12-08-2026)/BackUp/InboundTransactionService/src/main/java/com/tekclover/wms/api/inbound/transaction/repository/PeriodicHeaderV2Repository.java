package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.v2.PeriodicHeaderV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PeriodicHeaderV2Repository extends JpaRepository<PeriodicHeaderV2, Long>,
        JpaSpecificationExecutor<PeriodicHeaderV2>, StreamableJpaSpecificationRepository<PeriodicHeaderV2> {

    PeriodicHeaderV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNo(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo);

    PeriodicHeaderV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNo(
            String companyCode, String plantId, String languageId, String warehouseId, Long cycleCountTypeId, String cycleCountNo);


    Optional<PeriodicHeaderV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long cycleCountTypeId, String cycleCountNo, Long deletionIndicator);


}