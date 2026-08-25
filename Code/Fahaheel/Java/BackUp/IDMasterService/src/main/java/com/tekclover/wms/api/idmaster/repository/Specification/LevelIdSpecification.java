package com.tekclover.wms.api.idmaster.repository.Specification;


import com.tekclover.wms.api.idmaster.model.levelid.FindLevelId;
import com.tekclover.wms.api.idmaster.model.levelid.LevelId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class LevelIdSpecification implements Specification<LevelId> {
    FindLevelId findLevelId;
    public LevelIdSpecification(FindLevelId inputSearchParams) {
        this.findLevelId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<LevelId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findLevelId.getCompanyCodeId() != null && !findLevelId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findLevelId.getCompanyCodeId()));
        }
        if (findLevelId.getWarehouseId() != null && !findLevelId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findLevelId.getWarehouseId()));
        }
        if (findLevelId.getPlantId() != null && !findLevelId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findLevelId.getPlantId()));
        }
        if (findLevelId.getLevelId() != null && !findLevelId.getLevelId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("levelId");
            predicates.add(group.in(findLevelId.getLevelId()));
        }
        if (findLevelId.getLevelReference() != null && !findLevelId.getLevelReference().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("levelReference");
            predicates.add(group.in(findLevelId.getLevelReference()));
        }
        if (findLevelId.getLanguageId() != null && !findLevelId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findLevelId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
