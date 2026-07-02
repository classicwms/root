package com.tekclover.wms.api.enterprise.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.enterprise.model.storagetype.SearchStorageType;
import com.tekclover.wms.api.enterprise.model.storagetype.StorageType;

@SuppressWarnings("serial")
public class StorageTypeSpecification implements Specification<StorageType> {
	
	SearchStorageType searchStorageType;
	
	public StorageTypeSpecification(SearchStorageType inputSearchParams) {
		this.searchStorageType = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<StorageType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchStorageType.getWarehouseId() != null && !searchStorageType.getWarehouseId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("warehouseId"), searchStorageType.getWarehouseId()));
         }
        if (searchStorageType.getCompanyId() != null && !searchStorageType.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchStorageType.getCompanyId()));
        }
        if (searchStorageType.getLanguageId() != null && !searchStorageType.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStorageType.getLanguageId()));
        }
         if (searchStorageType.getStorageClassId() != null && searchStorageType.getStorageClassId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("storageClassId"), searchStorageType.getStorageClassId()));
         }
        if (searchStorageType.getStorageTypeId() != null && searchStorageType.getStorageTypeId().longValue() != 0) {
            predicates.add(cb.equal(root.get("storageTypeId"), searchStorageType.getStorageTypeId()));
        }
        if (searchStorageType.getLanguageId() != null && searchStorageType.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStorageType.getLanguageId()));
        }
                
         if (searchStorageType.getCreatedBy() != null && !searchStorageType.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchStorageType.getCreatedBy()));
         }
         
         if (searchStorageType.getStartCreatedOn() != null && searchStorageType.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchStorageType.getStartCreatedOn(), searchStorageType.getEndCreatedOn()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
