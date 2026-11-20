package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

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
    
    //============================PGIReversal================================================================
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblqualityheader SET STATUS_ID = :statusId, REF_FIELD_10 = :statusDescription, STATUS_TEXT = :statusDescription \n" +
            "WHERE LANG_ID = :languageId AND C_ID = :companyCodeId AND \n" +
            "PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber", nativeQuery = true)
    void updateQualityHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNumber") String refDocNumber,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);
}