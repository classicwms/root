package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.sublevelid.FindSubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.SubLevelId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SubLevelIdSpecification implements Specification<SubLevelId> {
    FindSubLevelId findSubLevelId;

    public SubLevelIdSpecification(FindSubLevelId inputSearchParams) {
        this.findSubLevelId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SubLevelId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findSubLevelId.getCompanyCodeId() != null && !findSubLevelId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findSubLevelId.getCompanyCodeId()));
        }

        if (findSubLevelId.getPlantId() != null && !findSubLevelId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findSubLevelId.getPlantId()));
        }

        if (findSubLevelId.getWarehouseId() != null && !findSubLevelId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findSubLevelId.getWarehouseId()));
        }

        if (findSubLevelId.getSubLevelId() != null && !findSubLevelId.getSubLevelId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("subLevelId");
            predicates.add(group.in(findSubLevelId.getSubLevelId()));
        }

        if (findSubLevelId.getLevelId() != null && !findSubLevelId.getLevelId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("levelId");
            predicates.add(group.in(findSubLevelId.getLevelId()));
        }

        if (findSubLevelId.getLanguageId() != null && !findSubLevelId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findSubLevelId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
