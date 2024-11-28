package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.masters.InboundIntegrationLog;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface InboundIntegrationLogRepository extends JpaRepository<InboundIntegrationLog, Long>,
        JpaSpecificationExecutor<InboundIntegrationLog>, StreamableJpaSpecificationRepository<InboundIntegrationLog> {

    public List<InboundIntegrationLog> findAll();

    public Optional<InboundIntegrationLog>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndIntegrationLogNumberAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String integrationLogNumber, String refDocNumber, Long deletionIndicator);

    public List<InboundIntegrationLog> findByIntegrationStatus(String string);
}