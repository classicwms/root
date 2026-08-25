package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.purchasereturn.FindPurchaseReturnHeader;
import com.almailem.ams.api.connector.model.purchasereturn.PurchaseReturnHeader;
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
public class PurchaseReturnHeaderSpecification implements Specification<PurchaseReturnHeader> {

    FindPurchaseReturnHeader findPurchaseReturnHeader;

    public PurchaseReturnHeaderSpecification(FindPurchaseReturnHeader inputSearchParams) {
        this.findPurchaseReturnHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PurchaseReturnHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPurchaseReturnHeader.getPurchaseReturnHeaderId() != null && !findPurchaseReturnHeader.getPurchaseReturnHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("purchaseReturnHeaderId");
            predicates.add(group.in(findPurchaseReturnHeader.getPurchaseReturnHeaderId()));
        }
        if (findPurchaseReturnHeader.getCompanyCode() != null && !findPurchaseReturnHeader.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findPurchaseReturnHeader.getCompanyCode()));
        }
        if (findPurchaseReturnHeader.getBranchCode() != null && !findPurchaseReturnHeader.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findPurchaseReturnHeader.getBranchCode()));
        }
        if (findPurchaseReturnHeader.getReturnOrderNo() != null && !findPurchaseReturnHeader.getReturnOrderNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("returnOrderNo");
            predicates.add(group.in(findPurchaseReturnHeader.getReturnOrderNo()));
        }

        if (findPurchaseReturnHeader.getFromOrderReceivedOn() != null && findPurchaseReturnHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findPurchaseReturnHeader.getFromOrderReceivedOn(),
                    findPurchaseReturnHeader.getToOrderReceivedOn()));
        }

        if (findPurchaseReturnHeader.getFromOrderProcessedOn() != null && findPurchaseReturnHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findPurchaseReturnHeader.getFromOrderProcessedOn(),
                    findPurchaseReturnHeader.getToOrderProcessedOn()));
        }

        if (findPurchaseReturnHeader.getProcessedStatusId() != null && !findPurchaseReturnHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findPurchaseReturnHeader.getProcessedStatusId()));
        }



        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
