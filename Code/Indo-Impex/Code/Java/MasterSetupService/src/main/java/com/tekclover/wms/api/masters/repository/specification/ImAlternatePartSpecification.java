package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.imalternateparts.ImAlternatePart;
import com.tekclover.wms.api.masters.model.imalternateparts.SearchImAlternateParts;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImAlternatePartSpecification implements Specification<ImAlternatePart> {

    SearchImAlternateParts searchImAlternateParts;

    public ImAlternatePartSpecification(SearchImAlternateParts inputSearchParams) {
        this.searchImAlternateParts = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImAlternatePart> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImAlternateParts.getWarehouseId() != null && !searchImAlternateParts.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchImAlternateParts.getWarehouseId()));
        }

        if (searchImAlternateParts.getCompanyCodeId() != null && !searchImAlternateParts.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchImAlternateParts.getCompanyCodeId()));
        }

        if (searchImAlternateParts.getPlantId() != null && !searchImAlternateParts.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchImAlternateParts.getPlantId()));
        }

        if (searchImAlternateParts.getItemCode() != null && !searchImAlternateParts.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchImAlternateParts.getItemCode()));
        }
        if (searchImAlternateParts.getAltItemCode() != null && !searchImAlternateParts.getAltItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("altItemCode");
            predicates.add(group.in(searchImAlternateParts.getAltItemCode()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}