package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	 * @param companyId
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @return
	 */
	@Query(value="SELECT COUNT(*) FROM tblputawayheader WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
			+ "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (20, 22) AND IS_DELETED = 0", nativeQuery=true)
    public long getPutawayHeaderCountByStatusId(
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
	public List<PutAwayHeader> findByWarehouseIdAndStatusIdAndInboundOrderTypeIdInAndDeletionIndicator (String warehouseId, Long statusId,
			List<Long> orderTypeId, Long deletionIndicator);
}