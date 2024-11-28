package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.workcenter.SearchWorkCenter;
import com.tekclover.wms.api.masters.model.workcenter.WorkCenter;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class WorkCenterSpecification implements Specification<WorkCenter> {

    SearchWorkCenter searchWorkCenter;

    public WorkCenterSpecification(SearchWorkCenter inputSearchParams) {
        this.searchWorkCenter = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<WorkCenter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchWorkCenter.getWarehouseId() != null && !searchWorkCenter.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchWorkCenter.getWarehouseId()));
        }

        if (searchWorkCenter.getWorkCenterType() != null && !searchWorkCenter.getWorkCenterType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("workCenterType");
            predicates.add(group.in(searchWorkCenter.getWorkCenterType()));
        }

        if (searchWorkCenter.getCompanyCodeId() != null && !searchWorkCenter.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchWorkCenter.getCompanyCodeId()));
        }

        if (searchWorkCenter.getWorkCenterId() != null && !searchWorkCenter.getWorkCenterId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("workCenterId");
            predicates.add(group.in(searchWorkCenter.getWorkCenterId()));
        }


        if (searchWorkCenter.getLanguageId() != null && !searchWorkCenter.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchWorkCenter.getLanguageId()));
        }

        if (searchWorkCenter.getPlantId() != null && !searchWorkCenter.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchWorkCenter.getPlantId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}