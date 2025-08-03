package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.stockreceipt.SearchStockReceiptLine;
import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptLine;
import com.almailem.ams.api.connector.model.supplierinvoice.SearchSupplierInvoiceHeader;
import com.almailem.ams.api.connector.model.supplierinvoice.SearchSupplierInvoiceLine;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceHeader;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StockReceiptLineSpecification implements Specification<StockReceiptLine> {

    SearchStockReceiptLine searchStockReceiptLine;

    public StockReceiptLineSpecification(SearchStockReceiptLine inputSearchParams){
        this.searchStockReceiptLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StockReceiptLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchStockReceiptLine.getCompanyCode() != null && !searchStockReceiptLine.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCode");
            predicates.add(group.in(searchStockReceiptLine.getCompanyCode()));
        }

        if (searchStockReceiptLine.getBranchCode() != null && !searchStockReceiptLine.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("branchCode");
            predicates.add(group.in(searchStockReceiptLine.getBranchCode()));
        }
        if (searchStockReceiptLine.getReceiptNo() != null && !searchStockReceiptLine.getReceiptNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("receiptNo");
            predicates.add(group.in(searchStockReceiptLine.getReceiptNo()));
        }
        if (searchStockReceiptLine.getLineNoForEachItem() != null && !searchStockReceiptLine.getLineNoForEachItem().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("lineNoForEachItem");
            predicates.add(group.in(searchStockReceiptLine.getLineNoForEachItem()));
        }
        if (searchStockReceiptLine.getItemCode() != null && !searchStockReceiptLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemCode");
            predicates.add(group.in(searchStockReceiptLine.getItemCode()));
        }
        if (searchStockReceiptLine.getItemDescription() != null && !searchStockReceiptLine.getItemDescription().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemDescription");
            predicates.add(group.in(searchStockReceiptLine.getItemDescription()));
        }
        if (searchStockReceiptLine.getManufacturerShortName() != null && !searchStockReceiptLine.getManufacturerShortName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("manufacturerShortName");
            predicates.add(group.in(searchStockReceiptLine.getManufacturerShortName()));
        }
        if (searchStockReceiptLine.getManufacturerCode() != null && !searchStockReceiptLine.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("manufacturerCode");
            predicates.add(group.in(searchStockReceiptLine.getManufacturerCode()));
        }
        if (searchStockReceiptLine.getUnitOfMeasure() != null && !searchStockReceiptLine.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("unitOfMeasure");
            predicates.add(group.in(searchStockReceiptLine.getUnitOfMeasure()));
        }

        if (searchStockReceiptLine.getStockReceiptLineId() != null && !searchStockReceiptLine.getStockReceiptLineId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("stockReceiptLineId");
            predicates.add(group.in(searchStockReceiptLine.getStockReceiptLineId()));
        }

        if (searchStockReceiptLine.getFromReceiptDate() != null && searchStockReceiptLine.getToReceiptDate() != null) {
            predicates.add(cb.between(root.get("receiptDate"), searchStockReceiptLine.getFromReceiptDate(),
                    searchStockReceiptLine.getToReceiptDate()));
        }


        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
