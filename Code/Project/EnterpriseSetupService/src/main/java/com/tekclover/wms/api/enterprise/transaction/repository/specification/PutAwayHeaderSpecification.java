package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway.SearchPutAwayHeader;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("serial")
public class PutAwayHeaderSpecification implements Specification<PutAwayHeader> {
	
	SearchPutAwayHeader searchPutAwayHeader;
	
	public PutAwayHeaderSpecification(SearchPutAwayHeader inputSearchParams) {
		this.searchPutAwayHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<PutAwayHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchPutAwayHeader.getWarehouseId() != null && !searchPutAwayHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchPutAwayHeader.getWarehouseId()));
         }
	
		  if (searchPutAwayHeader.getRefDocNumber() != null && !searchPutAwayHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchPutAwayHeader.getRefDocNumber()));
         }
		 
		 	 
		 if (searchPutAwayHeader.getPackBarcodes() != null && !searchPutAwayHeader.getPackBarcodes().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("packBarcodes");
        	 predicates.add(group.in(searchPutAwayHeader.getPackBarcodes()));
         }
		 
		 
		  if (searchPutAwayHeader.getPutAwayNumber() != null && !searchPutAwayHeader.getPutAwayNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("putAwayNumber");
        	 predicates.add(group.in(searchPutAwayHeader.getPutAwayNumber()));
         }
		 
          if (searchPutAwayHeader.getProposedStorageBin() != null && !searchPutAwayHeader.getProposedStorageBin().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("proposedStorageBin");
        	 predicates.add(group.in(searchPutAwayHeader.getProposedStorageBin()));
         }
		 
		 	 
          if (searchPutAwayHeader.getProposedHandlingEquipment() != null && !searchPutAwayHeader.getProposedHandlingEquipment().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("proposedHandlingEquipment");
        	 predicates.add(group.in(searchPutAwayHeader.getProposedHandlingEquipment()));
         }
        
         if (searchPutAwayHeader.getStatusId() != null && !searchPutAwayHeader.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchPutAwayHeader.getStatusId()));
         }	
		 
		  if (searchPutAwayHeader.getCreatedBy() != null && !searchPutAwayHeader.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchPutAwayHeader.getCreatedBy()));
         }			 		 
		 		 
         if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchPutAwayHeader.getStartCreatedOn(), searchPutAwayHeader.getEndCreatedOn()));
         }

		predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
		         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}