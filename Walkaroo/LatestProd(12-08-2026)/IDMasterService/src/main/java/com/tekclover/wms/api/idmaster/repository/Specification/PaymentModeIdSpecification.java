package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid.FindPaymentModeId;
import com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid.PaymentModeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PaymentModeIdSpecification implements Specification<PaymentModeId> {
    FindPaymentModeId findPaymentModeId;

    public PaymentModeIdSpecification(FindPaymentModeId inputSearchParams) {
        this.findPaymentModeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PaymentModeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPaymentModeId.getCompanyCodeId() != null && !findPaymentModeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findPaymentModeId.getCompanyCodeId()));
        }

        if (findPaymentModeId.getPlantId() != null && !findPaymentModeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findPaymentModeId.getPlantId()));
        }

        if (findPaymentModeId.getWarehouseId() != null && !findPaymentModeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findPaymentModeId.getWarehouseId()));
        }

        if (findPaymentModeId.getPaymentModeId() != null && !findPaymentModeId.getPaymentModeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("paymentModeId");
            predicates.add(group.in(findPaymentModeId.getPaymentModeId()));
        }

        if (findPaymentModeId.getStatusId() != null && !findPaymentModeId.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findPaymentModeId.getStatusId()));
        }
        if (findPaymentModeId.getLanguageId() != null && !findPaymentModeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findPaymentModeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
