package com.tekclover.wms.api.idmaster.repository.Specification;


import com.tekclover.wms.api.idmaster.model.outboundorderstatusid.FindOutboundOrderStatusId;
import com.tekclover.wms.api.idmaster.model.outboundorderstatusid.OutboundOrderStatusId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OutboundOrderStatusIdSpecification implements Specification<OutboundOrderStatusId> {

    FindOutboundOrderStatusId findOutboundOrderStatusId;

    public OutboundOrderStatusIdSpecification(FindOutboundOrderStatusId inputSearchParams) {
        this.findOutboundOrderStatusId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<OutboundOrderStatusId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findOutboundOrderStatusId.getCompanyCodeId() != null && !findOutboundOrderStatusId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findOutboundOrderStatusId.getCompanyCodeId()));
        }
        if (findOutboundOrderStatusId.getWarehouseId() != null && !findOutboundOrderStatusId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findOutboundOrderStatusId.getWarehouseId()));
        }
        if (findOutboundOrderStatusId.getOutboundOrderStatusId() != null && !findOutboundOrderStatusId.getOutboundOrderStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("outboundOrderStatusId");
            predicates.add(group.in(findOutboundOrderStatusId.getOutboundOrderStatusId()));
        }
        if (findOutboundOrderStatusId.getPlantId() != null && !findOutboundOrderStatusId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findOutboundOrderStatusId.getPlantId()));
        }
        if (findOutboundOrderStatusId.getLanguageId() != null && !findOutboundOrderStatusId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findOutboundOrderStatusId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }


}
