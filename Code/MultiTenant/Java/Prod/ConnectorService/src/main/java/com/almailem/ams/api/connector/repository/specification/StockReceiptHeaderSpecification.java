package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.stockreceipt.SearchStockReceiptHeader;
import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptHeader;
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
public class StockReceiptHeaderSpecification implements Specification<StockReceiptHeader> {

    SearchStockReceiptHeader searchStockReceiptHeader;

    public StockReceiptHeaderSpecification(SearchStockReceiptHeader inputSearchParams) {
        this.searchStockReceiptHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StockReceiptHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchStockReceiptHeader.getCompanyCode() != null && !searchStockReceiptHeader.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(searchStockReceiptHeader.getCompanyCode()));
        }

        if (searchStockReceiptHeader.getBranchCode() != null && !searchStockReceiptHeader.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(searchStockReceiptHeader.getBranchCode()));
        }

        if (searchStockReceiptHeader.getReceiptNo() != null && !searchStockReceiptHeader.getReceiptNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("receiptNo");
            predicates.add(group.in(searchStockReceiptHeader.getReceiptNo()));
        }

        if (searchStockReceiptHeader.getStockReceiptHeaderId() != null && !searchStockReceiptHeader.getStockReceiptHeaderId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("stockReceiptHeaderId");
            predicates.add(group.in(searchStockReceiptHeader.getStockReceiptHeaderId()));
        }

        if (searchStockReceiptHeader.getFromOrderReceivedOn() != null && searchStockReceiptHeader.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), searchStockReceiptHeader.getFromOrderReceivedOn(),
                    searchStockReceiptHeader.getToOrderReceivedOn()));
        }

        if (searchStockReceiptHeader.getFromOrderProcessedOn() != null && searchStockReceiptHeader.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), searchStockReceiptHeader.getFromOrderProcessedOn(),
                    searchStockReceiptHeader.getToOrderProcessedOn()));
        }

        if (searchStockReceiptHeader.getProcessedStatusId() != null && !searchStockReceiptHeader.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(searchStockReceiptHeader.getProcessedStatusId()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}

