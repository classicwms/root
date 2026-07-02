package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.SearchOrderManagementLineV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OrderManagementLineV2Specification implements Specification<OrderManagementLineV2> {

    SearchOrderManagementLineV2 searchOrderMangementLine;

    public OrderManagementLineV2Specification(SearchOrderManagementLineV2 inputSearchParams) {
        this.searchOrderMangementLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<OrderManagementLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchOrderMangementLine.getCompanyCodeId() != null && !searchOrderMangementLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchOrderMangementLine.getCompanyCodeId()));
        }

        if (searchOrderMangementLine.getPlantId() != null && !searchOrderMangementLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchOrderMangementLine.getPlantId()));
        }

        if (searchOrderMangementLine.getLanguageId() != null && !searchOrderMangementLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchOrderMangementLine.getLanguageId()));
        }

        if (searchOrderMangementLine.getWarehouseId() != null && !searchOrderMangementLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchOrderMangementLine.getWarehouseId()));
        }

        if (searchOrderMangementLine.getPreOutboundNo() != null && !searchOrderMangementLine.getPreOutboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preOutboundNo");
            predicates.add(group.in(searchOrderMangementLine.getPreOutboundNo()));
        }

        if (searchOrderMangementLine.getRefDocNumber() != null && !searchOrderMangementLine.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchOrderMangementLine.getRefDocNumber()));
        }

        if (searchOrderMangementLine.getPartnerCode() != null && !searchOrderMangementLine.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchOrderMangementLine.getPartnerCode()));
        }

        if (searchOrderMangementLine.getManufacturerName() != null && !searchOrderMangementLine.getManufacturerName().isEmpty()) {
            final Path<Group> group = root.<Group>get("manufacturerName");
            predicates.add(group.in(searchOrderMangementLine.getManufacturerName()));
        }

        if (searchOrderMangementLine.getItemCode() != null && !searchOrderMangementLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchOrderMangementLine.getItemCode()));
        }

        if (searchOrderMangementLine.getOutboundOrderTypeId() != null && !searchOrderMangementLine.getOutboundOrderTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("outboundOrderTypeId");
            predicates.add(group.in(searchOrderMangementLine.getOutboundOrderTypeId()));
        }

        if (searchOrderMangementLine.getStatusId() != null && !searchOrderMangementLine.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchOrderMangementLine.getStatusId()));
        }

        if (searchOrderMangementLine.getDescription() != null && !searchOrderMangementLine.getDescription().isEmpty()) {
            final Path<Group> group = root.<Group>get("description");
            predicates.add(group.in(searchOrderMangementLine.getDescription()));
        }

        if (searchOrderMangementLine.getSoType() != null && !searchOrderMangementLine.getSoType().isEmpty()) {
            final Path<Group> group = root.<Group>get("referenceField1");
            predicates.add(group.in(searchOrderMangementLine.getSoType()));
        }

        if (searchOrderMangementLine.getMaterialNo() != null && !searchOrderMangementLine.getMaterialNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("materialNo");
            predicates.add(group.in(searchOrderMangementLine.getMaterialNo()));
        }

        if (searchOrderMangementLine.getPriceSegment() != null && !searchOrderMangementLine.getPriceSegment().isEmpty()) {
            final Path<Group> group = root.<Group>get("priceSegment");
            predicates.add(group.in(searchOrderMangementLine.getPriceSegment()));
        }

        if (searchOrderMangementLine.getArticleNo() != null && !searchOrderMangementLine.getArticleNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("articleNo");
            predicates.add(group.in(searchOrderMangementLine.getArticleNo()));
        }
        if (searchOrderMangementLine.getGender() != null && !searchOrderMangementLine.getGender().isEmpty()) {
            final Path<Group> group = root.<Group>get("gender");
            predicates.add(group.in(searchOrderMangementLine.getGender()));
        }

        if (searchOrderMangementLine.getColor() != null && !searchOrderMangementLine.getColor().isEmpty()) {
            final Path<Group> group = root.<Group>get("color");
            predicates.add(group.in(searchOrderMangementLine.getColor()));
        }

        if (searchOrderMangementLine.getSize() != null && !searchOrderMangementLine.getSize().isEmpty()) {
            final Path<Group> group = root.<Group>get("size");
            predicates.add(group.in(searchOrderMangementLine.getSize()));
        }
        if (searchOrderMangementLine.getNoPairs() != null && !searchOrderMangementLine.getNoPairs().isEmpty()) {
            final Path<Group> group = root.<Group>get("noPairs");
            predicates.add(group.in(searchOrderMangementLine.getNoPairs()));
        }
        if (searchOrderMangementLine.getBarcodeId() != null && !searchOrderMangementLine.getBarcodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("barcodeId");
            predicates.add(group.in(searchOrderMangementLine.getBarcodeId()));
        }

        if (searchOrderMangementLine.getStartRequiredDeliveryDate() != null && searchOrderMangementLine.getEndRequiredDeliveryDate() != null) {
            predicates.add(cb.between(root.get("requiredDeliveryDate"), searchOrderMangementLine.getStartRequiredDeliveryDate(),
                    searchOrderMangementLine.getEndRequiredDeliveryDate()));
        }

        if (searchOrderMangementLine.getStartOrderDate() != null && searchOrderMangementLine.getEndOrderDate() != null) {
            predicates.add(cb.between(root.get("orderDate"), searchOrderMangementLine.getStartOrderDate(),
                    searchOrderMangementLine.getEndOrderDate()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
