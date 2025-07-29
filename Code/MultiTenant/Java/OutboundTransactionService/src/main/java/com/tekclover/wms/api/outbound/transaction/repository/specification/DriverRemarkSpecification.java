package com.tekclover.wms.api.outbound.transaction.repository.specification;

import com.tekclover.wms.api.outbound.transaction.model.driverremark.DriverRemark;
import com.tekclover.wms.api.outbound.transaction.model.driverremark.SearchDriverRemark;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DriverRemarkSpecification implements Specification<DriverRemark> {

    SearchDriverRemark searchDriverRemark;

    public DriverRemarkSpecification(SearchDriverRemark inputSearchParams) {
        this.searchDriverRemark = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DriverRemark> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchDriverRemark.getPreOutboundNo() != null && !searchDriverRemark.getPreOutboundNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchDriverRemark.getPreOutboundNo()));
        }
        if (searchDriverRemark.getRefDocNumber() != null && !searchDriverRemark.getRefDocNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("deliveryNo");
            predicates.add(group.in(searchDriverRemark.getRefDocNumber()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));

    }
}
