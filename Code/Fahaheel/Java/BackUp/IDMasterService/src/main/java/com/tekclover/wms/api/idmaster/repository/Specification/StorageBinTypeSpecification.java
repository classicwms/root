package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.storagebintypeid.FindStorageBinTypeId;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.StorageBinTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageBinTypeSpecification implements Specification<StorageBinTypeId> {
    FindStorageBinTypeId findStorageBinTypeId;

    public StorageBinTypeSpecification(FindStorageBinTypeId inputSearchParams) {
        this.findStorageBinTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StorageBinTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStorageBinTypeId.getCompanyCodeId() != null && !findStorageBinTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findStorageBinTypeId.getCompanyCodeId()));
        }

        if (findStorageBinTypeId.getPlantId() != null && !findStorageBinTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findStorageBinTypeId.getPlantId()));
        }

        if (findStorageBinTypeId.getWarehouseId() != null && !findStorageBinTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findStorageBinTypeId.getWarehouseId()));
        }

        if (findStorageBinTypeId.getStorageBinTypeId() != null && !findStorageBinTypeId.getStorageBinTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageBinTypeId");
            predicates.add(group.in(findStorageBinTypeId.getStorageBinTypeId()));
        }

        if (findStorageBinTypeId.getStorageTypeId() != null && !findStorageBinTypeId.getStorageTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageTypeId");
            predicates.add(group.in(findStorageBinTypeId.getStorageTypeId()));
        }

        if (findStorageBinTypeId.getStorageClassId() != null && !findStorageBinTypeId.getStorageClassId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("storageClassId");
            predicates.add(group.in(findStorageBinTypeId.getStorageClassId()));
        }
        if (findStorageBinTypeId.getLanguageId() != null && !findStorageBinTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findStorageBinTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
