package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.SearchPreOutboundLineV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PreOutboundLineV2Specification implements Specification<PreOutboundLineV2> {

    SearchPreOutboundLineV2 searchPreOutboundLine;

    public PreOutboundLineV2Specification(SearchPreOutboundLineV2 inputSearchParams) {
        this.searchPreOutboundLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PreOutboundLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPreOutboundLine.getCompanyCodeId() != null && !searchPreOutboundLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchPreOutboundLine.getCompanyCodeId()));
        }

        if (searchPreOutboundLine.getPlantId() != null && !searchPreOutboundLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchPreOutboundLine.getPlantId()));
        }

        if (searchPreOutboundLine.getLanguageId() != null && !searchPreOutboundLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchPreOutboundLine.getLanguageId()));
        }

        if (searchPreOutboundLine.getWarehouseId() != null && !searchPreOutboundLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPreOutboundLine.getWarehouseId()));
        }

        if (searchPreOutboundLine.getRefDocNumber() != null && !searchPreOutboundLine.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchPreOutboundLine.getRefDocNumber()));
        }

        if (searchPreOutboundLine.getPreOutboundNo() != null && !searchPreOutboundLine.getPreOutboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preOutboundNo");
            predicates.add(group.in(searchPreOutboundLine.getPreOutboundNo()));
        }

        if (searchPreOutboundLine.getPartnerCode() != null && !searchPreOutboundLine.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchPreOutboundLine.getPartnerCode()));
        }

        if (searchPreOutboundLine.getLineNumber() != null && !searchPreOutboundLine.getLineNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("lineNumber");
            predicates.add(group.in(searchPreOutboundLine.getLineNumber()));
        }

        if (searchPreOutboundLine.getItemCode() != null && !searchPreOutboundLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchPreOutboundLine.getItemCode()));
        }

        if (searchPreOutboundLine.getMaterialNo() != null && !searchPreOutboundLine.getMaterialNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("materialNo");
            predicates.add(group.in(searchPreOutboundLine.getMaterialNo()));
        }

        if (searchPreOutboundLine.getPriceSegment() != null && !searchPreOutboundLine.getPriceSegment().isEmpty()) {
            final Path<Group> group = root.<Group>get("priceSegment");
            predicates.add(group.in(searchPreOutboundLine.getPriceSegment()));
        }

        if (searchPreOutboundLine.getArticleNo() != null && !searchPreOutboundLine.getArticleNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("articleNo");
            predicates.add(group.in(searchPreOutboundLine.getArticleNo()));
        }
        if (searchPreOutboundLine.getGender() != null && !searchPreOutboundLine.getGender().isEmpty()) {
            final Path<Group> group = root.<Group>get("gender");
            predicates.add(group.in(searchPreOutboundLine.getGender()));
        }

        if (searchPreOutboundLine.getColor() != null && !searchPreOutboundLine.getColor().isEmpty()) {
            final Path<Group> group = root.<Group>get("color");
            predicates.add(group.in(searchPreOutboundLine.getColor()));
        }

        if (searchPreOutboundLine.getSize() != null && !searchPreOutboundLine.getSize().isEmpty()) {
            final Path<Group> group = root.<Group>get("size");
            predicates.add(group.in(searchPreOutboundLine.getSize()));
        }
        if (searchPreOutboundLine.getNoPairs() != null && !searchPreOutboundLine.getNoPairs().isEmpty()) {
            final Path<Group> group = root.<Group>get("noPairs");
            predicates.add(group.in(searchPreOutboundLine.getNoPairs()));
        }
        if (searchPreOutboundLine.getBarcodeId() != null && !searchPreOutboundLine.getBarcodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("barcodeId");
            predicates.add(group.in(searchPreOutboundLine.getBarcodeId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
