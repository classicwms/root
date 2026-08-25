package com.tekclover.wms.api.inbound.orders.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.orders.model.outbound.quality.QualityHeader;

@Repository
@Transactional
public interface QualityHeaderRepository extends JpaRepository<QualityHeader, Long>,
        JpaSpecificationExecutor<QualityHeader>, StreamableJpaSpecificationRepository<QualityHeader> {

    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    public List<QualityHeader> findAll();

    public Optional<QualityHeader> findByQualityInspectionNo(String qualityInspectionNo);

    public QualityHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo,
            String actualHeNo, Long deletionIndicator);


    public List<QualityHeader> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId, Long deletionIndicator);

    public List<QualityHeader> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPickupNumberAndPartnerCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String pickupNumber, String partnerCode,
            Long l);

    public QualityHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
            String qualityInspectionNo, String actualHeNo, long l);

    public List<QualityHeader> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo,
            Long l);

    @Query("Select count(ob) from QualityHeader ob where ob.warehouseId=:warehouseId and ob.refDocNumber=:refDocNumber and \r\n"
            + " ob.preOutboundNo=:preOutboundNo and ob.statusId = :statusId and ob.deletionIndicator=:deletionIndicator")
    public long getQualityHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
            @Param("warehouseId") String warehouseId, @Param("refDocNumber") String refDocNumber, @Param("preOutboundNo") String preOutboundNo,
            @Param("statusId") Long statusId, @Param("deletionIndicator") long deletionIndicator);

//    @Query(value = "Select count(ob) from tblqualityheader ob where ob.c_id = :companyCode and ob.plant_id = :plantId and "
//            + "ob.lang_id = :languageId and wh_id = :warehouseId and status_id = :statusId and ob.is_deleted = 0 GROUP BY ob.REF_DOC_NO",nativeQuery = true)
//    public Long getCount(
//            @Param("companyCode") String companyCode,
//            @Param("plantId") String plantId,
//            @Param("languageId") String languageId,
//            @Param("warehouseId") String warehouseId,
//            @Param("statusId") Long statusId);

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