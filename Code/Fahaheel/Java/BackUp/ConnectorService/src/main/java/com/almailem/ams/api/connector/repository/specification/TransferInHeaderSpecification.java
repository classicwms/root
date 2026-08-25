package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.transferin.SearchTransferInHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
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
public class TransferInHeaderSpecification implements Specification<TransferInHeader> {

    SearchTransferInHeader searchTransferInHeader;

    public TransferInHeaderSpecification(SearchTransferInHeader inputSearchParams) {
        this.searchTransferInHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<TransferInHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchTransferInHeader.getTransferInHeaderId() != null && !searchTransferInHeader.getTransferInHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferInHeaderId");
            predicates.add(group.in(searchTransferInHeader.getTransferInHeaderId()));
        }

        if (searchTransferInHeader.getSourceCompanyCode() != null && !searchTransferInHeader.getSourceCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("sourceCompanyCode");
            predicates.add(group.in(searchTransferInHeader.getSourceCompanyCode()));
        }

        if (searchTransferInHeader.getTargetCompanyCode() != null && !searchTransferInHeader.getTargetCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("targetCompanyCode");
            predicates.add(group.in(searchTransferInHeader.getTargetCompanyCode()));
        }

        if (searchTransferInHeader.getTransferOrderNo() != null && !searchTransferInHeader.getTransferOrderNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferOrderNo");
            predicates.add(group.in(searchTransferInHeader.getTransferOrderNo()));
        }
        if (searchTransferInHeader.getSourceBranchCode() != null && !searchTransferInHeader.getSourceBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("sourceBranchCode");
            predicates.add(group.in(searchTransferInHeader.getSourceBranchCode()));
        }
        if (searchTransferInHeader.getTargetBranchCode() != null && !searchTransferInHeader.getTargetBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("targetBranchCode");
            predicates.add(group.in(searchTransferInHeader.getTargetBranchCode()));
        }

        if (searchTransferInHeader.getFromTransferOrderDate() != null && searchTransferInHeader.getToTransferOrderDate() != null) {
            predicates.add(cb.between(root.get("transferOrderDate"), searchTransferInHeader.getFromTransferOrderDate(),
                    searchTransferInHeader.getToTransferOrderDate()));
        }

        if (searchTransferInHeader.getFromOrderReceivedOn() != null && searchTransferInHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), searchTransferInHeader.getFromOrderReceivedOn(), searchTransferInHeader.getToOrderReceivedOn()));
        }

        if (searchTransferInHeader.getFromOrderProcessedOn() != null && searchTransferInHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), searchTransferInHeader.getFromOrderProcessedOn(),
                    searchTransferInHeader.getToOrderProcessedOn()));
        }

        if (searchTransferInHeader.getProcessedStatusId() != null && !searchTransferInHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(searchTransferInHeader.getProcessedStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
