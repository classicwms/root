package com.tekclover.wms.api.idmaster.repository.Specification;
import com.tekclover.wms.api.idmaster.model.employeeid.EmployeeId;
import com.tekclover.wms.api.idmaster.model.employeeid.FindEmployeeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class EmployeeIdSpecification implements Specification<EmployeeId> {

    FindEmployeeId findEmployeeId;

    public EmployeeIdSpecification(FindEmployeeId inputSearchParams) {
        this.findEmployeeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<EmployeeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findEmployeeId.getCompanyCodeId() != null && !findEmployeeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findEmployeeId.getCompanyCodeId()));
        }

        if (findEmployeeId.getPlantId() != null && !findEmployeeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findEmployeeId.getPlantId()));
        }

        if (findEmployeeId.getWarehouseId() != null && !findEmployeeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findEmployeeId.getWarehouseId()));
        }

        if (findEmployeeId.getEmployeeId() != null && !findEmployeeId.getEmployeeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("employeeId");
            predicates.add(group.in(findEmployeeId.getEmployeeId()));
        }

        if (findEmployeeId.getEmployeeName() != null && !findEmployeeId.getEmployeeName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("employeeName");
            predicates.add(group.in(findEmployeeId.getEmployeeName()));
        }
        if (findEmployeeId.getLanguageId() != null && !findEmployeeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findEmployeeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
