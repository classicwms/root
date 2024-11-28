package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.FindOutboundOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OuboundOrderV2Specification implements Specification<OutboundOrderV2> {

    FindOutboundOrderV2 findOutboundOrderV2;

    public OuboundOrderV2Specification(FindOutboundOrderV2 inputSearchParams) {
        this.findOutboundOrderV2 = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<OutboundOrderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findOutboundOrderV2.getLanguageId() != null && !findOutboundOrderV2.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findOutboundOrderV2.getLanguageId()));
        }

        if (findOutboundOrderV2.getCompanyCode() != null && !findOutboundOrderV2.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findOutboundOrderV2.getCompanyCode()));
        }
        if (findOutboundOrderV2.getBranchCode() != null && !findOutboundOrderV2.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findOutboundOrderV2.getBranchCode()));
        }
        if (findOutboundOrderV2.getWarehouseID() != null && !findOutboundOrderV2.getWarehouseID().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseID");
            predicates.add(group.in(findOutboundOrderV2.getWarehouseID()));
        }

        if (findOutboundOrderV2.getMiddlewareId() != null && !findOutboundOrderV2.getMiddlewareId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("middlewareId");
            predicates.add(group.in(findOutboundOrderV2.getMiddlewareId()));
        }
        if (findOutboundOrderV2.getMiddlewareTable() != null && !findOutboundOrderV2.getMiddlewareTable().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("middlewareTable");
            predicates.add(group.in(findOutboundOrderV2.getMiddlewareTable()));
        }
        if (findOutboundOrderV2.getOrderId() != null && !findOutboundOrderV2.getOrderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("orderId");
            predicates.add(group.in(findOutboundOrderV2.getOrderId()));
        }
        if (findOutboundOrderV2.getRefDocumentNo() != null && !findOutboundOrderV2.getRefDocumentNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("refDocumentNo");
            predicates.add(group.in(findOutboundOrderV2.getRefDocumentNo()));
        }
        if (findOutboundOrderV2.getSalesInvoiceNumber() != null && !findOutboundOrderV2.getSalesInvoiceNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("salesInvoiceNumber");
            predicates.add(group.in(findOutboundOrderV2.getSalesInvoiceNumber()));
        }
        if (findOutboundOrderV2.getPickListNumber() != null && !findOutboundOrderV2.getPickListNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("pickListNumber");
            predicates.add(group.in(findOutboundOrderV2.getPickListNumber()));
        }
        if (findOutboundOrderV2.getCustomerId() != null && !findOutboundOrderV2.getCustomerId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("customerId");
            predicates.add(group.in(findOutboundOrderV2.getCustomerId()));
        }
        if (findOutboundOrderV2.getProcessedStatusId() != null && !findOutboundOrderV2.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findOutboundOrderV2.getProcessedStatusId()));
        }
        if (findOutboundOrderV2.getFromOrderReceivedOn() != null && findOutboundOrderV2.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findOutboundOrderV2.getFromOrderReceivedOn(),
                                      findOutboundOrderV2.getToOrderReceivedOn()));
        }

        if (findOutboundOrderV2.getFromOrderProcessedOn() != null && findOutboundOrderV2.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findOutboundOrderV2.getFromOrderProcessedOn(),
                                      findOutboundOrderV2.getToOrderProcessedOn()));
        }
        if (findOutboundOrderV2.getFromSalesInvoiceDate() != null && findOutboundOrderV2.getToSalesInvoiceDate() != null) {
            predicates.add(cb.between(root.get("salesInvoiceDate"), findOutboundOrderV2.getFromSalesInvoiceDate(),
                                      findOutboundOrderV2.getToSalesInvoiceDate()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}