package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.cyclecountscheduler.CycleCountScheduler;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.SearchCycleCountScheduler;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CycleCountSchedulerSpecification implements Specification<CycleCountScheduler> {

    SearchCycleCountScheduler searchCycleCountScheduler;

    public CycleCountSchedulerSpecification(SearchCycleCountScheduler inputSearchParams) {
        this.searchCycleCountScheduler = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<CycleCountScheduler> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchCycleCountScheduler.getWarehouseId() != null && !searchCycleCountScheduler.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchCycleCountScheduler.getWarehouseId()));
        }

        if (searchCycleCountScheduler.getCompanyCodeId() != null && !searchCycleCountScheduler.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchCycleCountScheduler.getCompanyCodeId()));
        }

        if (searchCycleCountScheduler.getPlantId() != null && !searchCycleCountScheduler.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchCycleCountScheduler.getPlantId()));
        }

        if (searchCycleCountScheduler.getLanguageId() != null && !searchCycleCountScheduler.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchCycleCountScheduler.getLanguageId()));
        }
        if (searchCycleCountScheduler.getCycleCountTypeId() != null && !searchCycleCountScheduler.getCycleCountTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("cycleCountTypeId");
            predicates.add(group.in(searchCycleCountScheduler.getCycleCountTypeId()));
        }
        if (searchCycleCountScheduler.getSchedulerNumber() != null && !searchCycleCountScheduler.getSchedulerNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("schedulerNumber");
            predicates.add(group.in(searchCycleCountScheduler.getSchedulerNumber()));
        }
        if (searchCycleCountScheduler.getLevelId() != null && !searchCycleCountScheduler.getLevelId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("levelId");
            predicates.add(group.in(searchCycleCountScheduler.getLevelId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}