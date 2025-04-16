package com.tekclover.wms.api.enterprise.transaction.repository.specification;


import com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.v2.FindInboundOrderV2;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InboundOrderV2Specification implements Specification<InboundOrderV2> {

    FindInboundOrderV2 findInboundOrder;

    public InboundOrderV2Specification(FindInboundOrderV2 inputSearchParams) {
        this.findInboundOrder = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InboundOrderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findInboundOrder.getBranchCode() != null && !findInboundOrder.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findInboundOrder.getBranchCode()));
        }
        if (findInboundOrder.getCompanyCode() != null && !findInboundOrder.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findInboundOrder.getCompanyCode()));
        }
        if (findInboundOrder.getTransferOrderNumber() != null && !findInboundOrder.getTransferOrderNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderNumber");
            predicates.add(group.in(findInboundOrder.getTransferOrderNumber()));
        }
        if (findInboundOrder.getMiddlewareId() != null && !findInboundOrder.getMiddlewareId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("middlewareId");
            predicates.add(group.in(findInboundOrder.getMiddlewareId()));
        }
        if (findInboundOrder.getMiddlewareTable() != null && !findInboundOrder.getMiddlewareTable().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("middlewareTable");
            predicates.add(group.in(findInboundOrder.getMiddlewareTable()));
        }
        if (findInboundOrder.getOrderId() != null && !findInboundOrder.getOrderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("orderId");
            predicates.add(group.in(findInboundOrder.getOrderId()));
        }
        if (findInboundOrder.getRefDocumentNo() != null && !findInboundOrder.getRefDocumentNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("refDocumentNo");
            predicates.add(group.in(findInboundOrder.getRefDocumentNo()));
        }
        if (findInboundOrder.getRefDocumentType() != null && !findInboundOrder.getRefDocumentType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("refDocumentType");
            predicates.add(group.in(findInboundOrder.getRefDocumentType()));
        }
        if (findInboundOrder.getWarehouseID() != null && !findInboundOrder.getWarehouseID().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseID");
            predicates.add(group.in(findInboundOrder.getWarehouseID()));
        }

        if (findInboundOrder.getProcessedStatusId() != null && !findInboundOrder.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findInboundOrder.getProcessedStatusId()));
        }
        if (findInboundOrder.getFromOrderReceivedOn() != null && findInboundOrder.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findInboundOrder.getFromOrderReceivedOn(), findInboundOrder.getToOrderReceivedOn()));
        }

        if (findInboundOrder.getFromOrderProcessedOn() != null && findInboundOrder.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findInboundOrder.getFromOrderProcessedOn(),
                    findInboundOrder.getToOrderProcessedOn()));
        }


        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}