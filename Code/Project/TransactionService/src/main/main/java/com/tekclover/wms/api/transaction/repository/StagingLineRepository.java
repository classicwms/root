package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.staging.StagingLineEntity;


@Repository
@Transactional
public interface StagingLineRepository extends JpaRepository<StagingLineEntity,Long>, 
		JpaSpecificationExecutor<StagingLineEntity> {
	
	/**
	 * 
	 */
	public List<StagingLineEntity> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param stagingNo
	 * @param palletCode
	 * @param caseCode
	 * @param lineNo
	 * @param itemCode
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<StagingLineEntity> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndItemCodeAndDeletionIndicator(
				String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String palletCode, String caseCode, Long lineNo, String itemCode, Long deletionIndicator);

	// WH_ID/PRE_IB_NO/REF_DOC_NO/STG_NO/IB_LINE_NO/ITM_CODE/CASE_CODE
	public List<StagingLineEntity> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, 
			String warehouseId, String preInboundNo, String refDocNumber, 
			String stagingNo, Long lineNo, String itemCode, String caseCode, 
			Long deletionIndicator);

	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param preInboundNo
	 * @param lineNo
	 * @param itemCode
	 * @param statusIds
	 * @param l
	 * @return
	 */
	public List<StagingLineEntity> findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndLineNoAndItemCodeAndStatusIdInAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String preInboundNo, Long lineNo, String itemCode,
			List<Long> statusIds, Long deletionIndicator);

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
	public List<StagingLineEntity> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
			String refDocNumber, Long lineNo, String itemCode, Long deletionIndicator);

	public List<StagingLineEntity> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
			String preInboundNo, Long lineNo, String itemCode, String caseCode, long l);

}