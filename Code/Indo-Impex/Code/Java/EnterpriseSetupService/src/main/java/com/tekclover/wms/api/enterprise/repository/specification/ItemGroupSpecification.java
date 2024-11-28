package com.tekclover.wms.api.enterprise.repository.specification;

import com.tekclover.wms.api.enterprise.model.itemgroup.ItemGroup;
import com.tekclover.wms.api.enterprise.model.itemgroup.SearchItemGroup;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ItemGroupSpecification implements Specification<ItemGroup> {

    SearchItemGroup searchItemGroup;

    public ItemGroupSpecification(SearchItemGroup inputSearchParams) {
        this.searchItemGroup = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ItemGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchItemGroup.getWarehouseId() != null && !searchItemGroup.getWarehouseId().isEmpty()) {
            predicates.add(cb.equal(root.get("warehouseId"), searchItemGroup.getWarehouseId()));
        }
        if (searchItemGroup.getCompanyId() != null && !searchItemGroup.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchItemGroup.getCompanyId()));
        }
        if (searchItemGroup.getPlantId() != null && !searchItemGroup.getPlantId().isEmpty()) {
            predicates.add(cb.equal(root.get("plantId"), searchItemGroup.getPlantId()));
        }

        if (searchItemGroup.getItemTypeId() != null && searchItemGroup.getItemTypeId().longValue() != 0) {
            predicates.add(cb.equal(root.get("itemTypeId"), searchItemGroup.getItemTypeId()));
        }

        if (searchItemGroup.getItemGroupId() != null && searchItemGroup.getItemGroupId().longValue() != 0) {
            predicates.add(cb.equal(root.get("itemGroupId"), searchItemGroup.getItemGroupId()));
        }

        if (searchItemGroup.getLanguageId() != null && !searchItemGroup.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchItemGroup.getLanguageId()));
        }

        if (searchItemGroup.getSubItemGroupId() != null && searchItemGroup.getSubItemGroupId().longValue() != 0) {
            predicates.add(cb.equal(root.get("subItemGroupId"), searchItemGroup.getSubItemGroupId()));
        }

        if (searchItemGroup.getCreatedBy() != null && !searchItemGroup.getCreatedBy().isEmpty()) {
            predicates.add(cb.equal(root.get("createdBy"), searchItemGroup.getCreatedBy()));
        }

        if (searchItemGroup.getStartCreatedOn() != null && searchItemGroup.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchItemGroup.getStartCreatedOn(), searchItemGroup.getEndCreatedOn()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}