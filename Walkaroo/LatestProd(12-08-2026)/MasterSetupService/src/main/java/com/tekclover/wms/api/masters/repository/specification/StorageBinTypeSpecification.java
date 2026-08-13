package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.storagebintype.SearchStorageBinType;
import com.tekclover.wms.api.masters.model.storagebintype.StorageBinType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageBinTypeSpecification implements Specification<StorageBinType> {
	
	SearchStorageBinType searchStorageBinType;
	
	public StorageBinTypeSpecification(SearchStorageBinType inputSearchParams) {
		this.searchStorageBinType = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<StorageBinType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchStorageBinType.getWarehouseId() != null && !searchStorageBinType.getWarehouseId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("warehouseId"), searchStorageBinType.getWarehouseId()));
         }
        if (searchStorageBinType.getCompanyId() != null && !searchStorageBinType.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchStorageBinType.getCompanyId()));
        }
        if (searchStorageBinType.getLanguageId() != null && !searchStorageBinType.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStorageBinType.getLanguageId()));
        }
         
         if (searchStorageBinType.getStorageTypeId() != null && searchStorageBinType.getStorageTypeId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("storageTypeId"), searchStorageBinType.getStorageTypeId()));
         }
         
         if (searchStorageBinType.getStorageBinTypeId() != null && searchStorageBinType.getStorageBinTypeId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("storageBinTypeId"), searchStorageBinType.getStorageBinTypeId()));
         }


        if (searchStorageBinType.getStorageClassId() != null && searchStorageBinType.getStorageClassId().longValue()!=0) {
            predicates.add(cb.equal(root.get("storageClassId"), searchStorageBinType.getStorageClassId()));
        }
        if (searchStorageBinType.getLanguageId() != null && searchStorageBinType.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStorageBinType.getLanguageId()));
        }
        
         if (searchStorageBinType.getCreatedBy() != null && !searchStorageBinType.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchStorageBinType.getCreatedBy()));
         }
         
         if (searchStorageBinType.getStartCreatedOn() != null && searchStorageBinType.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchStorageBinType.getStartCreatedOn(), searchStorageBinType.getEndCreatedOn()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
