package com.tekclover.wms.api.outbound.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.SearchPerpetualLine;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("serial")
public class PerpetualLineSpecification implements Specification<PerpetualLine> {

    SearchPerpetualLine searchPerpetualLine;

    public PerpetualLineSpecification(SearchPerpetualLine inputSearchParams) {
        this.searchPerpetualLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PerpetualLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPerpetualLine.getCycleCountNo() != null && !searchPerpetualLine.getCycleCountNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("cycleCountNo");
            predicates.add(group.in(searchPerpetualLine.getCycleCountNo()));
        }
        if (searchPerpetualLine.getCycleCounterId() != null && !searchPerpetualLine.getCycleCounterId().isEmpty()) {
            predicates.add(cb.equal(root.get("cycleCounterId"), searchPerpetualLine.getCycleCounterId()));
        }

        if (searchPerpetualLine.getLineStatusId() != null && !searchPerpetualLine.getLineStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchPerpetualLine.getLineStatusId()));
        }

        if (searchPerpetualLine.getWarehouseId() != null && !searchPerpetualLine.getWarehouseId().isEmpty()) {
            predicates.add(cb.equal(root.get("warehouseId"), searchPerpetualLine.getWarehouseId()));
        }

        if (searchPerpetualLine.getStartCreatedOn() != null && searchPerpetualLine.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchPerpetualLine.getStartCreatedOn(),
                    searchPerpetualLine.getEndCreatedOn()));
        }

        if (searchPerpetualLine.getItemCode() != null && !searchPerpetualLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchPerpetualLine.getItemCode()));
        }

        if (searchPerpetualLine.getPackBarcodes() != null && !searchPerpetualLine.getPackBarcodes().isEmpty()) {
            final Path<Group> group = root.<Group>get("packBarcodes");
            predicates.add(group.in(searchPerpetualLine.getPackBarcodes()));
        }

        if (searchPerpetualLine.getStorageBin() != null && !searchPerpetualLine.getStorageBin().isEmpty()) {
            final Path<Group> group = root.<Group>get("storageBin");
            predicates.add(group.in(searchPerpetualLine.getStorageBin()));
        }

        if (searchPerpetualLine.getStockTypeId() != null && !searchPerpetualLine.getStockTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("stockTypeId");
            predicates.add(group.in(searchPerpetualLine.getStockTypeId()));
        }

        if (searchPerpetualLine.getManufacturerPartNo() != null && !searchPerpetualLine.getManufacturerPartNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("manufacturerPartNo");
            predicates.add(group.in(searchPerpetualLine.getManufacturerPartNo()));
        }

        if (searchPerpetualLine.getStorageSectionId() != null && !searchPerpetualLine.getStorageSectionId().isEmpty()) {
            final Path<Group> group = root.<Group>get("storageSectionId");
            predicates.add(group.in(searchPerpetualLine.getStorageSectionId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
