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

    //-----------------------Added---By---Muru--------------------------------------------------------------
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblgrheader \r\n"
    		+ "	SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, \r\n"
    		+ "	GR_CNF_BY = :updatedBy, GR_CNF_ON = :updatedOn\r\n"
    		+ "	WHERE IS_DELETED = 0 AND \r\n"
    		+ "			C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \r\n"
    		+ "			REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo", nativeQuery = true)
    void updateGRHeaderStatusOnPartialConfirmation(
    		@Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblgrheader SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, \n" +
            "GR_UTD_ON = :updatedOn, GR_CNF_ON = :updatedOn \n" +
            "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId \n" +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber \n" +
            "AND PRE_IB_NO = :preInboundNo AND IS_DELETED = 0 \n" +
            "AND ((SELECT COUNT(1) FROM tblstagingline \n" +
            "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId \n" +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber \n" +
            "AND PRE_IB_NO = :preInboundNo AND IS_DELETED = 0) = \n" +
            "(SELECT COUNT(1) FROM (SELECT REF_DOC_NO FROM TBLGRLINE \n" +
            "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId \n" +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber \n" +
            "AND PRE_IB_NO = :preInboundNo AND IS_DELETED = 0 \n" +
            "GROUP BY IB_LINE_NO, ITM_CODE, MFR_NAME, REF_DOC_NO, ORD_QTY \n" +
            "HAVING SUM(GR_QTY) = ORD_QTY) X))", nativeQuery = true)
    void updateGrHeader(@Param("statusId") Long statusId,
                        @Param("statusDescription") String statusDescription,
                        @Param("companyCodeId") String companyCodeId,
                        @Param("plantId") String plantId,
                        @Param("languageId") String languageId,
                        @Param("warehouseId") String warehouseId,
                        @Param("refDocNumber") String refDocNumber,
                        @Param("preInboundNo") String preInboundNo,
                        @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblgrheader SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, " +
            "GR_CNF_BY = :updatedBy, GR_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId " +
            "AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
            "AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo " +
            "AND (select count(*) from tblinboundline where ref_doc_no = :refDocNumber and PRE_IB_NO = :preInboundNo) = " +
            "(select count(*) from tblinboundline where ref_doc_no = :refDocNumber and PRE_IB_NO = :preInboundNo and status_id = 24)", nativeQuery = true)
    void updateGrHeader(@Param("statusId") Long statusId, @Param("statusDescription") String statusDescription,
                        @Param("updatedBy") String updatedBy, @Param("updatedOn") Date updatedOn,
                        @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId,
                        @Param("languageId") String languageId, @Param("warehouseId") String warehouseId,
                        @Param("refDocNumber") String refDocNumber, @Param("preInboundNo") String preInboundNo);


}