package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.floorid.FindFloorId;
import com.tekclover.wms.api.idmaster.model.floorid.FloorId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FloorIdSpecification implements Specification<FloorId> {
    FindFloorId findFloorId;
    public FloorIdSpecification(FindFloorId inputSearchParams) {
        this.findFloorId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<FloorId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findFloorId.getCompanyCodeId() != null && !findFloorId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findFloorId.getCompanyCodeId()));
        }

        if (findFloorId.getPlantId() != null && !findFloorId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findFloorId.getPlantId()));
        }

        if (findFloorId.getFloorId() != null && !findFloorId.getFloorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("floorId");
            predicates.add(group.in(findFloorId.getFloorId()));
        }

        if (findFloorId.getWarehouseId() != null && !findFloorId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findFloorId.getWarehouseId()));
        }

        if (findFloorId.getLanguageId() != null && !findFloorId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findFloorId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
