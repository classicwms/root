package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.threepl.invoiceline.FindInvoiceLine;
import com.tekclover.wms.api.transaction.model.threepl.invoiceline.InvoiceLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InvoiceLineSpecification implements Specification<InvoiceLine> {
    FindInvoiceLine findInvoiceLine;

    public InvoiceLineSpecification(FindInvoiceLine inputSearchParams) {
        this.findInvoiceLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InvoiceLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findInvoiceLine.getWarehouseId() != null && !findInvoiceLine.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findInvoiceLine.getWarehouseId()));
        }

        if (findInvoiceLine.getCompanyCodeId() != null && !findInvoiceLine.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findInvoiceLine.getCompanyCodeId()));
        }

        if (findInvoiceLine.getInvoiceNumber() != null && !findInvoiceLine.getInvoiceNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("invoiceNumber");
            predicates.add(group.in(findInvoiceLine.getInvoiceNumber()));
        }

        if (findInvoiceLine.getPlantId() != null && !findInvoiceLine.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findInvoiceLine.getPlantId()));
        }
        if (findInvoiceLine.getPartnerCode() != null && !findInvoiceLine.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("partnerCode");
            predicates.add(group.in(findInvoiceLine.getPartnerCode()));
        }
        if (findInvoiceLine.getStatusId() != null && !findInvoiceLine.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findInvoiceLine.getStatusId()));
        }
        if (findInvoiceLine.getLineNumber() != null && !findInvoiceLine.getLineNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNumber");
            predicates.add(group.in(findInvoiceLine.getLineNumber()));
        }
        if (findInvoiceLine.getInvoiceHeaderId() != null && !findInvoiceLine.getInvoiceHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("invoiceHeaderId");
            predicates.add(group.in(findInvoiceLine.getInvoiceHeaderId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}