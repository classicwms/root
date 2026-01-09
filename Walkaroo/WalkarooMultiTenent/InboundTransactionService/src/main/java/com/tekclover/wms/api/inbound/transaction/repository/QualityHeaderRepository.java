package com.tekclover.wms.api.inbound.transaction.repository;


import com.tekclover.wms.api.inbound.transaction.model.quality.QualityHeader;
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
public interface QualityHeaderRepository extends JpaRepository<QualityHeader, Long>,
        JpaSpecificationExecutor<QualityHeader>, StreamableJpaSpecificationRepository<QualityHeader> {

    @Query(value = "SELECT COUNT(*) as count FROM tblqualityheader qh WHERE qh.c_id = :companyCode "
            + "AND qh.plant_id = :plantId AND qh.lang_id = :languageId AND qh.wh_id = :warehouseId "
            + "AND qh.status_id = :statusId AND qh.is_deleted = 0 GROUP BY qh.REF_DOC_NO", nativeQuery = true)
    public List<Long> getQualityHeaderCount(
            @Param("companyCode") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("statusId") Long statusId);

    @Query(value = "SELECT COUNT(ref_doc_no) as count FROM (\n"
            + "select distinct ref_doc_no from \n"
            + "tblqualityheader qh WHERE \n"
            + "(:companyCode IS NULL OR qh.c_id IN (:companyCode)) AND \n"
            + "(:plantId IS NULL OR qh.plant_id IN (:plantId)) AND \n"
            + "(:languageId IS NULL OR qh.lang_id IN (:languageId)) AND \n"
            + "(:warehouseId IS NULL OR qh.wh_id IN (:warehouseId)) AND \n"
            + "(qh.status_id IN (:statusId)) AND \n"
            + "qh.is_deleted = 0) x ", nativeQuery = true)
    public Long getQualityCount(
            @Param("companyCode") List<String> companyCode,
            @Param("plantId") List<String> plantId,
            @Param("languageId") List<String> languageId,
            @Param("warehouseId") List<String> warehouseId,
            @Param("statusId") Long statusId);


}
