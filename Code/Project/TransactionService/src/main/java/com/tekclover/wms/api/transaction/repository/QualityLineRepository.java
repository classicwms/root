package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;

@Repository
@Transactional
public interface QualityLineRepository extends JpaRepository<QualityLine,Long>, JpaSpecificationExecutor<QualityLine> {
	
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="1000"))
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
	@Query(value="SELECT SUM(QC_QTY) FROM tblqualityline WHERE WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND\r\n"
			+ "PRE_OB_NO = :preOutboundNo AND OB_LINE_NO = :obLineNumber AND ITM_CODE = :itemCode\r\n"
			+ "GROUP BY REF_DOC_NO", nativeQuery=true)
    public Double getQualityLineCount(@Param ("warehouseId") String warehouseId,
    									@Param ("refDocNumber") String refDocNumber,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("obLineNumber") Long obLineNumber,
    									@Param ("itemCode") String itemCode);

	public List<QualityLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers,
			List<String> itemCodes, long l);

	@Modifying
	@Query(value = "update tblqualityline set is_deleted = :deletionIndicator, QC_UTD_BY = :updatedBy, QC_UTD_ON = :updatedOn \n" +
			"where wh_id = :warehouseId and ref_doc_no = :refDocNumber and ITM_CODE = :itemCode and is_deleted = 0 ", nativeQuery = true)
	int deleteQualityLineInReversal(@Param("deletionIndicator") long deletionIndicator,
			@Param("updatedBy") String updatedBy,
			@Param("updatedOn") Date updatedOn,
			@Param("warehouseId") String warehouseId,
			@Param("refDocNumber") String refDocNumber,
			@Param("itemCode") String itemCode);

	@Query(value = "select * from tblqualityline where wh_id = :warehouseId and ref_doc_no = :refDocNumber and ITM_CODE = :itemCode and is_deleted = 0 ", nativeQuery = true)
	List<QualityLine> getQualityLineInReversal(@Param("warehouseId") String warehouseId,
			@Param("refDocNumber") String refDocNumber,
			@Param("itemCode") String itemCode);
}