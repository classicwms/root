package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.purchasereturn.FindPurchaseReturnHeader;
import com.almailem.ams.api.connector.model.purchasereturn.FindPurchaseReturnLine;
import com.almailem.ams.api.connector.model.purchasereturn.PurchaseReturnHeader;
import com.almailem.ams.api.connector.model.purchasereturn.PurchaseReturnLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PurchaseReturnLineSpecification implements Specification<PurchaseReturnLine> {

    FindPurchaseReturnLine findPurchaseReturnLine;

    public PurchaseReturnLineSpecification(FindPurchaseReturnLine inputSearchParams) {
        this.findPurchaseReturnLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PurchaseReturnLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPurchaseReturnLine.getPurchaseReturnLineId() != null && !findPurchaseReturnLine.getPurchaseReturnLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("purchaseReturnLineId");
            predicates.add(group.in(findPurchaseReturnLine.getPurchaseReturnLineId()));
        }
        if (findPurchaseReturnLine.getReturnOrderNo() != null && !findPurchaseReturnLine.getReturnOrderNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("returnOrderNo");
            predicates.add(group.in(findPurchaseReturnLine.getReturnOrderNo()));
        }
        if (findPurchaseReturnLine.getLineNoOfEachItemCode() != null && !findPurchaseReturnLine.getLineNoOfEachItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNoOfEachItemCode");
            predicates.add(group.in(findPurchaseReturnLine.getLineNoOfEachItemCode()));
        }
        if (findPurchaseReturnLine.getItemCode() != null && !findPurchaseReturnLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findPurchaseReturnLine.getItemCode()));
        }
        if (findPurchaseReturnLine.getReturnOrderQty() != null && !findPurchaseReturnLine.getReturnOrderQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("returnOrderQty");
            predicates.add(group.in(findPurchaseReturnLine.getReturnOrderQty()));
        }
        if (findPurchaseReturnLine.getUnitOfMeasure() != null && !findPurchaseReturnLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("unitOfMeasure");
            predicates.add(group.in(findPurchaseReturnLine.getUnitOfMeasure()));
        }
        if (findPurchaseReturnLine.getManufacturerCode() != null && !findPurchaseReturnLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findPurchaseReturnLine.getManufacturerCode()));
        }
        if (findPurchaseReturnLine.getManufacturerShortName() != null && !findPurchaseReturnLine.getManufacturerShortName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerShortName");
            predicates.add(group.in(findPurchaseReturnLine.getManufacturerShortName()));
        }




        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
