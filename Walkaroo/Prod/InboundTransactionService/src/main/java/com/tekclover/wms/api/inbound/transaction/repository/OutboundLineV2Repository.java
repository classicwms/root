package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.dto.v2.OutboundLineV2;
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
public interface OutboundLineV2Repository extends JpaRepository<OutboundLineV2, Long>,
        JpaSpecificationExecutor<OutboundLineV2>,
        StreamableJpaSpecificationRepository<OutboundLineV2> {
    @Query(value = "SELECT COUNT(ref_doc_no) as count FROM \n"
            + "tbloutboundline qh WHERE \n"
            + "(:companyCode IS NULL OR qh.c_id IN (:companyCode)) AND \n"
            + "(:plantId IS NULL OR qh.plant_id IN (:plantId)) AND \n"
            + "(:languageId IS NULL OR qh.lang_id IN (:languageId)) AND \n"
            + "(:warehouseId IS NULL OR qh.wh_id IN (:warehouseId)) AND \n"
            + "(qh.status_id IN (:statusId)) AND \n"
            + "qh.is_deleted = 0 ", nativeQuery = true)
    public Long gettrackingCount(
            @Param("companyCode") List<String> companyCode,
            @Param("plantId") List<String> plantId,
            @Param("languageId") List<String> languageId,
            @Param("warehouseId") List<String> warehouseId,
            @Param("statusId") List<Long> statusId);



}
