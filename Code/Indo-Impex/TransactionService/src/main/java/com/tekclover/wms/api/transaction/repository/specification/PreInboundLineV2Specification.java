package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.SearchPreInboundLineV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PreInboundLineV2Specification implements Specification<PreInboundLineEntityV2> {

    SearchPreInboundLineV2 searchPreInboundLine;

    public PreInboundLineV2Specification(SearchPreInboundLineV2 inputSearchParams) {
        this.searchPreInboundLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PreInboundLineEntityV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPreInboundLine.getCompanyCodeId() != null && !searchPreInboundLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchPreInboundLine.getCompanyCodeId()));
        }

        if (searchPreInboundLine.getPlantId() != null && !searchPreInboundLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchPreInboundLine.getPlantId()));
        }

        if (searchPreInboundLine.getLanguageId() != null && !searchPreInboundLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchPreInboundLine.getLanguageId()));
        }

        if (searchPreInboundLine.getWarehouseId() != null && !searchPreInboundLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPreInboundLine.getWarehouseId()));
        }

        if (searchPreInboundLine.getPreInboundNo() != null && !searchPreInboundLine.getPreInboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preInboundNo");
            predicates.add(group.in(searchPreInboundLine.getPreInboundNo()));
        }
        if (searchPreInboundLine.getItemCode() != null && !searchPreInboundLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchPreInboundLine.getItemCode()));
        }
        if (searchPreInboundLine.getManufacturerName() != null && !searchPreInboundLine.getManufacturerName().isEmpty()) {
            final Path<Group> group = root.<Group>get("manufacturerName");
            predicates.add(group.in(searchPreInboundLine.getManufacturerName()));
        }
        if (searchPreInboundLine.getInvoiceNo() != null && !searchPreInboundLine.getInvoiceNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("invoiceNo");
            predicates.add(group.in(searchPreInboundLine.getInvoiceNo()));
        }
        if (searchPreInboundLine.getPurchaseOrderNumber() != null && !searchPreInboundLine.getPurchaseOrderNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("purchaseOrderNumber");
            predicates.add(group.in(searchPreInboundLine.getPurchaseOrderNumber()));
        }
        if (searchPreInboundLine.getLineNo() != null && !searchPreInboundLine.getLineNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("lineNo");
            predicates.add(group.in(searchPreInboundLine.getLineNo()));
        }

        if (searchPreInboundLine.getInboundOrderTypeId() != null && !searchPreInboundLine.getInboundOrderTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("inboundOrderTypeId");
            predicates.add(group.in(searchPreInboundLine.getInboundOrderTypeId()));
        }

        if (searchPreInboundLine.getRefDocNumber() != null && !searchPreInboundLine.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchPreInboundLine.getRefDocNumber()));
        }

        if (searchPreInboundLine.getStartRefDocDate() != null && searchPreInboundLine.getEndRefDocDate() != null) {
            predicates.add(cb.between(root.get("refDocDate"), searchPreInboundLine.getStartRefDocDate(), searchPreInboundLine.getEndRefDocDate()));
        }

        if (searchPreInboundLine.getStatusId() != null && !searchPreInboundLine.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchPreInboundLine.getStatusId()));
        }

        if (searchPreInboundLine.getStartCreatedOn() != null && searchPreInboundLine.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchPreInboundLine.getStartCreatedOn(), searchPreInboundLine.getEndCreatedOn()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}