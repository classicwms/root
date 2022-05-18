package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundHeaderEntity;

@Repository
@Transactional
public interface PreInboundHeaderRepository extends JpaRepository<PreInboundHeaderEntity,Long>, 
			JpaSpecificationExecutor<PreInboundHeaderEntity> {
	
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
}