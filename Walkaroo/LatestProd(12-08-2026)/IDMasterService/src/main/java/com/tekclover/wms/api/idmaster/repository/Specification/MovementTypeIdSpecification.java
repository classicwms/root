package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.movementtypeid.FindMovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MovementTypeIdSpecification implements Specification<MovementTypeId> {
    FindMovementTypeId findMovementTypeId;

    public MovementTypeIdSpecification(FindMovementTypeId inputSearchParams) {
        this.findMovementTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<MovementTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findMovementTypeId.getCompanyCodeId() != null && !findMovementTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findMovementTypeId.getCompanyCodeId()));
        }
        if (findMovementTypeId.getWarehouseId() != null && !findMovementTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findMovementTypeId.getWarehouseId()));
        }
        if (findMovementTypeId.getMovementTypeId() != null && !findMovementTypeId.getMovementTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("movementTypeId");
            predicates.add(group.in(findMovementTypeId.getMovementTypeId()));
        }
        if (findMovementTypeId.getPlantId() != null && !findMovementTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findMovementTypeId.getPlantId()));
        }
        if (findMovementTypeId.getLanguageId() != null && !findMovementTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findMovementTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
