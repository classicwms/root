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

import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupLine;

@SuppressWarnings("serial")
public class PickupLineSpecification implements Specification<PickupLine> {
	
	SearchPickupLine searchPickupLine;
	
	public PickupLineSpecification(SearchPickupLine inputSearchParams) {
		this.searchPickupLine = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<PickupLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchPickupLine.getWarehouseId() != null && !searchPickupLine.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchPickupLine.getWarehouseId()));
         }		   
		
		 if (searchPickupLine.getPreOutboundNo() != null && !searchPickupLine.getPreOutboundNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("preOutboundNo");
        	 predicates.add(group.in(searchPickupLine.getPreOutboundNo()));
         }		 
		 	
         if (searchPickupLine.getRefDocNumber() != null && !searchPickupLine.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchPickupLine.getRefDocNumber()));
         }   
		 
		 if (searchPickupLine.getPartnerCode() != null && !searchPickupLine.getPartnerCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("partnerCode");
        	 predicates.add(group.in(searchPickupLine.getPartnerCode()));
         }
		 
		 if (searchPickupLine.getLineNumber() != null && !searchPickupLine.getLineNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("lineNumber");
        	 predicates.add(group.in(searchPickupLine.getLineNumber()));
         }         
		 
		 if (searchPickupLine.getPickupNumber() != null && !searchPickupLine.getPickupNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("pickupNumber");
        	 predicates.add(group.in(searchPickupLine.getPickupNumber()));
         }         
		 
		 if (searchPickupLine.getItemCode() != null && !searchPickupLine.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchPickupLine.getItemCode()));
         }		 
		 
		 if (searchPickupLine.getActualHeNo() != null && !searchPickupLine.getActualHeNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("actualHeNo");
        	 predicates.add(group.in(searchPickupLine.getActualHeNo()));
         }
		 
		 if (searchPickupLine.getPickedStorageBin() != null && !searchPickupLine.getPickedStorageBin().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("pickedStorageBin");
        	 predicates.add(group.in(searchPickupLine.getPickedStorageBin()));
         }
		 
		 if (searchPickupLine.getPickedPackCode() != null && !searchPickupLine.getPickedPackCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("pickedPackCode");
        	 predicates.add(group.in(searchPickupLine.getPickedPackCode()));
		 }

		predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
			
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
