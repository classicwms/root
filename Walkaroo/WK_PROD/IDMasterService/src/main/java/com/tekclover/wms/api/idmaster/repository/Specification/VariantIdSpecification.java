package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import com.tekclover.wms.api.idmaster.model.aisleid.FindAisleId;
import com.tekclover.wms.api.idmaster.model.varientid.FindVariantId;
import com.tekclover.wms.api.idmaster.model.varientid.VariantId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class VariantIdSpecification implements Specification<VariantId> {
    FindVariantId findVariantId;

    public VariantIdSpecification(FindVariantId inputSearchParams) {
        this.findVariantId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<VariantId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findVariantId.getCompanyCodeId() != null && !findVariantId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findVariantId.getCompanyCodeId()));
        }

        if (findVariantId.getPlantId() != null && !findVariantId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findVariantId.getPlantId()));
        }

        if (findVariantId.getWarehouseId() != null && !findVariantId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findVariantId.getWarehouseId()));
        }

        if (findVariantId.getVariantCode() != null && !findVariantId.getVariantCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("variantCode");
            predicates.add(group.in(findVariantId.getVariantCode()));
        }

        if (findVariantId.getVariantSubCode() != null && !findVariantId.getVariantSubCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("variantSubCode");
            predicates.add(group.in(findVariantId.getVariantSubCode()));
        }

        if (findVariantId.getLanguageId() != null && !findVariantId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findVariantId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
