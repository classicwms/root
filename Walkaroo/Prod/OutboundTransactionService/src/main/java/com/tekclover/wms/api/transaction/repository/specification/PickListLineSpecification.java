package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.v2.PickListLine;
import com.tekclover.wms.api.transaction.model.outbound.v2.SearchPickListLine;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PickListLineSpecification implements Specification<PickListLine> {

    SearchPickListLine searchPickListLine;

    public PickListLineSpecification(SearchPickListLine inputSearchParams) {
        this.searchPickListLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PickListLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPickListLine.getCompanyCodeId() != null && !searchPickListLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchPickListLine.getCompanyCodeId()));
        }

        if (searchPickListLine.getPlantId() != null && !searchPickListLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchPickListLine.getPlantId()));
        }

        if (searchPickListLine.getLanguageId() != null && !searchPickListLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchPickListLine.getLanguageId()));
        }

        if (searchPickListLine.getWarehouseId() != null && !searchPickListLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPickListLine.getWarehouseId()));
        }

        if (searchPickListLine.getPartnerCode() != null && !searchPickListLine.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchPickListLine.getPartnerCode()));
        }

        if (searchPickListLine.getLineNumber() != null && !searchPickListLine.getLineNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("lineNumber");
            predicates.add(group.in(searchPickListLine.getLineNumber()));
        }

        if (searchPickListLine.getItemCode() != null && !searchPickListLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchPickListLine.getItemCode()));
        }

        // orderType - Ref_Field_1
        if (searchPickListLine.getOrderType() != null && !searchPickListLine.getOrderType().isEmpty()) {
            final Path<Group> group = root.<Group>get("referenceField1");
            predicates.add(group.in(searchPickListLine.getOrderType()));
        }

        if (searchPickListLine.getStatusId() != null && !searchPickListLine.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchPickListLine.getStatusId()));
        }

        if (searchPickListLine.getFromDeliveryDate() != null && searchPickListLine.getToDeliveryDate() != null) {
            predicates.add(cb.between(root.get("deliveryConfirmedOn"),
                    searchPickListLine.getFromDeliveryDate(), searchPickListLine.getToDeliveryDate()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
