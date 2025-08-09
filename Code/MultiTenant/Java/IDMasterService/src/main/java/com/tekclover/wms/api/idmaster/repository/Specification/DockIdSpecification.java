package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.dockid.DockId;
import com.tekclover.wms.api.idmaster.model.dockid.FindDockId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DockIdSpecification implements Specification<DockId> {
    FindDockId findDockId;

    public DockIdSpecification(FindDockId inputSearchParams) {
        this.findDockId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DockId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findDockId.getCompanyCodeId() != null && !findDockId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findDockId.getCompanyCodeId()));
        }
        if (findDockId.getDockId() != null && !findDockId.getDockId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("dockId");
            predicates.add(group.in(findDockId.getDockId()));
        }
        if (findDockId.getPlantId() != null && !findDockId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findDockId.getPlantId()));
        }
        if (findDockId.getWarehouseId() != null && !findDockId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findDockId.getWarehouseId()));
        }
        if (findDockId.getLanguageId() != null && !findDockId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findDockId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
