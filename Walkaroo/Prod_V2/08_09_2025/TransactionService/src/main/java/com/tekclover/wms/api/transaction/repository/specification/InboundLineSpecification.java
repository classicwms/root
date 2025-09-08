package com.tekclover.wms.api.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.*;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.SearchInboundLine;

@SuppressWarnings("serial")
public class InboundLineSpecification implements Specification<InboundLine> {
	
	SearchInboundLine searchInboundLine;
	
	public InboundLineSpecification(SearchInboundLine inputSearchParams) {
		this.searchInboundLine = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<InboundLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchInboundLine.getWarehouseId() != null) {
        	 predicates.add (cb.equal(root.get("warehouseId"), searchInboundLine.getWarehouseId()));
         }
		 
         if (searchInboundLine.getStartConfirmedOn() != null && searchInboundLine.getEndConfirmedOn() != null) {
        	 predicates.add(cb.between(root.get("confirmedOn"), searchInboundLine.getStartConfirmedOn(), 
        			 searchInboundLine.getEndConfirmedOn()));
         }

        if (searchInboundLine.getStatusId() != null && !searchInboundLine.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("statusId");
            predicates.add(group.in(searchInboundLine.getStatusId()));
        }

        predicates.add (cb.equal(root.get("referenceField1"), searchInboundLine.getReferenceField1()));
		                    
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
