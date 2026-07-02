package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import com.tekclover.wms.api.idmaster.model.aisleid.FindAisleId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.FindSubItemGroupId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.SubItemGroupId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SubItemGroupIdSpecification implements Specification<SubItemGroupId> {
    FindSubItemGroupId findSubItemGroupId;

    public SubItemGroupIdSpecification(FindSubItemGroupId inputSearchParams) {
        this.findSubItemGroupId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SubItemGroupId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findSubItemGroupId.getCompanyCodeId() != null && !findSubItemGroupId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findSubItemGroupId.getCompanyCodeId()));
        }

        if (findSubItemGroupId.getPlantId() != null && !findSubItemGroupId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findSubItemGroupId.getPlantId()));
        }

        if (findSubItemGroupId.getWarehouseId() != null && !findSubItemGroupId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findSubItemGroupId.getWarehouseId()));
        }

        if (findSubItemGroupId.getItemGroupId() != null && !findSubItemGroupId.getItemGroupId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemGroupId");
            predicates.add(group.in(findSubItemGroupId.getItemGroupId()));
        }

        if (findSubItemGroupId.getSubItemGroupId() != null && !findSubItemGroupId.getSubItemGroupId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("subItemGroupId");
            predicates.add(group.in(findSubItemGroupId.getSubItemGroupId()));
        }

        if (findSubItemGroupId.getSubItemGroup() != null && !findSubItemGroupId.getSubItemGroup().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("subItemGroup");
            predicates.add(group.in(findSubItemGroupId.getSubItemGroup()));
        }
        if (findSubItemGroupId.getItemTypeId() != null && !findSubItemGroupId.getItemTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemTypeId");
            predicates.add(group.in(findSubItemGroupId.getItemTypeId()));
        }
        if (findSubItemGroupId.getLanguageId() != null && !findSubItemGroupId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findSubItemGroupId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
