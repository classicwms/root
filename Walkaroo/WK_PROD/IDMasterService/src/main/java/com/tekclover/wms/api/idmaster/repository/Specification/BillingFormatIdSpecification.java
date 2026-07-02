package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.FindBillingFormatId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BillingFormatIdSpecification implements Specification<BillingFormatId> {
    FindBillingFormatId findBillingFormatId;

    public BillingFormatIdSpecification(FindBillingFormatId inputSearchParams) {
        this.findBillingFormatId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BillingFormatId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findBillingFormatId.getCompanyCodeId() != null && !findBillingFormatId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findBillingFormatId.getCompanyCodeId()));
        }

        if (findBillingFormatId.getPlantId() != null && !findBillingFormatId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findBillingFormatId.getPlantId()));
        }

        if (findBillingFormatId.getWarehouseId() != null && !findBillingFormatId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findBillingFormatId.getWarehouseId()));
        }

        if (findBillingFormatId.getBillFormatId() != null && !findBillingFormatId.getBillFormatId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("billFormatId");
            predicates.add(group.in(findBillingFormatId.getBillFormatId()));
        }

        if (findBillingFormatId.getLanguageId() != null && !findBillingFormatId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findBillingFormatId.getLanguageId()));
        }
        if (findBillingFormatId.getStatusId() != null && !findBillingFormatId.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findBillingFormatId.getStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
