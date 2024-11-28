package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.SearchPreInboundHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PreInboundHeaderV2Specification implements Specification<PreInboundHeaderEntityV2> {

    SearchPreInboundHeaderV2 searchPreInboundHeader;

    public PreInboundHeaderV2Specification(SearchPreInboundHeaderV2 inputSearchParams) {
        this.searchPreInboundHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PreInboundHeaderEntityV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPreInboundHeader.getCompanyCodeId() != null && !searchPreInboundHeader.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchPreInboundHeader.getCompanyCodeId()));
        }

        if (searchPreInboundHeader.getPlantId() != null && !searchPreInboundHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchPreInboundHeader.getPlantId()));
        }

        if (searchPreInboundHeader.getLanguageId() != null && !searchPreInboundHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchPreInboundHeader.getLanguageId()));
        }

        if (searchPreInboundHeader.getWarehouseId() != null && !searchPreInboundHeader.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPreInboundHeader.getWarehouseId()));
        }

        if (searchPreInboundHeader.getPreInboundNo() != null && !searchPreInboundHeader.getPreInboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preInboundNo");
            predicates.add(group.in(searchPreInboundHeader.getPreInboundNo()));
        }

        if (searchPreInboundHeader.getInboundOrderTypeId() != null && !searchPreInboundHeader.getInboundOrderTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("inboundOrderTypeId");
            predicates.add(group.in(searchPreInboundHeader.getInboundOrderTypeId()));
        }

        if (searchPreInboundHeader.getRefDocNumber() != null && !searchPreInboundHeader.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchPreInboundHeader.getRefDocNumber()));
        }

        if (searchPreInboundHeader.getStartRefDocDate() != null && searchPreInboundHeader.getEndRefDocDate() != null) {
            predicates.add(cb.between(root.get("refDocDate"), searchPreInboundHeader.getStartRefDocDate(), searchPreInboundHeader.getEndRefDocDate()));
        }

        if (searchPreInboundHeader.getStatusId() != null && !searchPreInboundHeader.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchPreInboundHeader.getStatusId()));
        }

        if (searchPreInboundHeader.getStartCreatedOn() != null && searchPreInboundHeader.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchPreInboundHeader.getStartCreatedOn(), searchPreInboundHeader.getEndCreatedOn()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}