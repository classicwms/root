package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.threepl.pricelistassignment.FindPriceListAssignment;
import com.tekclover.wms.api.masters.model.threepl.pricelistassignment.PriceListAssignment;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PriceListAssignmentSpecification implements Specification<PriceListAssignment> {
    FindPriceListAssignment priceListAssignment;

    public PriceListAssignmentSpecification(FindPriceListAssignment inputSearchParams) {
        this.priceListAssignment = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PriceListAssignment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (priceListAssignment.getWarehouseId() != null && !priceListAssignment.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(priceListAssignment.getWarehouseId()));
        }

        if (priceListAssignment.getCompanyCodeId() != null && !priceListAssignment.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(priceListAssignment.getCompanyCodeId()));
        }

        if (priceListAssignment.getPlantId() != null && !priceListAssignment.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(priceListAssignment.getPlantId()));
        }
        if (priceListAssignment.getPriceListId() != null && !priceListAssignment.getPriceListId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("priceListId");
            predicates.add(group.in(priceListAssignment.getPriceListId()));
        }
        if (priceListAssignment.getPartnerCode() != null && !priceListAssignment.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("partnerCode");
            predicates.add(group.in(priceListAssignment.getPartnerCode()));
        }

        if (priceListAssignment.getLanguageId() != null && !priceListAssignment.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(priceListAssignment.getLanguageId()));
        }

        if (priceListAssignment.getStatusId() != null && !priceListAssignment.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(priceListAssignment.getStatusId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}