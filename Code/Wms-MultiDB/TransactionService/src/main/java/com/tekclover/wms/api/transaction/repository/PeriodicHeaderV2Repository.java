package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update PeriodicLineV2 ob SET ob.referenceField1 = 'True' \r\n "
            + " WHERE ob.companyCode = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.cycleCountNo = :cycleCountNo")
    void updatePeriodicHeaderV4(@Param("companyCodeId") String companyCodeId,
                                @Param("plantId") String plantId,
                                @Param("languageId") String languageId,
                                @Param("warehouseId") String warehouseId,
                                @Param("cycleCountNo") String cycleCountNo);


}