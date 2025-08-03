package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.strategyid.FindStrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.StrategyId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StrategyIdSpecification implements Specification<StrategyId> {

    FindStrategyId findStrategyId;

    public StrategyIdSpecification(FindStrategyId inputSearchParams) {
        this.findStrategyId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StrategyId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStrategyId.getCompanyCodeId() != null && !findStrategyId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findStrategyId.getCompanyCodeId()));
        }

        if (findStrategyId.getPlantId() != null && !findStrategyId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findStrategyId.getPlantId()));
        }

        if (findStrategyId.getWarehouseId() != null && !findStrategyId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findStrategyId.getWarehouseId()));
        }

        if (findStrategyId.getStrategyNo() != null && !findStrategyId.getStrategyNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("strategyNo");
            predicates.add(group.in(findStrategyId.getStrategyNo()));
        }

        if (findStrategyId.getStrategyTypeId() != null && !findStrategyId.getStrategyTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("strategyTypeId");
            predicates.add(group.in(findStrategyId.getStrategyTypeId()));
        }
        if (findStrategyId.getLanguageId() != null && !findStrategyId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findStrategyId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
