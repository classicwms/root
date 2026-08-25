package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway.v2.SearchPutAwayLineV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PutAwayLineV2Specification implements Specification<PutAwayLineV2> {

    SearchPutAwayLineV2 searchPutAwayLine;

    public PutAwayLineV2Specification(SearchPutAwayLineV2 inputSearchParams) {
        this.searchPutAwayLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PutAwayLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPutAwayLine.getCompanyCodeId() != null && !searchPutAwayLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchPutAwayLine.getCompanyCodeId()));
        }
        if (searchPutAwayLine.getPlantId() != null && !searchPutAwayLine.getPlantId().isEmpty()) {
                    final Path<Group> group = root.<Group>get("plantId");
                    predicates.add(group.in(searchPutAwayLine.getPlantId()));
        }
        if (searchPutAwayLine.getLanguageId() != null && !searchPutAwayLine.getLanguageId().isEmpty()) {
                    final Path<Group> group = root.<Group>get("languageId");
                    predicates.add(group.in(searchPutAwayLine.getLanguageId()));
        }
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

        if (searchPutAwayLine.getFromCreatedDate() != null && searchPutAwayLine.getToCreatedDate() != null) {
            predicates.add(cb.between(root.get("createdOn"),
                    searchPutAwayLine.getFromCreatedDate(), searchPutAwayLine.getToCreatedDate()));
        }

        if (searchPutAwayLine.getPackBarCodes() != null && !searchPutAwayLine.getPackBarCodes().isEmpty()) {
            final Path<Group> group = root.<Group>get("packBarcodes");
            predicates.add(group.in(searchPutAwayLine.getPackBarCodes()));
        }

        if (searchPutAwayLine.getBarcodeId() != null && !searchPutAwayLine.getBarcodeId().isEmpty()) {
            final Path<Group> group = root.<Group> get("barcodeId");
            predicates.add(group.in(searchPutAwayLine.getBarcodeId()));
        }

        if (searchPutAwayLine.getManufacturerCode() != null && !searchPutAwayLine.getManufacturerCode().isEmpty()) {
            final Path<Group> group = root.<Group> get("manufacturerCode");
            predicates.add(group.in(searchPutAwayLine.getManufacturerCode()));
        }


        if (searchPutAwayLine.getManufacturerName() != null && !searchPutAwayLine.getManufacturerName().isEmpty()) {
            final Path<Group> group = root.<Group> get("manufacturerName");
            predicates.add(group.in(searchPutAwayLine.getManufacturerName()));
        }


        if (searchPutAwayLine.getOrigin() != null && !searchPutAwayLine.getOrigin().isEmpty()) {
            final Path<Group> group = root.<Group> get("origin");
            predicates.add(group.in(searchPutAwayLine.getOrigin()));
        }

        if (searchPutAwayLine.getBrand() != null && !searchPutAwayLine.getBrand().isEmpty()) {
            final Path<Group> group = root.<Group> get("brand");
            predicates.add(group.in(searchPutAwayLine.getBrand()));
        }

        if (searchPutAwayLine.getStatusId() != null && !searchPutAwayLine.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group> get("statusId");
            predicates.add(group.in(searchPutAwayLine.getStatusId()));
        }


        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}