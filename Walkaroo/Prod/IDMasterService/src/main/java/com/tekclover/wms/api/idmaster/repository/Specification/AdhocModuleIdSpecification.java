package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.adhocmoduleid.AdhocModuleId;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.FindAdhocModuleId;
import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import com.tekclover.wms.api.idmaster.model.aisleid.FindAisleId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AdhocModuleIdSpecification implements Specification<AdhocModuleId> {

    FindAdhocModuleId findAdhocModuleId;
    public AdhocModuleIdSpecification(FindAdhocModuleId inputSearchParams) {
        this.findAdhocModuleId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<AdhocModuleId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findAdhocModuleId.getCompanyCodeId() != null && !findAdhocModuleId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findAdhocModuleId.getCompanyCodeId()));
        }

        if (findAdhocModuleId.getPlantId() != null && !findAdhocModuleId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findAdhocModuleId.getPlantId()));
        }

        if (findAdhocModuleId.getWarehouseId() != null && !findAdhocModuleId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findAdhocModuleId.getWarehouseId()));
        }

        if (findAdhocModuleId.getModuleId() != null && !findAdhocModuleId.getModuleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("moduleId");
            predicates.add(group.in(findAdhocModuleId.getModuleId()));
        }

        if (findAdhocModuleId.getAdhocModuleId() != null && !findAdhocModuleId.getAdhocModuleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("adhocModuleId");
            predicates.add(group.in(findAdhocModuleId.getAdhocModuleId()));
        }
        if (findAdhocModuleId.getLanguageId() != null && !findAdhocModuleId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findAdhocModuleId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
