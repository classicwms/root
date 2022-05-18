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

import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.SearchImBasicData1;

@SuppressWarnings("serial")
public class ImBasicData1Specification implements Specification<ImBasicData1> {
	
	SearchImBasicData1 searchImBasicData1;
	
	public ImBasicData1Specification(SearchImBasicData1 inputSearchParams) {
		this.searchImBasicData1 = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<ImBasicData1> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchImBasicData1.getWarehouseId() != null && !searchImBasicData1.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchImBasicData1.getWarehouseId()));
         }
         
         if (searchImBasicData1.getItemCode() != null && !searchImBasicData1.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchImBasicData1.getItemCode()));
         }
         
         if (searchImBasicData1.getDescription() != null && !searchImBasicData1.getDescription().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("description");
        	 predicates.add(group.in(searchImBasicData1.getDescription()));
         }
         
         if (searchImBasicData1.getItemType() != null && !searchImBasicData1.getItemType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemType");
        	 predicates.add(group.in(searchImBasicData1.getItemType()));
         }
         
         if (searchImBasicData1.getItemGroup() != null && !searchImBasicData1.getItemGroup().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemGroup");
        	 predicates.add(group.in(searchImBasicData1.getItemGroup()));
         }
		 
		 if (searchImBasicData1.getSubItemGroup() != null && !searchImBasicData1.getSubItemGroup().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("subItemGroup");
        	 predicates.add(group.in(searchImBasicData1.getSubItemGroup()));
         }
         
         if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdon"), searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn()));
         }
         
         if (searchImBasicData1.getCreatedBy() != null && !searchImBasicData1.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchImBasicData1.getCreatedBy()));
         }
		 
		 if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
             predicates.add(cb.between(root.get("updatedOn"), searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn()));
         }
         
         if (searchImBasicData1.getUpdatedBy() != null && !searchImBasicData1.getUpdatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("updatedBy");
        	 predicates.add(group.in(searchImBasicData1.getUpdatedBy()));
         }
                 
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
