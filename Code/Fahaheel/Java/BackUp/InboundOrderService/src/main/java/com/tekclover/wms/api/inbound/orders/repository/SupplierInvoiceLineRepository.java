package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.inbound.v2.SupplierInvoiceLine;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SupplierInvoiceLineRepository extends JpaRepository<SupplierInvoiceLine, Long>,
        JpaSpecificationExecutor<SupplierInvoiceLine>,
        StreamableJpaSpecificationRepository<SupplierInvoiceLine> {



}