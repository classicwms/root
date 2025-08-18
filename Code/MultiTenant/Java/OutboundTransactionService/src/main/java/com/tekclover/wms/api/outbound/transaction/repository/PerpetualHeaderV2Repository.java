package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderV2;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PerpetualHeaderV2Repository extends JpaRepository<PerpetualHeaderV2, Long>,
        JpaSpecificationExecutor<PerpetualHeaderV2>, StreamableJpaSpecificationRepository<PerpetualHeaderV2> {

    PerpetualHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNo(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo);

    Optional<PerpetualHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            Long cycleCountTypeId, String cycleCountNo, Long movementTypeId, Long subMovementTypeId, Long deletionIndicator);

    @Query(value = "SELECT * from tblperpetualheader \n " +
            "WHERE c_id = :companyCodeId AND plant_id = :plantId AND wh_id = :warehouseId \n" +
            "AND cc_typ_id = :cycleCountTypeId AND cc_no = :cycleCountNo AND mvt_typ_id = :movementTypeId \n " +
            "AND sub_mvt_typ_id = :subMovementTypeId AND is_deleted = 0", nativeQuery = true)
    PerpetualHeaderV2 getPerpetualHeader(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("cycleCountTypeId") Long cycleCountTypeId,
                                         @Param("cycleCountNo") String cycleCountNo,
                                         @Param("movementTypeId") Long movementTypeId,
                                         @Param("subMovementTypeId") Long subMovementTypeId);


    Optional<PerpetualHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, Long deletionIndicator);
}