package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

	@Query(value="select il.wh_id as warehouseId, il.itm_code as itemCode , \n" +
			" 'InBound' as documentType ,il.ref_doc_no as documentNumber, il.partner_code as partnerCode,\n" +
			" x.pa_cnf_on as confirmedOn, (COALESCE(il.accept_qty,0) + COALESCE(il.damage_qty,0)) as movementQty, im.text as itemText ,im.mfr_part as manufacturerSKU \n" +
			" from tblinboundline il\n" +
			" join tblimbasicdata1 im on il.itm_code = im.itm_code \n" +
			" join (select TOP 1 * from tblputawayline pa where pa.itm_code in (:itemCode) order by pa_cnf_on asc) as x on il.ref_doc_no = x.ref_doc_no  \n" +
			" WHERE il.ITM_CODE in (:itemCode) AND il.WH_ID in (:warehouseId) AND il.status_id in (:statusId)" , nativeQuery=true)
//			" AND il.ib_cnf_on between :fromDate and :toDate " , nativeQuery=true)
	public List<StockMovementReportImpl> findInboundLineForStockMovement(@Param("itemCode") List<String> itemCode,
																		  @Param ("warehouseId") List<String> warehouseId,
																		  @Param ("statusId") List<Long> statusId);
}

