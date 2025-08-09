package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.rowid.FindRowId;
import com.tekclover.wms.api.idmaster.model.rowid.RowId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class RowIdSpecification implements Specification<RowId> {
    FindRowId findRowId;

    public RowIdSpecification(FindRowId inputSearchParams) {
        this.findRowId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<RowId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findRowId.getCompanyCodeId() != null && !findRowId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findRowId.getCompanyCodeId()));
        }

        if (findRowId.getPlantId() != null && !findRowId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findRowId.getPlantId()));
        }

        if (findRowId.getWarehouseId() != null && !findRowId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findRowId.getWarehouseId()));
        }

        if (findRowId.getFloorId() != null && !findRowId.getFloorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("floorId");
            predicates.add(group.in(findRowId.getFloorId()));
        }

        if (findRowId.getStorageSectionId() != null && !findRowId.getStorageSectionId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageSectionId");
            predicates.add(group.in(findRowId.getStorageSectionId()));
        }

        if (findRowId.getRowId() != null && !findRowId.getRowId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("rowId");
            predicates.add(group.in(findRowId.getRowId()));
        }

        if (findRowId.getRowNumber() != null && !findRowId.getRowNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("rowNumber");
            predicates.add(group.in(findRowId.getRowNumber()));
        }

        if (findRowId.getAisleId() != null && !findRowId.getAisleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("aisleId");
            predicates.add(group.in(findRowId.getAisleId()));
        }

        if (findRowId.getLanguageId() != null && !findRowId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findRowId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
