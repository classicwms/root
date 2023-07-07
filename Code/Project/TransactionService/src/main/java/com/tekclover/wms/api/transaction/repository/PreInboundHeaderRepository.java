package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundHeaderEntity;

@Repository
@Transactional
public interface PreInboundHeaderRepository extends JpaRepository<PreInboundHeaderEntity,Long>, 
			JpaSpecificationExecutor<PreInboundHeaderEntity>, StreamableJpaSpecificationRepository<PreInboundHeaderEntity> {
	
	public List<PreInboundHeaderEntity> findAll();
	
	public Optional<PreInboundHeaderEntity> findByPreInboundNoAndWarehouseIdAndDeletionIndicator (String preInboundNo, 
			String warehouseId, Long deletionIndicator);
	
	public PreInboundHeaderEntity findByWarehouseId(String warehouseId);
	
	public Optional<PreInboundHeaderEntity> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
				String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, Long deletionIndicator);

	// Pass WH_ID in PREINBOUNDHEADER table and fetch the Count of values where STATUS_ID=06,07 and Autopopulate
	public long countByWarehouseIdAndStatusIdIn (String warehouseId, List<Long> statusId);

	
	public List<PreInboundHeaderEntity> findByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId, Long statusId, Long deletionIndicator);

	public Optional<PreInboundHeaderEntity> findByWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
			String warehouseId, String preInboundNo, String refDocNumner, Long deletionIndicator);

	public Optional<PreInboundHeaderEntity> findByPreInboundNoAndDeletionIndicator(String preInboundNo, long l);
	
	public Optional<PreInboundHeaderEntity> findByRefDocNumberAndDeletionIndicator(String refDocNumber, long l);

	@Query(value="Select REF_DOC_TYP from tblpreinboundheader where WH_ID = :warehouseId and ref_doc_no = :refDocNumber \n" +
			" and PRE_IB_NO = :preInboundNo and IS_DELETED = :delete ", nativeQuery=true)
	public String getReferenceDocumentTypeFromPreInboundHeader(@Param("warehouseId") String warehouseId,
															   @Param("preInboundNo") String preInboundNo,
															   @Param("refDocNumber") String refDocNumber,
															   @Param("delete") Long delete);
	
	/**
	 * 
	 * @param rssFeedEntryId
	 * @param isRead
	 */
	@Modifying(clearAutomatically = true)
	@Query("UPDATE PreInboundHeaderEntity ib SET ib.statusId = :statusId WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
	void updatePreInboundHeaderEntityStatus(@Param ("warehouseId") String warehouseId,
			@Param ("refDocNumber") String refDocNumber, @Param ("statusId") Long statusId);
}