package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.FindServiceTypeId;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.ServiceTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ServiceTypeIdSpecification implements Specification<ServiceTypeId> {
    FindServiceTypeId findServiceTypeId;
    public ServiceTypeIdSpecification(FindServiceTypeId inputSearchParams) {
        this.findServiceTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ServiceTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findServiceTypeId.getCompanyCodeId() != null && !findServiceTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findServiceTypeId.getCompanyCodeId()));
        }

        if (findServiceTypeId.getPlantId() != null && !findServiceTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findServiceTypeId.getPlantId()));
        }

        if (findServiceTypeId.getWarehouseId() != null && !findServiceTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findServiceTypeId.getWarehouseId()));
        }

        if (findServiceTypeId.getServiceTypeId() != null && !findServiceTypeId.getServiceTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("serviceTypeId");
            predicates.add(group.in(findServiceTypeId.getServiceTypeId()));
        }
        if (findServiceTypeId.getLanguageId() != null && !findServiceTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findServiceTypeId.getLanguageId()));
        }

        if (findServiceTypeId.getModuleId() != null && !findServiceTypeId.getModuleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("moduleId");
            predicates.add(group.in(findServiceTypeId.getModuleId()));
        }

        if (findServiceTypeId.getStatusId() != null && !findServiceTypeId.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findServiceTypeId.getStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
