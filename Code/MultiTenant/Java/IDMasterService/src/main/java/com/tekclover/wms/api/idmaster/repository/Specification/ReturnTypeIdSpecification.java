package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.returntypeid.FindReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.ReturnTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ReturnTypeIdSpecification implements Specification<ReturnTypeId> {

    FindReturnTypeId findReturnTypeId;

    public ReturnTypeIdSpecification(FindReturnTypeId inputSearchParams) {
        this.findReturnTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ReturnTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findReturnTypeId.getCompanyCodeId() != null && !findReturnTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findReturnTypeId.getCompanyCodeId()));
        }

        if (findReturnTypeId.getPlantId() != null && !findReturnTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findReturnTypeId.getPlantId()));
        }

        if (findReturnTypeId.getReturnTypeId() != null && !findReturnTypeId.getReturnTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("returnTypeId");
            predicates.add(group.in(findReturnTypeId.getReturnTypeId()));
        }

        if (findReturnTypeId.getWarehouseId() != null && !findReturnTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findReturnTypeId.getWarehouseId()));
        }
        if (findReturnTypeId.getLanguageId() != null && !findReturnTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findReturnTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
