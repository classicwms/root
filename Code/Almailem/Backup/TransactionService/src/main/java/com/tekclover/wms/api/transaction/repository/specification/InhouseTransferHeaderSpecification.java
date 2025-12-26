package com.tekclover.wms.api.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.transaction.model.mnc.InhouseTransferHeader;
import com.tekclover.wms.api.transaction.model.mnc.SearchInhouseTransferHeader;

@SuppressWarnings("serial")
public class InhouseTransferHeaderSpecification implements Specification<InhouseTransferHeader> {
	
	SearchInhouseTransferHeader searchInHouseTransferHeader;
	
	public InhouseTransferHeaderSpecification(SearchInhouseTransferHeader inputSearchParams) {
		this.searchInHouseTransferHeader = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<InhouseTransferHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchInHouseTransferHeader.getWarehouseId() != null && !searchInHouseTransferHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchInHouseTransferHeader.getWarehouseId()));
         }
         
         if (searchInHouseTransferHeader.getTransferNumber() != null && !searchInHouseTransferHeader.getTransferNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("transferNumber");
        	 predicates.add(group.in(searchInHouseTransferHeader.getTransferNumber()));
         }
         
         if (searchInHouseTransferHeader.getTransferTypeId() != null && !searchInHouseTransferHeader.getTransferTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("transferTypeId");
        	 predicates.add(group.in(searchInHouseTransferHeader.getTransferTypeId()));
         }
         
		 if (searchInHouseTransferHeader.getStatusId() != null && !searchInHouseTransferHeader.getStatusId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchInHouseTransferHeader.getStatusId()));
         }
         
         if (searchInHouseTransferHeader.getStartCreatedOn() != null && searchInHouseTransferHeader.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdon"), searchInHouseTransferHeader.getStartCreatedOn(), searchInHouseTransferHeader.getEndCreatedOn()));
         }
         
         if (searchInHouseTransferHeader.getCreatedBy() != null && !searchInHouseTransferHeader.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchInHouseTransferHeader.getCreatedBy()));
         }
		       
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
