package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.shelfid.FindShelfId;
import com.tekclover.wms.api.idmaster.model.shelfid.ShelfId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ShelfIdSpecification implements Specification<ShelfId> {
    FindShelfId findShelfId;

    public ShelfIdSpecification(FindShelfId inputSearchParams) {
        this.findShelfId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ShelfId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findShelfId.getCompanyCodeId() != null && !findShelfId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findShelfId.getCompanyCodeId()));
        }

        if (findShelfId.getPlantId() != null && !findShelfId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findShelfId.getPlantId()));
        }

        if (findShelfId.getWarehouseId() != null && !findShelfId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findShelfId.getWarehouseId()));
        }

        if (findShelfId.getFloorId() != null && !findShelfId.getFloorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("floorId");
            predicates.add(group.in(findShelfId.getFloorId()));
        }

        if (findShelfId.getStorageSectionId() != null && !findShelfId.getStorageSectionId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageSectionId");
            predicates.add(group.in(findShelfId.getStorageSectionId()));
        }

        if (findShelfId.getRowId() != null && !findShelfId.getRowId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("rowId");
            predicates.add(group.in(findShelfId.getRowId()));
        }
        if (findShelfId.getShelfId() != null && !findShelfId.getShelfId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("shelfId");
            predicates.add(group.in(findShelfId.getShelfId()));
        }
        if (findShelfId.getAisleId() != null && !findShelfId.getAisleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("aisleId");
            predicates.add(group.in(findShelfId.getAisleId()));
        }
        if (findShelfId.getSpanId() != null && !findShelfId.getSpanId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("spanId");
            predicates.add(group.in(findShelfId.getSpanId()));
        }
        if (findShelfId.getLanguageId() != null && !findShelfId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findShelfId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
