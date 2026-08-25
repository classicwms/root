package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.floorid.FindFloorId;
import com.tekclover.wms.api.idmaster.model.floorid.FloorId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.FindHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.HandlingEquipmentId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class HandlingEquipmentIdSpecification implements Specification<HandlingEquipmentId> {
    FindHandlingEquipmentId findHandlingEquipmentId;
    public HandlingEquipmentIdSpecification(FindHandlingEquipmentId inputSearchParams) {
        this.findHandlingEquipmentId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<HandlingEquipmentId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findHandlingEquipmentId.getCompanyCodeId() != null && !findHandlingEquipmentId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findHandlingEquipmentId.getCompanyCodeId()));
        }

        if (findHandlingEquipmentId.getPlantId() != null && !findHandlingEquipmentId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findHandlingEquipmentId.getPlantId()));
        }

        if (findHandlingEquipmentId.getWarehouseId() != null && !findHandlingEquipmentId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findHandlingEquipmentId.getWarehouseId()));
        }

        if (findHandlingEquipmentId.getHandlingEquipmentNumber() != null && !findHandlingEquipmentId.getHandlingEquipmentNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("handlingEquipmentNumber");
            predicates.add(group.in(findHandlingEquipmentId.getHandlingEquipmentNumber()));
        }
        if (findHandlingEquipmentId.getLanguageId() != null && !findHandlingEquipmentId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findHandlingEquipmentId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
