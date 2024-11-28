package com.tekclover.wms.api.enterprise.repository.specification;

import com.tekclover.wms.api.enterprise.model.storageclass.SearchStorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.StorageClass;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageClassSpecification implements Specification<StorageClass> {

    SearchStorageClass searchStorageClass;

    public StorageClassSpecification(SearchStorageClass inputSearchParams) {
        this.searchStorageClass = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StorageClass> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchStorageClass.getWarehouseId() != null && !searchStorageClass.getWarehouseId().isEmpty()) {
            predicates.add(cb.equal(root.get("warehouseId"), searchStorageClass.getWarehouseId()));
        }
        if (searchStorageClass.getCompanyId() != null && !searchStorageClass.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchStorageClass.getCompanyId()));
        }

        if (searchStorageClass.getLanguageId() != null && !searchStorageClass.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStorageClass.getLanguageId()));
        }

        if (searchStorageClass.getStorageClassId() != null && searchStorageClass.getStorageClassId().longValue() != 0) {
            predicates.add(cb.equal(root.get("storageClassId"), searchStorageClass.getStorageClassId()));
        }
        if (searchStorageClass.getLanguageId() != null && searchStorageClass.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStorageClass.getLanguageId()));
        }

        if (searchStorageClass.getCreatedBy() != null && !searchStorageClass.getCreatedBy().isEmpty()) {
            predicates.add(cb.equal(root.get("createdBy"), searchStorageClass.getCreatedBy()));
        }

        if (searchStorageClass.getStartCreatedOn() != null && searchStorageClass.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchStorageClass.getStartCreatedOn(), searchStorageClass.getEndCreatedOn()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}