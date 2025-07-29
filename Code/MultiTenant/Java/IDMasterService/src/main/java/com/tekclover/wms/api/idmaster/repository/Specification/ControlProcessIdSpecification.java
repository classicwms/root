package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.controlprocessid.ControlProcessId;
import com.tekclover.wms.api.idmaster.model.controlprocessid.FindControlProcessId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ControlProcessIdSpecification implements Specification<ControlProcessId> {
    FindControlProcessId findControlProcessId;

    public ControlProcessIdSpecification(FindControlProcessId inputSearchParams) {
        this.findControlProcessId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ControlProcessId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findControlProcessId.getCompanyCodeId() != null && !findControlProcessId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findControlProcessId.getCompanyCodeId()));
        }

        if (findControlProcessId.getPlantId() != null && !findControlProcessId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findControlProcessId.getPlantId()));
        }

        if (findControlProcessId.getWarehouseId() != null && !findControlProcessId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findControlProcessId.getWarehouseId()));
        }

        if (findControlProcessId.getControlProcessId() != null && !findControlProcessId.getControlProcessId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("controlProcessId");
            predicates.add(group.in(findControlProcessId.getControlProcessId()));
        }
        if (findControlProcessId.getLanguageId() != null && !findControlProcessId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findControlProcessId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
