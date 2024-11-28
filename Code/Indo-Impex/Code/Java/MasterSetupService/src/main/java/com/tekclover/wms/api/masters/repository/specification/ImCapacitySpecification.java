package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.imcapacity.ImCapacity;
import com.tekclover.wms.api.masters.model.imcapacity.SearchImCapacity;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class ImCapacitySpecification implements Specification<ImCapacity> {

    SearchImCapacity searchImCapacity;

    public ImCapacitySpecification(SearchImCapacity inputSearchParams) {
        this.searchImCapacity = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImCapacity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImCapacity.getWarehouseId() != null && !searchImCapacity.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchImCapacity.getWarehouseId()));
        }

        if (searchImCapacity.getItemCode() != null && !searchImCapacity.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchImCapacity.getItemCode()));
        }

        if (searchImCapacity.getCompanyCodeId() != null && !searchImCapacity.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchImCapacity.getCompanyCodeId()));
        }

        if (searchImCapacity.getLanguageId() != null && !searchImCapacity.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchImCapacity.getLanguageId()));
        }
        if (searchImCapacity.getPlantId() != null && !searchImCapacity.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchImCapacity.getPlantId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}