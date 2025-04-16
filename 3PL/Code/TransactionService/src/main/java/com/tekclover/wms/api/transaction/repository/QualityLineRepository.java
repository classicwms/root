package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;

@Repository
@Transactional
public interface QualityLineRepository extends JpaRepository<QualityLine, Long>,
        JpaSpecificationExecutor<QualityLine>, StreamableJpaSpecificationRepository<QualityLine> {

    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    public List<QualityLine> findAll();

    public Optional<QualityLine>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
            String languageId, Long companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, Long deletionIndicator);

    public Optional<QualityLine> findByPartnerCode(String partnerCode);

    public QualityLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, long l);

    public QualityLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String qualityInspectionNo, String itemCode, long l);

    public List<QualityLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, long l);

    // For Dashboard
    @Query(value = "SELECT SUM(QC_QTY) FROM tblqualityline WHERE WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND\r\n"
            + "PRE_OB_NO = :preOutboundNo AND OB_LINE_NO = :obLineNumber AND ITM_CODE = :itemCode\r\n"
            + "GROUP BY REF_DOC_NO", nativeQuery = true)
    public Double getQualityLineCount(@Param("warehouseId") String warehouseId,
                                      @Param("refDocNumber") String refDocNumber,
                                      @Param("preOutboundNo") String preOutboundNo,
                                      @Param("obLineNumber") Long obLineNumber,
                                      @Param("itemCode") String itemCode);

    public List<QualityLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers,
            List<String> itemCodes, long l);

    @Query(value = "select PACK_IND as packInt from tblbusinesspartner where PARTNER_CODE = :partnerCode and is_deleted =0 ", nativeQuery = true)
    public Long getPartnerInd(@Param("partnerCode") Long partnerCode);


    @Query(value = "select PACK_IND as packInt from tblbusinesspartner where WH_ID in (:warehouseId) And C_ID in (:companyCodeId) And PLANT_ID in (:plantId) And PRE_OB_NO in (:preOutboundNo) " +
            " And REF_DOC_NO in (:refDocNumber) And PARTNER_CODE in (:partnerCode) And QC_NO in (:qualityInspectionNo) And is_deleted = 0 ", nativeQuery = true)
    public String getPackInd(@Param("warehouseId") String warehouseId,
                             @Param("companyCodeId") String companyCodeId,
                             @Param("plantId") String plantId,
                             @Param("preOutboundNo") String preOutboundNo,
                             @Param("refDocNumber") String refDocNumber,
                             @Param("partnerCode") String partnerCode, // Use List if using "IN"
                             @Param("qualityInspectionNo") String qualityInspectionNo);

}