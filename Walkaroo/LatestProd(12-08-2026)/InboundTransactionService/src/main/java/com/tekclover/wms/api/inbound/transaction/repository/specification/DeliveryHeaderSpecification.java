package com.tekclover.wms.api.inbound.transaction.repository.specification;

import com.tekclover.wms.api.inbound.transaction.model.deliveryheader.DeliveryHeader;
import com.tekclover.wms.api.inbound.transaction.model.deliveryheader.SearchDeliveryHeader;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class DeliveryHeaderSpecification implements Specification<DeliveryHeader> {

    SearchDeliveryHeader searchDeliveryHeader;

    public DeliveryHeaderSpecification(SearchDeliveryHeader inputSearchParams) {
        this.searchDeliveryHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DeliveryHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchDeliveryHeader.getWarehouseId() != null && !searchDeliveryHeader.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchDeliveryHeader.getWarehouseId()));
        }
        if (searchDeliveryHeader.getDeliveryNo() != null && !searchDeliveryHeader.getDeliveryNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("deliveryNo");
            predicates.add(group.in(searchDeliveryHeader.getDeliveryNo()));
        }
        if (searchDeliveryHeader.getPlantId() != null && !searchDeliveryHeader.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchDeliveryHeader.getPlantId()));
        }
        if (searchDeliveryHeader.getCompanyCodeId() != null && !searchDeliveryHeader.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchDeliveryHeader.getCompanyCodeId()));
        }
        if (searchDeliveryHeader.getLanguageId() != null && !searchDeliveryHeader.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchDeliveryHeader.getLanguageId()));
        }
        if (searchDeliveryHeader.getStatusId() != null && !searchDeliveryHeader.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("statusId");
            predicates.add(group.in(searchDeliveryHeader.getStatusId()));
        }
        if (searchDeliveryHeader.getRemarks() != null && !searchDeliveryHeader.getRemarks().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("remarks");
            predicates.add(group.in(searchDeliveryHeader.getRemarks()));
        }
        if (searchDeliveryHeader.getRefDocNumber() != null && !searchDeliveryHeader.getRefDocNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("refDocNumber");
            predicates.add(group.in(searchDeliveryHeader.getRefDocNumber()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
