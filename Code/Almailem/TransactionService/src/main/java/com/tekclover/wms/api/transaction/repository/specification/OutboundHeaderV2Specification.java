package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.SearchOutboundHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OutboundHeaderV2Specification implements Specification<OutboundHeaderV2> {

    SearchOutboundHeaderV2 searchOutboundHeader;

    public OutboundHeaderV2Specification(SearchOutboundHeaderV2 inputSearchParams) {
        this.searchOutboundHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<OutboundHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchOutboundHeader.getCompanyCodeId() != null && !searchOutboundHeader.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchOutboundHeader.getCompanyCodeId()));
        }

        if (searchOutboundHeader.getPlantId() != null && !searchOutboundHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchOutboundHeader.getPlantId()));
        }

        if (searchOutboundHeader.getLanguageId() != null && !searchOutboundHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchOutboundHeader.getLanguageId()));
        }

        if (searchOutboundHeader.getWarehouseId() != null && !searchOutboundHeader.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchOutboundHeader.getWarehouseId()));
        }

        if (searchOutboundHeader.getRefDocNumber() != null && !searchOutboundHeader.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchOutboundHeader.getRefDocNumber()));
        }

        if (searchOutboundHeader.getPartnerCode() != null && !searchOutboundHeader.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchOutboundHeader.getPartnerCode()));
        }

        if (searchOutboundHeader.getOutboundOrderTypeId() != null && !searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("outboundOrderTypeId");
            predicates.add(group.in(searchOutboundHeader.getOutboundOrderTypeId()));
        }

        if (searchOutboundHeader.getStatusId() != null && !searchOutboundHeader.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchOutboundHeader.getStatusId()));
        }

        if (searchOutboundHeader.getSoType() != null && !searchOutboundHeader.getSoType().isEmpty()) {
            final Path<Group> group = root.<Group>get("referenceField1");
            predicates.add(group.in(searchOutboundHeader.getSoType()));
        }

        if (searchOutboundHeader.getStartRequiredDeliveryDate() != null && searchOutboundHeader.getEndRequiredDeliveryDate() != null) {
            predicates.add(cb.between(root.get("requiredDeliveryDate"), searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate()));
        }

        if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
            predicates.add(cb.between(root.get("deliveryConfirmedOn"), searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn()));
        }

        if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate()));
        }

        // orderType - Ref_Field_1
//		 if (searchOutboundHeader.getRefField1() == null) {
//             predicates.add(cb.equal(root.get("referenceField1"), searchOutboundHeader.getRefField1()));
//         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
