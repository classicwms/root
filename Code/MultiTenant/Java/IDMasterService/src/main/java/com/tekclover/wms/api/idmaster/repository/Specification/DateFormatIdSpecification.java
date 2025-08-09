package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.dateformatid.DateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.FindDateFormatId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DateFormatIdSpecification implements Specification<DateFormatId> {
    FindDateFormatId findDateFormatId;
    public DateFormatIdSpecification(FindDateFormatId inputSearchParams) {
        this.findDateFormatId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DateFormatId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findDateFormatId.getCompanyCodeId() != null && !findDateFormatId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findDateFormatId.getCompanyCodeId()));
        }

        if (findDateFormatId.getDateFormatId() != null && !findDateFormatId.getDateFormatId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("dateFormatId");
            predicates.add(group.in(findDateFormatId.getDateFormatId()));
        }
        if (findDateFormatId.getPlantId() != null && !findDateFormatId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findDateFormatId.getPlantId()));
        }
        if (findDateFormatId.getWarehouseId() != null && !findDateFormatId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findDateFormatId.getWarehouseId()));
        }
        if (findDateFormatId.getDateFormat() != null && !findDateFormatId.getDateFormat().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("dateFormat");
            predicates.add(group.in(findDateFormatId.getDateFormat()));
        }
        if (findDateFormatId.getLanguageId() != null && !findDateFormatId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findDateFormatId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
