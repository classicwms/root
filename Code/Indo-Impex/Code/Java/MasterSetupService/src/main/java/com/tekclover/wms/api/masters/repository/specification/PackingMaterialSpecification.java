package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.packingmaterial.PackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.SearchPackingMaterial;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PackingMaterialSpecification implements Specification<PackingMaterial> {

    SearchPackingMaterial searchPackingMaterial;

    public PackingMaterialSpecification(SearchPackingMaterial inputSearchParams) {
        this.searchPackingMaterial = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PackingMaterial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchPackingMaterial.getWarehouseId() != null && !searchPackingMaterial.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchPackingMaterial.getWarehouseId()));
        }

        if (searchPackingMaterial.getPackingMaterialNo() != null && !searchPackingMaterial.getPackingMaterialNo().isEmpty()) {
            final Path<Group> group = root.<Group>get("packingMaterialNo");
            predicates.add(group.in(searchPackingMaterial.getPackingMaterialNo()));
        }

        if (searchPackingMaterial.getDescription() != null && !searchPackingMaterial.getDescription().isEmpty()) {
            final Path<Group> group = root.<Group>get("description");
            predicates.add(group.in(searchPackingMaterial.getDescription()));
        }

        if (searchPackingMaterial.getBusinessPartnerCode() != null && !searchPackingMaterial.getBusinessPartnerCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("businessPartnerCode");
            predicates.add(group.in(searchPackingMaterial.getBusinessPartnerCode()));
        }

        if (searchPackingMaterial.getStatusId() != null && !searchPackingMaterial.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchPackingMaterial.getStatusId()));
        }

        if (searchPackingMaterial.getLanguageId() != null && !searchPackingMaterial.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchPackingMaterial.getLanguageId()));
        }

        if (searchPackingMaterial.getCompanyCodeId() != null && !searchPackingMaterial.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchPackingMaterial.getCompanyCodeId()));
        }

        if (searchPackingMaterial.getPlantId() != null && !searchPackingMaterial.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchPackingMaterial.getPlantId()));
        }

        if (searchPackingMaterial.getStartCreatedOn() != null && searchPackingMaterial.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchPackingMaterial.getStartCreatedOn(), searchPackingMaterial.getEndCreatedOn()));
        }

        if (searchPackingMaterial.getCreatedBy() != null && !searchPackingMaterial.getCreatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("createdBy");
            predicates.add(group.in(searchPackingMaterial.getCreatedBy()));
        }

        if (searchPackingMaterial.getStartUpdatedOn() != null && searchPackingMaterial.getEndUpdatedOn() != null) {
            predicates.add(cb.between(root.get("updatedOn"), searchPackingMaterial.getStartUpdatedOn(), searchPackingMaterial.getEndUpdatedOn()));
        }

        if (searchPackingMaterial.getUpdatedBy() != null && !searchPackingMaterial.getUpdatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("updatedBy");
            predicates.add(group.in(searchPackingMaterial.getUpdatedBy()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}