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

import com.tekclover.wms.api.masters.model.handlingequipment.HandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.SearchHandlingEquipment;

@SuppressWarnings("serial")
public class HandlingEquipmentSpecification implements Specification<HandlingEquipment> {
	
	SearchHandlingEquipment searchHandlingEquipment;
	
	public HandlingEquipmentSpecification(SearchHandlingEquipment inputSearchParams) {
		this.searchHandlingEquipment = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<HandlingEquipment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchHandlingEquipment.getWarehouseId() != null && !searchHandlingEquipment.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchHandlingEquipment.getWarehouseId()));
         }
         
         if (searchHandlingEquipment.getHandlingEquipmentId() != null && !searchHandlingEquipment.getHandlingEquipmentId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("handlingEquipmentId");
        	 predicates.add(group.in(searchHandlingEquipment.getHandlingEquipmentId()));
         }
         
         if (searchHandlingEquipment.getCategory() != null && !searchHandlingEquipment.getCategory().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("category");
        	 predicates.add(group.in(searchHandlingEquipment.getCategory()));
         }	
		 
		 if (searchHandlingEquipment.getHandlingUnit() != null && !searchHandlingEquipment.getHandlingUnit().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("handlingUnit");
        	 predicates.add(group.in(searchHandlingEquipment.getHandlingUnit()));
         }	
		 
		 if (searchHandlingEquipment.getStatusId() != null && !searchHandlingEquipment.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchHandlingEquipment.getStatusId()));
         }	
		 
         if (searchHandlingEquipment.getStartCreatedOn() != null && searchHandlingEquipment.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdon"), searchHandlingEquipment.getStartCreatedOn(), searchHandlingEquipment.getEndCreatedOn()));
         }
         
         if (searchHandlingEquipment.getCreatedBy() != null && !searchHandlingEquipment.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchHandlingEquipment.getCreatedBy()));
         }
		 
		 if (searchHandlingEquipment.getStartUpdatedOn() != null && searchHandlingEquipment.getEndUpdatedOn() != null) {
             predicates.add(cb.between(root.get("updatedOn"), searchHandlingEquipment.getStartUpdatedOn(), searchHandlingEquipment.getEndUpdatedOn()));
         }
         
         if (searchHandlingEquipment.getUpdatedBy() != null && !searchHandlingEquipment.getUpdatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("updatedBy");
        	 predicates.add(group.in(searchHandlingEquipment.getUpdatedBy()));
         }
                 
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
