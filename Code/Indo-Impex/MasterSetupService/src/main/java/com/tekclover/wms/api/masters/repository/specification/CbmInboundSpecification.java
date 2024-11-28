package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.threepl.cbminbound.CbmInbound;
import com.tekclover.wms.api.masters.model.threepl.cbminbound.FindCbmInbound;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CbmInboundSpecification implements Specification<CbmInbound> {
    FindCbmInbound findCbmInbound;

    public CbmInboundSpecification(FindCbmInbound inputSearchParams) {
        this.findCbmInbound = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<CbmInbound> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findCbmInbound.getWarehouseId() != null && !findCbmInbound.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findCbmInbound.getWarehouseId()));
        }

        if (findCbmInbound.getCompanyCodeId() != null && !findCbmInbound.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findCbmInbound.getCompanyCodeId()));
        }

        if (findCbmInbound.getPlantId() != null && !findCbmInbound.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findCbmInbound.getPlantId()));
        }
        if (findCbmInbound.getItemCode() != null && !findCbmInbound.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findCbmInbound.getItemCode()));
        }
        if (findCbmInbound.getStatusId() != null && !findCbmInbound.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findCbmInbound.getStatusId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}