package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.uomid.FindUomId;
import com.tekclover.wms.api.idmaster.model.uomid.UomId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class UomIdSpecification implements Specification<UomId> {
    FindUomId findUomId;

    public UomIdSpecification(FindUomId inputSearchParams) {
        this.findUomId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<UomId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findUomId.getCompanyCodeId() != null && !findUomId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findUomId.getCompanyCodeId()));
        }

        if (findUomId.getUomId() != null && !findUomId.getUomId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("uomId");
            predicates.add(group.in(findUomId.getUomId()));
        }

        if (findUomId.getUomType() != null && !findUomId.getUomType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("uomType");
            predicates.add(group.in(findUomId.getUomType()));
        }
        if (findUomId.getLanguageId() != null && !findUomId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findUomId.getLanguageId()));
        }
        if (findUomId.getPlantId() != null && !findUomId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findUomId.getPlantId()));
        }
        if (findUomId.getWarehouseId() != null && !findUomId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findUomId.getWarehouseId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
