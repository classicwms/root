package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.numberrangeitem.NumberRangeItem;
import com.tekclover.wms.api.masters.model.numberrangeitem.SearchNumberRangeItem;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class NumberRangeItemSpecification implements Specification<NumberRangeItem> {

    SearchNumberRangeItem searchNumberRangeItem;

    public NumberRangeItemSpecification(SearchNumberRangeItem inputSearchParams) {
        this.searchNumberRangeItem = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<NumberRangeItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchNumberRangeItem.getWarehouseId() != null && !searchNumberRangeItem.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchNumberRangeItem.getWarehouseId()));
        }

        if (searchNumberRangeItem.getSequenceNo() != null && !searchNumberRangeItem.getSequenceNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("sequenceNo");
            predicates.add(group.in(searchNumberRangeItem.getSequenceNo()));
        }

        if (searchNumberRangeItem.getItemTypeId() != null && !searchNumberRangeItem.getItemTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemTypeId");
            predicates.add(group.in(searchNumberRangeItem.getItemTypeId()));
        }

        if (searchNumberRangeItem.getCompanyCodeId() != null && !searchNumberRangeItem.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchNumberRangeItem.getCompanyCodeId()));
        }

        if (searchNumberRangeItem.getLanguageId() != null && !searchNumberRangeItem.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchNumberRangeItem.getLanguageId()));
        }

        if (searchNumberRangeItem.getPlantId() != null && !searchNumberRangeItem.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchNumberRangeItem.getPlantId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
