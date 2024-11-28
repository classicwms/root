package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.spanid.FindSpanId;
import com.tekclover.wms.api.idmaster.model.spanid.SpanId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SpanIdSpecification implements Specification<SpanId> {
    FindSpanId findSpanId;

    public SpanIdSpecification(FindSpanId inputSearchParams) {
        this.findSpanId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SpanId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findSpanId.getCompanyCodeId() != null && !findSpanId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findSpanId.getCompanyCodeId()));
        }

        if (findSpanId.getPlantId() != null && !findSpanId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findSpanId.getPlantId()));
        }

        if (findSpanId.getWarehouseId() != null && !findSpanId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findSpanId.getWarehouseId()));
        }

        if (findSpanId.getFloorId() != null && !findSpanId.getFloorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("floorId");
            predicates.add(group.in(findSpanId.getFloorId()));
        }

        if (findSpanId.getStorageSectionId() != null && !findSpanId.getStorageSectionId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageSectionId");
            predicates.add(group.in(findSpanId.getStorageSectionId()));
        }

        if (findSpanId.getRowId() != null && !findSpanId.getRowId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("rowId");
            predicates.add(group.in(findSpanId.getRowId()));
        }
        if (findSpanId.getAisleId() != null && !findSpanId.getAisleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("aisleId");
            predicates.add(group.in(findSpanId.getAisleId()));
        }
        if (findSpanId.getSpanId() != null && !findSpanId.getSpanId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("spanId");
            predicates.add(group.in(findSpanId.getSpanId()));
        }
        if (findSpanId.getLanguageId() != null && !findSpanId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findSpanId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
