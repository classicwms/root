package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.movementtypeid.FindMovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import com.tekclover.wms.api.idmaster.model.numberrange.FindNumberRange;
import com.tekclover.wms.api.idmaster.model.numberrange.NumberRange;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class NumberRangeSpecification implements Specification<NumberRange> {

    FindNumberRange findNumberRange;

    public NumberRangeSpecification(FindNumberRange inputSearchParams) {
        this.findNumberRange = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<NumberRange> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findNumberRange.getCompanyCodeId() != null && !findNumberRange.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findNumberRange.getCompanyCodeId()));
        }
        if (findNumberRange.getWarehouseId() != null && !findNumberRange.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findNumberRange.getWarehouseId()));
        }
        if (findNumberRange.getNumberRangeCode() != null && !findNumberRange.getNumberRangeCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("numberRangeCode");
            predicates.add(group.in(findNumberRange.getNumberRangeCode()));
        }
        if (findNumberRange.getPlantId() != null && !findNumberRange.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findNumberRange.getPlantId()));
        }
        if (findNumberRange.getLanguageId() != null && !findNumberRange.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findNumberRange.getLanguageId()));
        }
        if (findNumberRange.getFiscalYear() != null && !findNumberRange.getFiscalYear().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("fiscalYear");
            predicates.add(group.in(findNumberRange.getFiscalYear()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
