package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.packing.PackingLine;
import com.tekclover.wms.api.transaction.model.outbound.packing.SearchPackingLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PackingLineSpecification implements Specification<PackingLine> {

    SearchPackingLine searchPackingLine;

    public PackingLineSpecification(SearchPackingLine inputSearchParams) {
        this.searchPackingLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PackingLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPackingLine.getPackingNo() != null && !searchPackingLine.getPackingNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("packingNo");
            predicates.add(group.in(searchPackingLine.getPackingNo()));
        }
        if (searchPackingLine.getLanguageId() != null && !searchPackingLine.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchPackingLine.getLanguageId()));
        }
        if (searchPackingLine.getCompanyCodeId() != null && !searchPackingLine.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchPackingLine.getCompanyCodeId()));
        }
        if (searchPackingLine.getPlantId() != null && !searchPackingLine.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchPackingLine.getPlantId()));
        }
        if (searchPackingLine.getWarehouseId() != null && !searchPackingLine.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchPackingLine.getWarehouseId()));
        }
        if (searchPackingLine.getPreOutboundNo() != null && !searchPackingLine.getPreOutboundNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("preOutboundNo");
            predicates.add(group.in(searchPackingLine.getPreOutboundNo()));
        }
        if (searchPackingLine.getRefDocNumber() != null && !searchPackingLine.getRefDocNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("refDocNumber");
            predicates.add(group.in(searchPackingLine.getRefDocNumber()));
        }
        if (searchPackingLine.getPartnerCode() != null && !searchPackingLine.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("partnerCode");
            predicates.add(group.in(searchPackingLine.getPartnerCode()));
        }
        if (searchPackingLine.getLineNumber() != null && !searchPackingLine.getLineNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("lineNumber");
            predicates.add(group.in(searchPackingLine.getLineNumber()));
        }
        if (searchPackingLine.getItemCode() != null && !searchPackingLine.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchPackingLine.getItemCode()));
        }
        if (searchPackingLine.getStatusId() != null && !searchPackingLine.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(searchPackingLine.getStatusId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
