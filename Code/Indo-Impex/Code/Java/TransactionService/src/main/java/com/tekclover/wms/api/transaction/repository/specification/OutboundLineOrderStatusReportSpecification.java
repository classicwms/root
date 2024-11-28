package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.report.SearchOrderStatusReport;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OutboundLineOrderStatusReportSpecification implements Specification<OutboundLine> {

    SearchOrderStatusReport searchOrderStatusReport;

    public OutboundLineOrderStatusReportSpecification(SearchOrderStatusReport inputSearchParams) {
        this.searchOrderStatusReport = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<OutboundLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchOrderStatusReport.getWarehouseId() != null) {
            predicates.add(cb.equal(root.get("warehouseId"), searchOrderStatusReport.getWarehouseId()));
        }

        if (searchOrderStatusReport.getFromDeliveryDate() != null && searchOrderStatusReport.getToDeliveryDate() != null) {
            predicates.add(cb.between(root.get("deliveryConfirmedOn"),
                                      searchOrderStatusReport.getFromDeliveryDate(), searchOrderStatusReport.getToDeliveryDate()));
        }

        if (searchOrderStatusReport.getPartnerCode() != null && !searchOrderStatusReport.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchOrderStatusReport.getPartnerCode()));
        }

        if (searchOrderStatusReport.getRefDocNumber() != null && !searchOrderStatusReport.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchOrderStatusReport.getRefDocNumber()));
        }

        if (searchOrderStatusReport.getOrderType() != null && !searchOrderStatusReport.getOrderType().isEmpty()) {
            final Path<Group> group = root.<Group>get("referenceField1");
            predicates.add(group.in(searchOrderStatusReport.getOrderType()));
        }

        if (searchOrderStatusReport.getStatusId() != null && !searchOrderStatusReport.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchOrderStatusReport.getStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}