package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.periodic.FindPeriodicHeader;
import com.almailem.ams.api.connector.model.periodic.FindPeriodicLine;
import com.almailem.ams.api.connector.model.periodic.PeriodicHeader;
import com.almailem.ams.api.connector.model.periodic.PeriodicLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PeriodicLineSpecification implements Specification<PeriodicLine> {

    FindPeriodicLine findPeriodicLine;

    public PeriodicLineSpecification(FindPeriodicLine inputSearchParams) {
        this.findPeriodicLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PeriodicLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPeriodicLine.getPeriodicLineId() != null && !findPeriodicLine.getPeriodicLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("periodicLineId");
            predicates.add(group.in(findPeriodicLine.getPeriodicLineId()));
        }
        if (findPeriodicLine.getCycleCountNo() != null && !findPeriodicLine.getCycleCountNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("cycleCountNo");
            predicates.add(group.in(findPeriodicLine.getCycleCountNo()));
        }
        if (findPeriodicLine.getItemCode() != null && !findPeriodicLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findPeriodicLine.getItemCode()));
        }
        if (findPeriodicLine.getUnitOfMeasure() != null && !findPeriodicLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("unitOfMeasure");
            predicates.add(group.in(findPeriodicLine.getUnitOfMeasure()));
        }
        if (findPeriodicLine.getManufacturerCode() != null && !findPeriodicLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findPeriodicLine.getManufacturerCode()));
        }
        if (findPeriodicLine.getManufacturerName() != null && !findPeriodicLine.getManufacturerName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerName");
            predicates.add(group.in(findPeriodicLine.getManufacturerName()));
        }
        if (findPeriodicLine.getFrozenQty() != null && !findPeriodicLine.getFrozenQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("frozenQty");
            predicates.add(group.in(findPeriodicLine.getFrozenQty()));
        }
        if (findPeriodicLine.getLineNoOfEachItemCode() != null && !findPeriodicLine.getLineNoOfEachItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNoOfEachItemCode");
            predicates.add(group.in(findPeriodicLine.getLineNoOfEachItemCode()));
        }


        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
