package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.SearchInboundLineV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InboundLineV2Specification implements Specification<InboundLineV2> {

    SearchInboundLineV2 searchInboundLine;

    public InboundLineV2Specification(SearchInboundLineV2 inputSearchParams) {
        this.searchInboundLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InboundLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchInboundLine.getWarehouseId() != null && !searchInboundLine.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchInboundLine.getWarehouseId()));
//            predicates.add(cb.equal(root.get("warehouseId"), searchInboundLine.getWarehouseId()));
        }

        if (searchInboundLine.getStartConfirmedOn() != null && searchInboundLine.getEndConfirmedOn() != null) {
            predicates.add(cb.between(root.get("confirmedOn"), searchInboundLine.getStartConfirmedOn(),
                    searchInboundLine.getEndConfirmedOn()));
        }

        if (searchInboundLine.getStatusId() != null && !searchInboundLine.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(searchInboundLine.getStatusId()));
        }

        if (searchInboundLine.getCompanyCodeId() != null && !searchInboundLine.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(searchInboundLine.getCompanyCodeId()));
        }

        if (searchInboundLine.getItemCode() != null && !searchInboundLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchInboundLine.getItemCode()));
        }

        if (searchInboundLine.getRefDocNumber() != null && !searchInboundLine.getRefDocNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("refDocNumber");
            predicates.add(group.in(searchInboundLine.getRefDocNumber()));
        }

        if (searchInboundLine.getPlantId() != null && !searchInboundLine.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchInboundLine.getPlantId()));
        }

        if (searchInboundLine.getLanguageId() != null && !searchInboundLine.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchInboundLine.getLanguageId()));
        }

        if (searchInboundLine.getReferenceField1() != null) {
            predicates.add(cb.equal(root.get("referenceField1"), searchInboundLine.getReferenceField1()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
