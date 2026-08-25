package com.tekclover.wms.api.inbound.orders.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual.PerpetualHeader;
import com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual.SearchPerpetualHeader;

@SuppressWarnings("serial")
public class PerpetualHeaderSpecification implements Specification<PerpetualHeader> {
	
	SearchPerpetualHeader searchPerpetualHeader;
	
	public PerpetualHeaderSpecification(SearchPerpetualHeader inputSearchParams) {
		this.searchPerpetualHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<PerpetualHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();
		
		 if (searchPerpetualHeader.getWarehouseId() != null && !searchPerpetualHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchPerpetualHeader.getWarehouseId()));
         }
		 
		 if (searchPerpetualHeader.getCycleCountTypeId() != null && !searchPerpetualHeader.getCycleCountTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("cycleCountTypeId");
        	 predicates.add(group.in(searchPerpetualHeader.getCycleCountTypeId()));
         }
		 
         if (searchPerpetualHeader.getCycleCountNo() != null && !searchPerpetualHeader.getCycleCountNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("cycleCountNo");
        	 predicates.add(group.in(searchPerpetualHeader.getCycleCountNo()));
         }
		   
		 if (searchPerpetualHeader.getHeaderStatusId() != null && !searchPerpetualHeader.getHeaderStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchPerpetualHeader.getHeaderStatusId()));
         }	
		 
		 if (searchPerpetualHeader.getCreatedBy() != null && !searchPerpetualHeader.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchPerpetualHeader.getCreatedBy()));
         }   		 
	
		 if (searchPerpetualHeader.getStartCreatedOn() != null && searchPerpetualHeader.getEndCreatedOn() != null) {
        	 predicates.add(cb.between(root.get("createdOn"), searchPerpetualHeader.getStartCreatedOn(), 
        			 searchPerpetualHeader.getEndCreatedOn()));
         }
			
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
