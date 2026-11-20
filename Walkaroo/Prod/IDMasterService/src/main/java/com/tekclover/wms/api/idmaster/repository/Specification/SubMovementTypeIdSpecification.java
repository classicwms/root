package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.strategyid.FindStrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.StrategyId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.FindSubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.SubMovementTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SubMovementTypeIdSpecification implements Specification<SubMovementTypeId> {
    FindSubMovementTypeId findSubMovementTypeId;

    public SubMovementTypeIdSpecification(FindSubMovementTypeId inputSearchParams) {
        this.findSubMovementTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SubMovementTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findSubMovementTypeId.getCompanyCodeId() != null && !findSubMovementTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findSubMovementTypeId.getCompanyCodeId()));
        }

        if (findSubMovementTypeId.getPlantId() != null && !findSubMovementTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findSubMovementTypeId.getPlantId()));
        }

        if (findSubMovementTypeId.getWarehouseId() != null && !findSubMovementTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findSubMovementTypeId.getWarehouseId()));
        }

        if (findSubMovementTypeId.getMovementTypeId() != null && !findSubMovementTypeId.getMovementTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("movementTypeId");
            predicates.add(group.in(findSubMovementTypeId.getMovementTypeId()));
        }

        if (findSubMovementTypeId.getSubMovementTypeId() != null && !findSubMovementTypeId.getSubMovementTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("subMovementTypeId");
            predicates.add(group.in(findSubMovementTypeId.getSubMovementTypeId()));
        }

        if (findSubMovementTypeId.getLanguageId() != null && !findSubMovementTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findSubMovementTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
