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

import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;

@Repository
@Transactional
public interface InboundLineRepository extends JpaRepository<InboundLine,Long>, JpaSpecificationExecutor<InboundLine> {
	
	/**
	 * 
	 */
	public List<InboundLine> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preInboundNo
	 * @param lineNo
	 * @param itemCode
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<InboundLine> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
				String languageId, 
				String companyCode, 
				String plantId, 
				String warehouseId, 
				String refDocNumber, 
				String preInboundNo, 
				Long lineNo, 
				String itemCode, 
				Long deletionIndicator);

	public List<InboundLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
			String preInboundNo, Long deletionIndicator);

	
	// For Sending confirmation API
	public List<InboundLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndReferenceField1AndStatusIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
			String preInboundNo, String referenceField1, Long statusId, Long deletionIndicator);

	public List<InboundLine> findByRefDocNumberAndDeletionIndicator(String refDocNumber, long l);

	@Query(value="select il.wh_id as warehouseId, il.itm_code as itemCode, 'InBound' as documentType ,il.ref_doc_no as documentNumber, il.partner_code as partnerCode, "
			+ " (COALESCE(il.accept_qty,0) + COALESCE(il.damage_qty,0)) as movementQty, im.text as itemText ,im.mfr_part as manufacturerSKU from tblinboundline il "
			+ " join tblimbasicdata1 im on il.itm_code = im.itm_code WHERE il.ITM_CODE in (:itemCode) AND im.WH_ID in (:warehouseId) AND il.WH_ID in (:warehouseId) AND il.status_id in (:statusId) "
			+ " AND (il.accept_qty is not null OR il.damage_qty is not null)", 
			nativeQuery=true)
	public List<StockMovementReportImpl> findInboundLineForStockMovement(@Param("itemCode") List<String> itemCode,
																		  @Param ("warehouseId") List<String> warehouseId,
																		  @Param ("statusId") List<Long> statusId);
	
	@Query(value="Select top 1 PA_CNF_ON from tblputawayline where ref_doc_no = :refDocNo and itm_code = :itemCode order by PA_CNF_ON DESC", 
			nativeQuery=true)
	public Date findDateFromPutawayLine(@Param("refDocNo") String refDocNo, @Param("itemCode") String itemCode);

	public List<InboundLine> findByRefDocNumberAndWarehouseIdAndDeletionIndicator(String refDocNumber,
			String warehouseId, long l);

	long countByWarehouseIdAndConfirmedOnBetweenAndStatusIdAndReferenceField1IsNull(
			String warehouseId, Date fromDate, Date toDate,Long statusId);

	@Query(value="select (COALESCE(il.accept_qty,0) + COALESCE(il.damage_qty,0)) as quantity \n" +
			"from tblinboundline il where il.itm_code = :itemCode and il.IB_LINE_NO = :lineNo and il.ref_doc_no = :refDocNo and \n" +
			"il.PRE_IB_NO = :preInboundNo and il.wh_id = :warehouseId and il.IS_DELETED = 0 " ,nativeQuery=true)
	public Double getQuantityByRefDocNoAndPreInboundNoAndLineNoAndItemCode(@Param("itemCode") String itemCode,
																		 @Param ("refDocNo") String refDocNo,
																		 @Param ("preInboundNo") String preInboundNo,
																		   @Param ("lineNo") Long lineNo,
																		   @Param ("warehouseId") String warehouseId);
}

