//package com.tekclover.wms.api.transaction.repository.specification;
//
//import com.tekclover.wms.api.transaction.model.inbound.v2.SearchSupplierInvoiceHeader;
//import com.tekclover.wms.api.transaction.model.inbound.v2.SupplierInvoiceHeader;
//import org.springframework.context.annotation.DeferredImportSelector.Group;
//import org.springframework.data.jpa.domain.Specification;
//
//import javax.persistence.criteria.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@SuppressWarnings("serial")
//public class SupplierInvoiceHeaderSpecification implements Specification<SupplierInvoiceHeader> {
//
//    SearchSupplierInvoiceHeader searchSupplierInvoiceHeader;
//
//    public SupplierInvoiceHeaderSpecification(SearchSupplierInvoiceHeader inputSearchParams) {
//        this.searchSupplierInvoiceHeader = inputSearchParams;
//    }
//
//    @Override
//    public Predicate toPredicate(Root<SupplierInvoiceHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//
//        List<Predicate> predicates = new ArrayList<Predicate>();
//
//        if (searchSupplierInvoiceHeader.getCompanyCodeId() != null && !searchSupplierInvoiceHeader.getCompanyCodeId().isEmpty()) {
//            final Path<Group> group = root.<Group>get("companyCode");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getCompanyCodeId()));
//        }
//
//        if (searchSupplierInvoiceHeader.getPlantId() != null && !searchSupplierInvoiceHeader.getPlantId().isEmpty()) {
//            final Path<Group> group = root.<Group>get("plantId");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getPlantId()));
//        }
//
//        if (searchSupplierInvoiceHeader.getLanguageId() != null && !searchSupplierInvoiceHeader.getLanguageId().isEmpty()) {
//            final Path<Group> group = root.<Group>get("languageId");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getLanguageId()));
//        }
//
//        if (searchSupplierInvoiceHeader.getWarehouseId() != null && !searchSupplierInvoiceHeader.getWarehouseId().isEmpty()) {
//            final Path<Group> group = root.<Group>get("warehouseId");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getWarehouseId()));
//        }
//
//        if (searchSupplierInvoiceHeader.getInboundOrderTypeId() != null && !searchSupplierInvoiceHeader.getInboundOrderTypeId().isEmpty()) {
//            final Path<Group> group = root.<Group>get("inboundOrderTypeId");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getInboundOrderTypeId()));
//        }
//
//        if (searchSupplierInvoiceHeader.getPurchaseOrderNumber() != null && !searchSupplierInvoiceHeader.getPurchaseOrderNumber().isEmpty()) {
//            final Path<Group> group = root.<Group>get("purchaseOrderNumber");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getPurchaseOrderNumber()));
//        }
//
//        if (searchSupplierInvoiceHeader.getNewContainerNo() != null && !searchSupplierInvoiceHeader.getNewContainerNo().isEmpty()) {
//            final Path<Group> group = root.<Group>get("newContainerNo");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getNewContainerNo()));
//        }
//
//        if (searchSupplierInvoiceHeader.getOldContainerNo() != null && !searchSupplierInvoiceHeader.getOldContainerNo().isEmpty()) {
//            final Path<Group> group = root.<Group>get("oldContainerNo");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getOldContainerNo()));
//        }
//
//        if (searchSupplierInvoiceHeader.getNewPreInboundNo() != null && !searchSupplierInvoiceHeader.getNewPreInboundNo().isEmpty()) {
//            final Path<Group> group = root.<Group>get("newPreInboundNo");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getNewPreInboundNo()));
//        }
//
//        if (searchSupplierInvoiceHeader.getOldPreInboundNo() != null && !searchSupplierInvoiceHeader.getOldPreInboundNo().isEmpty()) {
//            final Path<Group> group = root.<Group>get("oldPreInboundNo");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getOldPreInboundNo()));
//        }
//
//        if (searchSupplierInvoiceHeader.getNewRefDocNumber() != null && !searchSupplierInvoiceHeader.getNewRefDocNumber().isEmpty()) {
//            final Path<Group> group = root.<Group>get("newRefDocNumber");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getNewRefDocNumber()));
//        }
//
//        if (searchSupplierInvoiceHeader.getOldRefDocNumber() != null && !searchSupplierInvoiceHeader.getOldRefDocNumber().isEmpty()) {
//            final Path<Group> group = root.<Group>get("oldRefDocNumber");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getOldRefDocNumber()));
//        }
//
//        if (searchSupplierInvoiceHeader.getOldStatusId() != null && !searchSupplierInvoiceHeader.getOldStatusId().isEmpty()) {
//            final Path<Group> group = root.<Group>get("oldStatusId");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getOldStatusId()));
//        }
//        if (searchSupplierInvoiceHeader.getNewStatusId() != null && !searchSupplierInvoiceHeader.getNewStatusId().isEmpty()) {
//            final Path<Group> group = root.<Group>get("newStatusId");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getNewStatusId()));
//        }
//        if (searchSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId() != null && !searchSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId().isEmpty()) {
//            final Path<Group> group = root.<Group>get("supplierInvoiceCancelHeaderId");
//            predicates.add(group.in(searchSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId()));
//        }
//
//        if (searchSupplierInvoiceHeader.getStartCreatedOn() != null && searchSupplierInvoiceHeader.getEndCreatedOn() != null) {
//            predicates.add(cb.between(root.get("createdOn"), searchSupplierInvoiceHeader.getStartCreatedOn(), searchSupplierInvoiceHeader.getEndCreatedOn()));
//        }
//
//        if (searchSupplierInvoiceHeader.getStartConfirmedOn() != null && searchSupplierInvoiceHeader.getEndConfirmedOn() != null) {
//            predicates.add(cb.between(root.get("confirmedOn"), searchSupplierInvoiceHeader.getStartConfirmedOn(), searchSupplierInvoiceHeader.getEndConfirmedOn()));
//        }
//
//        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
//
//        return cb.and(predicates.toArray(new Predicate[]{}));
//    }
//}