package com.tekclover.wms.api.inbound.transaction.repository.specification;

import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.SearchGrHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GrHeaderV2Specification implements Specification<GrHeaderV2> {

    SearchGrHeaderV2 searchGrHeader;

    public GrHeaderV2Specification(SearchGrHeaderV2 inputSearchParams) {
        this.searchGrHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<GrHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchGrHeader.getCompanyCodeId() != null && !searchGrHeader.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchGrHeader.getCompanyCodeId()));
        }

        if (searchGrHeader.getLanguageId() != null && !searchGrHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchGrHeader.getLanguageId()));
        }

        if (searchGrHeader.getPlantId() != null && !searchGrHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchGrHeader.getPlantId()));
        }

        if (searchGrHeader.getWarehouseId() != null && !searchGrHeader.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchGrHeader.getWarehouseId()));
        }

        if (searchGrHeader.getPreInboundNo() != null && !searchGrHeader.getPreInboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preInboundNo");
            predicates.add(group.in(searchGrHeader.getPreInboundNo()));
        }

        if (searchGrHeader.getRefDocNumber() != null && !searchGrHeader.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchGrHeader.getRefDocNumber()));
        }

        if (searchGrHeader.getGoodsReceiptNo() != null && !searchGrHeader.getGoodsReceiptNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("goodsReceiptNo");
            predicates.add(group.in(searchGrHeader.getGoodsReceiptNo()));
        }

        if (searchGrHeader.getCaseCode() != null && !searchGrHeader.getCaseCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("caseCode");
            predicates.add(group.in(searchGrHeader.getCaseCode()));
        }

        if (searchGrHeader.getInboundOrderTypeId() != null && !searchGrHeader.getInboundOrderTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("inboundOrderTypeId");
            predicates.add(group.in(searchGrHeader.getInboundOrderTypeId()));
        }

        if (searchGrHeader.getStatusId() != null && !searchGrHeader.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchGrHeader.getStatusId()));
        }

        if (searchGrHeader.getCreatedBy() != null && !searchGrHeader.getCreatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("createdBy");
            predicates.add(group.in(searchGrHeader.getCreatedBy()));
        }

        if (searchGrHeader.getStartCreatedOn() != null && searchGrHeader.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchGrHeader.getStartCreatedOn(), searchGrHeader.getEndCreatedOn()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
