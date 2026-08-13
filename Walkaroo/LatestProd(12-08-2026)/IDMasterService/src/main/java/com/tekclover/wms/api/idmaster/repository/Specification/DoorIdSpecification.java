package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.doorid.DoorId;
import com.tekclover.wms.api.idmaster.model.doorid.FindDoorId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DoorIdSpecification implements Specification<DoorId> {

    FindDoorId findDoorId;

    public DoorIdSpecification(FindDoorId inputSearchParams) {
        this.findDoorId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DoorId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findDoorId.getCompanyCodeId() != null && !findDoorId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findDoorId.getCompanyCodeId()));
        }

        if (findDoorId.getDoorId() != null && !findDoorId.getDoorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("doorId");
            predicates.add(group.in(findDoorId.getDoorId()));
        }
        if (findDoorId.getPlantId() != null && !findDoorId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findDoorId.getPlantId()));
        }
        if (findDoorId.getWarehouseId() != null && !findDoorId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findDoorId.getWarehouseId()));
        }
        if (findDoorId.getLanguageId() != null && !findDoorId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findDoorId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
