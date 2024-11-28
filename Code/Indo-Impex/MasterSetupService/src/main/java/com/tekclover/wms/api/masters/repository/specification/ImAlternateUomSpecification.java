package com.tekclover.wms.api.masters.repository.specification;


import com.tekclover.wms.api.masters.model.imalternateuom.ImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.SearchImAlternateUom;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImAlternateUomSpecification implements Specification<ImAlternateUom> {

    SearchImAlternateUom searchImAlternateUom;

    public ImAlternateUomSpecification(SearchImAlternateUom inputSearchParams) {
        this.searchImAlternateUom = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImAlternateUom> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImAlternateUom.getWarehouseId() != null && !searchImAlternateUom.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchImAlternateUom.getWarehouseId()));
        }

        if (searchImAlternateUom.getCompanyCodeId() != null && !searchImAlternateUom.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchImAlternateUom.getCompanyCodeId()));
        }

        if (searchImAlternateUom.getPlantId() != null && !searchImAlternateUom.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchImAlternateUom.getPlantId()));
        }

        if (searchImAlternateUom.getItemCode() != null && !searchImAlternateUom.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchImAlternateUom.getItemCode()));
        }
        if (searchImAlternateUom.getAlternateUom() != null && !searchImAlternateUom.getAlternateUom().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("alternateUom");
            predicates.add(group.in(searchImAlternateUom.getAlternateUom()));
        }
        if (searchImAlternateUom.getUomId() != null && !searchImAlternateUom.getUomId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("uomId");
            predicates.add(group.in(searchImAlternateUom.getUomId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}