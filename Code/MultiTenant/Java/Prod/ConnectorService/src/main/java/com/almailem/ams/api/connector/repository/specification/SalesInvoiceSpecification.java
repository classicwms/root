package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.salesinvoice.FindSalesInvoice;
import com.almailem.ams.api.connector.model.salesinvoice.SalesInvoice;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SalesInvoiceSpecification implements Specification<SalesInvoice> {

    FindSalesInvoice findSalesInvoice;

    public SalesInvoiceSpecification(FindSalesInvoice inputSearchParams) {
        this.findSalesInvoice = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SalesInvoice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findSalesInvoice.getSalesInvoiceId() != null && !findSalesInvoice.getSalesInvoiceId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("salesInvoiceId");
            predicates.add(group.in(findSalesInvoice.getSalesInvoiceId()));
        }
        if (findSalesInvoice.getCompanyCode() != null && !findSalesInvoice.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findSalesInvoice.getCompanyCode()));
        }
        if (findSalesInvoice.getBranchCode() != null && !findSalesInvoice.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findSalesInvoice.getBranchCode()));
        }
        if (findSalesInvoice.getSalesInvoiceNumber() != null && !findSalesInvoice.getSalesInvoiceNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("salesInvoiceNumber");
            predicates.add(group.in(findSalesInvoice.getSalesInvoiceNumber()));
        }
        if (findSalesInvoice.getSalesOrderNumber() != null && !findSalesInvoice.getSalesOrderNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("salesOrderNumber");
            predicates.add(group.in(findSalesInvoice.getSalesOrderNumber()));
        }
        if (findSalesInvoice.getPickListNumber() != null && !findSalesInvoice.getPickListNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("pickListNumber");
            predicates.add(group.in(findSalesInvoice.getPickListNumber()));
        }
        if (findSalesInvoice.getCustomerId() != null && !findSalesInvoice.getCustomerId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("customerId");
            predicates.add(group.in(findSalesInvoice.getCustomerId()));
        }
        if (findSalesInvoice.getFromOrderReceivedOn() != null && findSalesInvoice.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findSalesInvoice.getFromOrderReceivedOn(),
                    findSalesInvoice.getToOrderReceivedOn()));
        }

        if (findSalesInvoice.getFromOrderProcessedOn() != null && findSalesInvoice.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findSalesInvoice.getFromOrderProcessedOn(),
                    findSalesInvoice.getToOrderProcessedOn()));
        }

        if (findSalesInvoice.getProcessedStatusId() != null && !findSalesInvoice.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findSalesInvoice.getProcessedStatusId()));
        }


        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
