package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.gr.GrLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface GrLineRepository extends JpaRepository<GrLine,Long>, JpaSpecificationExecutor<GrLine> {
	
	public List<GrLine> findAll();
	public Optional<GrLine> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
				String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, 
				String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, Long deletionIndicator);

	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
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
	 * @param l
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
	
}