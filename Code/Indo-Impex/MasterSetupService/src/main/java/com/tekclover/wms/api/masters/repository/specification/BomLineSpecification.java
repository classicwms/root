package com.tekclover.wms.api.masters.repository.specification;


import com.tekclover.wms.api.masters.model.bom.BomLine;
import com.tekclover.wms.api.masters.model.bom.SearchBomLine;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BomLineSpecification implements Specification<BomLine> {

    SearchBomLine searchBomLine;


    public BomLineSpecification(SearchBomLine inputSearchParams) {
        this.searchBomLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BomLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchBomLine.getWarehouseId() != null && !searchBomLine.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchBomLine.getWarehouseId()));
        }

        if (searchBomLine.getLanguageId() != null && !searchBomLine.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchBomLine.getLanguageId()));
        }

        if (searchBomLine.getPlantId() != null && !searchBomLine.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchBomLine.getPlantId()));
        }

        if (searchBomLine.getSequenceNo() != null && !searchBomLine.getSequenceNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("sequenceNo");
            predicates.add(group.in(searchBomLine.getSequenceNo()));
        }

        if (searchBomLine.getBomNumber() != null && !searchBomLine.getBomNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("bomNumber");
            predicates.add(group.in(searchBomLine.getBomNumber()));
        }

        if (searchBomLine.getCompanyCode() != null && !searchBomLine.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(searchBomLine.getCompanyCode()));
        }

        if (searchBomLine.getChildItemCode() != null && !searchBomLine.getChildItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("childItemCode");
            predicates.add(group.in(searchBomLine.getChildItemCode()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}