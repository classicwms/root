package com.tekclover.wms.api.outbound.transaction.repository.specification;

import com.tekclover.wms.api.outbound.transaction.model.auditlog.AuditLog;
import com.tekclover.wms.api.outbound.transaction.model.auditlog.SearchAuditLog;
import lombok.Data;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class AuditLogSpecification implements Specification<AuditLog> {

    SearchAuditLog searchAuditLog;

    public AuditLogSpecification(SearchAuditLog inputSearchParams) {
        this.searchAuditLog = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<AuditLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchAuditLog.getWarehouseId() != null && !searchAuditLog.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchAuditLog.getWarehouseId()));
        }

        if (searchAuditLog.getCompanyCodeId() != null && !searchAuditLog.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchAuditLog.getCompanyCodeId()));
        }

        if (searchAuditLog.getPlantId() != null && !searchAuditLog.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchAuditLog.getPlantId()));
        }

        if (searchAuditLog.getLanguageId() != null && !searchAuditLog.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchAuditLog.getLanguageId()));
        }
        if (searchAuditLog.getAuditFileNumber() != null && !searchAuditLog.getAuditFileNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("auditFileNumber");
            predicates.add(group.in(searchAuditLog.getAuditFileNumber()));
        }
        if (searchAuditLog.getAuditLogNumber() != null && !searchAuditLog.getAuditLogNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("auditLogNumber");
            predicates.add(group.in(searchAuditLog.getAuditLogNumber()));
        }
        if (searchAuditLog.getObjectName() != null && !searchAuditLog.getObjectName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("objectName");
            predicates.add(group.in(searchAuditLog.getObjectName()));
        }
        if (searchAuditLog.getModifiedTableName() != null && !searchAuditLog.getModifiedTableName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("modifiedTableName");
            predicates.add(group.in(searchAuditLog.getModifiedTableName()));
        }
        if (searchAuditLog.getStartCreatedOn() != null && searchAuditLog.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchAuditLog.getStartCreatedOn(), searchAuditLog.getEndCreatedOn()));
        }
        if (searchAuditLog.getFinancialYear() != null && !searchAuditLog.getFinancialYear().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("financialYear");
            predicates.add(group.in(searchAuditLog.getFinancialYear()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
