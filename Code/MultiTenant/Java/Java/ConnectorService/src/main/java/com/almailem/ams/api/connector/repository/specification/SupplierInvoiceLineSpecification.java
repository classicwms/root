package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.supplierinvoice.SearchSupplierInvoiceHeader;
import com.almailem.ams.api.connector.model.supplierinvoice.SearchSupplierInvoiceLine;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceHeader;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierInvoiceLineSpecification implements Specification<SupplierInvoiceLine> {
    SearchSupplierInvoiceLine searchSupplierInvoiceLine;

    public SupplierInvoiceLineSpecification(SearchSupplierInvoiceLine inputSearchParams){
        this.searchSupplierInvoiceLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SupplierInvoiceLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchSupplierInvoiceLine.getCompanyCode() != null && !searchSupplierInvoiceLine.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCode");
            predicates.add(group.in(searchSupplierInvoiceLine.getCompanyCode()));
        }

        if (searchSupplierInvoiceLine.getBranchCode() != null && !searchSupplierInvoiceLine.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("branchCode");
            predicates.add(group.in(searchSupplierInvoiceLine.getBranchCode()));
        }

        if (searchSupplierInvoiceLine.getSupplierInvoiceNo() != null && !searchSupplierInvoiceLine.getSupplierInvoiceNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("supplierInvoiceNo");
            predicates.add(group.in(searchSupplierInvoiceLine.getSupplierInvoiceNo()));
        }

        if (searchSupplierInvoiceLine.getSupplierInvoiceLineId() != null && !searchSupplierInvoiceLine.getSupplierInvoiceLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("supplierInvoiceLineId");
            predicates.add(group.in(searchSupplierInvoiceLine.getSupplierInvoiceLineId()));
        }

        if (searchSupplierInvoiceLine.getLineNoForEachItem() != null && !searchSupplierInvoiceLine.getLineNoForEachItem().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("lineNoForEachItem");
            predicates.add(group.in(searchSupplierInvoiceLine.getLineNoForEachItem()));
        }

        if (searchSupplierInvoiceLine.getItemCode() != null && !searchSupplierInvoiceLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemCode");
            predicates.add(group.in(searchSupplierInvoiceLine.getItemCode()));
        }
        if (searchSupplierInvoiceLine.getItemDescription() != null && !searchSupplierInvoiceLine.getItemDescription().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemDescription");
            predicates.add(group.in(searchSupplierInvoiceLine.getItemDescription()));
        }
        if (searchSupplierInvoiceLine.getContainerNo() != null && !searchSupplierInvoiceLine.getContainerNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("containerNo");
            predicates.add(group.in(searchSupplierInvoiceLine.getContainerNo()));
        }
        if (searchSupplierInvoiceLine.getSupplierCode() != null && !searchSupplierInvoiceLine.getSupplierCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("supplierCode");
            predicates.add(group.in(searchSupplierInvoiceLine.getSupplierCode()));
        }
        if (searchSupplierInvoiceLine.getSupplierPartNo() != null && !searchSupplierInvoiceLine.getSupplierPartNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("supplierPartNo");
            predicates.add(group.in(searchSupplierInvoiceLine.getSupplierPartNo()));
        }
        if (searchSupplierInvoiceLine.getManufacturerShortName() != null && !searchSupplierInvoiceLine.getManufacturerShortName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("manufacturerShortName");
            predicates.add(group.in(searchSupplierInvoiceLine.getManufacturerShortName()));
        }
        if (searchSupplierInvoiceLine.getManufacturerCode() != null && !searchSupplierInvoiceLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("manufacturerCode");
            predicates.add(group.in(searchSupplierInvoiceLine.getManufacturerCode()));
        }
        if (searchSupplierInvoiceLine.getPurchaseOrderNo() != null && !searchSupplierInvoiceLine.getPurchaseOrderNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("purchaseOrderNo");
            predicates.add(group.in(searchSupplierInvoiceLine.getPurchaseOrderNo()));
        }
        if (searchSupplierInvoiceLine.getUnitOfMeasure() != null && !searchSupplierInvoiceLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("unitOfMeasure");
            predicates.add(group.in(searchSupplierInvoiceLine.getUnitOfMeasure()));
        }
        if (searchSupplierInvoiceLine.getInvoiceQty() != null && !searchSupplierInvoiceLine.getInvoiceQty().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("invoiceQty");
            predicates.add(group.in(searchSupplierInvoiceLine.getInvoiceQty()));
        }
        if (searchSupplierInvoiceLine.getFromInvoiceDate() != null && searchSupplierInvoiceLine.getToInvoiceDate() != null) {
            predicates.add(cb.between(root.get("invoiceDate"), searchSupplierInvoiceLine.getFromInvoiceDate(),
                    searchSupplierInvoiceLine.getToInvoiceDate()));
        }


        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
