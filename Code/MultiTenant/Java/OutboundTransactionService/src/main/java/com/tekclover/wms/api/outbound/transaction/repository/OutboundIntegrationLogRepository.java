package com.tekclover.wms.api.outbound.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.OutboundIntegrationLog;


@Repository
@Transactional
public interface OutboundIntegrationLogRepository extends JpaRepository<OutboundIntegrationLog, Long>,
        JpaSpecificationExecutor<OutboundIntegrationLog>, StreamableJpaSpecificationRepository<OutboundIntegrationLog> {

    public List<OutboundIntegrationLog> findAll();

    public Optional<OutboundIntegrationLog>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndIntegrationLogNumberAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String integrationLogNumber, String refDocNumber, Long deletionIndicator);

    public List<OutboundIntegrationLog> findByIntegrationStatus(String string);
}