package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.inbound.staging.v2.SearchStagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StagingHeaderV2Specification implements Specification<StagingHeaderV2> {

    SearchStagingHeaderV2 searchStagingHeader;

    public StagingHeaderV2Specification(SearchStagingHeaderV2 inputSearchParams) {
        this.searchStagingHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StagingHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchStagingHeader.getCompanyCodeId() != null && !searchStagingHeader.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchStagingHeader.getCompanyCodeId()));
        }

        if (searchStagingHeader.getPlantId() != null && !searchStagingHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchStagingHeader.getPlantId()));
        }

        if (searchStagingHeader.getLanguageId() != null && !searchStagingHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchStagingHeader.getLanguageId()));
        }

        if (searchStagingHeader.getWarehouseId() != null && !searchStagingHeader.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchStagingHeader.getWarehouseId()));
        }

        if (searchStagingHeader.getPreInboundNo() != null && !searchStagingHeader.getPreInboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preInboundNo");
            predicates.add(group.in(searchStagingHeader.getPreInboundNo()));
        }

        if (searchStagingHeader.getRefDocNumber() != null && !searchStagingHeader.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchStagingHeader.getRefDocNumber()));
        }

        if (searchStagingHeader.getStagingNo() != null && !searchStagingHeader.getStagingNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("stagingNo");
            predicates.add(group.in(searchStagingHeader.getStagingNo()));
        }

        if (searchStagingHeader.getInboundOrderTypeId() != null && !searchStagingHeader.getInboundOrderTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("inboundOrderTypeId");
            predicates.add(group.in(searchStagingHeader.getInboundOrderTypeId()));
        }

        if (searchStagingHeader.getStatusId() != null && !searchStagingHeader.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchStagingHeader.getStatusId()));
        }

        if (searchStagingHeader.getCreatedBy() != null && !searchStagingHeader.getCreatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("createdBy");
            predicates.add(group.in(searchStagingHeader.getCreatedBy()));
        }

        if (searchStagingHeader.getStartCreatedOn() != null && searchStagingHeader.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchStagingHeader.getStartCreatedOn(), searchStagingHeader.getEndCreatedOn()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}