package com.tekclover.wms.api.inbound.orders.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.OrderManagementHeader;


@Repository
@Transactional
public interface OrderManagementHeaderRepository extends JpaRepository<OrderManagementHeader, Long>,
        JpaSpecificationExecutor<OrderManagementHeader>, StreamableJpaSpecificationRepository<OrderManagementHeader> {

    public List<OrderManagementHeader> findAll();

    public Optional<OrderManagementHeader>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long deletionIndicator);

    public Optional<OrderManagementHeader> findByRefDocNumber(String refDocNumber);

    public OrderManagementHeader findByPreOutboundNo(String preOutboundNo);

    public OrderManagementHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, long l);
}