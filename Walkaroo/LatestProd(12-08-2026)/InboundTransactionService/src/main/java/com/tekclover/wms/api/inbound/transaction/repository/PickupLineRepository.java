package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.pickup.PickupLine;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PickupLineRepository extends JpaRepository<PickupLine, Long>,
        JpaSpecificationExecutor<PickupLine>, StreamableJpaSpecificationRepository<PickupLine> {


    @Query(value = "select level_id as levelId from tblhhtuser where usr_id in (:userId) and " +
            "c_id in (:companyCode) and plant_id in (:plantId) and lang_id in (:languageId) and " +
            " wh_id in (:warehouseId) and is_deleted = 0 ", nativeQuery = true)
    public String getLevelId(@Param("companyCode")String companyCode,
                             @Param("plantId")String plantId,
                             @Param("languageId")String languageId,
                             @Param("warehouseId")String warehouseId,
                             @Param(value = "userId")String userId);

}
