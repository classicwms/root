package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceheader.FindProformaInvoiceHeader;
import com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceheader.ProformaInvoiceHeader;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ProformaInvoiceHeaderSpecification implements Specification<ProformaInvoiceHeader> {
    FindProformaInvoiceHeader findProformaInvoiceHeader;

    public ProformaInvoiceHeaderSpecification(FindProformaInvoiceHeader inputSearchParams) {
        this.findProformaInvoiceHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ProformaInvoiceHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findProformaInvoiceHeader.getWarehouseId() != null && !findProformaInvoiceHeader.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(findProformaInvoiceHeader.getWarehouseId()));
        }

        if (findProformaInvoiceHeader.getCompanyCodeId() != null && !findProformaInvoiceHeader.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(findProformaInvoiceHeader.getCompanyCodeId()));
        }

        if (findProformaInvoiceHeader.getPlantId() != null && !findProformaInvoiceHeader.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(findProformaInvoiceHeader.getPlantId()));
        }
        if (findProformaInvoiceHeader.getPartnerCode() != null && !findProformaInvoiceHeader.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("partnerCode");
            predicates.add(group.in(findProformaInvoiceHeader.getPartnerCode()));
        }
        if (findProformaInvoiceHeader.getStatusId() != null && !findProformaInvoiceHeader.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("statusId");
            predicates.add(group.in(findProformaInvoiceHeader.getStatusId()));
        }
        if (findProformaInvoiceHeader.getProformaBillNo() != null && !findProformaInvoiceHeader.getProformaBillNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("proformaBillNo");
            predicates.add(group.in(findProformaInvoiceHeader.getProformaBillNo()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}