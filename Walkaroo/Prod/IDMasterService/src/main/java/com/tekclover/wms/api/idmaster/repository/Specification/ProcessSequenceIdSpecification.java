package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.processsequenceid.FindProcessSequenceId;
import com.tekclover.wms.api.idmaster.model.processsequenceid.ProcessSequenceId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessSequenceIdSpecification implements Specification<ProcessSequenceId> {
    FindProcessSequenceId findProcessSequenceId;

    public ProcessSequenceIdSpecification(FindProcessSequenceId inputSearchParams) {
        this.findProcessSequenceId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ProcessSequenceId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findProcessSequenceId.getCompanyCodeId() != null && !findProcessSequenceId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findProcessSequenceId.getCompanyCodeId()));
        }

        if (findProcessSequenceId.getPlantId() != null && !findProcessSequenceId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findProcessSequenceId.getPlantId()));
        }

        if (findProcessSequenceId.getProcessId() != null && !findProcessSequenceId.getProcessId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processId");
            predicates.add(group.in(findProcessSequenceId.getProcessId()));
        }

        if (findProcessSequenceId.getWarehouseId() != null && !findProcessSequenceId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findProcessSequenceId.getWarehouseId()));
        }
        if (findProcessSequenceId.getProcessSequenceId() != null && !findProcessSequenceId.getProcessSequenceId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processSequenceId");
            predicates.add(group.in(findProcessSequenceId.getProcessSequenceId()));
        }

        if (findProcessSequenceId.getLanguageId() != null && !findProcessSequenceId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findProcessSequenceId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
