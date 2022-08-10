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

import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.SearchInventoryMovement;

@SuppressWarnings("serial")
public class InventoryMovementSpecification implements Specification<InventoryMovement> {
	
	SearchInventoryMovement searchInventoryMovement;
	
	public InventoryMovementSpecification(SearchInventoryMovement inputSearchParams) {
		this.searchInventoryMovement = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<InventoryMovement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

		 if (searchInventoryMovement.getWarehouseId() != null && !searchInventoryMovement.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchInventoryMovement.getWarehouseId()));
         }

         if (searchInventoryMovement.getMovementType() != null && !searchInventoryMovement.getMovementType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("movementType");
        	 predicates.add(group.in(searchInventoryMovement.getMovementType()));
         }
		 
		 if (searchInventoryMovement.getSubmovementType() != null && !searchInventoryMovement.getSubmovementType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("submovementType");
        	 predicates.add(group.in(searchInventoryMovement.getSubmovementType()));
         }
		 
		  if (searchInventoryMovement.getPalletCode() != null && !searchInventoryMovement.getPalletCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("palletCode");
        	 predicates.add(group.in(searchInventoryMovement.getPalletCode()));
         }
		 
		  if (searchInventoryMovement.getCaseCode() != null && !searchInventoryMovement.getCaseCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("caseCode");
        	 predicates.add(group.in(searchInventoryMovement.getCaseCode()));
         }
		 		 
		  if (searchInventoryMovement.getPackBarcodes() != null && !searchInventoryMovement.getPackBarcodes().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("packBarcodes");
        	 predicates.add(group.in(searchInventoryMovement.getPackBarcodes()));
         }
		 
		 if (searchInventoryMovement.getItemCode() != null && !searchInventoryMovement.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchInventoryMovement.getItemCode()));
         }
		 
		  if (searchInventoryMovement.getVariantCode() != null && !searchInventoryMovement.getVariantCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("variantCode");
        	 predicates.add(group.in(searchInventoryMovement.getVariantCode()));
         }
		 
		 if (searchInventoryMovement.getVariantSubCode() != null && !searchInventoryMovement.getVariantSubCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("variantSubCode");
        	 predicates.add(group.in(searchInventoryMovement.getVariantSubCode()));
         }
		 
		 if (searchInventoryMovement.getBatchSerialNumber() != null && !searchInventoryMovement.getBatchSerialNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("batchSerialNumber");
        	 predicates.add(group.in(searchInventoryMovement.getBatchSerialNumber()));
         }
		 
		 if (searchInventoryMovement.getMovementDocumentNo() != null && !searchInventoryMovement.getMovementDocumentNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("movementDocumentNo");
        	 predicates.add(group.in(searchInventoryMovement.getMovementDocumentNo()));
         }	  
		 
		 if (searchInventoryMovement.getFromCreatedOn() != null && searchInventoryMovement.getToCreatedOn() != null) {
        	 predicates.add(cb.between(root.get("createdOn"), searchInventoryMovement.getFromCreatedOn(), searchInventoryMovement.getToCreatedOn()));
         }
		          	         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
