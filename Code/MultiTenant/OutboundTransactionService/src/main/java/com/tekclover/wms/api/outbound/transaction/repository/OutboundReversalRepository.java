package com.tekclover.wms.api.outbound.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.outbound.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OutboundReversalRepository extends JpaRepository<OutboundReversal, Long>,
        JpaSpecificationExecutor<OutboundReversal>, StreamableJpaSpecificationRepository<OutboundReversal> {

    public List<OutboundReversal> findAll();

    public Optional<OutboundReversal>
    findByLanguageIdAndRefDocNumberAndPlantIdAndWarehouseIdAndOutboundReversalNoAndReversalTypeAndDeletionIndicator(
            String languageId, String refDocNumber, String plantId, String warehouseId, String outboundReversalNo, String reversalType, Long deletionIndicator);

    public Optional<OutboundReversal> findByOutboundReversalNo(String outboundReversalNo);
}