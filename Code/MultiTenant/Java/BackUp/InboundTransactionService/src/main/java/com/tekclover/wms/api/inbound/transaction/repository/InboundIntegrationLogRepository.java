package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLog;

@Repository
@Transactional
public interface InboundIntegrationLogRepository extends JpaRepository<InboundIntegrationLog,Long>,
		JpaSpecificationExecutor<InboundIntegrationLog>, StreamableJpaSpecificationRepository<InboundIntegrationLog> {
	
	public List<InboundIntegrationLog> findAll();
	public Optional<InboundIntegrationLog> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndIntegrationLogNumberAndRefDocNumberAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String integrationLogNumber, String refDocNumber, Long deletionIndicator);
	public List<InboundIntegrationLog> findByIntegrationStatus(String string);
}