package com.tekclover.wms.api.enterprise.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.enterprise.model.storagebintype.SearchStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.StorageBinType;

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
         
         if (searchStorageBinType.getStorageTypeId() != null && searchStorageBinType.getStorageTypeId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("storageTypeId"), searchStorageBinType.getStorageTypeId()));
         }
         
         if (searchStorageBinType.getStorageBinTypeId() != null && searchStorageBinType.getStorageBinTypeId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("storageBinTypeId"), searchStorageBinType.getStorageBinTypeId()));
         }
        
         if (searchStorageBinType.getCreatedBy() != null && !searchStorageBinType.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchStorageBinType.getCreatedBy()));
         }
         
         if (searchStorageBinType.getStartCreatedOn() != null && searchStorageBinType.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchStorageBinType.getStartCreatedOn(), searchStorageBinType.getEndCreatedOn()));
         }
         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
