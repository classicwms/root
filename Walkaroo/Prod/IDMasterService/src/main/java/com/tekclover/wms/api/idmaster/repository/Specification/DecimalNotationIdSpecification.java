package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.decimalnotationid.DecimalNotationId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.FindDecimalNotationId;
import lombok.Data;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DecimalNotationIdSpecification implements Specification<DecimalNotationId> {
    FindDecimalNotationId findDecimalNotationId;

    public DecimalNotationIdSpecification(FindDecimalNotationId inputSearchParams) {
        this.findDecimalNotationId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DecimalNotationId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findDecimalNotationId.getCompanyCodeId() != null && !findDecimalNotationId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findDecimalNotationId.getCompanyCodeId()));
        }

        if (findDecimalNotationId.getDecimalNotationId() != null && !findDecimalNotationId.getDecimalNotationId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("decimalNotationId");
            predicates.add(group.in(findDecimalNotationId.getDecimalNotationId()));
        }
        if (findDecimalNotationId.getPlantId() != null && !findDecimalNotationId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findDecimalNotationId.getPlantId()));
        }
        if (findDecimalNotationId.getWarehouseId() != null && !findDecimalNotationId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findDecimalNotationId.getWarehouseId()));
        }
        if (findDecimalNotationId.getDecimalNotation() != null && !findDecimalNotationId.getDecimalNotation().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("decimalNotation");
            predicates.add(group.in(findDecimalNotationId.getDecimalNotation()));
        }
        if (findDecimalNotationId.getLanguageId() != null && !findDecimalNotationId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findDecimalNotationId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
