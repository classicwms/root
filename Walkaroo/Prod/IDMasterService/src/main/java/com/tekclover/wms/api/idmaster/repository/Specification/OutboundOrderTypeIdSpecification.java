package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.outboundordertypeid.FindOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.OutboundOrderTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OutboundOrderTypeIdSpecification implements Specification<OutboundOrderTypeId> {
FindOutboundOrderTypeId findOutboundOrderTypeId;

    public OutboundOrderTypeIdSpecification(FindOutboundOrderTypeId inputSearchParams) {
        this.findOutboundOrderTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<OutboundOrderTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findOutboundOrderTypeId.getCompanyCodeId() != null && !findOutboundOrderTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findOutboundOrderTypeId.getCompanyCodeId()));
        }
        if (findOutboundOrderTypeId.getWarehouseId() != null && !findOutboundOrderTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findOutboundOrderTypeId.getWarehouseId()));
        }
        if (findOutboundOrderTypeId.getOutboundOrderTypeId() != null && !findOutboundOrderTypeId.getOutboundOrderTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("outboundOrderTypeId");
            predicates.add(group.in(findOutboundOrderTypeId.getOutboundOrderTypeId()));
        }
        if (findOutboundOrderTypeId.getPlantId() != null && !findOutboundOrderTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findOutboundOrderTypeId.getPlantId()));
        }

        if (findOutboundOrderTypeId.getLanguageId() != null && !findOutboundOrderTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findOutboundOrderTypeId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
