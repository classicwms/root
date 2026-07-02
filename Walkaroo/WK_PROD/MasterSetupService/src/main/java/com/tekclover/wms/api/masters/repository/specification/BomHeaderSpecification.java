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

import com.tekclover.wms.api.masters.model.bom.BomHeader;
import com.tekclover.wms.api.masters.model.bom.SearchBomHeader;

@SuppressWarnings("serial")
public class BomHeaderSpecification implements Specification<BomHeader> {
	
	SearchBomHeader searchBomHeader;
	
	public BomHeaderSpecification(SearchBomHeader inputSearchParams) {
		this.searchBomHeader = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<BomHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchBomHeader.getWarehouseId() != null && !searchBomHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchBomHeader.getWarehouseId()));
         }
         
         if (searchBomHeader.getParentItemCode() != null && !searchBomHeader.getParentItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("parentItemCode");
        	 predicates.add(group.in(searchBomHeader.getParentItemCode()));
         }
		         
         if (searchBomHeader.getBomNumber() != null && !searchBomHeader.getBomNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("bomNumber");
        	 predicates.add(group.in(searchBomHeader.getBomNumber()));
         }
         
         if (searchBomHeader.getStatusId() != null && !searchBomHeader.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchBomHeader.getStatusId()));
         }

        if (searchBomHeader.getLanguageId() != null && !searchBomHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group> get("languageId");
            predicates.add(group.in(searchBomHeader.getLanguageId()));
        }

        if (searchBomHeader.getCompanyCode() != null && !searchBomHeader.getCompanyCode().isEmpty()) {
            final Path<Group> group = root.<Group> get("companyCode");
            predicates.add(group.in(searchBomHeader.getCompanyCode()));
        }

        if (searchBomHeader.getPlantId() != null && !searchBomHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group> get("plantId");
            predicates.add(group.in(searchBomHeader.getPlantId()));
        }

        if (searchBomHeader.getStartCreatedOn() != null && searchBomHeader.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchBomHeader.getStartCreatedOn(), searchBomHeader.getEndCreatedOn()));
         }
         
         if (searchBomHeader.getCreatedBy() != null && !searchBomHeader.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchBomHeader.getCreatedBy()));
         }
		 
		 if (searchBomHeader.getStartUpdatedOn() != null && searchBomHeader.getEndUpdatedOn() != null) {
             predicates.add(cb.between(root.get("updatedOn"), searchBomHeader.getStartUpdatedOn(), searchBomHeader.getEndUpdatedOn()));
         }
         
         if (searchBomHeader.getUpdatedBy() != null && !searchBomHeader.getUpdatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("updatedBy");
        	 predicates.add(group.in(searchBomHeader.getUpdatedBy()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
