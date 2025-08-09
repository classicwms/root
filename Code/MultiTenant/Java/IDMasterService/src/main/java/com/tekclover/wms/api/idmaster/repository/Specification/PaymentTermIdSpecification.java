package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.FindPaymentTermId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.PaymentTermId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PaymentTermIdSpecification implements Specification<PaymentTermId> {
    FindPaymentTermId findPaymentTermId;
    public PaymentTermIdSpecification(FindPaymentTermId inputSearchParams) {
        this.findPaymentTermId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PaymentTermId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPaymentTermId.getCompanyCodeId() != null && !findPaymentTermId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findPaymentTermId.getCompanyCodeId()));
        }

        if (findPaymentTermId.getPlantId() != null && !findPaymentTermId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findPaymentTermId.getPlantId()));
        }

        if (findPaymentTermId.getWarehouseId() != null && !findPaymentTermId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findPaymentTermId.getWarehouseId()));
        }

        if (findPaymentTermId.getPaymentTermId() != null && !findPaymentTermId.getPaymentTermId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("paymentTermId");
            predicates.add(group.in(findPaymentTermId.getPaymentTermId()));
        }

        if (findPaymentTermId.getStatusId() != null && !findPaymentTermId.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findPaymentTermId.getStatusId()));
        }
        if (findPaymentTermId.getLanguageId() != null && !findPaymentTermId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findPaymentTermId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
