package com.tekclover.wms.api.inbound.transaction.repository.specification;

import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.FindPalletIdAssignment;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PalletIdAssignment;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PalletIdAssignmentSpecification implements Specification<PalletIdAssignment> {

    FindPalletIdAssignment findPalletIdAssignment;

    public PalletIdAssignmentSpecification(FindPalletIdAssignment inputSearchParams) {
        this.findPalletIdAssignment = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PalletIdAssignment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (findPalletIdAssignment.getCompanyCodeId() != null && !findPalletIdAssignment.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(findPalletIdAssignment.getCompanyCodeId()));
        }
        if (findPalletIdAssignment.getPlantId() != null && !findPalletIdAssignment.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(findPalletIdAssignment.getPlantId()));
        }
        if (findPalletIdAssignment.getLanguageId() != null && !findPalletIdAssignment.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(findPalletIdAssignment.getLanguageId()));
        }
        if (findPalletIdAssignment.getWarehouseId() != null && !findPalletIdAssignment.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(findPalletIdAssignment.getWarehouseId()));
        }
        if (findPalletIdAssignment.getPalletId() != null && !findPalletIdAssignment.getPalletId().isEmpty()) {
            final Path<Group> group = root.<Group>get("palletId");
            predicates.add(group.in(findPalletIdAssignment.getPalletId()));
        }
        if (findPalletIdAssignment.getPaId() != null && !findPalletIdAssignment.getPaId().isEmpty()) {
            final Path<Group> group = root.<Group>get("paId");
            predicates.add(group.in(findPalletIdAssignment.getPaId()));
        }
        if (findPalletIdAssignment.getAssignedUserId() != null && !findPalletIdAssignment.getAssignedUserId().isEmpty()) {
            final Path<Group> group = root.<Group>get("assignedUserId");
            predicates.add(group.in(findPalletIdAssignment.getAssignedUserId()));
        }
        if (findPalletIdAssignment.getStatusId() != null && !findPalletIdAssignment.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(findPalletIdAssignment.getStatusId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
