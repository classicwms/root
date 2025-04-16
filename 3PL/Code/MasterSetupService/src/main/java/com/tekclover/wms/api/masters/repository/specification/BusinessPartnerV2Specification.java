package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.SearchBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BusinessPartnerV2Specification implements Specification<BusinessPartnerV2> {

    SearchBusinessPartner searchBusinessPartner;

    public BusinessPartnerV2Specification(SearchBusinessPartner inputSearchParams) {
        this.searchBusinessPartner = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BusinessPartnerV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchBusinessPartner.getWarehouseId() != null && !searchBusinessPartner.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchBusinessPartner.getWarehouseId()));
        }
        if (searchBusinessPartner.getCompanyCodeId() != null && !searchBusinessPartner.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchBusinessPartner.getCompanyCodeId()));
        }

        if (searchBusinessPartner.getPlantId() != null && !searchBusinessPartner.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchBusinessPartner.getPlantId()));
        }


        if (searchBusinessPartner.getBusinessPartnerType() != null && !searchBusinessPartner.getBusinessPartnerType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("businessPartnerType");
            predicates.add(group.in(searchBusinessPartner.getBusinessPartnerType()));
        }

        if (searchBusinessPartner.getPartnerCode() != null && !searchBusinessPartner.getPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("partnerCode");
            predicates.add(group.in(searchBusinessPartner.getPartnerCode()));
        }

        if (searchBusinessPartner.getPartnerName() != null && !searchBusinessPartner.getPartnerName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("partnerName");
            predicates.add(group.in(searchBusinessPartner.getPartnerName()));
        }

        if (searchBusinessPartner.getStatusId() != null && !searchBusinessPartner.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("statusId");
            predicates.add(group.in(searchBusinessPartner.getStatusId()));
        }

        if (searchBusinessPartner.getLanguageId() != null && !searchBusinessPartner.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchBusinessPartner.getLanguageId()));
        }

        if (searchBusinessPartner.getStartCreatedOn() != null && searchBusinessPartner.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchBusinessPartner.getStartCreatedOn(), searchBusinessPartner.getEndCreatedOn()));
        }

        if (searchBusinessPartner.getCreatedBy() != null && !searchBusinessPartner.getCreatedBy().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("createdBy");
            predicates.add(group.in(searchBusinessPartner.getCreatedBy()));
        }

        if (searchBusinessPartner.getStartUpdatedOn() != null && searchBusinessPartner.getEndUpdatedOn() != null) {
            predicates.add(cb.between(root.get("updatedOn"), searchBusinessPartner.getStartUpdatedOn(), searchBusinessPartner.getEndUpdatedOn()));
        }

        if (searchBusinessPartner.getUpdatedBy() != null && !searchBusinessPartner.getUpdatedBy().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("updatedBy");
            predicates.add(group.in(searchBusinessPartner.getUpdatedBy()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));


    }
}

