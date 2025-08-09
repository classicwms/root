package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.transferout.FindTransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.FindTransferOutLine;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TransferOutLineSpecification implements Specification<TransferOutLine> {

    FindTransferOutLine findTransferOutLine;

    public TransferOutLineSpecification(FindTransferOutLine inputSearchParams) {
        this.findTransferOutLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<TransferOutLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findTransferOutLine.getTransferOutLineId() != null && !findTransferOutLine.getTransferOutLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOutLineId");
            predicates.add(group.in(findTransferOutLine.getTransferOutLineId()));
        }
        if (findTransferOutLine.getTransferOrderNumber() != null && !findTransferOutLine.getTransferOrderNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderNumber");
            predicates.add(group.in(findTransferOutLine.getTransferOrderNumber()));
        }
        if (findTransferOutLine.getLineNumberOfEachItem() != null && !findTransferOutLine.getLineNumberOfEachItem().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNumberOfEachItem");
            predicates.add(group.in(findTransferOutLine.getLineNumberOfEachItem()));
        }
        if (findTransferOutLine.getItemCode() != null && !findTransferOutLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findTransferOutLine.getItemCode()));
        }
        if (findTransferOutLine.getItemDescription() != null && !findTransferOutLine.getItemDescription().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemDescription");
            predicates.add(group.in(findTransferOutLine.getItemDescription()));
        }
        if (findTransferOutLine.getTransferOrderQty() != null && !findTransferOutLine.getTransferOrderQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderQty");
            predicates.add(group.in(findTransferOutLine.getTransferOrderQty()));
        }
        if (findTransferOutLine.getUnitOfMeasure() != null && !findTransferOutLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("unitOfMeasure");
            predicates.add(group.in(findTransferOutLine.getUnitOfMeasure()));
        }
        if (findTransferOutLine.getManufacturerCode() != null && !findTransferOutLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findTransferOutLine.getManufacturerCode()));
        }
        if (findTransferOutLine.getManufacturerShortName() != null && !findTransferOutLine.getManufacturerShortName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerShortName");
            predicates.add(group.in(findTransferOutLine.getManufacturerShortName()));
        }
        if (findTransferOutLine.getTransferOrderQty() != null && !findTransferOutLine.getTransferOrderQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderQty");
            predicates.add(group.in(findTransferOutLine.getTransferOrderQty()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
