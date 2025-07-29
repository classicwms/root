package com.tekclover.wms.api.outbound.transaction.repository.specification;


import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.InventoryV2;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.SearchInventoryV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InventoryV2Specification implements Specification<InventoryV2> {

	SearchInventoryV2 searchInventory;

	public InventoryV2Specification(SearchInventoryV2 inputSearchParams) {
		this.searchInventory = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<InventoryV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

		if (searchInventory.getCompanyCodeId() != null && !searchInventory.getCompanyCodeId().isEmpty()) {
			final Path<Group> group = root.<Group> get("companyCodeId");
			predicates.add(group.in(searchInventory.getCompanyCodeId()));
		}

		if (searchInventory.getPlantId() != null && !searchInventory.getPlantId().isEmpty()) {
					final Path<Group> group = root.<Group> get("plantId");
					predicates.add(group.in(searchInventory.getPlantId()));
		}

		if (searchInventory.getLanguageId() != null && !searchInventory.getLanguageId().isEmpty()) {
					final Path<Group> group = root.<Group> get("languageId");
					predicates.add(group.in(searchInventory.getLanguageId()));
		}

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

		 if (searchInventory.getBarcodeId() != null && !searchInventory.getBarcodeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("barcodeId");
        	 predicates.add(group.in(searchInventory.getBarcodeId()));
         }

         if (searchInventory.getManufacturerCode() != null && !searchInventory.getManufacturerCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("manufacturerCode");
        	 predicates.add(group.in(searchInventory.getManufacturerCode()));
         }
		 
		 if (searchInventory.getReferenceDocumentNo() != null && !searchInventory.getReferenceDocumentNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceDocumentNo");
        	 predicates.add(group.in(searchInventory.getReferenceDocumentNo()));
         }
		 
		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
		     	         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
