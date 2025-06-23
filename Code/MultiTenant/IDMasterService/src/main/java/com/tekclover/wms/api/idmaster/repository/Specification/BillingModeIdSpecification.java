package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.BillingModeId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.FindBillingModeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BillingModeIdSpecification implements Specification<BillingModeId> {

    FindBillingModeId findBillingModeId;

    public BillingModeIdSpecification(FindBillingModeId inputSearchParams) {
        this.findBillingModeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BillingModeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findBillingModeId.getCompanyCodeId() != null && !findBillingModeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findBillingModeId.getCompanyCodeId()));
        }

        if (findBillingModeId.getPlantId() != null && !findBillingModeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findBillingModeId.getPlantId()));
        }

        if (findBillingModeId.getWarehouseId() != null && !findBillingModeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findBillingModeId.getWarehouseId()));
        }

        if (findBillingModeId.getBillModeId() != null && !findBillingModeId.getBillModeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("billModeId");
            predicates.add(group.in(findBillingModeId.getBillModeId()));
        }

        if (findBillingModeId.getStatusId() != null && !findBillingModeId.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findBillingModeId.getStatusId()));
        }
        if (findBillingModeId.getLanguageId() != null && !findBillingModeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findBillingModeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
