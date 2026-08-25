package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import com.tekclover.wms.api.idmaster.model.aisleid.FindAisleId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AisledSpecification implements Specification<AisleId> {

    FindAisleId findAisleId;
    public AisledSpecification(FindAisleId inputSearchParams) {
        this.findAisleId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<AisleId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findAisleId.getCompanyCodeId() != null && !findAisleId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findAisleId.getCompanyCodeId()));
        }

        if (findAisleId.getPlantId() != null && !findAisleId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findAisleId.getPlantId()));
        }

        if (findAisleId.getWarehouseId() != null && !findAisleId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findAisleId.getWarehouseId()));
        }

        if (findAisleId.getFloorId() != null && !findAisleId.getFloorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("floorId");
            predicates.add(group.in(findAisleId.getFloorId()));
        }

        if (findAisleId.getStorageSectionId() != null && !findAisleId.getStorageSectionId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageSectionId");
            predicates.add(group.in(findAisleId.getStorageSectionId()));
        }

        if (findAisleId.getAisleId() != null && !findAisleId.getAisleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("aisleId");
            predicates.add(group.in(findAisleId.getAisleId()));
        }
        if (findAisleId.getLanguageId() != null && !findAisleId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findAisleId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }


}
