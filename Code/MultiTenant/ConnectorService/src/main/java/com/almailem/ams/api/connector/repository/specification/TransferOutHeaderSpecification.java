package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.transferout.FindTransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
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
public class TransferOutHeaderSpecification implements Specification<TransferOutHeader> {

    FindTransferOutHeader findTransferOutHeader;

    public TransferOutHeaderSpecification(FindTransferOutHeader inputSearchParams) {
        this.findTransferOutHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<TransferOutHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findTransferOutHeader.getTransferOutHeaderId() != null && !findTransferOutHeader.getTransferOutHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOutHeaderId");
            predicates.add(group.in(findTransferOutHeader.getTransferOutHeaderId()));
        }
        if (findTransferOutHeader.getSourceCompanyCode() != null && !findTransferOutHeader.getSourceCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("sourceCompanyCode");
            predicates.add(group.in(findTransferOutHeader.getSourceCompanyCode()));
        }
        if (findTransferOutHeader.getTargetCompanyCode() != null && !findTransferOutHeader.getTargetCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("targetCompanyCode");
            predicates.add(group.in(findTransferOutHeader.getTargetCompanyCode()));
        }
        if (findTransferOutHeader.getTransferOrderNumber() != null && !findTransferOutHeader.getTransferOrderNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderNumber");
            predicates.add(group.in(findTransferOutHeader.getTransferOrderNumber()));
        }
        if (findTransferOutHeader.getSourceBranchCode() != null && !findTransferOutHeader.getSourceBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("sourceBranchCode");
            predicates.add(group.in(findTransferOutHeader.getSourceBranchCode()));
        }
        if (findTransferOutHeader.getTargetBranchCode() != null && !findTransferOutHeader.getTargetBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("targetBranchCode");
            predicates.add(group.in(findTransferOutHeader.getTargetBranchCode()));
        }
        if (findTransferOutHeader.getFromOrderReceivedOn() != null && findTransferOutHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findTransferOutHeader.getFromOrderReceivedOn(),
                    findTransferOutHeader.getToOrderReceivedOn()));
        }

        if (findTransferOutHeader.getFromOrderProcessedOn() != null && findTransferOutHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findTransferOutHeader.getFromOrderProcessedOn(),
                    findTransferOutHeader.getToOrderProcessedOn()));
        }

        if (findTransferOutHeader.getProcessedStatusId() != null && !findTransferOutHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findTransferOutHeader.getProcessedStatusId()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
