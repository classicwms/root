package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.supplierinvoice.SearchSupplierInvoiceHeader;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceHeader;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;


import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SupplierInvoiceHeaderSpecification implements Specification<SupplierInvoiceHeader> {

    SearchSupplierInvoiceHeader searchSupplierInvoiceHeader;

    public SupplierInvoiceHeaderSpecification(SearchSupplierInvoiceHeader inputSearchParams){
        this.searchSupplierInvoiceHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SupplierInvoiceHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchSupplierInvoiceHeader.getCompanyCode() != null && !searchSupplierInvoiceHeader.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCode");
            predicates.add(group.in(searchSupplierInvoiceHeader.getCompanyCode()));
        }

        if (searchSupplierInvoiceHeader.getBranchCode() != null && !searchSupplierInvoiceHeader.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("branchCode");
            predicates.add(group.in(searchSupplierInvoiceHeader.getBranchCode()));
        }

        if (searchSupplierInvoiceHeader.getSupplierInvoiceNo() != null && !searchSupplierInvoiceHeader.getSupplierInvoiceNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("supplierInvoiceNo");
            predicates.add(group.in(searchSupplierInvoiceHeader.getSupplierInvoiceNo()));
        }

        if (searchSupplierInvoiceHeader.getSupplierInvoiceHeaderId() != null && !searchSupplierInvoiceHeader.getSupplierInvoiceHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("supplierInvoiceHeaderId");
            predicates.add(group.in(searchSupplierInvoiceHeader.getSupplierInvoiceHeaderId()));
        }
        if (searchSupplierInvoiceHeader.getFromOrderProcessedOn() != null && searchSupplierInvoiceHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), searchSupplierInvoiceHeader.getFromOrderProcessedOn(),
                    searchSupplierInvoiceHeader.getToOrderProcessedOn()));
        }
        if (searchSupplierInvoiceHeader.getFromOrderReceivedOn() != null && searchSupplierInvoiceHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), searchSupplierInvoiceHeader.getFromOrderReceivedOn(),
                    searchSupplierInvoiceHeader.getToOrderReceivedOn()));
        }

        if (searchSupplierInvoiceHeader.getProcessedStatusId() != null && !searchSupplierInvoiceHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("processedStatusId");
            predicates.add(group.in(searchSupplierInvoiceHeader.getProcessedStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}



