package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.quality.v2.SearchQualityHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class QualityHeaderV2Specification implements Specification<QualityHeaderV2> {

    SearchQualityHeaderV2 searchQualityHeader;

    public QualityHeaderV2Specification(SearchQualityHeaderV2 inputSearchParams) {
        this.searchQualityHeader = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<QualityHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchQualityHeader.getCompanyCodeId() != null && !searchQualityHeader.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchQualityHeader.getCompanyCodeId()));
        }

        if (searchQualityHeader.getPlantId() != null && !searchQualityHeader.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchQualityHeader.getPlantId()));
        }

        if (searchQualityHeader.getLanguageId() != null && !searchQualityHeader.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchQualityHeader.getLanguageId()));
        }

        if (searchQualityHeader.getRefDocNumber() != null && !searchQualityHeader.getRefDocNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("refDocNumber");
            predicates.add(group.in(searchQualityHeader.getRefDocNumber()));
        }

        if (searchQualityHeader.getPreOutboundNo() != null && !searchQualityHeader.getPreOutboundNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("preOutboundNo");
            predicates.add(group.in(searchQualityHeader.getPreOutboundNo()));
        }

        if (searchQualityHeader.getPartnerCode() != null && !searchQualityHeader.getPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("partnerCode");
            predicates.add(group.in(searchQualityHeader.getPartnerCode()));
        }

        if (searchQualityHeader.getQualityInspectionNo() != null && !searchQualityHeader.getQualityInspectionNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("qualityInspectionNo");
            predicates.add(group.in(searchQualityHeader.getQualityInspectionNo()));
        }

        if (searchQualityHeader.getActualHeNo() != null && !searchQualityHeader.getActualHeNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("actualHeNo");
            predicates.add(group.in(searchQualityHeader.getActualHeNo()));
        }

        if (searchQualityHeader.getOutboundOrderTypeId() != null && !searchQualityHeader.getOutboundOrderTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("outboundOrderTypeId");
            predicates.add(group.in(searchQualityHeader.getOutboundOrderTypeId()));
        }

        if (searchQualityHeader.getStatusId() != null && !searchQualityHeader.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchQualityHeader.getStatusId()));
        }

        if (searchQualityHeader.getSoType() != null && !searchQualityHeader.getSoType().isEmpty()) {
            final Path<Group> group = root.<Group>get("referenceField1");
            predicates.add(group.in(searchQualityHeader.getSoType()));
        }

        if (searchQualityHeader.getStartQualityCreatedOn() != null && searchQualityHeader.getEndQualityCreatedOn() != null) {
            predicates.add(cb.between(root.get("qualityCreatedOn"), searchQualityHeader.getStartQualityCreatedOn(), searchQualityHeader.getEndQualityCreatedOn()));
        }


        if (searchQualityHeader.getWarehouseId() != null && !searchQualityHeader.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchQualityHeader.getWarehouseId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}