package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.itemtypeid.FindItemTypeId;
import com.tekclover.wms.api.idmaster.model.itemtypeid.ItemTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ItemTypeIdSpecification implements Specification<ItemTypeId> {
    FindItemTypeId findItemTypeId;
    public ItemTypeIdSpecification(FindItemTypeId inputSearchParams) {
        this.findItemTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ItemTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findItemTypeId.getCompanyCodeId() != null && !findItemTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findItemTypeId.getCompanyCodeId()));
        }

        if (findItemTypeId.getPlantId() != null && !findItemTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findItemTypeId.getPlantId()));
        }

        if (findItemTypeId.getWarehouseId() != null && !findItemTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findItemTypeId.getWarehouseId()));
        }

        if (findItemTypeId.getItemTypeId() != null && !findItemTypeId.getItemTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemTypeId");
            predicates.add(group.in(findItemTypeId.getItemTypeId()));
        }

        if (findItemTypeId.getItemType() != null && !findItemTypeId.getItemType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemType");
            predicates.add(group.in(findItemTypeId.getItemType()));
        }
        if (findItemTypeId.getLanguageId() != null && !findItemTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findItemTypeId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
