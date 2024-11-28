package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.imstrategies.ImStrategies;
import com.tekclover.wms.api.masters.model.imstrategies.SearchImStrategies;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImStrategiesSpecification implements Specification<ImStrategies> {

    SearchImStrategies searchImStrategies;

    public ImStrategiesSpecification(SearchImStrategies inputSearchParams) {
        this.searchImStrategies = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImStrategies> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImStrategies.getWarehouseId() != null && !searchImStrategies.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchImStrategies.getWarehouseId()));
        }

        if (searchImStrategies.getSequenceIndicator() != null && !searchImStrategies.getSequenceIndicator().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("sequenceIndicator");
            predicates.add(group.in(searchImStrategies.getSequenceIndicator()));
        }


        if (searchImStrategies.getItemCode() != null && !searchImStrategies.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchImStrategies.getItemCode()));
        }

        if (searchImStrategies.getStrategyTypeId() != null && !searchImStrategies.getStrategyTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("strategyTypeId");
            predicates.add(group.in(searchImStrategies.getStrategyTypeId()));
        }

        if (searchImStrategies.getCompanyCodeId() != null && !searchImStrategies.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchImStrategies.getCompanyCodeId()));
        }

        if (searchImStrategies.getLanguageId() != null && !searchImStrategies.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchImStrategies.getLanguageId()));
        }

        if (searchImStrategies.getPlantId() != null && !searchImStrategies.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchImStrategies.getPlantId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}