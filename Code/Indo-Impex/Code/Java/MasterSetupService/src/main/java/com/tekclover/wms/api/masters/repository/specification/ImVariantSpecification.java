package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.imvariant.ImVariant;
import com.tekclover.wms.api.masters.model.imvariant.SearchImVariant;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImVariantSpecification implements Specification<ImVariant> {

    SearchImVariant searchImVariant;

    public ImVariantSpecification(SearchImVariant inputSearchParams) {
        this.searchImVariant = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImVariant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImVariant.getWarehouseId() != null && !searchImVariant.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchImVariant.getWarehouseId()));
        }

        if (searchImVariant.getVariantType() != null && !searchImVariant.getVariantType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("variantType");
            predicates.add(group.in(searchImVariant.getVariantType()));
        }


        if (searchImVariant.getItemCode() != null && !searchImVariant.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchImVariant.getItemCode()));
        }

        if (searchImVariant.getVariantCode() != null && !searchImVariant.getVariantCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("variantCode");
            predicates.add(group.in(searchImVariant.getVariantCode()));
        }

        if (searchImVariant.getCompanyCodeId() != null && !searchImVariant.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchImVariant.getCompanyCodeId()));
        }

        if (searchImVariant.getLanguageId() != null && !searchImVariant.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchImVariant.getLanguageId()));
        }

        if (searchImVariant.getPlantId() != null && !searchImVariant.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchImVariant.getPlantId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}