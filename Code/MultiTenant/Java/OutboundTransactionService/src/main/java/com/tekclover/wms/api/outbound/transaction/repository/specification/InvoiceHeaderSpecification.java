package com.tekclover.wms.api.outbound.transaction.repository.specification;

import com.tekclover.wms.api.outbound.transaction.model.threepl.invoiceheader.FindInvoiceHeader;
import com.tekclover.wms.api.outbound.transaction.model.threepl.invoiceheader.InvoiceHeader;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InvoiceHeaderSpecification implements Specification<InvoiceHeader> {
    FindInvoiceHeader findInvoiceHeader;

    public InvoiceHeaderSpecification(FindInvoiceHeader inputSearchParams) {
        this.findInvoiceHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InvoiceHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findInvoiceHeader.getWarehouseId() != null && !findInvoiceHeader.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(findInvoiceHeader.getWarehouseId()));
        }

        if (findInvoiceHeader.getCompanyCodeId() != null && !findInvoiceHeader.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(findInvoiceHeader.getCompanyCodeId()));
        }

        if (findInvoiceHeader.getInvoiceNumber() != null && !findInvoiceHeader.getInvoiceNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("invoiceNumber");
            predicates.add(group.in(findInvoiceHeader.getInvoiceNumber()));
        }

        if (findInvoiceHeader.getPlantId() != null && !findInvoiceHeader.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(findInvoiceHeader.getPlantId()));
        }
        if (findInvoiceHeader.getPartnerCode() != null && !findInvoiceHeader.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("partnerCode");
            predicates.add(group.in(findInvoiceHeader.getPartnerCode()));
        }
        if (findInvoiceHeader.getStatusId() != null && !findInvoiceHeader.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("statusId");
            predicates.add(group.in(findInvoiceHeader.getStatusId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
