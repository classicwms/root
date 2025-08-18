package com.tekclover.wms.api.inbound.orders.repository.specification;

import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.SearchStagingLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StagingLineV2Specification implements Specification<StagingLineEntityV2> {

    SearchStagingLineV2 searchStagingLine;

    public StagingLineV2Specification(SearchStagingLineV2 inputSearchParams) {
        this.searchStagingLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StagingLineEntityV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchStagingLine.getCompanyCodeId() != null && !searchStagingLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCode");
            predicates.add(group.in(searchStagingLine.getCompanyCodeId()));
        }

        if (searchStagingLine.getLanguageId() != null && !searchStagingLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchStagingLine.getLanguageId()));
        }

        if (searchStagingLine.getPlantId() != null && !searchStagingLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchStagingLine.getPlantId()));
        }

        if (searchStagingLine.getWarehouseId() != null && !searchStagingLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchStagingLine.getWarehouseId()));
        }

        if (searchStagingLine.getPreInboundNo() != null && !searchStagingLine.getPreInboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preInboundNo");
            predicates.add(group.in(searchStagingLine.getPreInboundNo()));
        }

        if (searchStagingLine.getRefDocNumber() != null && !searchStagingLine.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchStagingLine.getRefDocNumber()));
        }

        if (searchStagingLine.getStagingNo() != null && !searchStagingLine.getStagingNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("stagingNo");
            predicates.add(group.in(searchStagingLine.getStagingNo()));
        }

        if (searchStagingLine.getPalletCode() != null && !searchStagingLine.getPalletCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("palletCode");
            predicates.add(group.in(searchStagingLine.getPalletCode()));
        }

        if (searchStagingLine.getCaseCode() != null && !searchStagingLine.getCaseCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("caseCode");
            predicates.add(group.in(searchStagingLine.getCaseCode()));
        }

        if (searchStagingLine.getLineNo() != null && !searchStagingLine.getLineNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("lineNo");
            predicates.add(group.in(searchStagingLine.getLineNo()));
        }

        if (searchStagingLine.getItemCode() != null && !searchStagingLine.getItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("itemCode");
            predicates.add(group.in(searchStagingLine.getItemCode()));
        }

        if (searchStagingLine.getStatusId() != null && !searchStagingLine.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchStagingLine.getStatusId()));
        }

        if (searchStagingLine.getManufacturerCode() != null && !searchStagingLine.getManufacturerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("manufacturerCode");
            predicates.add(group.in(searchStagingLine.getManufacturerCode()));
        }

        if (searchStagingLine.getManufacturerName() != null && !searchStagingLine.getManufacturerName().isEmpty()) {
            final Path<Group> group = root.<Group>get("manufacturerName");
            predicates.add(group.in(searchStagingLine.getManufacturerName()));
        }

        if (searchStagingLine.getBrand() != null && !searchStagingLine.getBrand().isEmpty()) {
            final Path<Group> group = root.<Group>get("brand");
            predicates.add(group.in(searchStagingLine.getBrand()));
        }

        if (searchStagingLine.getOrigin() != null && !searchStagingLine.getOrigin().isEmpty()) {
            final Path<Group> group = root.<Group>get("origin");
            predicates.add(group.in(searchStagingLine.getOrigin()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
