package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.itemgroupid.FindItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.ItemGroupId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ItemGroupIdSpecification implements Specification<ItemGroupId> {
    FindItemGroupId findItemGroupId;

    public ItemGroupIdSpecification(FindItemGroupId inputSearchParams) {
        this.findItemGroupId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ItemGroupId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findItemGroupId.getCompanyCodeId() != null && !findItemGroupId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findItemGroupId.getCompanyCodeId()));
        }

        if (findItemGroupId.getPlantId() != null && !findItemGroupId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findItemGroupId.getPlantId()));
        }

        if (findItemGroupId.getWarehouseId() != null && !findItemGroupId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findItemGroupId.getWarehouseId()));
        }

        if (findItemGroupId.getItemGroupId() != null && !findItemGroupId.getItemGroupId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemGroupId");
            predicates.add(group.in(findItemGroupId.getItemGroupId()));
        }

        if (findItemGroupId.getItemTypeId() != null && !findItemGroupId.getItemTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemTypeId");
            predicates.add(group.in(findItemGroupId.getItemTypeId()));
        }

        if (findItemGroupId.getItemGroup() != null && !findItemGroupId.getItemGroup().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemGroup");
            predicates.add(group.in(findItemGroupId.getItemGroup()));
        }
        if (findItemGroupId.getLanguageId() != null && !findItemGroupId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findItemGroupId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
