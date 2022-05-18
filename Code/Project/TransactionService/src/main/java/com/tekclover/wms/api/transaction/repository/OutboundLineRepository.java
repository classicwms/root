package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
	
	public List<OutboundLine> findByDeliveryConfirmedOnBetween (Date s, Date t);
	
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
			+ "GROUP BY OB_LINE_NO;", nativeQuery=true)
    public List<Long> getSumOfOrderedQty(@Param ("warehouseId") String warehouseId,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("refDocNumber") String refDocNumber); 
	
	@Query(value="SELECT COUNT(OB_LINE_NO) AS deliveryLines \r\n"
			+ "FROM tbloutboundline \r\n"
			+ "WHERE WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
			+ "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL AND DLV_QTY > 0\r\n"
			+ "GROUP BY OB_LINE_NO;", nativeQuery=true)
    public List<Long> getDeliveryLines(@Param ("warehouseId") String warehouseId,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("refDocNumber") String refDocNumber);

	public List<OutboundLine> findByRefDocNumberAndItemCodeAndDeletionIndicator(String refDocNumber, String itemCode,
			Long deletionIndicator);

	public List<OutboundLine> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndStatusIdAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String referenceField2, Long statusId,
			Long deletionIndicator);

	public List<OutboundLine> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String referenceField2, Long deletionIndicator);

}