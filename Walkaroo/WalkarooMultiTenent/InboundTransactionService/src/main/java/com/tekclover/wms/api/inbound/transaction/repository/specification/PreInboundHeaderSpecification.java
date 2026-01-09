package com.tekclover.wms.api.inbound.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.PreInboundHeaderEntity;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.SearchPreInboundHeader;

@SuppressWarnings("serial")
public class PreInboundHeaderSpecification implements Specification<PreInboundHeaderEntity> {
	
	SearchPreInboundHeader searchPreInboundHeader;
	
	public PreInboundHeaderSpecification(SearchPreInboundHeader inputSearchParams) {
		this.searchPreInboundHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<PreInboundHeaderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchPreInboundHeader.getWarehouseId() != null && !searchPreInboundHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchPreInboundHeader.getWarehouseId()));
         }
         
         if (searchPreInboundHeader.getPreInboundNo() != null && !searchPreInboundHeader.getPreInboundNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("preInboundNo");
        	 predicates.add(group.in(searchPreInboundHeader.getPreInboundNo()));
         }
         
		 if (searchPreInboundHeader.getInboundOrderTypeId() != null && !searchPreInboundHeader.getInboundOrderTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("inboundOrderTypeId");
        	 predicates.add(group.in(searchPreInboundHeader.getInboundOrderTypeId()));
         }
		 
		 if (searchPreInboundHeader.getRefDocNumber() != null && !searchPreInboundHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchPreInboundHeader.getRefDocNumber()));
         }
		 
		 if (searchPreInboundHeader.getStartRefDocDate() != null && searchPreInboundHeader.getEndRefDocDate() != null) {
        	 predicates.add(cb.between(root.get("refDocDate"), searchPreInboundHeader.getStartRefDocDate(), searchPreInboundHeader.getEndRefDocDate()));
         }
		 
         if (searchPreInboundHeader.getStatusId() != null && !searchPreInboundHeader.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchPreInboundHeader.getStatusId()));
         }	
		 
         if (searchPreInboundHeader.getStartCreatedOn() != null && searchPreInboundHeader.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchPreInboundHeader.getStartCreatedOn(), searchPreInboundHeader.getEndCreatedOn()));
         }
                   
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
