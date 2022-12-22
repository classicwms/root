package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStatusIdInAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber,
			List<Long> statusIds, Long deletionIndicator);
	
	/**
	 * 
	 * @param companyId
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @return
	 */
	@Query(value="SELECT COUNT(*) FROM tblputawayline WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
			+ "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (20, 22) AND IS_DELETED = 0", nativeQuery=true)
    public long getPutawayLineCountByStatusId(
    		@Param ("companyId") String companyId,
			@Param ("plantId") String plantId,
			@Param ("warehouseId") String warehouseId,
			@Param ("preInboundNo") String preInboundNo,
			@Param ("refDocNumber") String refDocNumber); 		
	
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
}