package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.SearchQualityLineV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class QualityLineV2Specification implements Specification<QualityLineV2> {

    SearchQualityLineV2 searchQualityLine;

    public QualityLineV2Specification(SearchQualityLineV2 inputSearchParams) {
        this.searchQualityLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<QualityLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchQualityLine.getCompanyCodeId() != null && !searchQualityLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchQualityLine.getCompanyCodeId()));
        }

        if (searchQualityLine.getPlantId() != null && !searchQualityLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchQualityLine.getPlantId()));
        }

        if (searchQualityLine.getLanguageId() != null && !searchQualityLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchQualityLine.getLanguageId()));
        }

        if (searchQualityLine.getWarehouseId() != null && !searchQualityLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchQualityLine.getWarehouseId()));
        }

        if (searchQualityLine.getPreOutboundNo() != null && !searchQualityLine.getPreOutboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preOutboundNo");
            predicates.add(group.in(searchQualityLine.getPreOutboundNo()));
        }

        if (searchQualityLine.getRefDocNumber() != null && !searchQualityLine.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchQualityLine.getRefDocNumber()));
        }

        if (searchQualityLine.getPartnerCode() != null && !searchQualityLine.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchQualityLine.getPartnerCode()));
        }

        if (searchQualityLine.getLineNumber() != null && !searchQualityLine.getLineNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("lineNumber");
            predicates.add(group.in(searchQualityLine.getLineNumber()));
        }

        if (searchQualityLine.getQualityInspectionNo() != null && !searchQualityLine.getQualityInspectionNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("qualityInspectionNo");
            predicates.add(group.in(searchQualityLine.getQualityInspectionNo()));
        }

        if (searchQualityLine.getItemCode() != null && !searchQualityLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchQualityLine.getItemCode()));
        }

        if (searchQualityLine.getStatusId() != null && !searchQualityLine.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchQualityLine.getStatusId()));
        }

        if (searchQualityLine.getMaterialNo() != null && !searchQualityLine.getMaterialNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("materialNo");
            predicates.add(group.in(searchQualityLine.getMaterialNo()));
        }

        if (searchQualityLine.getPriceSegment() != null && !searchQualityLine.getPriceSegment().isEmpty()) {
            final Path<Group> group = root.<Group>get("priceSegment");
            predicates.add(group.in(searchQualityLine.getPriceSegment()));
        }

        if (searchQualityLine.getArticleNo() != null && !searchQualityLine.getArticleNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("articleNo");
            predicates.add(group.in(searchQualityLine.getArticleNo()));
        }
        if (searchQualityLine.getGender() != null && !searchQualityLine.getGender().isEmpty()) {
            final Path<Group> group = root.<Group>get("gender");
            predicates.add(group.in(searchQualityLine.getGender()));
        }

        if (searchQualityLine.getColor() != null && !searchQualityLine.getColor().isEmpty()) {
            final Path<Group> group = root.<Group>get("color");
            predicates.add(group.in(searchQualityLine.getColor()));
        }

        if (searchQualityLine.getSize() != null && !searchQualityLine.getSize().isEmpty()) {
            final Path<Group> group = root.<Group>get("size");
            predicates.add(group.in(searchQualityLine.getSize()));
        }
        if (searchQualityLine.getNoPairs() != null && !searchQualityLine.getNoPairs().isEmpty()) {
            final Path<Group> group = root.<Group>get("noPairs");
            predicates.add(group.in(searchQualityLine.getNoPairs()));
        }
        if (searchQualityLine.getBarcodeId() != null && !searchQualityLine.getBarcodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("barcodeId");
            predicates.add(group.in(searchQualityLine.getBarcodeId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
