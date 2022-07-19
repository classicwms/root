package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.transaction.model.impl.ShipmentDispatchSummaryReportImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

	@Query(value="select ol.ref_doc_no as soNumber, ol.partner_code as partnerCode,\n" +
			"(IF(sum(ol.dlv_qty) is not null , sum(ol.dlv_qty), 0)) as shippedQty,\n" +
			"sum(ol.ord_qty) as orderedQty,\n" +
			"count(ol.ord_qty) as linesOrdered,\n" +
			"COUNT(IF(ol.dlv_qty is not null and ol.dlv_qty > 0, ol.dlv_qty, NULL)) as linesShipped,\n" +
			"(ROUND((((IF(sum(ol.dlv_qty) is not null , sum(ol.dlv_qty), 0)) / sum(ol.ord_qty)) * 100),2)) as percentageShipped,\n" +
			"oh.ref_doc_date as orderReceiptTime\n" +
			"from tbloutboundline ol\n" +
			"join tbloutboundheader oh on oh.ref_doc_no = ol.ref_doc_no \n" +
			"where ol.partner_code in ( :partnerCode ) AND \n" +
			"(ol.dlv_cnf_on BETWEEN :fromDeliveryDate AND :toDeliveryDate) and ol.ref_field_2 is null\n" +
			"group by ol.ref_doc_no,ol.partner_code, oh.ref_doc_date\n" +
			"order by ol.ref_doc_no", nativeQuery=true)
	public List<ShipmentDispatchSummaryReportImpl> getOrderLinesForShipmentDispatchReportWithPartnerCode(@Param ("partnerCode") List<String> partnerCodes,
																						  @Param ("fromDeliveryDate") Date fromDeliveryDate,
																						  @Param ("toDeliveryDate") Date toDeliveryDate);

	@Query(value="select ol.ref_doc_no as soNumber, ol.partner_code as partnerCode,\n" +
			"(IF(sum(ol.dlv_qty) is not null , sum(ol.dlv_qty), 0)) as shippedQty,\n" +
			"sum(ol.ord_qty) as orderedQty,\n" +
			"count(ol.ord_qty) as linesOrdered,\n" +
			"COUNT(IF(ol.dlv_qty is not null and ol.dlv_qty > 0, ol.dlv_qty, NULL)) as linesShipped,\n" +
			"(ROUND((((IF(sum(ol.dlv_qty) is not null , sum(ol.dlv_qty), 0)) / sum(ol.ord_qty)) * 100),2)) as percentageShipped,\n" +
			"oh.ref_doc_date as orderReceiptTime\n" +
			"from tbloutboundline ol\n" +
			"join tbloutboundheader oh on oh.ref_doc_no = ol.ref_doc_no \n" +
			"where \n" +
			"(ol.dlv_cnf_on BETWEEN :fromDeliveryDate AND :toDeliveryDate) and ol.ref_field_2 is null\n" +
			"group by ol.ref_doc_no,ol.partner_code, oh.ref_doc_date\n" +
			"order by ol.ref_doc_no", nativeQuery=true)
	public List<ShipmentDispatchSummaryReportImpl> getOrderLinesForShipmentDispatchReportWithoutPartnerCode(@Param ("fromDeliveryDate") Date fromDeliveryDate,
																						  @Param ("toDeliveryDate") Date toDeliveryDate);

}