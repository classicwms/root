package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface GrLineV2Repository extends JpaRepository<GrLineV2, Long>, JpaSpecificationExecutor<GrLineV2>,
        StreamableJpaSpecificationRepository<GrLineV2> {


    List<GrLineV2> findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
            String goodsReceiptNo, String itemCode, Long lineNo, String languageId,
            String companyCode, String plantId, String refDocNumber, String packBarcodes,
            String warehouseId, String preInboundNo, String caseCode, Long deletionIndicator);

    Optional<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String goodsReceiptNo, String palletCode, String caseCode,
            String packBarcodes, Long lineNo, String itemCode, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String preInboundNo, String refDocNumber, String packBarcodes,
            Long lineNo, String itemCode, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String refDocNumber, String packBarcodes, String warehouseId,
            String preInboundNo, String caseCode, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            Long lineNo, String itemCode, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<GrLineV2> findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndCreatedOnAndDeletionIndicator(
            String goodsReceiptNo, String itemCode, Long lineNo,
            String languageId, String companyCode, String plantId,
            String refDocNumber, String packBarcodes, String warehouseId,
            String preInboundNo, String caseCode, Date createdOn, Long deletionIndicator);

    List<GrLineV2> findByCompanyCodeAndLanguageIdAndBranchCodeAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String languageId, String branchCode, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNumber, String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    GrLineV2 findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, String packBarcodes,
            String warehouseId, String preInboundNo, String itemCode, String manufacturerName, Long deletionIndicator);

    List<GrLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    @Query(value = "select count(*) \n" +
            "from tblgrline where c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and \n" +
            "wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and PRE_IB_NO = :preInboundNo and is_deleted = 0 and STATUS_ID = :statusId",nativeQuery = true)
    public Long getGrLineStatus17Count(@Param("companyCode") String companyCode,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("refDocNumber") String refDocNumber,
                                       @Param("preInboundNo") String preInboundNo,
                                       @Param("statusId") Long statusId);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, String warehouseId,
            String preInboundNo, String itemCode, String manufacturerName, Long lineNumber, Long deletionIndicator);

    @Query(value = "select sum(gr_qty) \n" +
            "from tblgrline where c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and \n" +
            "wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and PRE_IB_NO = :preInboundNo and \n" +
            "gr_no = :goodsReceiptNo and pal_code = :palletCode and case_code = :caseCode and \n" +
            "itm_code = :itemCode and mfr_name = :manufacturerName and \n" +
            "is_deleted = 0 and ib_line_no = :lineNo \n"+
            "group by itm_code,mfr_name,pre_ib_no,ref_doc_no,gr_no,pal_code,case_code,ib_line_no,lang_id,wh_id,plant_id,c_id ",nativeQuery = true)
    public Double getGrLineQuantity(@Param("companyCode") String companyCode,
                                  @Param("plantId") String plantId,
                                  @Param("languageId") String languageId,
                                  @Param("warehouseId") String warehouseId,
                                  @Param("refDocNumber") String refDocNumber,
                                  @Param("preInboundNo") String preInboundNo,
                                  @Param("goodsReceiptNo") String goodsReceiptNo,
                                  @Param("palletCode") String palletCode,
                                  @Param("caseCode") String caseCode,
                                  @Param("itemCode") String itemCode,
                                  @Param("manufacturerName") String manufacturerName,
                                  @Param("lineNo") Long lineNo);
}