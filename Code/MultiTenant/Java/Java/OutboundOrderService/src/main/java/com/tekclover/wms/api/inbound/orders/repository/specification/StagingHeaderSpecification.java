package com.tekclover.wms.api.inbound.orders.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.tekclover.wms.api.inbound.orders.model.inbound.staging.SearchStagingHeader;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.inbound.orders.model.inbound.staging.StagingHeader;

@SuppressWarnings("serial")
public class StagingHeaderSpecification implements Specification<StagingHeader> {
	
	SearchStagingHeader searchStagingHeader;
	
	public StagingHeaderSpecification(SearchStagingHeader inputSearchParams) {
		this.searchStagingHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<StagingHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchStagingHeader.getWarehouseId() != null && !searchStagingHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchStagingHeader.getWarehouseId()));
         }
		 
		 if (searchStagingHeader.getPreInboundNo() != null && !searchStagingHeader.getPreInboundNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("preInboundNo");
        	 predicates.add(group.in(searchStagingHeader.getPreInboundNo()));
         }
		 
		  if (searchStagingHeader.getRefDocNumber() != null && !searchStagingHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchStagingHeader.getRefDocNumber()));
         }
		 
		  if (searchStagingHeader.getStagingNo() != null && !searchStagingHeader.getStagingNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("stagingNo");
        	 predicates.add(group.in(searchStagingHeader.getStagingNo()));
         }
		 
          if (searchStagingHeader.getInboundOrderTypeId() != null && !searchStagingHeader.getInboundOrderTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("inboundOrderTypeId");
        	 predicates.add(group.in(searchStagingHeader.getInboundOrderTypeId()));
         }		 
        
         if (searchStagingHeader.getStatusId() != null && !searchStagingHeader.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchStagingHeader.getStatusId()));
         }	
		 
		  if (searchStagingHeader.getCreatedBy() != null && !searchStagingHeader.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchStagingHeader.getCreatedBy()));
         }			 		 
		 		 
         if (searchStagingHeader.getStartCreatedOn() != null && searchStagingHeader.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchStagingHeader.getStartCreatedOn(), searchStagingHeader.getEndCreatedOn()));
         }
		         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
