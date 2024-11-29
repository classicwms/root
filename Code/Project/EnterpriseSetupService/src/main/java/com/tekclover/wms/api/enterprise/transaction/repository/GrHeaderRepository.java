package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface GrHeaderRepository extends JpaRepository<GrHeader,Long>, JpaSpecificationExecutor<GrHeader>,
		StreamableJpaSpecificationRepository<GrHeader> {
	
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
	
	/**
	 * 
	 * @param rssFeedEntryId
	 * @param isRead
	 */
	@Modifying(clearAutomatically = true)
	@Query("UPDATE GrHeader ib SET ib.statusId = :statusId WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
	void updateGrHeaderStatus(@Param ("warehouseId") String warehouseId,
			@Param ("refDocNumber") String refDocNumber, @Param ("statusId") Long statusId);
}