package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceline.FindProformaInvoiceLine;
import com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ProformaInvoiceLineSpecification implements Specification<ProformaInvoiceLine> {
    FindProformaInvoiceLine findProformaInvoiceLine;
    public ProformaInvoiceLineSpecification(FindProformaInvoiceLine inputSearchParams) {
        this.findProformaInvoiceLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ProformaInvoiceLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findProformaInvoiceLine.getWarehouseId() != null && !findProformaInvoiceLine.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(findProformaInvoiceLine.getWarehouseId()));
        }

        if (findProformaInvoiceLine.getCompanyCodeId() != null && !findProformaInvoiceLine.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(findProformaInvoiceLine.getCompanyCodeId()));
        }

        if (findProformaInvoiceLine.getPlantId() != null && !findProformaInvoiceLine.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(findProformaInvoiceLine.getPlantId()));
        }
        if (findProformaInvoiceLine.getPartnerCode() != null && !findProformaInvoiceLine.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("partnerCode");
            predicates.add(group.in(findProformaInvoiceLine.getPartnerCode()));
        }
        if (findProformaInvoiceLine.getStatusId() != null && !findProformaInvoiceLine.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("statusId");
            predicates.add(group.in(findProformaInvoiceLine.getStatusId()));
        }
        if (findProformaInvoiceLine.getProformaBillNo() != null && !findProformaInvoiceLine.getProformaBillNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("proformaBillNo");
            predicates.add(group.in(findProformaInvoiceLine.getProformaBillNo()));
        }
        if (findProformaInvoiceLine.getLineNumber() != null && !findProformaInvoiceLine.getLineNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("lineNumber");
            predicates.add(group.in(findProformaInvoiceLine.getLineNumber()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}