package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.picklist.FindPickListHeader;
import com.almailem.ams.api.connector.model.picklist.PickListHeader;
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
public class PickListHeaderSpecification implements Specification<PickListHeader> {

    FindPickListHeader findPickListHeader;

    public PickListHeaderSpecification(FindPickListHeader inputSearchParams) {
        this.findPickListHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PickListHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPickListHeader.getPickListHeaderId() != null && !findPickListHeader.getPickListHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("pickListHeaderId");
            predicates.add(group.in(findPickListHeader.getPickListHeaderId()));
        }
        if (findPickListHeader.getCompanyCode() != null && !findPickListHeader.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findPickListHeader.getCompanyCode()));
        }
        if (findPickListHeader.getBranchCode() != null && !findPickListHeader.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findPickListHeader.getBranchCode()));
        }
        if (findPickListHeader.getSalesOrderNo() != null && !findPickListHeader.getSalesOrderNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("salesOrderNo");
            predicates.add(group.in(findPickListHeader.getSalesOrderNo()));
        }
        if (findPickListHeader.getPickListNo() != null && !findPickListHeader.getPickListNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("pickListNo");
            predicates.add(group.in(findPickListHeader.getPickListNo()));
        }
        if (findPickListHeader.getTokenNumber() != null && !findPickListHeader.getTokenNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("tokenNumber");
            predicates.add(group.in(findPickListHeader.getTokenNumber()));
        }

        if (findPickListHeader.getFromOrderReceivedOn() != null && findPickListHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findPickListHeader.getFromOrderReceivedOn(),
                    findPickListHeader.getToOrderReceivedOn()));
        }

        if (findPickListHeader.getFromOrderProcessedOn() != null && findPickListHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findPickListHeader.getFromOrderProcessedOn(),
                    findPickListHeader.getToOrderProcessedOn()));
        }

        if (findPickListHeader.getProcessedStatusId() != null && !findPickListHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findPickListHeader.getProcessedStatusId()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
