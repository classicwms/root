package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import com.tekclover.wms.api.idmaster.model.aisleid.FindAisleId;
import com.tekclover.wms.api.idmaster.model.approvalid.ApprovalId;
import com.tekclover.wms.api.idmaster.model.approvalid.FindApprovalId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ApprovalIdSpecification implements Specification<ApprovalId> {
    FindApprovalId findApprovalId;

    public ApprovalIdSpecification(FindApprovalId inputSearchParams) {
        this.findApprovalId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ApprovalId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findApprovalId.getCompanyCodeId() != null && !findApprovalId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findApprovalId.getCompanyCodeId()));
        }

        if (findApprovalId.getPlantId() != null && !findApprovalId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findApprovalId.getPlantId()));
        }

        if (findApprovalId.getWarehouseId() != null && !findApprovalId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findApprovalId.getWarehouseId()));
        }

        if (findApprovalId.getApprovalId() != null && !findApprovalId.getApprovalId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("approvalId");
            predicates.add(group.in(findApprovalId.getApprovalId()));
        }

        if (findApprovalId.getApprovalLevel() != null && !findApprovalId.getApprovalLevel().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("approvalLevel");
            predicates.add(group.in(findApprovalId.getApprovalLevel()));
        }

        if (findApprovalId.getApprovalProcessId() != null && !findApprovalId.getApprovalProcessId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("approvalProcessId");
            predicates.add(group.in(findApprovalId.getApprovalProcessId()));
        }
        if (findApprovalId.getApproverCode() != null && !findApprovalId.getApproverCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("approverCode");
            predicates.add(group.in(findApprovalId.getApproverCode()));
        }
        if (findApprovalId.getApproverName() != null && !findApprovalId.getApproverName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("approverName");
            predicates.add(group.in(findApprovalId.getApproverName()));
        }
        if (findApprovalId.getLanguageId() != null && !findApprovalId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findApprovalId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
