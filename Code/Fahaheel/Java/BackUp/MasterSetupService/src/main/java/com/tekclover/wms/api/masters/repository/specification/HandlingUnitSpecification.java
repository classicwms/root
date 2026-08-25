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

import com.tekclover.wms.api.masters.model.handlingunit.HandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.SearchHandlingUnit;

@SuppressWarnings("serial")
public class HandlingUnitSpecification implements Specification<HandlingUnit> {
	
	SearchHandlingUnit searchHandlingUnit;
	
	public HandlingUnitSpecification(SearchHandlingUnit inputSearchParams) {
		this.searchHandlingUnit = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<HandlingUnit> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchHandlingUnit.getWarehouseId() != null && !searchHandlingUnit.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchHandlingUnit.getWarehouseId()));
         }
         
         if (searchHandlingUnit.getHandlingUnit() != null && !searchHandlingUnit.getHandlingUnit().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("handlingUnit");
        	 predicates.add(group.in(searchHandlingUnit.getHandlingUnit()));
         }

		if (searchHandlingUnit.getCompanyCodeId() != null && !searchHandlingUnit.getCompanyCodeId().isEmpty()) {
			final Path<Group> group = root.<Group> get("companyCodeId");
			predicates.add(group.in(searchHandlingUnit.getCompanyCodeId()));
		}
		
		if (searchHandlingUnit.getPlantId() != null && !searchHandlingUnit.getPlantId().isEmpty()) {
			final Path<Group> group = root.<Group> get("plantId");
			predicates.add(group.in(searchHandlingUnit.getPlantId()));
		}

		 if (searchHandlingUnit.getReferenceField1() != null && !searchHandlingUnit.getReferenceField1().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField1");
        	 predicates.add(group.in(searchHandlingUnit.getReferenceField1()));
         }
		 
		if (searchHandlingUnit.getStatusId() != null && !searchHandlingUnit.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchHandlingUnit.getStatusId()));
         }

		if (searchHandlingUnit.getLanguageId() != null && !searchHandlingUnit.getLanguageId().isEmpty()) {
			final Path<Group> group = root.<Group> get("languageId");
			predicates.add(group.in(searchHandlingUnit.getLanguageId()));
		}

		if (searchHandlingUnit.getStartCreatedOn() != null && searchHandlingUnit.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchHandlingUnit.getStartCreatedOn(), searchHandlingUnit.getEndCreatedOn()));
         }
         
         if (searchHandlingUnit.getCreatedBy() != null && !searchHandlingUnit.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchHandlingUnit.getCreatedBy()));
         }
		 
		 if (searchHandlingUnit.getStartUpdatedOn() != null && searchHandlingUnit.getEndUpdatedOn() != null) {
             predicates.add(cb.between(root.get("updatedOn"), searchHandlingUnit.getStartUpdatedOn(), searchHandlingUnit.getEndUpdatedOn()));
         }
         
         if (searchHandlingUnit.getUpdatedBy() != null && !searchHandlingUnit.getUpdatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("updatedBy");
        	 predicates.add(group.in(searchHandlingUnit.getUpdatedBy()));
         }
		predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
