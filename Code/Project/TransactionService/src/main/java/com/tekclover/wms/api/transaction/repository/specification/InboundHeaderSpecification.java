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

import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.SearchInboundHeader;

@SuppressWarnings("serial")
public class InboundHeaderSpecification implements Specification<InboundHeader> {
	
	SearchInboundHeader searchInboundHeader;
	
	public InboundHeaderSpecification(SearchInboundHeader inputSearchParams) {
		this.searchInboundHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<InboundHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchInboundHeader.getWarehouseId() != null && !searchInboundHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchInboundHeader.getWarehouseId()));
         }
		 
		  if (searchInboundHeader.getRefDocNumber() != null && !searchInboundHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchInboundHeader.getRefDocNumber()));
         }
		 
          if (searchInboundHeader.getInboundOrderTypeId() != null && !searchInboundHeader.getInboundOrderTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("inboundOrderTypeId");
        	 predicates.add(group.in(searchInboundHeader.getInboundOrderTypeId()));
         }
		 
         if (searchInboundHeader.getContainerNo() != null && !searchInboundHeader.getContainerNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("containerNo");
        	 predicates.add(group.in(searchInboundHeader.getContainerNo()));
         }			 		 
		 
         if (searchInboundHeader.getStatusId() != null && !searchInboundHeader.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchInboundHeader.getStatusId()));
         }	
		 
         if (searchInboundHeader.getStartCreatedOn() != null && searchInboundHeader.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchInboundHeader.getStartCreatedOn(), searchInboundHeader.getEndCreatedOn()));
         }
		 
         if (searchInboundHeader.getStartConfirmedOn() != null && searchInboundHeader.getEndConfirmedOn() != null) {
        	 predicates.add(cb.between(root.get("confirmedOn"), searchInboundHeader.getStartConfirmedOn(), searchInboundHeader.getEndConfirmedOn()));
         }
		                    
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
