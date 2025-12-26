package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.cyclecount.periodic.v2.PeriodicHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.cyclecount.periodic.v2.SearchPeriodicHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PeriodicHeaderV2Specification implements Specification<PeriodicHeaderV2> {

    SearchPeriodicHeaderV2 searchPeriodicHeader;

    public PeriodicHeaderV2Specification(SearchPeriodicHeaderV2 inputSearchParams) {
        this.searchPeriodicHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PeriodicHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPeriodicHeader.getCompanyCodeId() != null && !searchPeriodicHeader.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchPeriodicHeader.getCompanyCodeId()));
        }
        if (searchPeriodicHeader.getPlantId() != null && !searchPeriodicHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchPeriodicHeader.getPlantId()));
        }
        if (searchPeriodicHeader.getLanguageId() != null && !searchPeriodicHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchPeriodicHeader.getLanguageId()));
        }
        if (searchPeriodicHeader.getWarehouseId() != null && !searchPeriodicHeader.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPeriodicHeader.getWarehouseId()));
        }

        if (searchPeriodicHeader.getCycleCountTypeId() != null && !searchPeriodicHeader.getCycleCountTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("cycleCountTypeId");
            predicates.add(group.in(searchPeriodicHeader.getCycleCountTypeId()));
        }

        if (searchPeriodicHeader.getCycleCountNo() != null && !searchPeriodicHeader.getCycleCountNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("cycleCountNo");
            predicates.add(group.in(searchPeriodicHeader.getCycleCountNo()));
        }

        if (searchPeriodicHeader.getHeaderStatusId() != null && !searchPeriodicHeader.getHeaderStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchPeriodicHeader.getHeaderStatusId()));
        }

        if (searchPeriodicHeader.getCreatedBy() != null && !searchPeriodicHeader.getCreatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("createdBy");
            predicates.add(group.in(searchPeriodicHeader.getCreatedBy()));
        }

        if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchPeriodicHeader.getStartCreatedOn(), searchPeriodicHeader.getEndCreatedOn()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}