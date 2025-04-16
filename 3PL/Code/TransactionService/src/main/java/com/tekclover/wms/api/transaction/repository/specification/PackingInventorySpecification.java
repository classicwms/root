package com.tekclover.wms.api.transaction.repository.specification;


import com.tekclover.wms.api.transaction.model.outbound.packing.FindPackingInventory;
import com.tekclover.wms.api.transaction.model.outbound.packing.PackingInventory;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PackingInventorySpecification implements Specification<PackingInventory> {


    FindPackingInventory findPackingInventory;

    public PackingInventorySpecification(FindPackingInventory inputSearchParams) {
        this.findPackingInventory = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PackingInventory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPackingInventory.getPlantId() != null && !findPackingInventory.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findPackingInventory.getPlantId()));
        }

        if (findPackingInventory.getCompanyId() != null && !findPackingInventory.getCompanyId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyId");
            predicates.add(group.in(findPackingInventory.getCompanyId()));
        }

        if (findPackingInventory.getLanguageId() != null && !findPackingInventory.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findPackingInventory.getLanguageId()));
        }

        if (findPackingInventory.getWarehouseId() != null && !findPackingInventory.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findPackingInventory.getWarehouseId()));
        }

        if (findPackingInventory.getPackingMaterialNo() != null && !findPackingInventory.getPackingMaterialNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("packingMaterialNo");
            predicates.add(group.in(findPackingInventory.getPackingMaterialNo()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
