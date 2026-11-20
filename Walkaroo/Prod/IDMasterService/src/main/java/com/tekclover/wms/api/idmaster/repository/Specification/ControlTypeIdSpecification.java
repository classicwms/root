package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.controltypeid.ControlTypeId;
import com.tekclover.wms.api.idmaster.model.controltypeid.FindControlTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ControlTypeIdSpecification implements Specification<ControlTypeId> {
    FindControlTypeId findControlTypeId;

    public ControlTypeIdSpecification(FindControlTypeId inputSearchParams) {
        this.findControlTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ControlTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findControlTypeId.getCompanyCodeId() != null && !findControlTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findControlTypeId.getCompanyCodeId()));
        }

        if (findControlTypeId.getPlantId() != null && !findControlTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findControlTypeId.getPlantId()));
        }

        if (findControlTypeId.getWarehouseId() != null && !findControlTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findControlTypeId.getWarehouseId()));
        }

        if (findControlTypeId.getControlTypeId() != null && !findControlTypeId.getControlTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("controlTypeId");
            predicates.add(group.in(findControlTypeId.getControlTypeId()));
        }
        if (findControlTypeId.getLanguageId() != null && !findControlTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findControlTypeId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
