package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;

@Repository
@Transactional
public interface PutAwayHeaderRepository extends JpaRepository<PutAwayHeader,Long>, JpaSpecificationExecutor<PutAwayHeader> {
	
	public List<PutAwayHeader> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param goodsReceiptNo
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param putAwayNumber
	 * @param proposedStorageBin
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<PutAwayHeader> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndPutAwayNumberAndProposedStorageBinAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String preInboundNo, 
				String refDocNumber, String goodsReceiptNo, String palletCode, String caseCode, String packBarcodes, 
				String putAwayNumber, String proposedStorageBin, Long deletionIndicator);
	
	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param putAwayNumber
	 * @param deletionIndicator
	 * @return
	 */
	public List<PutAwayHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
			String refDocNumber, String putAwayNumber, Long deletionIndicator);

	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param deletionIndicator
	 * @return
	 */
	public List<PutAwayHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
			String refDocNumber, Long deletionIndicator);

	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param refDocNumber
	 * @param l
	 * @return
	 */
	public List<PutAwayHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String refDocNumber, List<Long> statusId, Long deletionIndicator);

	
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
	public List<PutAwayHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String refDocNumber, String packBarcodes, Long deletionIndicator);

	/**
	 * 
	 * @param warehouseId
	 * @param statusId
	 * @param orderTypeId
	 * @return
	 */
	public List<PutAwayHeader> findByWarehouseIdAndStatusIdAndInboundOrderTypeIdIn (String warehouseId, Long statusId,
			List<Long> orderTypeId);
}