package com.tekclover.wms.api.outbound.transaction.repository.specification;

import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.FindPickUpHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PickHeaderV2Specification implements Specification<PickupHeaderV2> {

    FindPickUpHeader searchPickupHeader;

    public PickHeaderV2Specification(FindPickUpHeader inputSearchParams) {
        this.searchPickupHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PickupHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();


        if (searchPickupHeader.getPickupNumber() != null && !searchPickupHeader.getPickupNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("pickupNumber");
            predicates.add(group.in(searchPickupHeader.getPickupNumber()));
        }

        if (searchPickupHeader.getCompanyCodeId() != null && !searchPickupHeader.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchPickupHeader.getCompanyCodeId()));
        }

        if (searchPickupHeader.getPlantId() != null && !searchPickupHeader.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchPickupHeader.getPlantId()));
        }

        if (searchPickupHeader.getLanguageId() != null && !searchPickupHeader.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchPickupHeader.getLanguageId()));
        }

        if (searchPickupHeader.getWarehouseId() != null && !searchPickupHeader.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchPickupHeader.getWarehouseId()));
        }

        if (searchPickupHeader.getAssignedPickerId() != null && !searchPickupHeader.getAssignedPickerId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("assignedPickerId");
            predicates.add(group.in(searchPickupHeader.getAssignedPickerId()));
        }

        if (searchPickupHeader.getFromDate() != null && searchPickupHeader.getToDate() != null) {
            predicates.add(cb.between(root.get("pickupCreatedOn"), searchPickupHeader.getFromDate(), searchPickupHeader.getToDate()));

        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
