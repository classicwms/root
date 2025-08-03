package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.handlingunitid.FindHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.HandlingUnitId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class HandlingUnitIdSpecification implements Specification<HandlingUnitId> {
    FindHandlingUnitId findHandlingUnitId;

    public HandlingUnitIdSpecification(FindHandlingUnitId inputSearchParams) {
        this.findHandlingUnitId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<HandlingUnitId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findHandlingUnitId.getCompanyCodeId() != null && !findHandlingUnitId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findHandlingUnitId.getCompanyCodeId()));
        }

        if (findHandlingUnitId.getPlantId() != null && !findHandlingUnitId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findHandlingUnitId.getPlantId()));
        }

        if (findHandlingUnitId.getWarehouseId() != null && !findHandlingUnitId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findHandlingUnitId.getWarehouseId()));
        }

        if (findHandlingUnitId.getHandlingUnitNumber() != null && !findHandlingUnitId.getHandlingUnitNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("handlingUnitNumber");
            predicates.add(group.in(findHandlingUnitId.getHandlingUnitNumber()));
        }
        if (findHandlingUnitId.getLanguageId() != null && !findHandlingUnitId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findHandlingUnitId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
