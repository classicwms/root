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

import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundHeader;

@SuppressWarnings("serial")
public class OutboundHeaderSpecification implements Specification<OutboundHeader> {
	
	SearchOutboundHeader searchOutboundHeader;
	
	public OutboundHeaderSpecification(SearchOutboundHeader inputSearchParams) {
		this.searchOutboundHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<OutboundHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchOutboundHeader.getWarehouseId() != null && !searchOutboundHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchOutboundHeader.getWarehouseId()));
         }
		 
		 if (searchOutboundHeader.getRefDocNumber() != null && !searchOutboundHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchOutboundHeader.getRefDocNumber()));
         }
		 
		  if (searchOutboundHeader.getPartnerCode() != null && !searchOutboundHeader.getPartnerCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("partnerCode");
        	 predicates.add(group.in(searchOutboundHeader.getPartnerCode()));
         }
		 
		 if (searchOutboundHeader.getOutboundOrderTypeId() != null && !searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("outboundOrderTypeId");
        	 predicates.add(group.in(searchOutboundHeader.getOutboundOrderTypeId()));
         }
		 
		 if (searchOutboundHeader.getStatusId() != null && !searchOutboundHeader.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchOutboundHeader.getStatusId()));
         }	
		 
		 if (searchOutboundHeader.getSoType() != null && !searchOutboundHeader.getSoType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField1");
        	 predicates.add(group.in(searchOutboundHeader.getSoType()));
         }
		
		 if (searchOutboundHeader.getStartRequiredDeliveryDate() != null && searchOutboundHeader.getEndRequiredDeliveryDate() != null) {
        	 predicates.add(cb.between(root.get("requiredDeliveryDate"), searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate()));
         }
		 
		 if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
        	 predicates.add(cb.between(root.get("deliveryConfirmedOn"), searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn()));
         }
		 		
		 if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
        	 predicates.add(cb.between(root.get("orderDate"), searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate()));
         }
		 
		// orderType - Ref_Field_1
//		 if (searchOutboundHeader.getRefField1() == null) {
//             predicates.add(cb.equal(root.get("referenceField1"), searchOutboundHeader.getRefField1()));
//         }
			
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
