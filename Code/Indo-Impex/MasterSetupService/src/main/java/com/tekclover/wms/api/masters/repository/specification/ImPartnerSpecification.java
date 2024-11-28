package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.impartner.ImPartner;
import com.tekclover.wms.api.masters.model.impartner.SearchImPartner;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImPartnerSpecification implements Specification<ImPartner> {
    SearchImPartner searchImPartner;

    public ImPartnerSpecification(SearchImPartner inputSearchParams) {
        this.searchImPartner = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImPartner> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImPartner.getWarehouseId() != null && !searchImPartner.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchImPartner.getWarehouseId()));
        }
        if (searchImPartner.getBusinessPartnerCode() != null && !searchImPartner.getBusinessPartnerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("businessPartnerCode");
            predicates.add(group.in(searchImPartner.getBusinessPartnerCode()));
        }

        if (searchImPartner.getBusinessPartnerType() != null && !searchImPartner.getBusinessPartnerType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("businessPartnerType");
            predicates.add(group.in(searchImPartner.getBusinessPartnerType()));
        }

        if (searchImPartner.getPartnerItemBarcode() != null && !searchImPartner.getPartnerItemBarcode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("partnerItemBarcode");
            predicates.add(group.in(searchImPartner.getPartnerItemBarcode()));
        }

        if (searchImPartner.getItemCode() != null && !searchImPartner.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(searchImPartner.getItemCode()));
        }

        if (searchImPartner.getCompanyCodeId() != null && !searchImPartner.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchImPartner.getCompanyCodeId()));
        }

        if (searchImPartner.getLanguageId() != null && !searchImPartner.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchImPartner.getLanguageId()));
        }

        if (searchImPartner.getPlantId() != null && !searchImPartner.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchImPartner.getPlantId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}