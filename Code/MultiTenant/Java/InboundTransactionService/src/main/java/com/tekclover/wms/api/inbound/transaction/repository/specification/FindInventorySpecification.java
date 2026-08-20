package com.tekclover.wms.api.inbound.transaction.repository.specification;

import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.FindInventoryV9;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FindInventorySpecification implements Specification<InventoryV2> {

    FindInventoryV9 searchInventory;

    public FindInventorySpecification(FindInventoryV9 inputSearchParams) {
        this.searchInventory = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InventoryV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchInventory.getCompanyCodeId() != null && !searchInventory.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchInventory.getCompanyCodeId()));
        }

        if (searchInventory.getPlantId() != null && !searchInventory.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchInventory.getPlantId()));
        }

        if (searchInventory.getLanguageId() != null && !searchInventory.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchInventory.getLanguageId()));
        }

        if (searchInventory.getWarehouseId() != null && !searchInventory.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchInventory.getWarehouseId()));
        }

        if (searchInventory.getItemCode() != null && !searchInventory.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemCode");
            predicates.add(group.in(searchInventory.getItemCode()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
