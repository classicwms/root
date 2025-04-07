package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.transaction.model.report.PickerDenialReportImpl;

@Repository
@Transactional
public interface PickupLineRepository extends JpaRepository<PickupLine,Long>, JpaSpecificationExecutor<PickupLine>{
	
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="1000"))
	public List<PickupLine> findAll();
	
	public Optional<PickupLine> findByActualHeNo(String actualHeNo);
	
	public PickupLine findByPickupNumber(String pickupNumber);

	public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, Long deletionIndicator);

	public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndActualHeNoAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String pickupNumber, String itemCode, String pickedStorageBin, String pickedPackCode, String actualHeNo,
			long l);

	public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, Long deletionIndicator);

	public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndPickedPackCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode,String pickedPackCode, Long deletionIndicator);
	
//	public PickupLine findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndActualHeNoAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
//			String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
//			String pickupNumber, String itemCode, String actualHeNo, String pickedStorageBin, String pickedPackCode, Long deletionIndicator);
	
	public PickupLine findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String pickupNumber, String itemCode, String pickedStorageBin, String pickedPackCode, Long deletionIndicator);
	
	@Query(value="SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND\r\n"
			+ "PRE_OB_NO = :preOutboundNo AND OB_LINE_NO = :obLineNumber AND ITM_CODE = :itemCode AND IS_DELETED = 0 \r\n"
			+ "GROUP BY REF_DOC_NO", nativeQuery=true)
    public Double getPickupLineCount(@Param ("warehouseId") String warehouseId,
    									@Param ("refDocNumber") String refDocNumber,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("obLineNumber") Long obLineNumber,
    									@Param ("itemCode") String itemCode);

	public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers,
			List<String> itemCodes, long l);

	@Query(value="SELECT COUNT(OB_LINE_NO) AS lineCount FROM tblpickupline \r\n"
			+ "	WHERE WH_ID = :warehouseId AND REF_DOC_NO IN :refDocNumber AND PRE_OB_NO = :preOutboundNo\r\n"
			+ "	AND STATUS_ID=50 AND IS_DELETED = 0 GROUP BY REF_DOC_NO", nativeQuery=true)
	public Double getCountByWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
			@Param ("warehouseId") String warehouseId,			
			@Param ("preOutboundNo") String preOutboundNo,
			@Param ("refDocNumber") List<String> refDocNumber);

	public List<PickupLine> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoInAndRefDocNumberInAndPartnerCodeAndStatusIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, List<String> preOutboundNo,
			List<String> refDocNumber, String partnerCode, Long statusId, long l); 
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Query (value = "SELECT SUM(PU_QTY) AS SUM_PUQTY_VALUE FROM tblpickupline \r\n"
	+ " WHERE ITM_CODE IN :itemCode AND STATUS_ID = 50 AND IS_DELETED = 0 \r\n"
	+ " AND PICK_CTD_ON BETWEEN :dateFrom AND :dateTo GROUP BY ITM_CODE", nativeQuery = true)
	public Double findSumOfPickupLineQty (@Param(value = "itemCode") List<String> itemCode, 
			@Param(value = "dateFrom") Date dateFrom,
			@Param(value = "dateTo") Date dateTo);

	//===================================Picker denial Report====================================================================

	@Query(value = "SELECT tpl.PARTNER_CODE partnerCode,tpl.REF_DOC_NO refDocNumber,tph.REF_FIELD_1 orderType FROM tblpickupline tpl \r\n"
			+ "join tblpickupheader tph on tph.pu_no = tpl.pu_no and tph.ref_doc_no = tpl.ref_doc_no and tph.wh_id = tpl.wh_id \r\n"
			+ "join tbloutboundheader toh on toh.ref_doc_no = tpl.ref_doc_no and toh.wh_id = tpl.wh_id and toh.pre_ob_no = tpl.pre_ob_no and toh.status_id = 59 \r\n"
			+ " WHERE tpl.WH_ID IN :warehouseId AND tpl.IS_DELETED = 0 and tpl.remark is not null \r\n"
			+ " AND toh.DLV_CNF_ON BETWEEN :dateFrom AND :dateTo GROUP BY tpl.PARTNER_CODE,tpl.REF_DOC_NO,tph.REF_FIELD_1", nativeQuery = true)
	public List<PickerDenialReportImpl> getPartnerCodeList(@Param("warehouseId") List<String> warehouseId,
														   @Param("dateFrom") Date dateFrom,
														   @Param("dateTo") Date dateTo);

	@Query(value = "SELECT tpl.PARTNER_CODE partnerCode,tph.REF_FIELD_1 orderType FROM tblpickupline tpl\r\n"
			+ "join tblpickupheader tph on tph.pu_no = tpl.pu_no and tph.ref_doc_no = tpl.ref_doc_no and tph.wh_id = tpl.wh_id \r\n"
			+ "join tbloutboundheader toh on toh.ref_doc_no = tpl.ref_doc_no and toh.wh_id = tpl.wh_id and toh.pre_ob_no = tpl.pre_ob_no and toh.status_id = 59 \r\n"
			+ " WHERE tpl.WH_ID IN :warehouseId AND tpl.IS_DELETED = 0 and tpl.remark is not null \r\n"
			+ " AND toh.DLV_CNF_ON BETWEEN :dateFrom AND :dateTo GROUP BY tpl.PARTNER_CODE,tph.REF_FIELD_1", nativeQuery = true)
	public List<PickerDenialReportImpl> getPartnerCodeSummaryList(@Param("warehouseId") List<String> warehouseId,
																  @Param("dateFrom") Date dateFrom,
																  @Param("dateTo") Date dateTo);

	@Query(value =
			"select pu_no into #pickupNumberList from tblpickupheader where is_deleted = 0 and ref_doc_no = :refDocNumber and \n" +
					"wh_id = :warehouseId and partner_code = :partnerCode and ref_field_1 = :orderType\n" +

			"select *,\n" +
			"(x1.skuOrdered - x1.skuShipped) skuDenied,\n" +
			"round(((x1.shippedQty/x1.orderedQty)*100),2) percentageShipped\n" +
			"from (select top 1 \n" +
			"tph.C_ID companyCodeId,\n" +
			"tph.PLANT_ID plantId,\n" +
			"tph.LANG_ID languageId,\n" +
			"tph.WH_ID warehouseId,\n" +
			"tph.PRE_OB_NO preOutboundNo,\n" +
			":refDocNumber refDocNumber,\n" +
			":partnerCode partnerCode,\n" +
			"tbp.partner_nm partnerName,\n" +
			"tph.ref_field_1 orderType,\n" +
			"tpl.PICK_CTD_BY pickupCreatedBy,\n" +
			"toh.dlv_cnf_on deliveryConfirmedOn,\n" +
			"format(toh.dlv_cnf_on,'dd-MM-yyyy hh:mm:ss') sDeliveryDate,\n" +
			"format(tpl.PICK_CNF_ON,'dd-MM-yyyy') sDenialDate,\n" +
			"tpl.PICK_CNF_BY pickupConfirmedBy,\n" +
			"tpl.PICK_CNF_ON denialDate,\n" +
			"(select count(*) from #pickupNumberList) skuOrdered,\n" +
			"(select count(*) from (select count(OB_LINE_NO) lineCount from tblpickupline where pu_no in (select * from #pickupNumberList) and remark is null \n" +
			" and wh_id = :warehouseId group by OB_LINE_NO) x) skuShipped,\n" +
			"(select sum(ALLOC_QTY) from tblpickupline where pu_no in (select * from #pickupNumberList) and wh_id = :warehouseId) orderedQty,\n" +
			"(select sum(PICK_CNF_QTY) from tblpickupline where pu_no in (select * from #pickupNumberList) and wh_id = :warehouseId) shippedQty\n" +
			"from tblpickupline tpl\n" +
			"join tblbusinesspartner tbp on tbp.partner_code = :partnerCode and tbp.wh_id = tpl.wh_id\n" +
			"join tblpickupheader tph on tph.pu_no = tpl.pu_no and tph.ref_doc_no = :refDocNumber and tph.wh_id = tpl.wh_id and tph.ref_field_1 = :orderType \r\n" +
			"join tbloutboundheader toh on toh.ref_doc_no = :refDocNumber and toh.wh_id = tpl.wh_id and toh.pre_ob_no = tpl.pre_ob_no \r\n" +
			"WHERE tpl.WH_ID IN :warehouseId AND tpl.IS_DELETED = 0 and tpl.remark is not null and tpl.ref_doc_no = :refDocNumber and tpl.pu_no in (select * from #pickupNumberList) \r\n" +
			"AND toh.DLV_CNF_ON BETWEEN :dateFrom AND :dateTo AND toh.status_id in (51, 59)) x1", nativeQuery = true)
	public PickerDenialReportImpl getReportHeader(@Param("partnerCode") String partnerCode,
												  @Param("refDocNumber") String refDocNumber,
												  @Param("orderType") String orderType,
												  @Param("warehouseId") List<String> warehouseId,
												  @Param("dateFrom") Date dateFrom,
												  @Param("dateTo") Date dateTo);

	@Query(value =
			"select pu_no into #pickupNumberList from tblpickupheader where is_deleted = 0 and ref_doc_no = :refDocNumber and \n" +
					"wh_id = :warehouseId and partner_code = :partnerCode and ref_field_1 = :orderType\n" +

					"select \n" +
					"tpl.WH_ID warehouseId,\n" +
					"tpl.PU_NO pickupNumber,\n" +
					"tpl.ITM_CODE itemCode,\n" +
					"tpl.ITEM_TEXT description,\n" +
					"tpl.ASS_PICKER_ID assignedPickerId,\n" +
					"tpl.OB_LINE_NO lineNumber,\n" +
					"tpl.PICK_PACK_BARCODE pickedPackCode,\n" +
					"tpl.PICK_ST_BIN pickedStorageBin,\n" +
					"tpl.remark remarks,\n" +
					"sum(tpl.ALLOC_QTY) orderedQty,\n" +
					"sum(tpl.PICK_CNF_QTY) shippedQty\n" +
					"from tblpickupline tpl\n" +
					"WHERE tpl.WH_ID IN :warehouseId AND tpl.IS_DELETED = 0 and tpl.pu_no in (select * from #pickupNumberList)\r\n" +
//					"AND tpl.PICK_CNF_ON BETWEEN :dateFrom AND :dateTo " +
					"and tpl.remark is not null \n" +
					"group by wh_id,pu_no,itm_code,item_text,ass_picker_id,ob_line_no,pick_pack_barcode,pick_st_bin,remark", nativeQuery = true)
	public List<PickerDenialReportImpl> getReportLines(@Param("partnerCode") String partnerCode,
													   @Param("refDocNumber") String refDocNumber,
													   @Param("orderType") String orderType,
													   @Param("warehouseId") List<String> warehouseId);
