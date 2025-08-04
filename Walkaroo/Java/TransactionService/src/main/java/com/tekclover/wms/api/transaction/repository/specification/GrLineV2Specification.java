package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.SearchGrLineV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GrLineV2Specification implements Specification<GrLineV2> {

    SearchGrLineV2 searchGrLine;

    public GrLineV2Specification(SearchGrLineV2 inputSearchParams) {
        this.searchGrLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<GrLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchGrLine.getCompanyCodeId() != null && !searchGrLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchGrLine.getCompanyCodeId()));
        }

        if (searchGrLine.getPlantId() != null && !searchGrLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchGrLine.getPlantId()));
        }

        if (searchGrLine.getLanguageId() != null && !searchGrLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchGrLine.getLanguageId()));
        }

        if (searchGrLine.getWarehouseId() != null && !searchGrLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchGrLine.getWarehouseId()));
        }

        if (searchGrLine.getPreInboundNo() != null && !searchGrLine.getPreInboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preInboundNo");
            predicates.add(group.in(searchGrLine.getPreInboundNo()));
        }

        if (searchGrLine.getRefDocNumber() != null && !searchGrLine.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchGrLine.getRefDocNumber()));
        }

        if (searchGrLine.getPackBarcodes() != null && !searchGrLine.getPackBarcodes().isEmpty()) {
            final Path<Group> group = root.<Group>get("packBarcodes");
            predicates.add(group.in(searchGrLine.getPackBarcodes()));
        }

        if (searchGrLine.getLineNo() != null && !searchGrLine.getLineNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("lineNo");
            predicates.add(group.in(searchGrLine.getLineNo()));
        }

        if (searchGrLine.getItemCode() != null && !searchGrLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchGrLine.getItemCode()));
        }

        if (searchGrLine.getCaseCode() != null && !searchGrLine.getCaseCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("caseCode");
            predicates.add(group.in(searchGrLine.getCaseCode()));
        }

        if (searchGrLine.getStatusId() != null && !searchGrLine.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchGrLine.getStatusId()));
        }

        if (searchGrLine.getBarcodeId() != null && !searchGrLine.getBarcodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("barcodeId");
            predicates.add(group.in(searchGrLine.getBarcodeId()));
        }

        if (searchGrLine.getManufacturerCode() != null && !searchGrLine.getManufacturerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("manufacturerCode");
            predicates.add(group.in(searchGrLine.getManufacturerCode()));
        }

        if (searchGrLine.getManufacturerName() != null && !searchGrLine.getManufacturerName().isEmpty()) {
            final Path<Group> group = root.<Group>get("manufacturerName");
            predicates.add(group.in(searchGrLine.getManufacturerName()));
        }

        if (searchGrLine.getOrigin() != null && !searchGrLine.getOrigin().isEmpty()) {
            final Path<Group> group = root.<Group>get("origin");
            predicates.add(group.in(searchGrLine.getOrigin()));
        }

        if (searchGrLine.getInterimStorageBin() != null && !searchGrLine.getInterimStorageBin().isEmpty()) {
            final Path<Group> group = root.<Group>get("interimStorageBin");
            predicates.add(group.in(searchGrLine.getInterimStorageBin()));
        }

        if (searchGrLine.getBrand() != null && !searchGrLine.getBrand().isEmpty()) {
            final Path<Group> group = root.<Group>get("brand");
            predicates.add(group.in(searchGrLine.getBrand()));
        }

        if (searchGrLine.getRejectType() != null && !searchGrLine.getRejectType().isEmpty()) {
            final Path<Group> group = root.<Group>get("rejectType");
            predicates.add(group.in(searchGrLine.getRejectType()));
        }

        if (searchGrLine.getRejectReason() != null && !searchGrLine.getRejectReason().isEmpty()) {
            final Path<Group> group = root.<Group>get("rejectReason");
            predicates.add(group.in(searchGrLine.getRejectReason()));
        }
        if (searchGrLine.getMaterialNo() != null && !searchGrLine.getMaterialNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("materialNo");
            predicates.add(group.in(searchGrLine.getMaterialNo()));
        }

        if (searchGrLine.getPriceSegment() != null && !searchGrLine.getPriceSegment().isEmpty()) {
            final Path<Group> group = root.<Group>get("priceSegment");
            predicates.add(group.in(searchGrLine.getPriceSegment()));
        }

        if (searchGrLine.getArticleNo() != null && !searchGrLine.getArticleNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("articleNo");
            predicates.add(group.in(searchGrLine.getArticleNo()));
        }
        if (searchGrLine.getGender() != null && !searchGrLine.getGender().isEmpty()) {
            final Path<Group> group = root.<Group>get("gender");
            predicates.add(group.in(searchGrLine.getGender()));
        }

        if (searchGrLine.getColor() != null && !searchGrLine.getColor().isEmpty()) {
            final Path<Group> group = root.<Group>get("color");
            predicates.add(group.in(searchGrLine.getColor()));
        }

        if (searchGrLine.getSize() != null && !searchGrLine.getSize().isEmpty()) {
            final Path<Group> group = root.<Group>get("size");
            predicates.add(group.in(searchGrLine.getSize()));
        }
        if (searchGrLine.getNoPairs() != null && !searchGrLine.getNoPairs().isEmpty()) {
            final Path<Group> group = root.<Group>get("noPairs");
            predicates.add(group.in(searchGrLine.getNoPairs()));
        }

        if (searchGrLine.getStartCreatedOn() != null && searchGrLine.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchGrLine.getStartCreatedOn(), searchGrLine.getEndCreatedOn()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
