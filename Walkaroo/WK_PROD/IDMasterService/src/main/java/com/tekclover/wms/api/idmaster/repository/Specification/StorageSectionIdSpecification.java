package com.tekclover.wms.api.idmaster.repository.Specification;


import com.tekclover.wms.api.idmaster.model.storagesectionid.FindStorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageSectionIdSpecification implements Specification<StorageSectionId> {
    FindStorageSectionId findStorageSectionId;

    public StorageSectionIdSpecification(FindStorageSectionId inputSearchParams) {
        this.findStorageSectionId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StorageSectionId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStorageSectionId.getCompanyCodeId() != null && !findStorageSectionId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findStorageSectionId.getCompanyCodeId()));
        }

        if (findStorageSectionId.getPlantId() != null && !findStorageSectionId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findStorageSectionId.getPlantId()));
        }

        if (findStorageSectionId.getWarehouseId() != null && !findStorageSectionId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findStorageSectionId.getWarehouseId()));
        }

        if (findStorageSectionId.getFloorId() != null && !findStorageSectionId.getFloorId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("floorId");
            predicates.add(group.in(findStorageSectionId.getFloorId()));
        }

        if (findStorageSectionId.getStorageSectionId() != null && !findStorageSectionId.getStorageSectionId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageSectionId");
            predicates.add(group.in(findStorageSectionId.getStorageSectionId()));
        }

        if (findStorageSectionId.getStorageSection() != null && !findStorageSectionId.getStorageSection().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageSection");
            predicates.add(group.in(findStorageSectionId.getStorageSection()));
        }
        if (findStorageSectionId.getLanguageId() != null && !findStorageSectionId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findStorageSectionId.getLanguageId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
