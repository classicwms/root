package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.inbound.v2.SupplierInvoiceHeader;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SupplierInvoiceHeaderRepository extends JpaRepository<SupplierInvoiceHeader, Long>,
        JpaSpecificationExecutor<SupplierInvoiceHeader>,
        StreamableJpaSpecificationRepository<SupplierInvoiceHeader> {


}