package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.inboundorderstatusid.FindInboundOrderStatusId;
import com.tekclover.wms.api.idmaster.model.inboundorderstatusid.InboundOrderStatusId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InboundOrderStatusIdSpecification implements Specification<InboundOrderStatusId> {
    FindInboundOrderStatusId findInboundOrderStatusId;

    public InboundOrderStatusIdSpecification(FindInboundOrderStatusId inputSearchParams) {
        this.findInboundOrderStatusId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InboundOrderStatusId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findInboundOrderStatusId.getCompanyCodeId() != null && !findInboundOrderStatusId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findInboundOrderStatusId.getCompanyCodeId()));
        }

        if (findInboundOrderStatusId.getPlantId() != null && !findInboundOrderStatusId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findInboundOrderStatusId.getPlantId()));
        }

        if (findInboundOrderStatusId.getInboundOrderStatusId() != null && !findInboundOrderStatusId.getInboundOrderStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("inboundOrderStatusId");
            predicates.add(group.in(findInboundOrderStatusId.getInboundOrderStatusId()));
        }

        if (findInboundOrderStatusId.getWarehouseId() != null && !findInboundOrderStatusId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findInboundOrderStatusId.getWarehouseId()));
        }

        if (findInboundOrderStatusId.getLanguageId() != null && !findInboundOrderStatusId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findInboundOrderStatusId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
