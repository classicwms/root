package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.inboundordertypeid.FindInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.InboundOrderTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InboundOrderTypeIdSpecification implements Specification<InboundOrderTypeId> {
    FindInboundOrderTypeId findInboundOrderTypeId;

    public InboundOrderTypeIdSpecification(FindInboundOrderTypeId inputSearchParams) {
        this.findInboundOrderTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InboundOrderTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findInboundOrderTypeId.getCompanyCodeId() != null && !findInboundOrderTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findInboundOrderTypeId.getCompanyCodeId()));
        }

        if (findInboundOrderTypeId.getPlantId() != null && !findInboundOrderTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findInboundOrderTypeId.getPlantId()));
        }

        if (findInboundOrderTypeId.getWarehouseId() != null && !findInboundOrderTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findInboundOrderTypeId.getWarehouseId()));
        }

        if (findInboundOrderTypeId.getInboundOrderTypeId() != null && !findInboundOrderTypeId.getInboundOrderTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("inboundOrderTypeId");
            predicates.add(group.in(findInboundOrderTypeId.getInboundOrderTypeId()));
        }
        if (findInboundOrderTypeId.getLanguageId() != null && !findInboundOrderTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findInboundOrderTypeId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
