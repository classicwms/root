package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderV2;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.SearchPerpetualHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PerpetualHeaderV2Specification implements Specification<PerpetualHeaderV2> {

    SearchPerpetualHeaderV2 searchPerpetualHeader;

    public PerpetualHeaderV2Specification(SearchPerpetualHeaderV2 inputSearchParams) {
        this.searchPerpetualHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PerpetualHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPerpetualHeader.getCompanyCodeId() != null && !searchPerpetualHeader.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchPerpetualHeader.getCompanyCodeId()));
        }
        if (searchPerpetualHeader.getPlantId() != null && !searchPerpetualHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchPerpetualHeader.getPlantId()));
        }
        if (searchPerpetualHeader.getLanguageId() != null && !searchPerpetualHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchPerpetualHeader.getLanguageId()));
        }
        if (searchPerpetualHeader.getWarehouseId() != null && !searchPerpetualHeader.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPerpetualHeader.getWarehouseId()));
        }

        if (searchPerpetualHeader.getCycleCountTypeId() != null && !searchPerpetualHeader.getCycleCountTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("cycleCountTypeId");
            predicates.add(group.in(searchPerpetualHeader.getCycleCountTypeId()));
        }

        if (searchPerpetualHeader.getCycleCountNo() != null && !searchPerpetualHeader.getCycleCountNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("cycleCountNo");
            predicates.add(group.in(searchPerpetualHeader.getCycleCountNo()));
        }

        if (searchPerpetualHeader.getHeaderStatusId() != null && !searchPerpetualHeader.getHeaderStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchPerpetualHeader.getHeaderStatusId()));
        }

        if (searchPerpetualHeader.getCreatedBy() != null && !searchPerpetualHeader.getCreatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("createdBy");
            predicates.add(group.in(searchPerpetualHeader.getCreatedBy()));
        }

        if (searchPerpetualHeader.getStartCreatedOn() != null && searchPerpetualHeader.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchPerpetualHeader.getStartCreatedOn(),
                                      searchPerpetualHeader.getEndCreatedOn()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}