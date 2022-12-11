package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayLine;

@Repository
@Transactional
public interface PutAwayLineRepository extends JpaRepository<PutAwayLine,Long>, JpaSpecificationExecutor<PutAwayLine> {
	
	/**
	 * 
	 */
	public List<PutAwayLine> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param CompanyCode
	 * @param plantId
	 * @param warehouseId
	 * @param goodsReceiptNo
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param putAwayNumber
	 * @param lineNo
	 * @param itemCode
	 * @param proposedStorageBin
	 * @param confirmedStorageBin
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<PutAwayLine> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
				String languageId, String CompanyCode, String plantId, String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, Long lineNo, String itemCode, 
				String proposedStorageBin, List<String> confirmedStorageBin, Long deletionIndicator);

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
	 * @param deletionIndicator
	 * @return
	 */
	public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber,
			Long lineNo, String itemCode, Long deletionIndicator);

	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param refDocNumber
	 * @param l
	 * @return
	 */
	public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String refDocNumber, Long deletionIndicator);

	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param refDocNumber
	 * @param packBarcodes
	 * @param l
	 * @return
	 */
	public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
			String languageId, String CompanyCode, String plantId, String refDocNumber, String packBarcodes, Long deletionIndicator);

	public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
			String putAwayNumber, Long deletionIndicator);

	public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber,
			Long deletionIndicator);
}