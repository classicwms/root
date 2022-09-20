package com.tekclover.wms.api.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.SearchPutAwayLine;

@SuppressWarnings("serial")
public class PutAwayLineSpecification implements Specification<PutAwayLine> {

    SearchPutAwayLine searchPutAwayLine;

    public PutAwayLineSpecification(SearchPutAwayLine inputSearchParams) {
        this.searchPutAwayLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PutAwayLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPutAwayLine.getWarehouseId() != null && !searchPutAwayLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPutAwayLine.getWarehouseId()));
        }

        if (searchPutAwayLine.getGoodsReceiptNo() != null && !searchPutAwayLine.getGoodsReceiptNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("goodsReceiptNo");
            predicates.add(group.in(searchPutAwayLine.getGoodsReceiptNo()));
        }

        if (searchPutAwayLine.getPreInboundNo() != null && !searchPutAwayLine.getPreInboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preInboundNo");
            predicates.add(group.in(searchPutAwayLine.getPreInboundNo()));
        }

        if (searchPutAwayLine.getRefDocNumber() != null && !searchPutAwayLine.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchPutAwayLine.getRefDocNumber()));
        }

        if (searchPutAwayLine.getPutAwayNumber() != null && !searchPutAwayLine.getPutAwayNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("putAwayNumber");
            predicates.add(group.in(searchPutAwayLine.getPutAwayNumber()));
        }

        if (searchPutAwayLine.getLineNo() != null && !searchPutAwayLine.getLineNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("lineNo");
            predicates.add(group.in(searchPutAwayLine.getLineNo()));
        }

        if (searchPutAwayLine.getItemCode() != null && !searchPutAwayLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchPutAwayLine.getItemCode()));
        }

        if (searchPutAwayLine.getProposedStorageBin() != null && !searchPutAwayLine.getProposedStorageBin().isEmpty()) {
            final Path<Group> group = root.<Group>get("proposedStorageBin");
            predicates.add(group.in(searchPutAwayLine.getProposedStorageBin()));
        }

        if (searchPutAwayLine.getConfirmedStorageBin() != null && !searchPutAwayLine.getConfirmedStorageBin().isEmpty()) {
            final Path<Group> group = root.<Group>get("confirmedStorageBin");
            predicates.add(group.in(searchPutAwayLine.getConfirmedStorageBin()));
        }

        if (searchPutAwayLine.getFromConfirmedDate() != null && searchPutAwayLine.getToConfirmedDate() != null) {
            predicates.add(cb.between(root.get("confirmedOn"),
                    searchPutAwayLine.getFromConfirmedDate(), searchPutAwayLine.getToConfirmedDate()));
        }

        if (searchPutAwayLine.getPackBarCodes() != null && !searchPutAwayLine.getPackBarCodes().isEmpty()) {
            final Path<Group> group = root.<Group>get("packBarcodes");
            predicates.add(group.in(searchPutAwayLine.getPackBarCodes()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
