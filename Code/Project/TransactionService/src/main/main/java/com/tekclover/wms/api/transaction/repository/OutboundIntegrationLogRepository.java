package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationLog;


@Repository
@Transactional
public interface OutboundIntegrationLogRepository extends JpaRepository<OutboundIntegrationLog,Long>, JpaSpecificationExecutor<OutboundIntegrationLog> {
	
	public List<OutboundIntegrationLog> findAll();
	public Optional<OutboundIntegrationLog> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndIntegrationLogNumberAndRefDocNumberAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String integrationLogNumber, String refDocNumber, Long deletionIndicator);
}