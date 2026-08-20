package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.outbound.preoutbound.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface PreOutboundLineV2Repository extends JpaRepository<PreOutboundLineV2, Long>,
        JpaSpecificationExecutor<PreOutboundLineV2>, StreamableJpaSpecificationRepository<PreOutboundLineV2> {

    //    @Query(value = "select * from tblpreoutboundline where itm_code = :itemCode and status_id in (39,48) and is_deleted=0 and\n" +
//            "Exists(select * from tblstagingline where cross_dock = 1 and status_id = 14 and itm_code = :itemCode and is_deleted=0)",nativeQuery = true)
//    List<PreOutboundLineV2> findPreOutboundLineV6(@Param("itemCode") String itemCode);
    @Query(value = "select * from tblpreoutboundline where itm_code = :itemCode and status_id in (39,48,47) and is_deleted=0 and WH_ID not in ('4000') and \n" +
            "Exists(select * from tblstagingline where status_id = 14 and itm_code = :itemCode and is_deleted=0 and WH_ID not in ('4000'))", nativeQuery = true)
    List<PreOutboundLineV2> findPreOutboundLineV6(@Param("itemCode") String itemCode);

    @Query(value = "select * from tblpreoutboundline where itm_code = :itemCode and status_id = 5 and is_deleted=0 ", nativeQuery = true)
    List<PreOutboundLineV2> findPreOutboundLineV9(@Param("itemCode") String itemCode);

    @Modifying
    @Query(value = "update tblpreoutboundline set status_id = :statusId , status_text = :statusDescription \n" +
            "where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and " +
            " ref_doc_no = :refDocNo and pre_ob_no = :preOutboundNo and itm_code = :itemCode and is_deleted = 0 ", nativeQuery = true)
    void updatePreOutboundLineV6(@Param("companyCodeId") String companyCodeId,
                                 @Param("plantId") String plantId,
                                 @Param("languageId") String languageId,
                                 @Param("warehouseId") String warehouseId,
                                 @Param("refDocNo") String refDocNo,
                                 @Param("preOutboundNo") String preOutboundNo,
                                 @Param("itemCode") String itemCode,
                                 @Param("statusId") Long statusId,
                                 @Param("statusDescription") String statusDescription);

    @Modifying
    @Query(value = "update tblpreoutboundline set status_id = :statusId , status_text = :statusDescription " +
            " where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and " +
            " wh_id = :warehouseId and ref_doc_no = :refDocNo and pre_ob_no = :preOutboundNo and itm_code = :itemCode ", nativeQuery = true)
    void updatePreOutboundLineDeliveryV6(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNo") String refDocNo,
                                         @Param("preOutboundNo") String preOutboundNo,
                                         @Param("itemCode") String itemCode,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);

    @Modifying
    @Query(value = "update tbloutboundline set status_id = :statusId , status_text = :statusDescription , " +
            " DLV_CNF_ON = :deliveryConfirmedOn where C_ID = :companyCodeId and PLANT_ID = :plantId " +
            " and LANG_ID = :languageId and WH_ID = :warehouseId and REF_DOC_NO = :refDocNo and " +
            " PRE_OB_NO = :preOutboundNo and ITM_CODE = :itemCode and OB_LINE_NO = :lineNo ", nativeQuery = true)
    void updateOutboundLineV6(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNo") String refDocNo,
                              @Param("preOutboundNo") String preOutboundNo,
                              @Param("itemCode") String itemCode,
                              @Param("statusId") Long statusId,
                              @Param("statusDescription") String statusDescription,
                              @Param("deliveryConfirmedOn") Date deliveryConfirmedOn,
                              @Param("lineNo") Long lineNo);

    @Modifying
    @Query(value = "update tblordermangementline set status_id = :statusId , status_text = :statusDescription " +
            " where C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId " +
            " and REF_DOC_NO = :refDocNo and PRE_OB_NO = :preOutboundNo and ITM_CODE = :itemCode and OB_LINE_NO = :lineNo ", nativeQuery = true)
    void updateOrderManagementLineV6(@Param("companyCodeId") String companyCodeId,
                                     @Param("plantId") String plantId,
                                     @Param("languageId") String languageId,
                                     @Param("warehouseId") String warehouseId,
                                     @Param("refDocNo") String refDocNo,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("itemCode") String itemCode,
                                     @Param("statusId") Long statusId,
                                     @Param("statusDescription") String statusDescription,
                                     @Param("lineNo") Long lineNo);

    @Modifying
    @Query(value = "update tblpickupheader set status_id = :statusId , status_text = :statusDescription " +
            " where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and  " +
            " wh_id = :warehouseId and ref_doc_no = :refDocNo and pre_ob_no = :preOutboundNo and  " +
            " itm_code = :itemCode and ob_line_no = :lineNo ", nativeQuery = true)
    void updatePickupHeaderV6(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNo") String refDocNo,
                              @Param("preOutboundNo") String preOutboundNo,
                              @Param("itemCode") String itemCode,
                              @Param("statusId") Long statusId,
                              @Param("statusDescription") String statusDescription,
                              @Param("lineNo") Long lineNo);

    @Modifying
    @Query(value = "update tblpickupline set status_id = :statusId , status_text = :statusDescription " +
            " where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and " +
            " wh_id = :warehouseId and ref_doc_no = :refDocNo and pre_ob_no = :preOutboundNo and " +
            " itm_code = :itemCode and ob_line_no = :lineNo ", nativeQuery = true)
    void updatePickupLineV6(@Param("companyCodeId") String companyCodeId,
                            @Param("plantId") String plantId,
                            @Param("languageId") String languageId,
                            @Param("warehouseId") String warehouseId,
                            @Param("refDocNo") String refDocNo,
                            @Param("preOutboundNo") String preOutboundNo,
                            @Param("itemCode") String itemCode,
                            @Param("statusId") Long statusId,
                            @Param("statusDescription") String statusDescription,
                            @Param("lineNo") Long lineNo);

    @Query(value = "select COALESCE(SUM(ord_qty), 0) from tblpreoutboundline where C_ID = :companyCodeId " +
            " and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId and " +
            " REF_DOC_NO = :refDocNo and PRE_OB_NO = :preOutboundNo and IS_DELETED =0 ", nativeQuery = true)
    public Long sumOfOrderQtyPreOutboundLineV6(@Param("companyCodeId") String companyCodeId,
                                               @Param("plantId") String plantId,
                                               @Param("languageId") String languageId,
                                               @Param("warehouseId") String warehouseId,
                                               @Param("refDocNo") String refDocNo,
                                               @Param("preOutboundNo") String preOutboundNo);

//    @Query(value = "select COALESCE(SUM(ORD_QTY), 0) from tbloutboundline where C_ID = :companyCodeId " +
//            " and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId and " +
//            " REF_DOC_NO = :refDocNo and PRE_OB_NO = :preOutboundNo and IS_DELETED =0 ",nativeQuery = true)
//    public Long sumOfOrderQtyOutboundLineV6(@Param("companyCodeId") String companyCodeId,
//                                            @Param("plantId") String plantId,
//                                            @Param("languageId") String languageId,
//                                            @Param("warehouseId") String warehouseId,
//                                            @Param("refDocNo") String refDocNo,
//                                            @Param("preOutboundNo") String preOutboundNo);

    @Query(value = "select COALESCE(SUM(alloc_qty), 0) from tblordermangementline where C_ID = :companyCodeId " +
            " and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId and " +
            " REF_DOC_NO = :refDocNo and PRE_OB_NO = :preOutboundNo and IS_DELETED =0 ", nativeQuery = true)
    public Long sumOfOrderQtyOutboundLineV6(@Param("companyCodeId") String companyCodeId,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("refDocNo") String refDocNo,
                                            @Param("preOutboundNo") String preOutboundNo);

    @Query(value = "select status_id statusId from tblpreoutboundline where C_ID = :companyCodeId " +
            " and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId and " +
            " REF_DOC_NO = :refDocNo and PRE_OB_NO = :preOutboundNo and ITM_CODE = :itemCode and IS_DELETED =0 ", nativeQuery = true)
    public Long getPreOutboundLineV9(@Param("companyCodeId") String companyCodeId,
                                                       @Param("plantId") String plantId,
                                                       @Param("languageId") String languageId,
                                                       @Param("warehouseId") String warehouseId,
                                                       @Param("refDocNo") String refDocNo,
                                                       @Param("preOutboundNo") String preOutboundNo,
                                                       @Param("itemCode") String itemCode);

}