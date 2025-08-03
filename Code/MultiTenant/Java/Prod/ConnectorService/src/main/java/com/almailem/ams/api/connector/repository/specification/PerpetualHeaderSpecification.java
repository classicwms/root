package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.perpetual.FindPerpetualHeader;
import com.almailem.ams.api.connector.model.perpetual.PerpetualHeader;
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
public class PerpetualHeaderSpecification implements Specification<PerpetualHeader> {

    FindPerpetualHeader findPerpetualHeader;

    public PerpetualHeaderSpecification(FindPerpetualHeader inputSearchParams) {
        this.findPerpetualHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PerpetualHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPerpetualHeader.getPerpetualHeaderId() != null && !findPerpetualHeader.getPerpetualHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("perpetualHeaderId");
            predicates.add(group.in(findPerpetualHeader.getPerpetualHeaderId()));
        }
        if (findPerpetualHeader.getCompanyCode() != null && !findPerpetualHeader.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findPerpetualHeader.getCompanyCode()));
        }
        if (findPerpetualHeader.getBranchCode() != null && !findPerpetualHeader.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findPerpetualHeader.getBranchCode()));
        }
        if (findPerpetualHeader.getCycleCountNo() != null && !findPerpetualHeader.getCycleCountNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("cycleCountNo");
            predicates.add(group.in(findPerpetualHeader.getCycleCountNo()));
        }
        if (findPerpetualHeader.getFromOrderReceivedOn() != null && findPerpetualHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findPerpetualHeader.getFromOrderReceivedOn(),
                    findPerpetualHeader.getToOrderReceivedOn()));
        }

        if (findPerpetualHeader.getFromOrderProcessedOn() != null && findPerpetualHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findPerpetualHeader.getFromOrderProcessedOn(),
                    findPerpetualHeader.getToOrderProcessedOn()));
        }

        if (findPerpetualHeader.getProcessedStatusId() != null && !findPerpetualHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findPerpetualHeader.getProcessedStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
