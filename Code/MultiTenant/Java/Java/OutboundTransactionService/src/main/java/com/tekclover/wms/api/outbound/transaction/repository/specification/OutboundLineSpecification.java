package com.tekclover.wms.api.outbound.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.outbound.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.SearchOutboundLine;

@SuppressWarnings("serial")
public class OutboundLineSpecification implements Specification<OutboundLine> {
	
	SearchOutboundLine searchOutboundLine;
	
	public OutboundLineSpecification(SearchOutboundLine inputSearchParams) {
		this.searchOutboundLine = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<OutboundLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchOutboundLine.getWarehouseId() != null && !searchOutboundLine.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchOutboundLine.getWarehouseId()));
         }
		   
		 if (searchOutboundLine.getPreOutboundNo() != null && !searchOutboundLine.getPreOutboundNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("preOutboundNo");
        	 predicates.add(group.in(searchOutboundLine.getPreOutboundNo()));
         }
         
         if (searchOutboundLine.getRefDocNumber() != null && !searchOutboundLine.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchOutboundLine.getRefDocNumber()));
         }       		 
		 
		 if (searchOutboundLine.getPartnerCode() != null && !searchOutboundLine.getPartnerCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("partnerCode");
        	 predicates.add(group.in(searchOutboundLine.getPartnerCode()));
         }
		 
		 if (searchOutboundLine.getLineNumber() != null && !searchOutboundLine.getLineNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("lineNumber");
        	 predicates.add(group.in(searchOutboundLine.getLineNumber()));
         }		 
		 
		 if (searchOutboundLine.getItemCode() != null && !searchOutboundLine.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchOutboundLine.getItemCode()));
         }	
		 
		 // orderType - Ref_Field_1
		 if (searchOutboundLine.getOrderType() != null && !searchOutboundLine.getOrderType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField1");
        	 predicates.add(group.in(searchOutboundLine.getOrderType()));
         }	
		 
		 if (searchOutboundLine.getStatusId() != null && !searchOutboundLine.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchOutboundLine.getStatusId()));
         }	
		 
		 if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
             predicates.add(cb.between(root.get("deliveryConfirmedOn"), 
            		 searchOutboundLine.getFromDeliveryDate(), searchOutboundLine.getToDeliveryDate()));
         }
		 
		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
