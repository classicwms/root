package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.periodic.FindPeriodicHeader;
import com.almailem.ams.api.connector.model.periodic.PeriodicHeader;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PeriodicHeaderSpecification implements Specification<PeriodicHeader> {

    FindPeriodicHeader findPeriodicHeader;

    public PeriodicHeaderSpecification(FindPeriodicHeader inputSearchParams) {
        this.findPeriodicHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PeriodicHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPeriodicHeader.getPeriodicHeaderId() != null && !findPeriodicHeader.getPeriodicHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("periodicHeaderId");
            predicates.add(group.in(findPeriodicHeader.getPeriodicHeaderId()));
        }
        if (findPeriodicHeader.getCompanyCode() != null && !findPeriodicHeader.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findPeriodicHeader.getCompanyCode()));
        }
        if (findPeriodicHeader.getBranchCode() != null && !findPeriodicHeader.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findPeriodicHeader.getBranchCode()));
        }
        if (findPeriodicHeader.getCycleCountNo() != null && !findPeriodicHeader.getCycleCountNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("cycleCountNo");
            predicates.add(group.in(findPeriodicHeader.getCycleCountNo()));
        }
        if (findPeriodicHeader.getFromOrderReceivedOn() != null && findPeriodicHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findPeriodicHeader.getFromOrderReceivedOn(),
                    findPeriodicHeader.getToOrderReceivedOn()));
        }

        if (findPeriodicHeader.getFromOrderProcessedOn() != null && findPeriodicHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findPeriodicHeader.getFromOrderProcessedOn(),
                    findPeriodicHeader.getToOrderProcessedOn()));
        }

        if (findPeriodicHeader.getProcessedStatusId() != null && !findPeriodicHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findPeriodicHeader.getProcessedStatusId()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
