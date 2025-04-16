package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.packing.PackingHeader;
import com.tekclover.wms.api.transaction.model.outbound.packing.PackingLine;
import com.tekclover.wms.api.transaction.model.outbound.packing.SearchPackingHeader;
import com.tekclover.wms.api.transaction.model.outbound.packing.SearchPackingLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PackingHeaderSpecification implements Specification<PackingHeader> {

    SearchPackingHeader searchPackingHeader;

    public PackingHeaderSpecification(SearchPackingHeader inputSearchParams) {
        this.searchPackingHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PackingHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPackingHeader.getPackingNo() != null && !searchPackingHeader.getPackingNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("packingNo");
            predicates.add(group.in(searchPackingHeader.getPackingNo()));
        }
        if (searchPackingHeader.getLanguageId() != null && !searchPackingHeader.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchPackingHeader.getLanguageId()));
        }
        if (searchPackingHeader.getCompanyCodeId() != null && !searchPackingHeader.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchPackingHeader.getCompanyCodeId()));
        }
        if (searchPackingHeader.getPlantId() != null && !searchPackingHeader.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchPackingHeader.getPlantId()));
        }
        if (searchPackingHeader.getWarehouseId() != null && !searchPackingHeader.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchPackingHeader.getWarehouseId()));
        }
        if (searchPackingHeader.getPreOutboundNo() != null && !searchPackingHeader.getPreOutboundNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("preOutboundNo");
            predicates.add(group.in(searchPackingHeader.getPreOutboundNo()));
        }
        if (searchPackingHeader.getRefDocNumber() != null && !searchPackingHeader.getRefDocNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("refDocNumber");
            predicates.add(group.in(searchPackingHeader.getRefDocNumber()));
        }
        if (searchPackingHeader.getPartnerCode() != null && !searchPackingHeader.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("partnerCode");
            predicates.add(group.in(searchPackingHeader.getPartnerCode()));
        }
        if (searchPackingHeader.getQualityInspectionNo() != null && !searchPackingHeader.getQualityInspectionNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("qualityInspectionNo");
            predicates.add(group.in(searchPackingHeader.getQualityInspectionNo()));
        }
        if (searchPackingHeader.getStatusId() != null && !searchPackingHeader.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(searchPackingHeader.getStatusId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
