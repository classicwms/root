package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.processid.FindProcessId;
import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ProcessIdSpecification implements Specification<ProcessId> {
    FindProcessId findProcessId;

    public ProcessIdSpecification(FindProcessId inputSearchParams) {
        this.findProcessId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ProcessId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findProcessId.getCompanyCodeId() != null && !findProcessId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findProcessId.getCompanyCodeId()));
        }

        if (findProcessId.getPlantId() != null && !findProcessId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findProcessId.getPlantId()));
        }

        if (findProcessId.getProcessId() != null && !findProcessId.getProcessId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processId");
            predicates.add(group.in(findProcessId.getProcessId()));
        }

        if (findProcessId.getWarehouseId() != null && !findProcessId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findProcessId.getWarehouseId()));
        }

        if (findProcessId.getLanguageId() != null && !findProcessId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findProcessId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
