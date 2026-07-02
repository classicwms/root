package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.FindSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.SpecialStockIndicatorId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SpecialStockIndicatorIdSpecification implements Specification<SpecialStockIndicatorId> {
    FindSpecialStockIndicatorId findSpecialStockIndicatorId;

    public SpecialStockIndicatorIdSpecification(FindSpecialStockIndicatorId inputSearchParams) {
        this.findSpecialStockIndicatorId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SpecialStockIndicatorId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findSpecialStockIndicatorId.getCompanyCodeId() != null && !findSpecialStockIndicatorId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findSpecialStockIndicatorId.getCompanyCodeId()));
        }

        if (findSpecialStockIndicatorId.getPlantId() != null && !findSpecialStockIndicatorId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findSpecialStockIndicatorId.getPlantId()));
        }

        if (findSpecialStockIndicatorId.getWarehouseId() != null && !findSpecialStockIndicatorId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findSpecialStockIndicatorId.getWarehouseId()));
        }

        if (findSpecialStockIndicatorId.getSpecialStockIndicatorId() != null && !findSpecialStockIndicatorId.getSpecialStockIndicatorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("specialStockIndicatorId");
            predicates.add(group.in(findSpecialStockIndicatorId.getSpecialStockIndicatorId()));
        }

        if (findSpecialStockIndicatorId.getStockTypeId() != null && !findSpecialStockIndicatorId.getStockTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("stockTypeId");
            predicates.add(group.in(findSpecialStockIndicatorId.getStockTypeId()));
        }
        if (findSpecialStockIndicatorId.getLanguageId() != null && !findSpecialStockIndicatorId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findSpecialStockIndicatorId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
