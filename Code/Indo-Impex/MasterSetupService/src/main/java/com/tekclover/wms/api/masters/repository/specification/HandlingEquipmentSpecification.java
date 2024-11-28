package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.handlingequipment.HandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.SearchHandlingEquipment;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class HandlingEquipmentSpecification implements Specification<HandlingEquipment> {

    SearchHandlingEquipment searchHandlingEquipment;

    public HandlingEquipmentSpecification(SearchHandlingEquipment inputSearchParams) {
        this.searchHandlingEquipment = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<HandlingEquipment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchHandlingEquipment.getWarehouseId() != null && !searchHandlingEquipment.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchHandlingEquipment.getWarehouseId()));
        }
        if (searchHandlingEquipment.getCompanyCodeId() != null && !searchHandlingEquipment.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchHandlingEquipment.getCompanyCodeId()));
        }

        if (searchHandlingEquipment.getPlantId() != null && !searchHandlingEquipment.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchHandlingEquipment.getPlantId()));
        }
        if (searchHandlingEquipment.getLanguageId() != null && !searchHandlingEquipment.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchHandlingEquipment.getLanguageId()));
        }

        if (searchHandlingEquipment.getHandlingEquipmentId() != null && !searchHandlingEquipment.getHandlingEquipmentId().isEmpty()) {
            final Path<Group> group = root.<Group>get("handlingEquipmentId");
            predicates.add(group.in(searchHandlingEquipment.getHandlingEquipmentId()));
        }

        if (searchHandlingEquipment.getCategory() != null && !searchHandlingEquipment.getCategory().isEmpty()) {
            final Path<Group> group = root.<Group>get("category");
            predicates.add(group.in(searchHandlingEquipment.getCategory()));
        }

        if (searchHandlingEquipment.getHandlingUnit() != null && !searchHandlingEquipment.getHandlingUnit().isEmpty()) {
            final Path<Group> group = root.<Group>get("handlingUnit");
            predicates.add(group.in(searchHandlingEquipment.getHandlingUnit()));
        }

        if (searchHandlingEquipment.getStatusId() != null && !searchHandlingEquipment.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchHandlingEquipment.getStatusId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}