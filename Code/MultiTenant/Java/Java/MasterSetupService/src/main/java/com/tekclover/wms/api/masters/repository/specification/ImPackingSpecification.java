package com.tekclover.wms.api.masters.repository.specification;


import com.tekclover.wms.api.masters.model.impacking.ImPacking;
import com.tekclover.wms.api.masters.model.impacking.SearchImPacking;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImPackingSpecification implements Specification<ImPacking> {
    SearchImPacking searchImPacking;

    public ImPackingSpecification(SearchImPacking inputSearchParams) {
        this.searchImPacking = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImPacking> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImPacking.getWarehouseId() != null && !searchImPacking.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchImPacking.getWarehouseId()));
        }
        if (searchImPacking.getPackingMaterialNo() != null && !searchImPacking.getPackingMaterialNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("packingMaterialNo");
            predicates.add(group.in(searchImPacking.getPackingMaterialNo()));
        }

        if (searchImPacking.getItemCode() != null && !searchImPacking.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemCode");
            predicates.add(group.in(searchImPacking.getItemCode()));
        }

        if (searchImPacking.getCompanyCodeId() != null && !searchImPacking.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchImPacking.getCompanyCodeId()));
        }

        if (searchImPacking.getLanguageId() != null && !searchImPacking.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchImPacking.getLanguageId()));
        }

        if (searchImPacking.getPlantId() != null && !searchImPacking.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchImPacking.getPlantId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
