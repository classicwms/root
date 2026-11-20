package com.tekclover.wms.api.idmaster.repository.Specification;


import com.tekclover.wms.api.idmaster.model.workcenterid.FindWorkCenterId;
import com.tekclover.wms.api.idmaster.model.workcenterid.WorkCenterId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class WorkCenterIdSpecification implements Specification<WorkCenterId> {

    FindWorkCenterId findWorkCenterId;
    public WorkCenterIdSpecification(FindWorkCenterId inputSearchParams) {
        this.findWorkCenterId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<WorkCenterId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findWorkCenterId.getWarehouseId() != null && !findWorkCenterId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findWorkCenterId.getWarehouseId()));
        }

        if (findWorkCenterId.getWorkCenterId() != null && !findWorkCenterId.getWorkCenterId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("workCenterId");
            predicates.add(group.in(findWorkCenterId.getWorkCenterId()));
        }

        if (findWorkCenterId.getPlantId() != null && !findWorkCenterId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findWorkCenterId.getPlantId()));
        }

        if (findWorkCenterId.getCompanyCodeId() != null && !findWorkCenterId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findWorkCenterId.getCompanyCodeId()));
        }
        if (findWorkCenterId.getLanguageId() != null && !findWorkCenterId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findWorkCenterId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
