package com.tekclover.wms.api.inbound.orders.repository.specification;


import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.FindOutboundOrderLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.OutboundOrderLineV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OuboundOrderLineV2Specification implements Specification<OutboundOrderLineV2> {

    FindOutboundOrderLineV2 findOutboundOrderLineV2;

    public OuboundOrderLineV2Specification(FindOutboundOrderLineV2 inputSearchParams) {
        this.findOutboundOrderLineV2 = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<OutboundOrderLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();


        if (findOutboundOrderLineV2.getMiddlewareId() != null && !findOutboundOrderLineV2.getMiddlewareId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("middlewareId");
            predicates.add(group.in(findOutboundOrderLineV2.getMiddlewareId()));
        }
        if (findOutboundOrderLineV2.getMiddlewareTable() != null && !findOutboundOrderLineV2.getMiddlewareTable().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("middlewareTable");
            predicates.add(group.in(findOutboundOrderLineV2.getMiddlewareTable()));
        }
        if (findOutboundOrderLineV2.getOrderId() != null && !findOutboundOrderLineV2.getOrderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("orderId");
            predicates.add(group.in(findOutboundOrderLineV2.getOrderId()));
        }

        if (findOutboundOrderLineV2.getItemCode() != null && !findOutboundOrderLineV2.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findOutboundOrderLineV2.getItemCode()));
        }
        if (findOutboundOrderLineV2.getTransferOrderNumber() != null && !findOutboundOrderLineV2.getTransferOrderNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderNumber");
            predicates.add(group.in(findOutboundOrderLineV2.getTransferOrderNumber()));
        }

        if (findOutboundOrderLineV2.getSupplierInvoiceNo() != null && !findOutboundOrderLineV2.getSupplierInvoiceNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("supplierInvoiceNo");
            predicates.add(group.in(findOutboundOrderLineV2.getSupplierInvoiceNo()));
        }
        if (findOutboundOrderLineV2.getOrderedQty() != null && !findOutboundOrderLineV2.getOrderedQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("orderedQty");
            predicates.add(group.in(findOutboundOrderLineV2.getOrderedQty()));
        }
        if (findOutboundOrderLineV2.getManufacturerCode() != null && !findOutboundOrderLineV2.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findOutboundOrderLineV2.getManufacturerCode()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
