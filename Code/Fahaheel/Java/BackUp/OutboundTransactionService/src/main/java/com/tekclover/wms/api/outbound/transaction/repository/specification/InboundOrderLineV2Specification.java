package com.tekclover.wms.api.outbound.transaction.repository.specification;


import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2.FindInboundOrderLineV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InboundOrderLineV2Specification implements Specification<InboundOrderLinesV2> {

    FindInboundOrderLineV2 findInboundOrderLineV2;

    public InboundOrderLineV2Specification(FindInboundOrderLineV2 inputSearchParams) {
        this.findInboundOrderLineV2 = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InboundOrderLinesV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findInboundOrderLineV2.getMiddlewareId() != null && !findInboundOrderLineV2.getMiddlewareId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("middlewareId");
            predicates.add(group.in(findInboundOrderLineV2.getMiddlewareId()));
        }
        if (findInboundOrderLineV2.getMiddlewareHeaderId() != null && !findInboundOrderLineV2.getMiddlewareHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("middlewareHeaderId");
            predicates.add(group.in(findInboundOrderLineV2.getMiddlewareHeaderId()));
        }
        if (findInboundOrderLineV2.getReceivedQty() != null && !findInboundOrderLineV2.getReceivedQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("receivedQty");
            predicates.add(group.in(findInboundOrderLineV2.getReceivedQty()));
        }
        if (findInboundOrderLineV2.getTransferOrderNumber() != null && !findInboundOrderLineV2.getTransferOrderNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderNumber");
            predicates.add(group.in(findInboundOrderLineV2.getTransferOrderNumber()));
        }
        if (findInboundOrderLineV2.getSupplierCode() != null && !findInboundOrderLineV2.getSupplierCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("supplierCode");
            predicates.add(group.in(findInboundOrderLineV2.getSupplierCode()));
        }
        if (findInboundOrderLineV2.getOrderId() != null && !findInboundOrderLineV2.getOrderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("orderId");
            predicates.add(group.in(findInboundOrderLineV2.getOrderId()));
        }
        if (findInboundOrderLineV2.getItemCode() != null && !findInboundOrderLineV2.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findInboundOrderLineV2.getItemCode()));
        }
        if (findInboundOrderLineV2.getInvoiceNumber() != null && !findInboundOrderLineV2.getInvoiceNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("invoiceNumber");
            predicates.add(group.in(findInboundOrderLineV2.getInvoiceNumber()));
        }
        if (findInboundOrderLineV2.getFromExpectedDate() != null && findInboundOrderLineV2.getToExpectedDate() != null) {
            predicates.add(cb.between(root.get("expectedDate"), findInboundOrderLineV2.getFromExpectedDate(),
                    findInboundOrderLineV2.getToExpectedDate()));
        }
        if (findInboundOrderLineV2.getFromReceivedDate() != null && findInboundOrderLineV2.getToReceivedDate() != null) {
            predicates.add(cb.between(root.get("receivedDate"), findInboundOrderLineV2.getFromReceivedDate(),
                    findInboundOrderLineV2.getToReceivedDate()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
