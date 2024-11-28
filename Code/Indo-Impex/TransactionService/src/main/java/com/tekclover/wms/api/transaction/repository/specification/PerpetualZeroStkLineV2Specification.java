package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.PerpetualZeroStockLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.SearchPerpetualLineV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PerpetualZeroStkLineV2Specification implements Specification<PerpetualZeroStockLine> {

    SearchPerpetualLineV2 searchPerpetualLine;

    public PerpetualZeroStkLineV2Specification(SearchPerpetualLineV2 inputSearchParams) {
        this.searchPerpetualLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PerpetualZeroStockLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPerpetualLine.getCycleCountNo() != null && !searchPerpetualLine.getCycleCountNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("cycleCountNo");
            predicates.add(group.in(searchPerpetualLine.getCycleCountNo()));
        }

//         if (searchPerpetualLine.getCycleCounterId() != null && !searchPerpetualLine.getCycleCounterId().isEmpty()) {
////        	 predicates.add(cb.equal(root.get("cycleCounterId"), searchPerpetualLine.getCycleCounterId()));
//			 final Path<Group> group = root.<Group> get("cycleCounterId");
//			 predicates.add(group.in(searchPerpetualLine.getCycleCounterId()));
//         }

        if (searchPerpetualLine.getCycleCounterId() != null && !searchPerpetualLine.getCycleCounterId().isEmpty()) {
            predicates.add(cb.equal(root.get("cycleCounterId"), searchPerpetualLine.getCycleCounterId()));
        }
        if (searchPerpetualLine.getLineStatusId() != null && !searchPerpetualLine.getLineStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchPerpetualLine.getLineStatusId()));
        }

        if (searchPerpetualLine.getCompanyCodeId() != null && !searchPerpetualLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchPerpetualLine.getCompanyCodeId()));
        }
        if (searchPerpetualLine.getPlantId() != null && !searchPerpetualLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchPerpetualLine.getPlantId()));
        }
        if (searchPerpetualLine.getLanguageId() != null && !searchPerpetualLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchPerpetualLine.getLanguageId()));
        }
        if (searchPerpetualLine.getWarehouseId() != null && !searchPerpetualLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPerpetualLine.getWarehouseId()));
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