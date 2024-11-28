package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.impalletization.ImPalletization;
import com.tekclover.wms.api.masters.model.impalletization.SearchImPalletization;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImPalletizationSpecification implements Specification<ImPalletization> {

    SearchImPalletization searchImPalletization;

    public ImPalletizationSpecification(SearchImPalletization inputSearchParams) {
        this.searchImPalletization = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImPalletization> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImPalletization.getWarehouseId() != null && !searchImPalletization.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchImPalletization.getWarehouseId()));
        }
        if (searchImPalletization.getPalletizationLevel() != null && !searchImPalletization.getPalletizationLevel().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("palletizationLevel");
            predicates.add(group.in(searchImPalletization.getPalletizationLevel()));
        }

        if (searchImPalletization.getItemCode() != null && !searchImPalletization.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchImPalletization.getItemCode()));
        }

        if (searchImPalletization.getCompanyCodeId() != null && !searchImPalletization.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchImPalletization.getCompanyCodeId()));
        }

        if (searchImPalletization.getLanguageId() != null && !searchImPalletization.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchImPalletization.getLanguageId()));
        }

        if (searchImPalletization.getPlantId() != null && !searchImPalletization.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchImPalletization.getPlantId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}