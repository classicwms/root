package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.impl.OrderStatusReportImpl;
import com.tekclover.wms.api.transaction.model.impl.OutBoundLineImpl;
import com.tekclover.wms.api.transaction.model.impl.ShipmentDispatchSummaryReportImpl;
import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.report.FastSlowMovingDashboard;

@Repository
@Transactional
public interface OutboundLineRepository extends JpaRepository<OutboundLine,Long>, JpaSpecificationExecutor<OutboundLine> {

	String UPGRADE_SKIPLOCKED = "-2";
	public List<OutboundLine> findAll();

	public Optional<OutboundLine>
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
				String languageId, Long companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
				Long lineNumber, String itemCode, Long deletionIndicator);

	public Optional<OutboundLine> findByLineNumber(Long lineNumber);

	
//	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = UPGRADE_SKIPLOCKED)})
	@Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
	public OutboundLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, Long deletionIndicator);

	public List<OutboundLine> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long deletionIndicator);
	
	/*
	 * Delivery Queries
	 */
	@Query(value="SELECT COUNT(OB_LINE_NO) AS countOfOrdLines \r\n"
			+ "FROM tbloutboundline \r\n"
			+ "WHERE WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
			+ "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL \r\n"
			+ "GROUP BY OB_LINE_NO;", nativeQuery=true)
    public List<Long> getCountofOrderedLines(@Param ("warehouseId") String warehouseId,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("refDocNumber") String refDocNumber);

	@Query(value="SELECT SUM(ORD_QTY) AS ordQtyTotal \r\n"
			+ "FROM tbloutboundline \r\n"
			+ "WHERE WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
			+ "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL \r\n"
			+ "GROUP BY REF_DOC_NO;", nativeQuery=true)
    public List<Long> getSumOfOrderedQty(@Param ("warehouseId") String warehouseId,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("refDocNumber") String refDocNumber);
	
	@Query(value="SELECT SUM(ORD_QTY) AS ordQtyTotal \r\n"
			+ "FROM tbloutboundline \r\n"
			+ "WHERE WH_ID = :warehouseId AND PRE_OB_NO IN :preOutboundNo "
			+ "AND REF_DOC_NO IN :refDocNumber AND REF_FIELD_2 IS NULL AND PARTNER_CODE = :partnerCode \r\n"
			+ "GROUP BY PARTNER_CODE;", nativeQuery=true)
    public Long getSumOfOrderedQtyByPartnerCode (@Param ("warehouseId") String warehouseId,
    									@Param ("preOutboundNo") List<String> preOutboundNo,
    									@Param ("refDocNumber") List<String> refDocNumber,
    									@Param ("partnerCode") String partnerCode);

	@Query(value="SELECT COUNT(OB_LINE_NO) AS deliveryLines \r\n"
			+ "FROM tbloutboundline \r\n"
			+ "WHERE WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
			+ "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL AND DLV_QTY > 0\r\n"
			+ "GROUP BY REF_DOC_NO;", nativeQuery=true)
    public List<Long> getDeliveryLines(@Param ("warehouseId") String warehouseId,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("refDocNumber") String refDocNumber);
	
	@Query(value="SELECT SUM(DLV_QTY) AS deliveryQty \r\n"
			+ "FROM tbloutboundline \r\n"
			+ "WHERE WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
			+ "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL AND DLV_QTY > 0\r\n"
			+ "GROUP BY REF_DOC_NO;", nativeQuery=true)
    public List<Long> getDeliveryQty(@Param ("warehouseId") String warehouseId,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("refDocNumber") String refDocNumber);
	
	@Query(value="SELECT SUM(DLV_QTY) AS deliveryQty \r\n"
			+ "FROM tbloutboundline \r\n"
			+ "WHERE WH_ID = :warehouseId AND PRE_OB_NO IN :preOutboundNo "
			+ "AND REF_DOC_NO IN :refDocNumber AND REF_FIELD_2 IS NULL AND DLV_QTY > 0 \r\n"
			+ "AND PARTNER_CODE = :partnerCode GROUP BY PARTNER_CODE;", nativeQuery=true)
    public Long getDeliveryQtyByPartnerCode(@Param ("warehouseId") String warehouseId,
    									@Param ("preOutboundNo") List<String> preOutboundNo,
    									@Param ("refDocNumber") List<String> refDocNumber,
    									@Param ("partnerCode") String partnerCode);

	public List<OutboundLine> findByRefDocNumberAndItemCodeAndDeletionIndicator(String refDocNumber, String itemCode,
			Long deletionIndicator);

	public List<OutboundLine> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndStatusIdAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String referenceField2, Long statusId,
			Long deletionIndicator);

	public List<OutboundLine> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String referenceField2, Long deletionIndicator);

	/*
	 * Reports
	 */
	@Query (value = "SELECT COUNT(OB_LINE_NO) FROM tbloutboundline \r\n"
			+ " WHERE REF_DOC_NO IN :refDocNo AND REF_FIELD_2 IS NULL \r\n"
			+ " AND DLV_CNF_ON BETWEEN :startDate AND :endDate \r\n"
			+ " GROUP BY OB_LINE_NO", nativeQuery = true)
	public List<Long> findLineItem_NByRefDocNoAndRefField2IsNull (
			@Param(value = "refDocNo") List<String> refDocNo,
			@Param ("startDate") Date startDate,
			@Param ("endDate") Date endDate);
	
	@Query (value = "SELECT COUNT(OB_LINE_NO) FROM tbloutboundline \r\n"
			+ " WHERE REF_DOC_NO IN :refDocNo AND REF_FIELD_2 IS NULL \r\n"
			+ " GROUP BY OB_LINE_NO", nativeQuery = true)
	public List<Long> findLineItem_NByRefDocNoAndRefField2IsNull (
			@Param(value = "refDocNo") List<String> refDocNo);

	@Query (value = "SELECT COUNT(OB_LINE_NO) FROM tbloutboundline \r\n"
			+ " WHERE REF_DOC_NO IN :refDocNo AND DLV_QTY > 0 AND REF_FIELD_2 IS NULL \r\n"
			+ " AND DLV_CNF_ON BETWEEN :startDate AND :endDate \r\n"
			+ " GROUP BY OB_LINE_NO", nativeQuery = true)
	public List<Long> findShippedLines (
			@Param(value = "refDocNo") List<String> refDocNo,
			@Param ("startDate") Date startDate,
			@Param ("endDate") Date endDate);

	/*
	 * Line Shipped
	 * ---------------------
	 * Pass PRE_OB_NO/OB_LINE_NO/ITM_CODE in OUTBOUNDLINE table and fetch Count of OB_LINE_NO values
	 * where REF_FIELD_2 = Null and DLV_QTY>0
	 */
	@Query (value = "SELECT COUNT(OB_LINE_NO) FROM tbloutboundline \r\n"
			+ " WHERE PRE_OB_NO = :preOBNo AND OB_LINE_NO = :obLineNo AND ITM_CODE = :itemCode \r\n"
			+ " AND DLV_QTY > 0 AND REF_FIELD_2 IS NULL \r\n"
			+ " GROUP BY REF_DOC_NO", nativeQuery = true)
	public List<Long> findLineShipped(
			@Param(value = "preOBNo") String preOBNo,
			@Param(value = "obLineNo") Long obLineNo,
			@Param(value = "itemCode") String itemCode);

	/**
	 *
	 * @param customerCode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<OutboundLine> findByPartnerCodeInAndDeliveryConfirmedOnBetween(List<String> customerCode,
			Date startDate, Date endDate);

	public List<OutboundLine> findByDeliveryConfirmedOnBetween(Date startDate, Date endDate);

	@Query(value="select ol.ref_doc_no as soNumber, ol.partner_code as partnerCode,\r\n"
			+ "(CASE WHEN sum(ol.dlv_qty) is not null THEN sum(ol.dlv_qty) ELSE 0 END) as shippedQty,\r\n"
			+ "sum(ol.ord_qty) as orderedQty,\r\n"
			+ "count(ol.ord_qty) as linesOrdered,\r\n"
			+ "COUNT(CASE WHEN ol.dlv_qty is not null and ol.dlv_qty > 0 THEN  ol.dlv_qty ELSE  NULL END) as linesShipped,\r\n"
			+ "(ROUND((((CASE WHEN sum(ol.dlv_qty) is not null  THEN  sum(ol.dlv_qty) ELSE  0 END) / sum(ol.ord_qty)) * 100),2)) as percentageShipped,\r\n"
			+ "oh.ref_doc_date as orderReceiptTime\r\n"
			+ "from tbloutboundline ol\r\n"
			+ "join tbloutboundheader oh on oh.ref_doc_no = ol.ref_doc_no \r\n"
			+ "where (oh.dlv_cnf_on BETWEEN :fromDeliveryDate AND :toDeliveryDate) \r\n"
			+ "and oh.wh_id = :warehouseId and ol.ref_field_2 is null and oh.status_id = 59 \r\n"
			+ "group by ol.ref_doc_no,ol.partner_code, oh.ref_doc_date\r\n"
			+ "order by ol.ref_doc_no\r\n"
			+ "", nativeQuery=true)
	public List<ShipmentDispatchSummaryReportImpl> getOrderLinesForShipmentDispatchReport(@Param ("fromDeliveryDate") Date fromDeliveryDate,
																						  @Param ("toDeliveryDate") Date toDeliveryDate,
																						  @Param ("warehouseId") String warehouseId);

	@Query(value="SELECT \r\n"
			+ "OL.REF_DOC_NO AS sonumber, \r\n"
			+ "OL.DLV_ORD_NO AS donumber,\r\n"
			+ "OL.PARTNER_CODE AS partnercode, \r\n"
			+ "BP.PARTNER_NM AS partnerName, \r\n"
			+ "OL.WH_ID AS warehouseid, \r\n"
			+ "OL.ITM_CODE AS itemcode,\r\n"
			+ "OL.ITEM_TEXT AS itemdescription ,\r\n"
			+ "OL.ORD_QTY AS orderedqty, \r\n"
			+ "OL.DLV_QTY AS deliveryqty,OL.DLV_CNF_ON AS deliveryconfirmedon,\r\n"
			+ "(CASE \r\n"
			+ "	WHEN OL.STATUS_ID IS NOT NULL AND OL.STATUS_ID = 59 THEN  'DELIVERED'\r\n"
			+ "	WHEN OL.STATUS_ID IS NOT NULL AND (OL.STATUS_ID = 42 OR OL.STATUS_ID = 43 OR \r\n"
			+ "	OL.STATUS_ID = 48 OR OL.STATUS_ID = 50 OR OL.STATUS_ID = 55 OR OL.STATUS_ID = 57 OR OL.STATUS_ID = 39) THEN  'IN PROGRESS'\r\n"
			+ "	WHEN OL.STATUS_ID IS NOT NULL AND (OL.STATUS_ID = 47 OR OL.STATUS_ID = 51) THEN 'NOT FULFILLED' \r\n"
			+ "		ELSE NULL\r\n"
			+ "	END\r\n"
			+ ") AS STATUSIDNAME,\r\n"
			+ " OL.STATUS_ID AS STATUSID,\r\n"
			+ " OL.REF_FIELD_1 as ORDERTYPE,\r\n"
			+ "OH.REF_DOC_DATE AS REFDOCDATE, \r\n"
			+ "OH.REQ_DEL_DATE AS REQUIREDDELIVERYDATE\r\n"
			+ "FROM tbloutboundline OL\r\n"
			+ "JOIN tblbusinesspartner BP ON BP.PARTNER_CODE = OL.PARTNER_CODE\r\n"
			+ "JOIN tbloutboundheader OH ON OH.REF_DOC_NO = OL.REF_DOC_NO\r\n"
			+ "WHERE OL.WH_ID = :warehouseId AND (OL.DLV_CTD_ON BETWEEN :fromDeliveryDate AND :toDeliveryDate) \n"
			+ "and ol.is_deleted = 0 and OL.REF_FIELD_2 is null ", nativeQuery = true)
	public List<OrderStatusReportImpl> getOrderStatusReportFromOutboundLines(@Param ("warehouseId") String warehouseId,
																			 @Param ("fromDeliveryDate") Date fromDeliveryDate,
																			 @Param ("toDeliveryDate") Date toDeliveryDate);


	@Query(value="select \n" +
			"ref_doc_no as refDocNo,\n" +
			"count(ord_qty) as linesOrdered,\n" +
			"SUM(ORD_QTY) as orderedQty,\n" +
			"COUNT(CASE WHEN dlv_qty is not null and dlv_qty > 0 THEN  dlv_qty ELSE  NULL END) as linesShipped,\n" +
			"(CASE WHEN sum(dlv_qty) is not null THEN sum(dlv_qty) ELSE 0 END) as shippedQty\n" +
			"from tbloutboundline \n" +
			"where ref_doc_no in (:refDocNo) and ref_field_2 is null\n" +
			"group by ref_doc_no , c_id , lang_id, plant_id, wh_id, pre_ob_no, partner_code", nativeQuery=true)
	public List<OutBoundLineImpl> getOutBoundLineDataForOutBoundHeader(@Param ("refDocNo") List<String> refDocNo);

	@Query(value="select ol.wh_id as warehouseId, ol.itm_code as itemCode , \n" +
			" 'OutBound' as documentType , ol.ref_doc_no as documentNumber, ol.partner_code as customerCode,\n" +
			" ol.DLV_CNF_ON as confirmedOn, ol.dlv_qty as movementQty, im.text as itemText,im.mfr_part as manufacturerSKU \n" +
			" from tbloutboundline ol\n" +
			" join tblimbasicdata1 im on ol.itm_code = im.itm_code \n" +
			" WHERE ol.ITM_CODE in (:itemCode) AND im.WH_ID in (:warehouseId) AND ol.WH_ID in (:warehouseId) AND ol.status_id = :statusId " +
			" AND ol.DLV_CNF_ON between :fromDate and :toDate " , nativeQuery=true)
	public List<StockMovementReportImpl> findOutboundLineForStockMovement(@Param ("itemCode") List<String> itemCode,
																		  @Param ("warehouseId") List<String> warehouseId,
																		  @Param ("statusId") Long statusId,
																		  @Param ("fromDate") Date fromDate,
																		  @Param ("toDate") Date toDate);

	@Query(value="select ob.c_id,ob.itm_code,ob.lang_id,ob.ob_line_no,ob.partner_code,ob.plant_id,ob.pre_ob_no,ob.ref_doc_no,ob.wh_id,ob.str_no,ob.dlv_ctd_by,\n" +
			"ob.dlv_ctd_on,ob.is_deleted,ob.dlv_cnf_by,ob.dlv_cnf_on,ob.dlv_ord_no,ob.dlv_qty,ob.dlv_uom,ob.item_text,ob.ord_qty,ob.ord_uom,ob.ob_ord_typ_id,\n" +
			"ob.ref_field_1,ob.ref_field_2,ob.ref_field_3,ob.ref_field_4,ob.ref_field_5,ob.ref_field_6,ob.ref_field_7,ob.ref_field_8,\n" +
			"ob.dlv_rev_by,ob.dlv_rev_on,ob.sp_st_ind_id,ob.status_id,ob.stck_typ_id,ob.dlv_utd_by,ob.dlv_utd_on,ob.var_id,ob.var_sub_id,\n" +
			"(select SUM(p.PICK_CNF_QTY) from tblpickupline p \n" +
			"where \n" +
			"p.wh_id = ob.wh_id and p.PRE_OB_NO = ob.PRE_OB_NO and p.OB_LINE_NO = ob.OB_LINE_NO and p.itm_code = ob.itm_code and p.ref_doc_no = ob.ref_doc_no and p.is_deleted = 0 \n" +
			"group by p.ref_doc_no) as ref_field_9,\n" +
			"(select SUM(q.QC_QTY) from tblqualityline q\n" +
			"where \n" +
			"q.wh_id = ob.wh_id and q.PRE_OB_NO = ob.PRE_OB_NO and q.OB_LINE_NO = ob.OB_LINE_NO and q.itm_code = ob.itm_code and q.ref_doc_no = ob.ref_doc_no and q.is_deleted = 0 \n" +
			"group by q.ref_doc_no) as ref_field_10 \n" +
			"from tbloutboundline ob\n" +
			"where \n" +
			"(COALESCE(:warehouseId, null) IS NULL OR (ob.wh_id IN (:warehouseId))) and \n" +
			"(COALESCE(:refDocNo, null) IS NULL OR (ob.ref_doc_no IN (:refDocNo))) and \n" +
			"(COALESCE(:partnerCode, null) IS NULL OR (ob.partner_code IN (:partnerCode))) and \n" +
			"(COALESCE(:preObNumber, null) IS NULL OR (ob.pre_ob_no IN (:preObNumber))) and \n" +
			"(COALESCE(:statusId, null) IS NULL OR (ob.status_id IN (:statusId))) and \n" +
			"(COALESCE(:lineNo, null) IS NULL OR (ob.ob_line_no IN (:lineNo))) and \n" +
			"(COALESCE(:itemCode, null) IS NULL OR (ob.itm_code IN (:itemCode))) and\n" +
			"(COALESCE(:orderType, null) IS NULL OR (ob.ob_ord_typ_id IN (:orderType))) and \n" +
			"(COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) IS NULL OR (ob.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDeliveryDate), null))) \n" +
			"group by ob.c_id,ob.itm_code,ob.lang_id,ob.ob_line_no,ob.partner_code,ob.plant_id,ob.pre_ob_no,ob.ref_doc_no,ob.wh_id,ob.str_no,ob.dlv_ctd_by,\n" +
			"ob.dlv_ctd_on,ob.is_deleted,ob.dlv_cnf_by,ob.dlv_cnf_on,ob.dlv_ord_no,ob.dlv_qty,ob.dlv_uom,ob.item_text,ob.ord_qty,ob.ord_uom,ob.ob_ord_typ_id,\n" +
			"ob.ref_field_1,ob.ref_field_2,ob.ref_field_3,ob.ref_field_4,ob.ref_field_5,ob.ref_field_6,ob.ref_field_7,ob.ref_field_8,ob.ref_field_9,ob.ref_field_10,\n" +
			"ob.dlv_rev_by,ob.dlv_rev_on,ob.sp_st_ind_id,ob.status_id,ob.stck_typ_id,ob.dlv_utd_by,ob.dlv_utd_on,ob.var_id,ob.var_sub_id\n", nativeQuery = true)
	public List<OutboundLine> findOutboundLineNew(@Param ("warehouseId") List<String> warehouseId,
												  @Param ("fromDeliveryDate") Date fromDeliveryDate,
												  @Param ("toDeliveryDate") Date toDeliveryDate,
												  @Param ("preObNumber") List<String> preObNumber,
												  @Param ("refDocNo") List<String> refDocNo,
												  @Param ("lineNo") List<Long> lineNo,
												  @Param ("itemCode") List<String> itemCode,
												  @Param ("statusId") List<Long> statusId,
												  @Param ("orderType") List<String> orderType,
												  @Param ("partnerCode") List<String> partnerCode);

	long countByWarehouseIdAndDeliveryConfirmedOnBetweenAndStatusIdAndDeletionIndicatorAndReferenceField2IsNullAndDeliveryQtyIsNotNullAndDeliveryQtyGreaterThan(
			String warehouseId, Date fromDate, Date toDate,Long statusId,Long deletionIndicator,Double deliveryQty );

	long countByWarehouseIdAndDeliveryConfirmedOnBetweenAndStatusIdAndDeletionIndicatorAndReferenceField1AndReferenceField2IsNullAndDeliveryQtyIsNotNullAndDeliveryQtyGreaterThan(
			String warehouseId, Date fromDate, Date toDate,Long statusId,Long deletionIndicator,String referenceField1,Double deliveryQty );


	@Query(value="select itm_code as itemCode,item_text as itemText, COALESCE(sum(dlv_qty),0) as deliveryQuantity \n" +
			"from tbloutboundline \n" +
			"where dlv_cnf_on between :fromDate and :toDate and wh_id = :warehouseId and dlv_qty is not null and dlv_qty > 0  \n" +
			"group by itm_code,item_text order by sum(dlv_qty) desc " , nativeQuery=true)
	public List<FastSlowMovingDashboard.FastSlowMovingDashboardImpl> getFastSlowMovingDashboardData(@Param ("warehouseId") String warehouseId,
																				 @Param ("fromDate") Date fromDate,
																				 @Param ("toDate") Date toDate);

	@Query("Select count(ob) from OutboundLine ob where ob.warehouseId=:warehouseId and ob.preOutboundNo=:preOutboundNo and \r\n"
			+ " ob.refDocNumber=:refDocNumber and ob.partnerCode=:partnerCode and ob.statusId in :statusId and ob.deletionIndicator=:deletionIndicator")
	public long getOutboudLineByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
			 @Param ("warehouseId") String warehouseId, @Param ("preOutboundNo") String preOutboundNo, 
			 @Param ("refDocNumber") String refDocNumber, @Param ("partnerCode") String partnerCode, @Param ("statusId") List<Long> statusId, 
			 @Param ("deletionIndicator") long deletionIndicator);

	public List<OutboundLine> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> statusId,
			long i);
	
	/**
	 * 
	 * @param warehouseId
	 * @param refDocNumber
	 * @param statusId
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE OutboundLine ob SET ob.statusId = :statusId, ob.deliveryConfirmedOn = :deliveryConfirmedOn \r\n"
			+ " WHERE ob.warehouseId = :warehouseId AND \r\n "
			+ " ob.refDocNumber = :refDocNumber AND ob.lineNumber in :lineNumber")
	public void updateOutboundLineStatus(@Param ("warehouseId") String warehouseId,
			@Param ("refDocNumber") String refDocNumber, 
			@Param ("statusId") Long statusId, 
			@Param ("lineNumber") List<Long> lineNumber,
			@Param ("deliveryConfirmedOn") Date deliveryConfirmedOn);
	
	/**
	 * 
	 * @param warehouseId
	 * @param refDocNumber
	 * @param statusId
	 * @param lineNumber
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE OutboundLine ob SET ob.deliveryOrderNo = :deliveryOrderNo,\r\n"
			+ "			ob.statusId = :statusId, ob.deliveryQty = :deliveryQty \r\n"
			+ "			WHERE ob.warehouseId = :warehouseId AND\r\n"
			+ "			ob.refDocNumber = :refDocNumber AND \r\n"
			+ "			ob.preOutboundNo in :preOutboundNo AND\r\n"
			+ "			ob.partnerCode in :partnerCode AND\r\n"
			+ "			ob.lineNumber in :lineNumber AND\r\n"
			+ "			ob.itemCode in :itemCode")
	public void updateOutboundLine(@Param ("warehouseId") String warehouseId,
			@Param ("refDocNumber") String refDocNumber, 
			@Param ("preOutboundNo") String preOutboundNo, 
			@Param ("partnerCode") String partnerCode, 
			@Param ("lineNumber") Long lineNumber, 
			@Param ("itemCode") String itemCode, 
			@Param ("deliveryOrderNo") String deliveryOrderNo,
			@Param ("statusId") Long statusId, 
			@Param ("deliveryQty") Double deliveryQty);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM tbloutboundlineinterim WHERE WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo \r\n"
			+ "AND REF_DOC_NO = :refDocNumber AND PARTNER_CODE = :partnerCode\r\n"
			+ "AND OB_LINE_NO = :lineNumber AND ITM_CODE = :itemCode", nativeQuery = true)
	public void deleteOutboundLine(@Param ("warehouseId") String warehouseId,			 
			@Param ("preOutboundNo") String preOutboundNo, 
			@Param ("refDocNumber") String refDocNumber,
			@Param ("partnerCode") String partnerCode, 
			@Param ("lineNumber") Long lineNumber, 
			@Param ("itemCode") String itemCode); 
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM tbloutboundline WHERE WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo \r\n"
			+ "AND REF_DOC_NO = :refDocNumber AND PARTNER_CODE = :partnerCode\r\n"
			+ "AND OB_LINE_NO = :lineNumber AND ITM_CODE = :itemCode", nativeQuery = true)
	public void deleteOutboundLineMain(@Param ("warehouseId") String warehouseId,			 
			@Param ("preOutboundNo") String preOutboundNo, 
			@Param ("refDocNumber") String refDocNumber,
			@Param ("partnerCode") String partnerCode, 
			@Param ("lineNumber") Long lineNumber, 
			@Param ("itemCode") String itemCode);
	
	//----------------------------STORED-PROCEDURE----------------------------------------------------------
	/*
	 * Calling this Stored Proc during Create PickupLine
	 */
	@Transactional
	@Procedure(procedureName = "obline_update_proc")
	public void updateStatusIdByProcedure(
			@Param("warehouseId") String warehouseId,
			@Param("preOutboundNo") String preOutboundNo,
			@Param("refDocNumber") String refDocNumber,
			@Param("partnerCode") String partnerCode,
			@Param("lineNumber") long lineNumber,
			@Param("itmCode") String itmCode,
			@Param("statusId") long statusId,
			@Param("loginUserId") String loginUserId
		);
	
	/*
	 * Calling this Stored Proc during Create QualityLine
	 * ------------------------------------------------------
	 *  WarehouseId, PreOutboundNo, RefDocNumber, PartnerCode, LineNumber, ItemCode, DeliveryQty, DeliveryOrderNo, StatusId(57L);
	 */
	@Transactional
	@Procedure(procedureName = "obline_update_qlcreate_proc")
	public void updateOBlineByQLCreateProcedure(
			@Param("warehouseId") String warehouseId,
			@Param("preOutboundNo") String preOutboundNo,
			@Param("refDocNumber") String refDocNumber,
			@Param("partnerCode") String partnerCode,
			@Param("lineNumber") Long lineNumber,
			@Param("itmCode") String itmCode,			
			@Param("deliveryQty") Double deliveryQty,
			@Param("deliveryOrderNo") String deliveryOrderNo,
			@Param("statusId") Long statusId
		);

	//----------------------------------------------------------------------------------------------------------------------------------------
//	@Modifying(clearAutomatically = true)
//	@Query("UPDATE OutboundLine ob SET ob.requiredDeliveryDate = :requiredDeliveryDate WHERE ob.warehouseId = :warehouseId AND ob.refDocNumber = :refDocNumber")
//	void updateOutboundHeaderRequiredDeliveryDate(@Param ("warehouseId") String warehouseId,
//			@Param ("refDocNumber") String refDocNumber, @Param ("requiredDeliveryDate") Date requiredDeliveryDate);
}