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

import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.SearchGrHeader;

@SuppressWarnings("serial")
public class GrHeaderSpecification implements Specification<GrHeader> {
	
	SearchGrHeader searchGrHeader;
	
	public GrHeaderSpecification(SearchGrHeader inputSearchParams) {
		this.searchGrHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<GrHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchGrHeader.getWarehouseId() != null && !searchGrHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchGrHeader.getWarehouseId()));
         }
		 
		 if (searchGrHeader.getPreInboundNo() != null && !searchGrHeader.getPreInboundNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("preInboundNo");
        	 predicates.add(group.in(searchGrHeader.getPreInboundNo()));
         }
		 
		  if (searchGrHeader.getRefDocNumber() != null && !searchGrHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchGrHeader.getRefDocNumber()));
         }
		 
		  if (searchGrHeader.getGoodsReceiptNo() != null && !searchGrHeader.getGoodsReceiptNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("goodsReceiptNo");
        	 predicates.add(group.in(searchGrHeader.getGoodsReceiptNo()));
         }
		 
		  if (searchGrHeader.getCaseCode() != null && !searchGrHeader.getCaseCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("caseCode");
        	 predicates.add(group.in(searchGrHeader.getCaseCode()));
         }
		 
          if (searchGrHeader.getInboundOrderTypeId() != null && !searchGrHeader.getInboundOrderTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("inboundOrderTypeId");
        	 predicates.add(group.in(searchGrHeader.getInboundOrderTypeId()));
         }		 
        
         if (searchGrHeader.getStatusId() != null && !searchGrHeader.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchGrHeader.getStatusId()));
         }	
		 
		  if (searchGrHeader.getCreatedBy() != null && !searchGrHeader.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchGrHeader.getCreatedBy()));
         }			 		 
		 		 
         if (searchGrHeader.getStartCreatedOn() != null && searchGrHeader.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchGrHeader.getStartCreatedOn(), searchGrHeader.getEndCreatedOn()));
         }
         
         if (searchGrHeader.getDeletionIndicator() != null) {
             predicates.add(cb.equal(root.get("deletionIndicator"), searchGrHeader.getDeletionIndicator()));
         }
		         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
