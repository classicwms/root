package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.SearchInventory;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("serial")
public class InventorySpecification implements Specification<Inventory> {
	
	SearchInventory searchInventory;
	
	public InventorySpecification(SearchInventory inputSearchParams) {
		this.searchInventory = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<Inventory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

		 if (searchInventory.getWarehouseId() != null && !searchInventory.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchInventory.getWarehouseId()));
         }

         if (searchInventory.getPackBarcodes() != null && !searchInventory.getPackBarcodes().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("packBarcodes");
        	 predicates.add(group.in(searchInventory.getPackBarcodes()));
         }
		 
		 if (searchInventory.getItemCode() != null && !searchInventory.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchInventory.getItemCode()));
         }
		 
		 if (searchInventory.getStorageBin() != null && !searchInventory.getStorageBin().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("storageBin");
        	 predicates.add(group.in(searchInventory.getStorageBin()));
         }
		 
		 if (searchInventory.getStockTypeId() != null && !searchInventory.getStockTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("stockTypeId");
        	 predicates.add(group.in(searchInventory.getStockTypeId()));
         }
		 
		 if (searchInventory.getSpecialStockIndicatorId() != null && !searchInventory.getSpecialStockIndicatorId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("specialStockIndicatorId");
        	 predicates.add(group.in(searchInventory.getSpecialStockIndicatorId()));
         }

		 if (searchInventory.getStorageSectionId() != null && !searchInventory.getStorageSectionId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField10");
        	 predicates.add(group.in(searchInventory.getStorageSectionId()));
         }
		  
		 if (searchInventory.getBinClassId() != null && !searchInventory.getBinClassId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("binClassId");
        	 predicates.add(group.in(searchInventory.getBinClassId()));
         }
		 
		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
		     	         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}