package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.report.BillingTransactionReportImpl;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.gr.GrLine;

import javax.persistence.TemporalType;


@Repository
@Transactional
public interface GrLineRepository extends JpaRepository<GrLine,Long>,
		JpaSpecificationExecutor<GrLine>, StreamableJpaSpecificationRepository<GrLine> {

	public List<GrLine> findAll();
	public Optional<GrLine>
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
				String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
				String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, Long deletionIndicator);

	/**
	 *
	 * @param languageId
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param goodsReceiptNo
	 * @param lineNo
	 * @param itemCode
	 * @param deletionIndicator
	 * @return
	 */
	public List<GrLine>
	findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndLineNoAndItemCodeAndStatusIdAndDeletionIndicator(
			String languageId,
			String companyCode,
			String plantId,
			String warehouseId,
			String preInboundNo,
			String refDocNumber,
			String goodsReceiptNo,
			Long lineNo,
			String itemCode,
			Long statusId,
			Long deletionIndicator);


	/**
	 *
	 * PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
	 */
	public List<GrLine>
	findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndLineNoAndItemCodeAndCaseCodeAndPackBarcodesAndDeletionIndicator(
			String languageId,
			String companyCode,
			String plantId,
			String warehouseId,
			String goodsReceiptNo,
			Long lineNo,
			String itemCode,
			String caseCode,
			String packBarcodes,
			Long deletionIndicator);

	/**
	 *
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param packBarcodes
	 * @param lineNo
	 * @param itemCode
	 * @param deletionIndicator
	 * @return
	 */
	public List<GrLine> findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String preInboundNo, String refDocNumber,
			String packBarcodes, Long lineNo, String itemCode, Long deletionIndicator);

	/**
	 *
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param refDocNumber
	 * @param packBarcodes
	 * @param warehouseId
	 * @param preInboundNo
	 * @param caseCode
	 * @return
	 */
	public List<GrLine> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String refDocNumber, String packBarcodes,
			String warehouseId, String preInboundNo, String caseCode, Long deletionIndicator);

	/**
	 *
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param lineNo
	 * @param itemCode
	 * @param l
	 * @return
	 */
	public List<GrLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
			String refDocNumber, Long lineNo, String itemCode, Long l);

	public List<GrLine> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String refDocNumber, String packBarcodes, Long l);


	public List<GrLine> findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
			String goodsReceiptNo,String itemCode,Long lineNo,String languageId, String companyCode, String plantId, String refDocNumber, String packBarcodes,
			String warehouseId, String preInboundNo, String caseCode, Long deletionIndicator);

	@Transactional
	@Query(value = "Select tg.c_id as companyCodeId, tg.lang_id as languageId, tg.plant_id as plantId, tg.wh_id as warehouseId ,\n" +
			"tg.partner_code as partnerCode, tg.partner_nm as partnerName \n" +
			"from tblbusinesspartner tg \n" +
			"Where (coalesce(:languageId , null) is null or (lang_id IN (:languageId))) and (coalesce(:companyCodeId, null) is null or (c_id IN (:companyCodeId))) and " +
			"(coalesce(:plantId, null) is null or (plant_id IN (:plantId))) and (coalesce(:warehouseId, null) is null or (wh_id IN (:warehouseId))) and " +
			"(coalesce(:partnerCode, null) is null or (partner_code IN (:partnerCode))) and is_deleted=0 ", nativeQuery = true)
	public List<BillingTransactionReportImpl> grBillingTransactionReport(
			@Param("companyCodeId") String companyCodeId,
			@Param("plantId") String plantId,
			@Param("languageId") String languageId,
			@Param("warehouseId") String warehouseId,
			@Param("partnerCode") String partnerId
	);

	@Query(value = "SELECT SUM(TOTAL_TPL_CBM) totalCbm, sum(RATE) totalRate, min(currency) currency FROM tblgrline WHERE c_id = :companyId AND plant_id = :plantId " +
			"AND lang_id = :languageId AND wh_id = :warehouseId AND partner_code = :partnerCode " +
			"AND is_deleted = 0 And (PROFORMA_INVOICE_NO is null or PROFORMA_INVOICE_NO =0) "  +
			"AND (:startCreatedOn IS NULL OR gr_ctd_on >= :startCreatedOn) " +
			"AND (:endCreatedOn IS NULL OR gr_ctd_on <= :endCreatedOn)",
			nativeQuery = true)
	IKeyValuePair getCbm(@Param("companyId") String companyId,
						 @Param("plantId") String plantId,
						 @Param("languageId") String languageId,
						 @Param("warehouseId") String warehouseId,
						 @Param("partnerCode") String partnerCode,
						 @Param("startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
						 @Param("endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn);


}