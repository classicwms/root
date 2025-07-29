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

import com.tekclover.wms.api.inbound.orders.model.outbound.SearchOutboundReversal;
import com.tekclover.wms.api.inbound.orders.model.outbound.outboundreversal.OutboundReversal;

@SuppressWarnings("serial")
public class OutboundReversalSpecification implements Specification<OutboundReversal> {
	
	SearchOutboundReversal searchOutboundReversal;
	
	public OutboundReversalSpecification(SearchOutboundReversal inputSearchParams) {
		this.searchOutboundReversal = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<OutboundReversal> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchOutboundReversal.getOutboundReversalNo() != null && !searchOutboundReversal.getOutboundReversalNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("outboundReversalNo");
        	 predicates.add(group.in(searchOutboundReversal.getOutboundReversalNo()));
         }
		   
		 if (searchOutboundReversal.getReversalType() != null && !searchOutboundReversal.getReversalType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("reversalType");
        	 predicates.add(group.in(searchOutboundReversal.getReversalType()));
         }
         
         if (searchOutboundReversal.getRefDocNumber() != null && !searchOutboundReversal.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchOutboundReversal.getRefDocNumber()));
         }       		 
		 
		 if (searchOutboundReversal.getPartnerCode() != null && !searchOutboundReversal.getPartnerCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("partnerCode");
        	 predicates.add(group.in(searchOutboundReversal.getPartnerCode()));
         }
		 
		 if (searchOutboundReversal.getItemCode() != null && !searchOutboundReversal.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchOutboundReversal.getItemCode()));
         }		 
		 
		 if (searchOutboundReversal.getPackBarcode() != null && !searchOutboundReversal.getPackBarcode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("packBarcode");
        	 predicates.add(group.in(searchOutboundReversal.getPackBarcode()));
         }
		 
		  if (searchOutboundReversal.getStatusId() != null && !searchOutboundReversal.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchOutboundReversal.getStatusId()));
         }	
		 
		 if (searchOutboundReversal.getReversedBy() != null && !searchOutboundReversal.getReversedBy().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("reversedBy");
        	 predicates.add(group.in(searchOutboundReversal.getReversedBy()));
         }	
		
		  if (searchOutboundReversal.getStartReversedOn() != null && searchOutboundReversal.getEndReversedOn() != null) {
        	 predicates.add(cb.between(root.get("reversedOn"), searchOutboundReversal.getStartReversedOn(), searchOutboundReversal.getEndReversedOn()));
         }
			
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
