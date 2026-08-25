package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.perpetual.FindPerpetualHeader;
import com.almailem.ams.api.connector.model.perpetual.FindPerpetualLine;
import com.almailem.ams.api.connector.model.perpetual.PerpetualHeader;
import com.almailem.ams.api.connector.model.perpetual.PerpetualLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PerpetualLineSpecification implements Specification<PerpetualLine> {

    FindPerpetualLine findPerpetualLine;

    public PerpetualLineSpecification(FindPerpetualLine inputSearchParams) {
        this.findPerpetualLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PerpetualLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPerpetualLine.getPerpetualLineId() != null && !findPerpetualLine.getPerpetualLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("perpetualLineId");
            predicates.add(group.in(findPerpetualLine.getPerpetualLineId()));
        }
        if (findPerpetualLine.getCycleCountNo() != null && !findPerpetualLine.getCycleCountNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("cycleCountNo");
            predicates.add(group.in(findPerpetualLine.getCycleCountNo()));
        }
        if (findPerpetualLine.getItemCode() != null && !findPerpetualLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findPerpetualLine.getItemCode()));
        }
        if (findPerpetualLine.getUnitOfMeasure() != null && !findPerpetualLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("unitOfMeasure");
            predicates.add(group.in(findPerpetualLine.getUnitOfMeasure()));
        }
        if (findPerpetualLine.getManufacturerName() != null && !findPerpetualLine.getManufacturerName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerName");
            predicates.add(group.in(findPerpetualLine.getManufacturerName()));
        }
        if (findPerpetualLine.getManufacturerCode() != null && !findPerpetualLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findPerpetualLine.getManufacturerCode()));
        }
        if (findPerpetualLine.getFrozenQty() != null && !findPerpetualLine.getFrozenQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("frozenQty");
            predicates.add(group.in(findPerpetualLine.getFrozenQty()));
        }
        if (findPerpetualLine.getLineNoOfEachItemCode() != null && !findPerpetualLine.getLineNoOfEachItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNoOfEachItemCode");
            predicates.add(group.in(findPerpetualLine.getLineNoOfEachItemCode()));
        }


        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
