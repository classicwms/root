package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
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

import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface GrHeaderV2Repository extends JpaRepository<GrHeaderV2, Long>, JpaSpecificationExecutor<GrHeaderV2>,
        StreamableJpaSpecificationRepository<GrHeaderV2> {


    Optional<GrHeaderV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String stagingNo, String goodsReceiptNo, String palletCode, String caseCode, Long deletionIndicator);

    GrHeaderV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndCaseCodeAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String goodsReceiptNo, String caseCode, String refDocNumber, Long deletionIndicator);

    @Query(value = "select \n" +
            "* \n" +
            "from \n" +
            "tblgrheader \n" +
            "where \n" +
            "c_id IN (:companyCode) and \n" +
            "lang_id IN (:languageId) and \n" +
            "plant_id IN(:plantId) and \n" +
            "wh_id IN (:warehouseId) and \n" +
            "gr_no IN (:goodsReceiptNo) and \n" +
            "case_code IN (:caseCode) and \n" +
            "ref_doc_no IN (:refDocNumber) and \n" +
            "is_deleted = 0", nativeQuery = true)
    List<GrHeaderV2> getGrHeaderV2(
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "goodsReceiptNo") String goodsReceiptNo,
            @Param(value = "caseCode") String caseCode,
            @Param(value = "companyCode") String companyCode,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "refDocNumber") String refDocNumber
    );

    List<GrHeaderV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, Long deletionIndicator);

    List<GrHeaderV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, String warehouseId, String preInboundNo, String caseCode, Long deletionIndicator);

    GrHeaderV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, Long deletionIndicator);

    GrHeaderV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    GrHeaderV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE GrHeaderV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updateGrHeaderStatus(@Param("warehouseId") String warehouseId,
                              @Param("companyCode") String companyCode,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("statusId") Long statusId,
                              @Param("statusDescription") String statusDescription);

    @Transactional
    @Procedure(procedureName = "grheader_status_update_proc")
    public void updateGrheaderStatusUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("goodsReceiptNo") String goodsReceiptNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedOn") Date updatedOn
    );
    @Transactional
    @Procedure(procedureName = "grheader_status_update_ib_cnf_proc")
    public void updateGrheaderStatusUpdateInboundConfirmProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

    GrHeaderV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    //====================================walkaroo==================================================================
    @Query(value = "select \n" +
            "count(*) \n" +
            "from \n" +
            "tblgrheader \n" +
            "where \n" +
            "c_id IN (:companyCode) and \n" +
            "lang_id IN (:languageId) and \n" +
            "plant_id IN(:plantId) and \n" +
            "wh_id IN (:warehouseId) and \n" +
            "PARENT_PRODUCTION_ORDER_NO IN (:parentProductionOrderNo) and \n" +
            "is_deleted = 0 group by PARENT_PRODUCTION_ORDER_NO", nativeQuery = true)
    Long getGrHeaderHeaderCountV3(@Param(value = "companyCode") String companyCode,
                                  @Param(value = "plantId") String plantId,
                                  @Param(value = "languageId") String languageId,
                                  @Param(value = "warehouseId") String warehouseId,
                                  @Param(value = "parentProductionOrderNo") String parentProductionOrderNo);

    @Query(value = "select \n" +
            "count(*) \n" +
            "from \n" +
            "tbliborder2 \n" +
            "where \n" +
            "company_code IN (:companyCode) and \n" +
            "branch_code IN (:plantId) and \n" +
            "parent_production_order_no IN (:parentProductionOrderNo)  \n" +
            "group by parent_production_order_no", nativeQuery = true)
    Long getIbOrderHeaderCountV3(@Param(value = "companyCode") String companyCode,
                                  @Param(value = "plantId") String plantId,
                                  @Param(value = "parentProductionOrderNo") String parentProductionOrderNo);

    @Query(value = "SELECT CASE WHEN \n" +
            "(select count(*) from tblgrheader where c_id = :companyCode and plant_id = :plantId and lang_id IN (:languageId) and wh_id IN (:warehouseId) and \n" +
            "PARENT_PRODUCTION_ORDER_NO IN (:parentProductionOrderNo) and is_deleted = 0 group by PARENT_PRODUCTION_ORDER_NO) = \n" +
            "(select count(*) from tbliborder2 where company_code IN (:companyCode) and \n" +
            "branch_code IN (:plantId) and language_id = :languageId and warehouseid = :warehouseId and\n" +
            "parent_production_order_no IN (:parentProductionOrderNo)  \n" +
            "group by parent_production_order_no) THEN 1 else 0 END as Result ", nativeQuery = true)
    Long getPutAwayHeaderCreateValidationV3(@Param(value = "companyCode") String companyCode,
                                            @Param(value = "plantId") String plantId,
                                            @Param(value = "languageId") String languageId,
                                            @Param(value = "warehouseId") String warehouseId,
                                            @Param(value = "parentProductionOrderNo") String parentProductionOrderNo);

    @Modifying
    @Query(value = "UPDATE tblgrheader SET VEHICLE_NO = :vehicleNo WHERE " +
            "CASE_CODE = :caseCode AND PAL_CODE = :palletCode AND PRE_IB_NO = :preInboundNo " +
            "AND REF_DOC_NO = :refDocNo AND IS_DELETED = 0", nativeQuery = true)
    void updateVehicleNumber(@Param("caseCode") String caseCode,
                             @Param("palletCode") String palletCode,
                             @Param("preInboundNo") String preInboundNo,
                             @Param("refDocNo") String refDocNo,
                             @Param("vehicleNo") String vehicleNo);
    
    //-------------------SAP-DATA-UPDATE-----------------------------------------------------------------------
    @Modifying
    @Query(value = "UPDATE tblgrheader \r\n"
    		+ "SET sap_type = :sapType \r\n"
    		+ "WHERE ref_doc_no = :refDocNumber AND is_deleted = 0", nativeQuery = true)
    void updateGRHeader_SAP(@Param("refDocNumber") String refDocNumber, @Param("sapType") String sapType);
}