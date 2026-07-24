package com.tekclover.wms.api.inbound.transaction.repository.specification;

import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.SearchInboundHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InboundHeaderV2Specification implements Specification<InboundHeaderV2> {

    SearchInboundHeaderV2 searchInboundHeader;

    public InboundHeaderV2Specification(SearchInboundHeaderV2 inputSearchParams) {
        this.searchInboundHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InboundHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchInboundHeader.getCompanyCodeId() != null && !searchInboundHeader.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchInboundHeader.getCompanyCodeId()));
        }

        if (searchInboundHeader.getPlantId() != null && !searchInboundHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchInboundHeader.getPlantId()));
        }

        if (searchInboundHeader.getLanguageId() != null && !searchInboundHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchInboundHeader.getLanguageId()));
        }

        if (searchInboundHeader.getWarehouseId() != null && !searchInboundHeader.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchInboundHeader.getWarehouseId()));
        }

        if (searchInboundHeader.getRefDocNumber() != null && !searchInboundHeader.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchInboundHeader.getRefDocNumber()));
        }

        if (searchInboundHeader.getInboundOrderTypeId() != null && !searchInboundHeader.getInboundOrderTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("inboundOrderTypeId");
            predicates.add(group.in(searchInboundHeader.getInboundOrderTypeId()));
        }

        if (searchInboundHeader.getContainerNo() != null && !searchInboundHeader.getContainerNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("containerNo");
            predicates.add(group.in(searchInboundHeader.getContainerNo()));
        }

        if (searchInboundHeader.getStatusId() != null && !searchInboundHeader.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchInboundHeader.getStatusId()));
        }

        if (searchInboundHeader.getStartCreatedOn() != null && searchInboundHeader.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchInboundHeader.getStartCreatedOn(), searchInboundHeader.getEndCreatedOn()));
        }

        if (searchInboundHeader.getStartConfirmedOn() != null && searchInboundHeader.getEndConfirmedOn() != null) {
            predicates.add(cb.between(root.get("confirmedOn"), searchInboundHeader.getStartConfirmedOn(), searchInboundHeader.getEndConfirmedOn()));
        }

        final Path<Group> group = root.<Group>get("deletionIndicator");
        predicates.add(group.in(0L));

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