//													   @Param("warehouseId") List<String> warehouseId,
//													   @Param("dateFrom") Date dateFrom,
//													   @Param("dateTo") Date dateTo);

	@Query(value =
			"select pu_no into #pickupNumberList from tblpickupheader where is_deleted = 0 and \n" +
					"wh_id = :warehouseId and partner_code = :partnerCode and ref_field_1 = :orderType\n" +

					"select \n" +
					"tpl.PARTNER_CODE partnerCode,\n" +
					"tbp.partner_nm partnerName,\n" +
					"count(tpl.REMARK) denialCount,\n" +
					"tpl.remark remarks,\n" +
					"(case when tph.ref_field_1 = 'S' then 'Special' else 'Normal' end) orderType \n" +
					"from tblpickupline tpl\n" +
					"join tblbusinesspartner tbp on tbp.partner_code = tpl.partner_code and tbp.wh_id = tpl.wh_id\n" +
					"join tbloutboundheader toh on toh.ref_doc_no = tpl.ref_doc_no and toh.wh_id = tpl.wh_id and toh.pre_ob_no = tpl.pre_ob_no and toh.status_id = 59 \r\n" +
					"join tblpickupheader tph on tph.pu_no = tpl.pu_no and tph.ref_doc_no = tpl.ref_doc_no and tph.wh_id = tpl.wh_id and tph.ref_field_1 = :orderType \r\n" +
					"WHERE tpl.WH_ID IN :warehouseId AND tpl.IS_DELETED = 0 and tpl.pu_no in (select * from #pickupNumberList)\r\n" +
					"AND toh.DLV_CNF_ON BETWEEN :dateFrom AND :dateTo and tpl.remark is not null \n" +
					"group by tpl.remark,tpl.partner_code,tbp.partner_nm,tph.ref_field_1", nativeQuery = true)
	public List<PickerDenialReportImpl> getSummaryList(@Param("partnerCode") String partnerCode,
													   @Param("orderType") String orderType,
													   @Param("warehouseId") List<String> warehouseId,
													   @Param("dateFrom") Date dateFrom,
													   @Param("dateTo") Date dateTo);
}