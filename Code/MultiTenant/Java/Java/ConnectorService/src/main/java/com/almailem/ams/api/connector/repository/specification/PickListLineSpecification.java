package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.picklist.FindPickListHeader;
import com.almailem.ams.api.connector.model.picklist.FindPickListLine;
import com.almailem.ams.api.connector.model.picklist.PickListHeader;
import com.almailem.ams.api.connector.model.picklist.PickListLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PickListLineSpecification implements Specification<PickListLine> {

    FindPickListLine findPickListLine;

    public PickListLineSpecification(FindPickListLine inputSearchParams) {
        this.findPickListLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PickListLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPickListLine.getPickListLineId() != null && !findPickListLine.getPickListLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("pickListLineId");
            predicates.add(group.in(findPickListLine.getPickListLineId()));
        }
        if (findPickListLine.getSalesOrderNo() != null && !findPickListLine.getSalesOrderNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("salesOrderNo");
            predicates.add(group.in(findPickListLine.getSalesOrderNo()));
        }
        if (findPickListLine.getPickListNo() != null && !findPickListLine.getPickListNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("pickListNo");
            predicates.add(group.in(findPickListLine.getPickListNo()));
        }
        if (findPickListLine.getItemCode() != null && !findPickListLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findPickListLine.getItemCode()));
        }
        if (findPickListLine.getItemDescription() != null && !findPickListLine.getItemDescription().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemDescription");
            predicates.add(group.in(findPickListLine.getItemDescription()));
        }
        if (findPickListLine.getPickListQty() != null && !findPickListLine.getPickListQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("pickListQty");
            predicates.add(group.in(findPickListLine.getPickListQty()));
        }
        if (findPickListLine.getUnitOfMeasure() != null && !findPickListLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("unitOfMeasure");
            predicates.add(group.in(findPickListLine.getUnitOfMeasure()));
        }
        if (findPickListLine.getManufacturerCode() != null && !findPickListLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findPickListLine.getManufacturerCode()));
        }
        if (findPickListLine.getManufacturerShortName() != null && !findPickListLine.getManufacturerShortName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerShortName");
            predicates.add(group.in(findPickListLine.getManufacturerShortName()));
        }
        if (findPickListLine.getLineNumberOfEachItem() != null && !findPickListLine.getLineNumberOfEachItem().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNumberOfEachItem");
            predicates.add(group.in(findPickListLine.getLineNumberOfEachItem()));
        }

            return cb.and(predicates.toArray(new Predicate[]{}));
        }

    }
