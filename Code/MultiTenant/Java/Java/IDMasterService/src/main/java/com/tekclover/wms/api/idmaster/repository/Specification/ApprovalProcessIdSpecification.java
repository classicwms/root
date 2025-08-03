package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.FindApprovalProcessId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ApprovalProcessIdSpecification  implements Specification<ApprovalProcessId> {

    FindApprovalProcessId findApprovalProcessId;

    public ApprovalProcessIdSpecification(FindApprovalProcessId inputSearchParams) {
        this.findApprovalProcessId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ApprovalProcessId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findApprovalProcessId.getCompanyCodeId() != null && !findApprovalProcessId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findApprovalProcessId.getCompanyCodeId()));
        }

        if (findApprovalProcessId.getPlantId() != null && !findApprovalProcessId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findApprovalProcessId.getPlantId()));
        }

        if (findApprovalProcessId.getWarehouseId() != null && !findApprovalProcessId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findApprovalProcessId.getWarehouseId()));
        }

        if (findApprovalProcessId.getApprovalProcessId() != null && !findApprovalProcessId.getApprovalProcessId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("approvalProcessId");
            predicates.add(group.in(findApprovalProcessId.getApprovalProcessId()));
        }
        if (findApprovalProcessId.getLanguageId() != null && !findApprovalProcessId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findApprovalProcessId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
