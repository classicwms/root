package com.tekclover.wms.api.idmaster.repository.Specification;


import com.tekclover.wms.api.idmaster.model.plantid.FindPlantId;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PlantIdSpecification implements Specification<PlantId> {
    FindPlantId findPlantId;
    public PlantIdSpecification(FindPlantId inputSearchParams) {
        this.findPlantId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PlantId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPlantId.getPlantId() != null && !findPlantId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findPlantId.getPlantId()));
        }

        if (findPlantId.getCompanyCodeId() != null && !findPlantId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findPlantId.getCompanyCodeId()));
        }

        if (findPlantId.getLanguageId() != null && !findPlantId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findPlantId.getLanguageId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
