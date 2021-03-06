package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.inbound.gr.GrHeader;

@Repository
@Transactional
public interface GrHeaderRepository extends JpaRepository<GrHeader,Long>, JpaSpecificationExecutor<GrHeader> {
	
	public List<GrHeader> findAll();
	
	public Optional<GrHeader> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String goodsReceiptNo, 
				String palletCode, String caseCode, Long deletionIndicator);

	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param goodsReceiptNo
	 * @param caseCode
	 * @param refDocNumber
	 * @param deletionIndicator
	 * @return
	 */
	public List<GrHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndCaseCodeAndRefDocNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String goodsReceiptNo,
			String caseCode, String refDocNumber, Long deletionIndicator);

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
	public List<GrHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String refDocNumber, String warehouseId, 
			String preInboundNo, String caseCode, Long deletionIndicator);

	public List<GrHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
			String refDocNumber, long l);
}