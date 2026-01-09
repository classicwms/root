package com.tekclover.wms.api.inbound.transaction.repository.specification;

import com.tekclover.wms.api.inbound.transaction.model.cyclecount.stockadjustment.SearchStockAdjustment;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.stockadjustment.StockAdjustment;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StockAdjustmentSpecification implements Specification<StockAdjustment> {

	SearchStockAdjustment searchInventory;

	public StockAdjustmentSpecification(SearchStockAdjustment inputSearchParams) {
		this.searchInventory = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<StockAdjustment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

		if (searchInventory.getCompanyCodeId() != null && !searchInventory.getCompanyCodeId().isEmpty()) {
			final Path<Group> group = root.<Group> get("companyCode");
			predicates.add(group.in(searchInventory.getCompanyCodeId()));
		}

		if (searchInventory.getPlantId() != null && !searchInventory.getPlantId().isEmpty()) {
			final Path<Group> group = root.<Group> get("branchCode");
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

		if (searchInventory.getManufacturerName() != null && !searchInventory.getManufacturerName().isEmpty()) {
			final Path<Group> group = root.<Group> get("manufacturerName");
			predicates.add(group.in(searchInventory.getManufacturerName()));
		}
		if (searchInventory.getStockAdjustmentId() != null && !searchInventory.getStockAdjustmentId().isEmpty()) {
			final Path<Group> group = root.<Group> get("stockAdjustmentId");
			predicates.add(group.in(searchInventory.getStockAdjustmentId()));
		}
		if (searchInventory.getStockAdjustmentKey() != null && !searchInventory.getStockAdjustmentKey().isEmpty()) {
			final Path<Group> group = root.<Group> get("stockAdjustmentKey");
			predicates.add(group.in(searchInventory.getStockAdjustmentKey()));
		}

		if (searchInventory.getReferenceDocumentNo() != null && !searchInventory.getReferenceDocumentNo().isEmpty()) {
			final Path<Group> group = root.<Group> get("referenceDocumentNo");
			predicates.add(group.in(searchInventory.getReferenceDocumentNo()));
		}

		predicates.add(cb.equal(root.get("deletionIndicator"), 0L));

		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
