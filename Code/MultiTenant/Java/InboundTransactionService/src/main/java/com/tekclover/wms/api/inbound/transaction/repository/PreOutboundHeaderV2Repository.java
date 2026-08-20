package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.outbound.preoutbound.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PreOutboundHeaderV2Repository extends JpaRepository<PreOutboundHeaderV2, Long>,
        JpaSpecificationExecutor<PreOutboundHeaderV2>, StreamableJpaSpecificationRepository<PreOutboundHeaderV2> {


    PreOutboundHeaderV2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                   String languageId,String companyCodeId,String plantId,String warehouseId,String refDocNumber,String preOutboundNo,Long deletion);

    @Modifying
    @Query(value = "update tblpreoutboundheader set status_id = :statusId , status_text = :text " +
            " where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId " +
            " and wh_id = :warehouseId and ref_doc_no = :refDocNo and pre_ob_no = :preOutboundNo ",nativeQuery = true)
    void updatePreOutboundHeaderV6(@Param("companyCodeId") String companyCodeId,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("refDocNo") String refDocNo,
                                   @Param("preOutboundNo") String preOutboundNo,
                                   @Param("statusId") Long statusId,
                                   @Param("text") String text);

    @Modifying
    @Query(value = "update tblpreoutboundheader set status_id = :statusId , status_text = :text " +
            " where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId " +
            " and wh_id = :warehouseId and ref_doc_no = :refDocNo and pre_ob_no = :preOutboundNo ",nativeQuery = true)
    void updateOutboundHeaderV6(@Param("companyCodeId") String companyCodeId,
                                @Param("plantId") String plantId,
                                @Param("languageId") String languageId,
                                @Param("warehouseId") String warehouseId,
                                @Param("refDocNo") String refDocNo,
                                @Param("preOutboundNo") String preOutboundNo,
                                @Param("statusId") Long statusId,
                                @Param("text") String text);

    @Modifying
    @Query(value = "update tblpreoutboundheader set status_id = :statusId , status_text = :text " +
            " where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId " +
            " and wh_id = :warehouseId and ref_doc_no = :refDocNo and pre_ob_no = :preOutboundNo ",nativeQuery = true)
    void updateOrderManagementHeaderV6(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("refDocNo") String refDocNo,
                                       @Param("preOutboundNo") String preOutboundNo,
                                       @Param("statusId") Long statusId,
                                       @Param("text") String text);

    //BF
    @Query(value = "select * from tblpreoutboundheader where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and " +
            " wh_id = :warehouseId and PRE_OB_NO = :preOutboundNo and REF_DOC_NO = :refDocNumber and IS_DELETED = 0 ", nativeQuery = true)
    public PreOutboundHeaderV2 getPreOutboundHeaderV9(@Param("companyCodeId") String companyCode,
                                                      @Param("plantId") String plantId,
                                                      @Param("languageId") String languageId,
                                                      @Param("warehouseId") String warehouseId,
                                                      @Param("preOutboundNo") String preOutboundNo,
                                                      @Param("refDocNumber") String refDocNumber);
}