package com.tekclover.wms.api.inbound.transaction.repository.specification;


import com.tekclover.wms.api.inbound.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.inbound.transaction.model.errorlog.FindErrorLog;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ErrorLogSpecification implements Specification<ErrorLog> {

    FindErrorLog findErrorLog;

    public ErrorLogSpecification(FindErrorLog inputSearchParams) {
        this.findErrorLog = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ErrorLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (findErrorLog.getCompanyCodeId() != null && !findErrorLog.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findErrorLog.getCompanyCodeId()));
        }
        if (findErrorLog.getLanguageId() != null && !findErrorLog.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findErrorLog.getLanguageId()));
        }
        if (findErrorLog.getWarehouseId() != null && !findErrorLog.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findErrorLog.getWarehouseId()));
        }
        if (findErrorLog.getPlantId() != null && !findErrorLog.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findErrorLog.getPlantId()));
        }
        if (findErrorLog.getOrderId() != null && !findErrorLog.getOrderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("orderId");
            predicates.add(group.in(findErrorLog.getOrderId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}



