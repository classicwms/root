package com.tekclover.wms.api.masters.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.masters.model.storagebin.SearchStorageBin;
import com.tekclover.wms.api.masters.model.storagebin.StorageBin;

@SuppressWarnings("serial")
public class StorageBinSpecification implements Specification<StorageBin> {
	
	SearchStorageBin searchStorageBin;
	
	public StorageBinSpecification(SearchStorageBin inputSearchParams) {
		this.searchStorageBin = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<StorageBin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchStorageBin.getWarehouseId() != null && !searchStorageBin.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchStorageBin.getWarehouseId()));
         }
         
         if (searchStorageBin.getStorageBin() != null && !searchStorageBin.getStorageBin().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("storageBin");
        	 predicates.add(group.in(searchStorageBin.getStorageBin()));
         }
         
         if (searchStorageBin.getFloorId() != null && !searchStorageBin.getFloorId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("floorId");
        	 predicates.add(group.in(searchStorageBin.getFloorId()));
         }
         
         if (searchStorageBin.getStorageSectionId() != null && !searchStorageBin.getStorageSectionId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("storageSectionId");
        	 predicates.add(group.in(searchStorageBin.getStorageSectionId()));
         }
         
         if (searchStorageBin.getRowId() != null && !searchStorageBin.getRowId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("rowId");
        	 predicates.add(group.in(searchStorageBin.getRowId()));
         }
		 
		 if (searchStorageBin.getAisleNumber() != null && !searchStorageBin.getAisleNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("aisleNumber");
        	 predicates.add(group.in(searchStorageBin.getAisleNumber()));
         }
         
		  if (searchStorageBin.getSpanId() != null && !searchStorageBin.getSpanId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("spanId");
        	 predicates.add(group.in(searchStorageBin.getSpanId()));
         }
		 
		  if (searchStorageBin.getShelfId() != null && !searchStorageBin.getShelfId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("shelfId");
        	 predicates.add(group.in(searchStorageBin.getShelfId()));
         }
		 
         if (searchStorageBin.getStartCreatedOn() != null && searchStorageBin.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdon"), searchStorageBin.getStartCreatedOn(), searchStorageBin.getEndCreatedOn()));
         }
         
         if (searchStorageBin.getCreatedBy() != null && !searchStorageBin.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchStorageBin.getCreatedBy()));
         }
		 
		 if (searchStorageBin.getStartUpdatedOn() != null && searchStorageBin.getEndUpdatedOn() != null) {
             predicates.add(cb.between(root.get("updatedOn"), searchStorageBin.getStartUpdatedOn(), searchStorageBin.getEndUpdatedOn()));
         }
         
         if (searchStorageBin.getUpdatedBy() != null && !searchStorageBin.getUpdatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("updatedBy");
        	 predicates.add(group.in(searchStorageBin.getUpdatedBy()));
         }
         
         if (searchStorageBin.getStatusId() != null && !searchStorageBin.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchStorageBin.getStatusId()));
         }	
                 
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
