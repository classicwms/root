package com.tekclover.wms.api.outbound.transaction.repository.specification;

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.SearchPeriodicHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PeriodicHeaderV6Specification implements Specification<PeriodicHeaderV2> {

    SearchPeriodicHeaderV2 searchPeriodicHeader;

    public PeriodicHeaderV6Specification(SearchPeriodicHeaderV2 inputSearchParams) {
        this.searchPeriodicHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PeriodicHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPeriodicHeader.getCompanyCodeId() != null && !searchPeriodicHeader.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(root.get("companyCode").in(searchPeriodicHeader.getCompanyCodeId()));
        }
        if (searchPeriodicHeader.getPlantId() != null && !searchPeriodicHeader.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(root.get("plantId").in(searchPeriodicHeader.getPlantId()));
        }
        if (searchPeriodicHeader.getLanguageId() != null && !searchPeriodicHeader.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(root.get("languageId").in(searchPeriodicHeader.getLanguageId()));
        }
        if (searchPeriodicHeader.getWarehouseId() != null && !searchPeriodicHeader.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(root.get("warehouseId").in(searchPeriodicHeader.getWarehouseId()));
        }

        if (searchPeriodicHeader.getCycleCountTypeId() != null && !searchPeriodicHeader.getCycleCountTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("cycleCountTypeId");
            predicates.add(root.get("cycleCountTypeId").in(searchPeriodicHeader.getCycleCountTypeId()));
        }

        if (searchPeriodicHeader.getCycleCountNo() != null && !searchPeriodicHeader.getCycleCountNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("cycleCountNo");
            predicates.add(root.get("cycleCountNo").in(searchPeriodicHeader.getCycleCountNo()));
        }

        if (searchPeriodicHeader.getHeaderStatusId() != null && !searchPeriodicHeader.getHeaderStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(root.get("statusId").in(searchPeriodicHeader.getHeaderStatusId()));
        }

        if (searchPeriodicHeader.getCreatedBy() != null && !searchPeriodicHeader.getCreatedBy().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("createdBy");
            predicates.add(root.get("createdBy").in(searchPeriodicHeader.getCreatedBy()));
        }

        if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchPeriodicHeader.getStartCreatedOn(), searchPeriodicHeader.getEndCreatedOn()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
