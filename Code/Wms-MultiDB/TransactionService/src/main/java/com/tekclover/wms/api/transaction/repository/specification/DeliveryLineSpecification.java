package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.deliveryline.DeliveryLine;
import com.tekclover.wms.api.transaction.model.deliveryline.SearchDeliveryLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DeliveryLineSpecification implements Specification<DeliveryLine> {

    SearchDeliveryLine searchDeliveryLine;

    public DeliveryLineSpecification(SearchDeliveryLine inputSearchParams) {
        this.searchDeliveryLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DeliveryLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchDeliveryLine.getWarehouseId() != null && !searchDeliveryLine.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchDeliveryLine.getWarehouseId()));
        }
        if (searchDeliveryLine.getDeliveryNo() != null && !searchDeliveryLine.getDeliveryNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("deliveryNo");
            predicates.add(group.in(searchDeliveryLine.getDeliveryNo()));
        }
        if (searchDeliveryLine.getPlantId() != null && !searchDeliveryLine.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchDeliveryLine.getPlantId()));
        }
        if (searchDeliveryLine.getCompanyCodeId() != null && !searchDeliveryLine.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchDeliveryLine.getCompanyCodeId()));
        }
        if (searchDeliveryLine.getLanguageId() != null && !searchDeliveryLine.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchDeliveryLine.getLanguageId()));
        }
        if (searchDeliveryLine.getLineNumber() != null && !searchDeliveryLine.getLineNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNumber");
            predicates.add(group.in(searchDeliveryLine.getLineNumber()));
        }
        if (searchDeliveryLine.getItemCode() != null && !searchDeliveryLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchDeliveryLine.getItemCode()));
        }
        if (searchDeliveryLine.getBarcodeId() != null && !searchDeliveryLine.getBarcodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("barcodeId");
            predicates.add(group.in(searchDeliveryLine.getBarcodeId()));
        }
        if (searchDeliveryLine.getManufacturerName() != null && !searchDeliveryLine.getManufacturerName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerName");
            predicates.add(group.in(searchDeliveryLine.getManufacturerName()));
        }
        if (searchDeliveryLine.getInvoiceNumber() != null && !searchDeliveryLine.getInvoiceNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("invoiceNumber");
            predicates.add(group.in(searchDeliveryLine.getInvoiceNumber()));
        }
        if (searchDeliveryLine.getStatusId() != null && !searchDeliveryLine.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(searchDeliveryLine.getStatusId()));
        }
        if (searchDeliveryLine.getRefDocNumber() != null && !searchDeliveryLine.getRefDocNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("refDocNumber");
            predicates.add(group.in(searchDeliveryLine.getRefDocNumber()));
        }
        if (searchDeliveryLine.getRemarks() != null && !searchDeliveryLine.getRemarks().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("remarks");
            predicates.add(group.in(searchDeliveryLine.getRemarks()));
        }
        if (searchDeliveryLine.getVehicleNo() != null && !searchDeliveryLine.getVehicleNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("vehicleNo");
            predicates.add(group.in(searchDeliveryLine.getVehicleNo()));
        }
        if (searchDeliveryLine.getDriverId() != null && !searchDeliveryLine.getDriverId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("driverName");
            predicates.add(group.in(searchDeliveryLine.getDriverId()));
        }
        if (searchDeliveryLine.getReDelivered() != null) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("reDelivered");
            predicates.add(group.in(searchDeliveryLine.getReDelivered()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}