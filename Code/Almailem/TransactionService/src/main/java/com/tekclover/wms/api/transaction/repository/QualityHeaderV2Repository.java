package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QualityHeaderV2Repository extends JpaRepository<QualityHeaderV2, Long>,
        JpaSpecificationExecutor<QualityHeaderV2>, StreamableJpaSpecificationRepository<QualityHeaderV2> {


    QualityHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo, Long deletionIndicator);

    @Query("Select count(ob) from QualityHeader ob where ob.companyCodeId=:companyCodeId and ob.plantId=:plantId and ob.languageId=:languageId and ob.warehouseId=:warehouseId and ob.refDocNumber=:refDocNumber and \r\n"
            + " ob.preOutboundNo=:preOutboundNo and ob.statusId = :statusId and ob.deletionIndicator=:deletionIndicator")
    public long getQualityHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicatorV2(
            @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId,
            @Param("languageId") String languageId, @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber, @Param("preOutboundNo") String preOutboundNo,
            @Param("statusId") Long statusId, @Param("deletionIndicator") Long deletionIndicator);

    QualityHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
            String qualityInspectionNo, String actualHeNo, Long deletionIndicator);

    List<QualityHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo, Long deletionIndicator);

    List<QualityHeaderV2> findByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId, Long statusId, Long deletionIndicator);

    List<QualityHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPickupNumberAndPartnerCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String preOutboundNo, String refDocNumber, String pickupNumber, String partnerCode, Long deletionIndicator);

    Optional<QualityHeaderV2> findByQualityInspectionNo(String qualityInspectionNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE QualityHeaderV2 qh SET qh.statusId = 55, qh.referenceField10 = :referenceField10, qh.statusDescription = :referenceField10 \r\n"
            + " WHERE qh.qualityInspectionNo = :qualityInspectionNo")
    public void updateQualityHeader (@Param ("referenceField10") String referenceField10,
                                     @Param ("qualityInspectionNo") String qualityInspectionNo);

    @Transactional
    @Procedure(procedureName = "quality_header_update_proc")
    public void updateQualityHeaderStatusUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("qualityInspectionNo") String qualityInspectionNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("@created") String created);

    List<QualityHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<QualityHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);


    @Query(value = "select * from tblqualityheader where is_deleted = 0 and " +
            "(coalesce(:warehouseId, null) is null or (wh_id in (:warehouseId))) and " +
            "(coalesce(:companyId, null) is null or (c_id in (:companyId))) and " +
            "(coalesce(:plantId, null) is null or (plant_id in (:plantId))) and " +
            "(coalesce(:languageId, null) is null or (lang_id in (:languageId))) and " +
            "(coalesce(:refDocNumber, null) is null or (ref_doc_no in (:refDocNumber))) and " +
            "(coalesce(:partnerCode, null) is null or (partner_code in (:partnerCode))) and " +
            "(coalesce(:preOutboundNo, null) is null or (pre_ob_no in (:preOutboundNo))) and " +
            "(coalesce(:qualityInspectionNo, null) is null or (QC_NO in (:qualityInspectionNo))) and " +
            "(coalesce(:actualHeNo, null) is null or (PICK_HE_NO in (:actualHeNo))) and " +
            "(coalesce(:outboundOrderTypeId, null) is null or (OB_ORD_TYP_ID in (:outboundOrderTypeId))) and " +
            "(coalesce(:statusId, null) is null or (status_id in (:statusId))) and " +
            "(coalesce(:soType, null) is null or (ref_field_4 in (:soType))) and " +
            "(COALESCE(CONVERT(VARCHAR(255), :startCreatedOn), null) IS NULL OR (QC_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startCreatedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endCreatedOn), null))) ", nativeQuery = true)
    List<QualityHeaderV2> findQualityHeader(@Param("warehouseId") List<String> warehouseId,
                                            @Param("companyId") List<String> companyId,
                                            @Param("plantId") List<String> plantId,
                                            @Param("languageId") List<String> languageId,
                                            @Param("refDocNumber") List<String> refDocNumber,
                                            @Param("partnerCode") List<String> partnerCode,
                                            @Param("qualityInspectionNo") List<String> qualityInspectionNo,
                                            @Param("preOutboundNo") List<String> preOutboundNo,
                                            @Param("actualHeNo") List<String> actualHeNo,
                                            @Param("outboundOrderTypeId") List<Long> outboundOrderTypeId,
                                            @Param("statusId") List<Long> statusId,
                                            @Param("soType") List<String> soType,
                                            @Param("startCreatedOn") Date startCreatedOn,
                                            @Param("endCreatedOn") Date endCreatedOn);
}