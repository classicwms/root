//package com.tekclover.wms.api.inbound.transaction.repository.specification;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Path;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//
//import org.springframework.context.annotation.DeferredImportSelector.Group;
//import org.springframework.data.jpa.domain.Specification;
//
//import com.tekclover.wms.api.inbound.transaction.model.inbound.stock.InventoryStock;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.stock.SearchInventoryStock;
//
//@SuppressWarnings("serial")
//public class InventoryStockSpecification implements Specification<InventoryStock> {
//
//	SearchInventoryStock searchInventoryStock;
//
//	/**
//	 *
//	 * @param inputSearchParams
//	 */
//	public InventoryStockSpecification(SearchInventoryStock inputSearchParams) {
//		this.searchInventoryStock = inputSearchParams;
//	}
//
//	/**
//	 *
//	 */
//	@Override
//    public Predicate toPredicate(Root<InventoryStock> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//
//         List<Predicate> predicates = new ArrayList<Predicate>();
//
//		 if (searchInventoryStock.getWarehouseId() != null && !searchInventoryStock.getWarehouseId().isEmpty()) {
//        	 final Path<Group> group = root.<Group> get("warehouseId");
//        	 predicates.add(group.in(searchInventoryStock.getWarehouseId()));
//         }
//
//         if (searchInventoryStock.getPackBarcodes() != null && !searchInventoryStock.getPackBarcodes().isEmpty()) {
//        	 final Path<Group> group = root.<Group> get("packBarcodes");
//        	 predicates.add(group.in(searchInventoryStock.getPackBarcodes()));
//         }
//
//		 if (searchInventoryStock.getItemCode() != null && !searchInventoryStock.getItemCode().isEmpty()) {
//        	 final Path<Group> group = root.<Group> get("itemCode");
//        	 predicates.add(group.in(searchInventoryStock.getItemCode()));
//         }
//
//		 if (searchInventoryStock.getStorageBin() != null && !searchInventoryStock.getStorageBin().isEmpty()) {
//        	 final Path<Group> group = root.<Group> get("storageBin");
//        	 predicates.add(group.in(searchInventoryStock.getStorageBin()));
//         }
//
//		 if (searchInventoryStock.getStockTypeId() != null && !searchInventoryStock.getStockTypeId().isEmpty()) {
//        	 final Path<Group> group = root.<Group> get("stockTypeId");
//        	 predicates.add(group.in(searchInventoryStock.getStockTypeId()));
//         }
//
//		 if (searchInventoryStock.getSpecialStockIndicatorId() != null && !searchInventoryStock.getSpecialStockIndicatorId().isEmpty()) {
//        	 final Path<Group> group = root.<Group> get("specialStockIndicatorId");
//        	 predicates.add(group.in(searchInventoryStock.getSpecialStockIndicatorId()));
//         }
//
//		 if (searchInventoryStock.getStorageSectionId() != null && !searchInventoryStock.getStorageSectionId().isEmpty()) {
//        	 final Path<Group> group = root.<Group> get("referenceField10");
//        	 predicates.add(group.in(searchInventoryStock.getStorageSectionId()));
//         }
//
//		 if (searchInventoryStock.getBinClassId() != null && !searchInventoryStock.getBinClassId().isEmpty()) {
//        	 final Path<Group> group = root.<Group> get("binClassId");
//        	 predicates.add(group.in(searchInventoryStock.getBinClassId()));
//         }
//
//		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
//         return cb.and(predicates.toArray(new Predicate[] {}));
//     }
//}
