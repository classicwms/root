package com.tekclover.wms.api.inbound.orders.repository.specification;

import com.tekclover.wms.api.inbound.orders.model.outbound.outboundreversal.v2.OutboundReversalV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.outboundreversal.v2.SearchOutboundReversalV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OutboundReversalV2Specification implements Specification<OutboundReversalV2> {

    SearchOutboundReversalV2 searchOutboundReversal;

    public OutboundReversalV2Specification(SearchOutboundReversalV2 inputSearchParams) {
        this.searchOutboundReversal = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<OutboundReversalV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchOutboundReversal.getCompanyCodeId() != null && !searchOutboundReversal.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchOutboundReversal.getCompanyCodeId()));
        }

        if (searchOutboundReversal.getPlantId() != null && !searchOutboundReversal.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchOutboundReversal.getPlantId()));
        }

        if (searchOutboundReversal.getLanguageId() != null && !searchOutboundReversal.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchOutboundReversal.getLanguageId()));
        }

        if (searchOutboundReversal.getOutboundReversalNo() != null && !searchOutboundReversal.getOutboundReversalNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("outboundReversalNo");
            predicates.add(group.in(searchOutboundReversal.getOutboundReversalNo()));
        }

        if (searchOutboundReversal.getReversalType() != null && !searchOutboundReversal.getReversalType().isEmpty()) {
            final Path<Group> group = root.<Group>get("reversalType");
            predicates.add(group.in(searchOutboundReversal.getReversalType()));
        }

        if (searchOutboundReversal.getRefDocNumber() != null && !searchOutboundReversal.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchOutboundReversal.getRefDocNumber()));
        }

        if (searchOutboundReversal.getPartnerCode() != null && !searchOutboundReversal.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchOutboundReversal.getPartnerCode()));
        }

        if (searchOutboundReversal.getItemCode() != null && !searchOutboundReversal.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchOutboundReversal.getItemCode()));
        }

        if (searchOutboundReversal.getPackBarcode() != null && !searchOutboundReversal.getPackBarcode().isEmpty()) {
            final Path<Group> group = root.<Group>get("packBarcode");
            predicates.add(group.in(searchOutboundReversal.getPackBarcode()));
        }

        if (searchOutboundReversal.getStatusId() != null && !searchOutboundReversal.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchOutboundReversal.getStatusId()));
        }

        if (searchOutboundReversal.getReversedBy() != null && !searchOutboundReversal.getReversedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("reversedBy");
            predicates.add(group.in(searchOutboundReversal.getReversedBy()));
        }

        if (searchOutboundReversal.getStartReversedOn() != null && searchOutboundReversal.getEndReversedOn() != null) {
            predicates.add(cb.between(root.get("reversedOn"), searchOutboundReversal.getStartReversedOn(), searchOutboundReversal.getEndReversedOn()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
