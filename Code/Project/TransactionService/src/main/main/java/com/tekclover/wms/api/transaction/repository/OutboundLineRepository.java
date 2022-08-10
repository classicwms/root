package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.transaction.model.impl.OutBoundLineImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.impl.OrderStatusReportImpl;
import com.tekclover.wms.api.transaction.model.impl.ShipmentDispatchSummaryReportImpl;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;

@Repository
@Transactional
public interface OutboundLineRepository extends JpaRepository<OutboundLine,Long>, JpaSpecificationExecutor<OutboundLine> {
	
	public List<OutboundLine> findAll();
	
	public Optional<OutboundLine> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
				String languageId, Long companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, 
				Long lineNumber, String itemCode, Long deletionIndicator);
	
	public Optional<OutboundLine> findByLineNumber(Long lineNumber);
	
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

//	@Query(value="select\n" +
//			"  ol.ref_doc_no soNumber,\n" +
//			"  ol.partner_code partnerCode,\n" +
//			"  case\n" +
//			"    when sum(ol.dlv_qty) is not null then sum(ol.dlv_qty)\n" +
//			"    else 0\n" +
//			"  end shippedQty,\n" +
//			"  sum(ol.ord_qty) orderedQty,\n" +
//			"  count(ol.ord_qty) linesOrdered,\n" +
//			"  count(case\n" +
//			"    when (\n" +
//			"      ol.dlv_qty is not null\n" +
//			"      and ol.dlv_qty > 0\n" +
//			"    ) then ol.dlv_qty\n" +
//			"    else null\n" +
//			"  end) linesShipped,\n" +
//			"  round(\n" +
//			"    ((case\n" +
//			"      when sum(ol.dlv_qty) is not null then sum(ol.dlv_qty)\n" +
//			"      else 0\n" +
//			"    end / sum(ol.ord_qty)) * 100),\n" +
//			"    2\n" +
//			"  ) percentageShipped,\n" +
//			"  oh.ref_doc_date orderReceiptTime\n" +
//			"from tbloutboundline ol\n" +
//			"  join tbloutboundheader oh\n" +
//			"    on oh.ref_doc_no = ol.ref_doc_no\n" +
//			"where (\n" +
//			"  ol.dlv_cnf_on between :fromDeliveryDate and :toDeliveryDate\n" +
//			"  and ol.ref_field_2 is null\n" +
//			"  and oh.status_id = 59\n" +
//			"  and (\n" +
//			"    coalesce(:partnerCode) is null\n" +
//			"    or ol.partner_code in (:partnerCode)\n" +
//			"  )\n" +
//			")\n" +
//			"group by ol.ref_doc_no, ol.partner_code, oh.ref_doc_date\n" +
//			"order by ol.ref_doc_no", nativeQuery=true)
//	public List<ShipmentDispatchSummaryReportImpl> getOrderLinesForShipmentDispatchReport(@Param ("partnerCode") List<String> partnerCode,
//																						  @Param ("fromDeliveryDate") Date fromDeliveryDate,
//																						  @Param ("toDeliveryDate") Date toDeliveryDate);
	
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
			+ "	OL.STATUS_ID = 48 OR OL.STATUS_ID = 50 OR OL.STATUS_ID = 55) THEN  'IN PROGRESS'\r\n"
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
			+ "WHERE OL.WH_ID = :warehouseId AND (OL.DLV_CNF_ON BETWEEN :fromDeliveryDate AND :toDeliveryDate)", nativeQuery = true)
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

}