package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.pickup.PickupHeader;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PickupHeaderRepository extends JpaRepository<PickupHeader, Long>,
        JpaSpecificationExecutor<PickupHeader>, StreamableJpaSpecificationRepository<PickupHeader> {

    // Count for MobileDashBoard
    @Query(value = "SELECT COUNT(ref_doc_no) AS count FROM (\n"
            + " select distinct ref_doc_no from \n "
            + " tblpickupheader WHERE \n"
            + "(:languageId IS NULL OR LANG_ID = :languageId) AND \n"
            + "(:companyCode IS NULL OR C_ID = :companyCode) AND \n"
            + "(:plantId IS NULL OR PLANT_ID = :plantId) AND \n"
            + "(:warehouseId IS NULL OR WH_ID = :warehouseId) AND \n"
//            + "(:levelId IS NULL OR LEVEL_ID = :levelId) AND \n"
            + "(STATUS_ID IN (:statusId)) AND \n"
            + "(OB_ORD_TYP_ID IN (:orderTypeId)) AND \n"
            + "(COALESCE(:assignPickerId, null) IS NULL OR (ASS_PICKER_ID IN (:assignPickerId))) AND \n"
//            + "(:orderTypeId IS NULL OR IB_ORD_TYP_ID = :orderTypeId) AND "
            + " IS_DELETED = 0 ) x", nativeQuery = true)
    public Long getPickupHeaderCount(@Param("companyCode") List<String> companyCode,
                                     @Param("plantId") List<String> plantId,
                                     @Param("warehouseId") List<String> warehouseId,
                                     @Param("languageId") List<String> languageId,
//                                           @Param("levelId") String levelId,
                                     @Param("assignPickerId") List<String> assignPickerId,
                                     @Param("statusId") Long statusId,
                                     @Param("orderTypeId") List<Long> orderTypeId);




}
