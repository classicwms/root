package com.tekclover.wms.api.enterprise.repository.specification;

import com.tekclover.wms.api.enterprise.model.storagesection.SearchStorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.StorageSection;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageSectionSpecification implements Specification<StorageSection> {

    SearchStorageSection searchStorageSection;

    public StorageSectionSpecification(SearchStorageSection inputSearchParams) {
        this.searchStorageSection = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StorageSection> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchStorageSection.getCompanyId() != null && !searchStorageSection.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchStorageSection.getCompanyId()));
        }

        if (searchStorageSection.getPlantId() != null && !searchStorageSection.getPlantId().isEmpty()) {
            predicates.add(cb.equal(root.get("plantId"), searchStorageSection.getPlantId()));
        }

        if (searchStorageSection.getWarehouseId() != null && !searchStorageSection.getWarehouseId().isEmpty()) {
            predicates.add(cb.equal(root.get("warehouseId"), searchStorageSection.getWarehouseId()));
        }
        if (searchStorageSection.getLanguageId() != null && !searchStorageSection.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStorageSection.getLanguageId()));
        }

        if (searchStorageSection.getFloorId() != null && searchStorageSection.getFloorId().longValue() != 0) {
            predicates.add(cb.equal(root.get("floorId"), searchStorageSection.getFloorId()));
        }

        if (searchStorageSection.getStorageSectionId() != null && searchStorageSection.getStorageSectionId().longValue() != 0) {
            predicates.add(cb.equal(root.get("storageSectionId"), searchStorageSection.getStorageSectionId()));
        }

        if (searchStorageSection.getCreatedBy() != null && !searchStorageSection.getCreatedBy().isEmpty()) {
            predicates.add(cb.equal(root.get("createdBy"), searchStorageSection.getCreatedBy()));
        }

        if (searchStorageSection.getStartCreatedOn() != null && searchStorageSection.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchStorageSection.getStartCreatedOn(), searchStorageSection.getEndCreatedOn()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}