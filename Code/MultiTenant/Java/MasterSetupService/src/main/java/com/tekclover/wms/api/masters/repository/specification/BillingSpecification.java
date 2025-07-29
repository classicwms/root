package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.threepl.billing.Billing;
import com.tekclover.wms.api.masters.model.threepl.billing.FindBilling;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BillingSpecification implements Specification<Billing> {
    FindBilling findBilling;

    public BillingSpecification(FindBilling inputSearchParams) {
        this.findBilling = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Billing> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findBilling.getWarehouseId() != null && !findBilling.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(findBilling.getWarehouseId()));
        }

        if (findBilling.getCompanyCodeId() != null && !findBilling.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(findBilling.getCompanyCodeId()));
        }

        if (findBilling.getModuleId() != null && !findBilling.getModuleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("moduleId");
            predicates.add(group.in(findBilling.getModuleId()));
        }

        if (findBilling.getPlantId() != null && !findBilling.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(findBilling.getPlantId()));
        }
        if (findBilling.getPartnerCode() != null && !findBilling.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("partnerCode");
            predicates.add(group.in(findBilling.getPartnerCode()));
        }
        if (findBilling.getStatusId() != null && !findBilling.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("statusId");
            predicates.add(group.in(findBilling.getStatusId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
