package com.tekclover.wms.api.idmaster.repository.Specification;
import com.tekclover.wms.api.idmaster.model.storagetypeid.FindStorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageTypeIdSpecification implements Specification<StorageTypeId> {
    FindStorageTypeId findStorageTypeId;
    public StorageTypeIdSpecification(FindStorageTypeId inputSearchParams) {
        this.findStorageTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StorageTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStorageTypeId.getCompanyCodeId() != null && !findStorageTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findStorageTypeId.getCompanyCodeId()));
        }

        if (findStorageTypeId.getPlantId() != null && !findStorageTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findStorageTypeId.getPlantId()));
        }

        if (findStorageTypeId.getWarehouseId() != null && !findStorageTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findStorageTypeId.getWarehouseId()));
        }

        if (findStorageTypeId.getStorageClassId() != null && !findStorageTypeId.getStorageClassId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageClassId");
            predicates.add(group.in(findStorageTypeId.getStorageClassId()));
        }

        if (findStorageTypeId.getStorageTypeId() != null && !findStorageTypeId.getStorageTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageTypeId");
            predicates.add(group.in(findStorageTypeId.getStorageTypeId()));
        }
        if (findStorageTypeId.getLanguageId() != null && !findStorageTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findStorageTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
