package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.salesreturn.FindSalesReturnHeader;
import com.almailem.ams.api.connector.model.salesreturn.FindSalesReturnLine;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnHeader;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SalesReturnLineSpecification implements Specification<SalesReturnLine> {

    FindSalesReturnLine findSalesReturnLine;

    public SalesReturnLineSpecification(FindSalesReturnLine inputSearchParams) {
        this.findSalesReturnLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SalesReturnLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findSalesReturnLine.getSalesReturnLineId() != null && !findSalesReturnLine.getSalesReturnLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("salesReturnLineId");
            predicates.add(group.in(findSalesReturnLine.getSalesReturnLineId()));
        }
        if (findSalesReturnLine.getItemCode() != null && !findSalesReturnLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findSalesReturnLine.getItemCode()));
        }
        if (findSalesReturnLine.getItemDescription() != null && !findSalesReturnLine.getItemDescription().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemDescription");
            predicates.add(group.in(findSalesReturnLine.getItemDescription()));
        }
        if (findSalesReturnLine.getReferenceInvoiceNo() != null && !findSalesReturnLine.getReferenceInvoiceNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("referenceInvoiceNo");
            predicates.add(group.in(findSalesReturnLine.getReferenceInvoiceNo()));
        }
        if (findSalesReturnLine.getManufacturerCode() != null && !findSalesReturnLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findSalesReturnLine.getManufacturerCode()));
        }
        if (findSalesReturnLine.getManufacturerShortName() != null && !findSalesReturnLine.getManufacturerShortName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerShortName");
            predicates.add(group.in(findSalesReturnLine.getManufacturerShortName()));
        }
        if (findSalesReturnLine.getReturnQty() != null && !findSalesReturnLine.getReturnQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("returnQty");
            predicates.add(group.in(findSalesReturnLine.getReturnQty()));
        }
        if (findSalesReturnLine.getUnitOfMeasure() != null && !findSalesReturnLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("unitOfMeasure");
            predicates.add(group.in(findSalesReturnLine.getUnitOfMeasure()));
        }
        if (findSalesReturnLine.getLineNoOfEachItem() != null && !findSalesReturnLine.getLineNoOfEachItem().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNoOfEachItem");
            predicates.add(group.in(findSalesReturnLine.getLineNoOfEachItem()));
        }

        if (findSalesReturnLine.getFromReturnOrderDate() != null && findSalesReturnLine.getToReturnOrderDate() != null) {
            predicates.add(cb.between(root.get("returnOrderDate"), findSalesReturnLine.getFromReturnOrderDate(),
                    findSalesReturnLine.getToReturnOrderDate()));
        }



        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
