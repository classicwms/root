package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.storageclassid.FindStorageClassId;
import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageClassIdSpecification implements Specification<StorageClassId> {
    FindStorageClassId findStorageClassId;

    public StorageClassIdSpecification(FindStorageClassId inputSearchParams) {
        this.findStorageClassId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StorageClassId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStorageClassId.getCompanyCodeId() != null && !findStorageClassId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findStorageClassId.getCompanyCodeId()));
        }

        if (findStorageClassId.getPlantId() != null && !findStorageClassId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findStorageClassId.getPlantId()));
        }

        if (findStorageClassId.getWarehouseId() != null && !findStorageClassId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findStorageClassId.getWarehouseId()));
        }

        if (findStorageClassId.getStorageClassId() != null && !findStorageClassId.getStorageClassId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageClassId");
            predicates.add(group.in(findStorageClassId.getStorageClassId()));
        }

        if (findStorageClassId.getLanguageId() != null && !findStorageClassId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findStorageClassId.getLanguageId()));
        }


        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
