package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.refdoctypeid.FindRefDocTypeId;
import com.tekclover.wms.api.idmaster.model.refdoctypeid.RefDocTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class RefDocTypeIdSpecification implements Specification<RefDocTypeId> {

    FindRefDocTypeId findRefDocTypeId;

    public RefDocTypeIdSpecification(FindRefDocTypeId inputSearchParams) {
        this.findRefDocTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<RefDocTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findRefDocTypeId.getCompanyCodeId() != null && !findRefDocTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findRefDocTypeId.getCompanyCodeId()));
        }

        if (findRefDocTypeId.getPlantId() != null && !findRefDocTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findRefDocTypeId.getPlantId()));
        }

        if (findRefDocTypeId.getReferenceDocumentTypeId() != null && !findRefDocTypeId.getReferenceDocumentTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("referenceDocumentTypeId");
            predicates.add(group.in(findRefDocTypeId.getReferenceDocumentTypeId()));
        }

        if (findRefDocTypeId.getWarehouseId() != null && !findRefDocTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findRefDocTypeId.getWarehouseId()));
        }

        if (findRefDocTypeId.getLanguageId() != null && !findRefDocTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findRefDocTypeId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
