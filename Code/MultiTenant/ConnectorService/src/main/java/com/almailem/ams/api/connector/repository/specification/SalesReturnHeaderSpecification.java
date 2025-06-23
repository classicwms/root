package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.salesreturn.FindSalesReturnHeader;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnHeader;
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
public class SalesReturnHeaderSpecification implements Specification<SalesReturnHeader> {

    FindSalesReturnHeader findSalesReturnHeader;

    public SalesReturnHeaderSpecification(FindSalesReturnHeader inputSearchParams) {
        this.findSalesReturnHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<SalesReturnHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findSalesReturnHeader.getSalesReturnHeaderId() != null && !findSalesReturnHeader.getSalesReturnHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("salesReturnHeaderId");
            predicates.add(group.in(findSalesReturnHeader.getSalesReturnHeaderId()));
        }
        if (findSalesReturnHeader.getCompanyCode() != null && !findSalesReturnHeader.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findSalesReturnHeader.getCompanyCode()));
        }
        if (findSalesReturnHeader.getBranchCodeOfReceivingWarehouse() != null && !findSalesReturnHeader.getBranchCodeOfReceivingWarehouse().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCodeOfReceivingWarehouse");
            predicates.add(group.in(findSalesReturnHeader.getBranchCodeOfReceivingWarehouse()));
        }
        if (findSalesReturnHeader.getReturnOrderNo() != null && !findSalesReturnHeader.getReturnOrderNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("returnOrderNo");
            predicates.add(group.in(findSalesReturnHeader.getReturnOrderNo()));
        }

        if (findSalesReturnHeader.getFromOrderReceivedOn() != null && findSalesReturnHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findSalesReturnHeader.getFromOrderReceivedOn(),
                    findSalesReturnHeader.getToOrderReceivedOn()));
        }

        if (findSalesReturnHeader.getFromOrderProcessedOn() != null && findSalesReturnHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findSalesReturnHeader.getFromOrderProcessedOn(),
                    findSalesReturnHeader.getToOrderProcessedOn()));
        }

        if (findSalesReturnHeader.getProcessedStatusId() != null && !findSalesReturnHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findSalesReturnHeader.getProcessedStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
