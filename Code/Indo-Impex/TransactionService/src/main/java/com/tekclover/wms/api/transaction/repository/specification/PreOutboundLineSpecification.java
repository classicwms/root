package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundLine;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PreOutboundLineSpecification implements Specification<PreOutboundLine> {

    SearchPreOutboundLine searchPreOutboundLine;

    public PreOutboundLineSpecification(SearchPreOutboundLine inputSearchParams) {
        this.searchPreOutboundLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PreOutboundLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPreOutboundLine.getWarehouseId() != null && !searchPreOutboundLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPreOutboundLine.getWarehouseId()));
        }

        if (searchPreOutboundLine.getRefDocNumber() != null && !searchPreOutboundLine.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchPreOutboundLine.getRefDocNumber()));
        }

        if (searchPreOutboundLine.getPreOutboundNo() != null && !searchPreOutboundLine.getPreOutboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preOutboundNo");
            predicates.add(group.in(searchPreOutboundLine.getPreOutboundNo()));
        }

        if (searchPreOutboundLine.getPartnerCode() != null && !searchPreOutboundLine.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchPreOutboundLine.getPartnerCode()));
        }

        if (searchPreOutboundLine.getLineNumber() != null && !searchPreOutboundLine.getLineNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("lineNumber");
            predicates.add(group.in(searchPreOutboundLine.getLineNumber()));
        }

        if (searchPreOutboundLine.getItemCode() != null && !searchPreOutboundLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchPreOutboundLine.getItemCode()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}