package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.warehousetypeid.FindWarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.WarehouseTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class WarehouseTypeIdSpecification implements Specification<WarehouseTypeId> {
    FindWarehouseTypeId findWarehouseTypeId;

    public WarehouseTypeIdSpecification(FindWarehouseTypeId inputSearchParams) {
        this.findWarehouseTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<WarehouseTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findWarehouseTypeId.getWarehouseId() != null && !findWarehouseTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findWarehouseTypeId.getWarehouseId()));
        }

        if (findWarehouseTypeId.getWarehouseTypeId() != null && !findWarehouseTypeId.getWarehouseTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseTypeId");
            predicates.add(group.in(findWarehouseTypeId.getWarehouseTypeId()));
        }

        if (findWarehouseTypeId.getPlantId() != null && !findWarehouseTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findWarehouseTypeId.getPlantId()));
        }

        if (findWarehouseTypeId.getCompanyCodeId() != null && !findWarehouseTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findWarehouseTypeId.getCompanyCodeId()));
        }
        if (findWarehouseTypeId.getLanguageId() != null && !findWarehouseTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findWarehouseTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
