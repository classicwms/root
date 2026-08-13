package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.BillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.FindBillingFrequencyId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BillingFrequencyIdSpecification implements Specification<BillingFrequencyId> {

    FindBillingFrequencyId findBillingFrequencyId;

    public BillingFrequencyIdSpecification(FindBillingFrequencyId inputSearchParams) {
        this.findBillingFrequencyId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BillingFrequencyId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findBillingFrequencyId.getCompanyCodeId() != null && !findBillingFrequencyId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findBillingFrequencyId.getCompanyCodeId()));
        }

        if (findBillingFrequencyId.getPlantId() != null && !findBillingFrequencyId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findBillingFrequencyId.getPlantId()));
        }

        if (findBillingFrequencyId.getWarehouseId() != null && !findBillingFrequencyId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findBillingFrequencyId.getWarehouseId()));
        }

        if (findBillingFrequencyId.getBillFrequencyId() != null && !findBillingFrequencyId.getBillFrequencyId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("billFrequencyId");
            predicates.add(group.in(findBillingFrequencyId.getBillFrequencyId()));
        }
        if (findBillingFrequencyId.getLanguageId() != null && !findBillingFrequencyId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findBillingFrequencyId.getLanguageId()));
        }

        if (findBillingFrequencyId.getStatusId() != null && !findBillingFrequencyId.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findBillingFrequencyId.getStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
