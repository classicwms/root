package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.report.BillingTransactionReport;
import com.tekclover.wms.api.transaction.model.report.FindBillingTransactionReport;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class BillingTransactionReportSpecification implements Specification<BillingTransactionReport> {

    FindBillingTransactionReport findBillingTransactionReport;

    public BillingTransactionReportSpecification(FindBillingTransactionReport inputSearchParams) {
        this.findBillingTransactionReport = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BillingTransactionReport> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (findBillingTransactionReport.getPartnerCode() != null && !findBillingTransactionReport.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("customer");
            predicates.add(group.in(findBillingTransactionReport.getPartnerCode()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}