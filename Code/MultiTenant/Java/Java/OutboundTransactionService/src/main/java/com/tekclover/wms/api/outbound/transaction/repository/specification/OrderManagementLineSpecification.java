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

import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.SearchOrderManagementLine;

@SuppressWarnings("serial")
public class OrderManagementLineSpecification implements Specification<OrderManagementLine> {
	
	SearchOrderManagementLine searchOrderMangementLine;
	
	public OrderManagementLineSpecification(SearchOrderManagementLine inputSearchParams) {
		this.searchOrderMangementLine = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<OrderManagementLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchOrderMangementLine.getWarehouseId() != null && !searchOrderMangementLine.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchOrderMangementLine.getWarehouseId()));
         }
		   
		 if (searchOrderMangementLine.getPreOutboundNo() != null && !searchOrderMangementLine.getPreOutboundNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("preOutboundNo");
        	 predicates.add(group.in(searchOrderMangementLine.getPreOutboundNo()));
         }
         
         if (searchOrderMangementLine.getRefDocNumber() != null && !searchOrderMangementLine.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchOrderMangementLine.getRefDocNumber()));
         }       		 
		 
		 if (searchOrderMangementLine.getPartnerCode() != null && !searchOrderMangementLine.getPartnerCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("partnerCode");
        	 predicates.add(group.in(searchOrderMangementLine.getPartnerCode()));
         }
		 
		 if (searchOrderMangementLine.getItemCode() != null && !searchOrderMangementLine.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchOrderMangementLine.getItemCode()));
         }		 
		 
		 if (searchOrderMangementLine.getOutboundOrderTypeId() != null && !searchOrderMangementLine.getOutboundOrderTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("outboundOrderTypeId");
        	 predicates.add(group.in(searchOrderMangementLine.getOutboundOrderTypeId()));
         }
		 
		  if (searchOrderMangementLine.getStatusId() != null && !searchOrderMangementLine.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchOrderMangementLine.getStatusId()));
         }	
		 
		 if (searchOrderMangementLine.getDescription() != null && !searchOrderMangementLine.getDescription().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("description");
        	 predicates.add(group.in(searchOrderMangementLine.getDescription()));
         }	
		 
		 if (searchOrderMangementLine.getSoType() != null && !searchOrderMangementLine.getSoType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField1");
        	 predicates.add(group.in(searchOrderMangementLine.getSoType()));
         }
		
		  if (searchOrderMangementLine.getStartRequiredDeliveryDate() != null && searchOrderMangementLine.getEndRequiredDeliveryDate() != null) {
        	 predicates.add(cb.between(root.get("requiredDeliveryDate"), searchOrderMangementLine.getStartRequiredDeliveryDate(), 
        			 searchOrderMangementLine.getEndRequiredDeliveryDate()));
         }
		 		
		  if (searchOrderMangementLine.getStartOrderDate() != null && searchOrderMangementLine.getEndOrderDate() != null) {
        	 predicates.add(cb.between(root.get("orderDate"), searchOrderMangementLine.getStartOrderDate(), 
        			 searchOrderMangementLine.getEndOrderDate()));
         }
			
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
