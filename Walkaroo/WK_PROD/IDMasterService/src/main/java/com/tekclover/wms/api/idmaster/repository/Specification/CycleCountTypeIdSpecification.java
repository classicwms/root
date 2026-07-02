package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.CycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.FindCycleCountTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CycleCountTypeIdSpecification implements Specification<CycleCountTypeId> {
    FindCycleCountTypeId findCycleCountTypeId;

    public CycleCountTypeIdSpecification(FindCycleCountTypeId inputSearchParams) {
        this.findCycleCountTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<CycleCountTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findCycleCountTypeId.getCompanyCodeId() != null && !findCycleCountTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findCycleCountTypeId.getCompanyCodeId()));
        }

        if (findCycleCountTypeId.getPlantId() != null && !findCycleCountTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findCycleCountTypeId.getPlantId()));
        }

        if (findCycleCountTypeId.getWarehouseId() != null && !findCycleCountTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findCycleCountTypeId.getWarehouseId()));
        }

        if (findCycleCountTypeId.getCycleCountTypeId() != null && !findCycleCountTypeId.getCycleCountTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("cycleCountTypeId");
            predicates.add(group.in(findCycleCountTypeId.getCycleCountTypeId()));
        }

        if (findCycleCountTypeId.getLanguageId() != null && !findCycleCountTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findCycleCountTypeId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
