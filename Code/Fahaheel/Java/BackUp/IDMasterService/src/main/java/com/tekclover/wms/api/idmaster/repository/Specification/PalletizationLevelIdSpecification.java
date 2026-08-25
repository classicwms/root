package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.palletizationlevelid.FindPalletizationLevelId;
import com.tekclover.wms.api.idmaster.model.palletizationlevelid.PalletizationLevelId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PalletizationLevelIdSpecification implements Specification<PalletizationLevelId> {

    FindPalletizationLevelId findPalletizationLevelId;

    public PalletizationLevelIdSpecification(FindPalletizationLevelId inputSearchParams) {
        this.findPalletizationLevelId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PalletizationLevelId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPalletizationLevelId.getCompanyCodeId() != null && !findPalletizationLevelId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findPalletizationLevelId.getCompanyCodeId()));
        }
        if (findPalletizationLevelId.getWarehouseId() != null && !findPalletizationLevelId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findPalletizationLevelId.getWarehouseId()));
        }
        if (findPalletizationLevelId.getPalletizationLevelId() != null && !findPalletizationLevelId.getPalletizationLevelId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("palletizationLevelId");
            predicates.add(group.in(findPalletizationLevelId.getPalletizationLevelId()));
        }
        if (findPalletizationLevelId.getPlantId() != null && !findPalletizationLevelId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findPalletizationLevelId.getPlantId()));
        }
        if (findPalletizationLevelId.getPalletizationLevel() != null && !findPalletizationLevelId.getPalletizationLevel().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("palletizationLevel");
            predicates.add(group.in(findPalletizationLevelId.getPalletizationLevel()));
        }
        if (findPalletizationLevelId.getPalletizationLevelReference() != null && !findPalletizationLevelId.getPalletizationLevelReference().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("palletizationLevelReference");
            predicates.add(group.in(findPalletizationLevelId.getPalletizationLevelReference()));
        }
        if (findPalletizationLevelId.getLanguageId() != null && !findPalletizationLevelId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findPalletizationLevelId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
