package com.almailem.ams.api.connector.repository.specification;


import com.almailem.ams.api.connector.model.transferin.SearchTransferInHeader;
import com.almailem.ams.api.connector.model.transferin.SearchTransferInLine;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TransferInLineSpecification implements Specification<TransferInLine> {

    SearchTransferInLine searchTransferInLine;

    public TransferInLineSpecification(SearchTransferInLine inputSearchParams) {
        this.searchTransferInLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<TransferInLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchTransferInLine.getTransferInLineId() != null && !searchTransferInLine.getTransferInLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferInLineId");
            predicates.add(group.in(searchTransferInLine.getTransferInLineId()));
        }

        if (searchTransferInLine.getTransferOrderNo() != null && !searchTransferInLine.getTransferOrderNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderNo");
            predicates.add(group.in(searchTransferInLine.getTransferOrderNo()));
        }

        if (searchTransferInLine.getLineNoOfEachItem() != null && !searchTransferInLine.getLineNoOfEachItem().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNoOfEachItem");
            predicates.add(group.in(searchTransferInLine.getLineNoOfEachItem()));
        }

        if (searchTransferInLine.getItemCode() != null && !searchTransferInLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchTransferInLine.getItemCode()));
        }
        if (searchTransferInLine.getItemDescription() != null && !searchTransferInLine.getItemDescription().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemDescription");
            predicates.add(group.in(searchTransferInLine.getItemDescription()));
        }
        if (searchTransferInLine.getTransferQty() != null && !searchTransferInLine.getTransferQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferQty");
            predicates.add(group.in(searchTransferInLine.getTransferQty()));
        }
        if (searchTransferInLine.getUnitOfMeasure() != null && !searchTransferInLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("unitOfMeasure");
            predicates.add(group.in(searchTransferInLine.getUnitOfMeasure()));
        }
        if (searchTransferInLine.getManufacturerCode() != null && !searchTransferInLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(searchTransferInLine.getManufacturerCode()));
        }
        if (searchTransferInLine.getManufacturerShortName() != null && !searchTransferInLine.getManufacturerShortName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerShortName");
            predicates.add(group.in(searchTransferInLine.getManufacturerShortName()));
        }



        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}

