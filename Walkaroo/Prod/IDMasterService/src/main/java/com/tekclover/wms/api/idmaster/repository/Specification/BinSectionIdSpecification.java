package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import com.tekclover.wms.api.idmaster.model.aisleid.FindAisleId;
import com.tekclover.wms.api.idmaster.model.binsectionid.BinSectionId;
import com.tekclover.wms.api.idmaster.model.binsectionid.FindBinSectionId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BinSectionIdSpecification implements Specification<BinSectionId> {

    FindBinSectionId findBinSectionId;

    public BinSectionIdSpecification(FindBinSectionId inputSearchParams) {
        this.findBinSectionId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BinSectionId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findBinSectionId.getCompanyCodeId() != null && !findBinSectionId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findBinSectionId.getCompanyCodeId()));
        }

        if (findBinSectionId.getPlantId() != null && !findBinSectionId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findBinSectionId.getPlantId()));
        }

        if (findBinSectionId.getWarehouseId() != null && !findBinSectionId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findBinSectionId.getWarehouseId()));
        }

        if (findBinSectionId.getBinSectionId() != null && !findBinSectionId.getBinSectionId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("binSectionId");
            predicates.add(group.in(findBinSectionId.getBinSectionId()));
        }
        if (findBinSectionId.getLanguageId() != null && !findBinSectionId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findBinSectionId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
