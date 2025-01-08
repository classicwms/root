package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.numberrangestoragebin.NumberRangeStorageBin;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.SearchNumberRangeStorageBin;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class NumberRangeStorageBinSpecification implements Specification<NumberRangeStorageBin> {

    SearchNumberRangeStorageBin SearchNumberRangeStorageBin;

    public NumberRangeStorageBinSpecification(SearchNumberRangeStorageBin inputSearchParams) {
        this.SearchNumberRangeStorageBin = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<NumberRangeStorageBin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (SearchNumberRangeStorageBin.getWarehouseId() != null && !SearchNumberRangeStorageBin.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(SearchNumberRangeStorageBin.getWarehouseId()));
        }

        if (SearchNumberRangeStorageBin.getAisleNumber() != null && !SearchNumberRangeStorageBin.getAisleNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("aisleNumber");
            predicates.add(group.in(SearchNumberRangeStorageBin.getAisleNumber()));
        }

        if (SearchNumberRangeStorageBin.getStorageSectionId() != null && !SearchNumberRangeStorageBin.getStorageSectionId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageSectionId");
            predicates.add(group.in(SearchNumberRangeStorageBin.getStorageSectionId()));
        }

        if (SearchNumberRangeStorageBin.getCompanyCodeId() != null && !SearchNumberRangeStorageBin.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(SearchNumberRangeStorageBin.getCompanyCodeId()));
        }

        if (SearchNumberRangeStorageBin.getFloorId() != null && !SearchNumberRangeStorageBin.getFloorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("floorId");
            predicates.add(group.in(SearchNumberRangeStorageBin.getFloorId()));
        }

        if (SearchNumberRangeStorageBin.getRowId() != null && !SearchNumberRangeStorageBin.getRowId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("rowId");
            predicates.add(group.in(SearchNumberRangeStorageBin.getRowId()));
        }

        if (SearchNumberRangeStorageBin.getLanguageId() != null && !SearchNumberRangeStorageBin.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(SearchNumberRangeStorageBin.getLanguageId()));
        }

        if (SearchNumberRangeStorageBin.getPlantId() != null && !SearchNumberRangeStorageBin.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(SearchNumberRangeStorageBin.getPlantId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}