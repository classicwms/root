package com.tekclover.wms.api.inbound.orders.repository.specification;

import com.tekclover.wms.api.inbound.orders.model.unallocatedorder.SearchUnallocatedOrderLineV2;
import com.tekclover.wms.api.inbound.orders.model.unallocatedorder.UnallocatedOrderLineV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class UnallocatedOrderLineV2Specification implements Specification<UnallocatedOrderLineV2> {

    SearchUnallocatedOrderLineV2 searchUnallocatedOrderLineV2;

    public UnallocatedOrderLineV2Specification(SearchUnallocatedOrderLineV2 inputSearchParams) {
        this.searchUnallocatedOrderLineV2 = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<UnallocatedOrderLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchUnallocatedOrderLineV2.getCompanyCodeId() != null && !searchUnallocatedOrderLineV2.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getCompanyCodeId()));
        }

        if (searchUnallocatedOrderLineV2.getPlantId() != null && !searchUnallocatedOrderLineV2.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getPlantId()));
        }

        if (searchUnallocatedOrderLineV2.getLanguageId() != null && !searchUnallocatedOrderLineV2.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getLanguageId()));
        }

        if (searchUnallocatedOrderLineV2.getWarehouseId() != null && !searchUnallocatedOrderLineV2.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getWarehouseId()));
        }

        if (searchUnallocatedOrderLineV2.getPreOutboundNo() != null && !searchUnallocatedOrderLineV2.getPreOutboundNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("preOutboundNo");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getPreOutboundNo()));
        }

        if (searchUnallocatedOrderLineV2.getRefDocNumber() != null && !searchUnallocatedOrderLineV2.getRefDocNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("refDocNumber");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getRefDocNumber()));
        }

        if (searchUnallocatedOrderLineV2.getPartnerCode() != null && !searchUnallocatedOrderLineV2.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("partnerCode");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getPartnerCode()));
        }

        if (searchUnallocatedOrderLineV2.getManufacturerName() != null && !searchUnallocatedOrderLineV2.getManufacturerName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerName");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getManufacturerName()));
        }

        if (searchUnallocatedOrderLineV2.getItemCode() != null && !searchUnallocatedOrderLineV2.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getItemCode()));
        }

        if (searchUnallocatedOrderLineV2.getOutboundOrderTypeId() != null && !searchUnallocatedOrderLineV2.getOutboundOrderTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("outboundOrderTypeId");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getOutboundOrderTypeId()));
        }

        if (searchUnallocatedOrderLineV2.getStatusId() != null && !searchUnallocatedOrderLineV2.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getStatusId()));
        }

        if (searchUnallocatedOrderLineV2.getDescription() != null && !searchUnallocatedOrderLineV2.getDescription().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("description");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getDescription()));
        }

        if (searchUnallocatedOrderLineV2.getSoType() != null && !searchUnallocatedOrderLineV2.getSoType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("referenceField1");
            predicates.add(group.in(searchUnallocatedOrderLineV2.getSoType()));
        }

        if (searchUnallocatedOrderLineV2.getStartRequiredDeliveryDate() != null && searchUnallocatedOrderLineV2.getEndRequiredDeliveryDate() != null) {
            predicates.add(cb.between(root.get("requiredDeliveryDate"), searchUnallocatedOrderLineV2.getStartRequiredDeliveryDate(),
                    searchUnallocatedOrderLineV2.getEndRequiredDeliveryDate()));
        }

        if (searchUnallocatedOrderLineV2.getStartOrderDate() != null && searchUnallocatedOrderLineV2.getEndOrderDate() != null) {
            predicates.add(cb.between(root.get("orderDate"), searchUnallocatedOrderLineV2.getStartOrderDate(),
                    searchUnallocatedOrderLineV2.getEndOrderDate()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
