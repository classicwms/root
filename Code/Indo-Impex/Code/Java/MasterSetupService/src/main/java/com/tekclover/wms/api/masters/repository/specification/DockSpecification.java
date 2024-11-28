package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.dock.Dock;
import com.tekclover.wms.api.masters.model.dock.SearchDock;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class DockSpecification implements Specification<Dock> {

    SearchDock searchDock;

    public DockSpecification(SearchDock inputSearchParams) {
        this.searchDock = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Dock> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchDock.getWarehouseId() != null && !searchDock.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchDock.getWarehouseId()));
        }

        if (searchDock.getCompanyCodeId() != null && !searchDock.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchDock.getCompanyCodeId()));
        }

        if (searchDock.getPlantId() != null && !searchDock.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchDock.getPlantId()));
        }
        if (searchDock.getLanguageId() != null && !searchDock.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchDock.getLanguageId()));
        }
        if (searchDock.getDockId() != null && !searchDock.getDockId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("dockId");
            predicates.add(group.in(searchDock.getDockId()));
        }
        if (searchDock.getDockType() != null && !searchDock.getDockType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("dockType");
            predicates.add(group.in(searchDock.getDockType()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }


}