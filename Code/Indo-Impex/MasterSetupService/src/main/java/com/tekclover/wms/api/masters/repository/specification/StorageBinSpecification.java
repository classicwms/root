package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.storagebin.SearchStorageBin;
import com.tekclover.wms.api.masters.model.storagebin.StorageBin;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageBinSpecification implements Specification<StorageBin> {

    SearchStorageBin searchStorageBin;

    public StorageBinSpecification(SearchStorageBin inputSearchParams) {
        this.searchStorageBin = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StorageBin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchStorageBin.getWarehouseId() != null && !searchStorageBin.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchStorageBin.getWarehouseId()));
        }

        if (searchStorageBin.getStorageBin() != null && !searchStorageBin.getStorageBin().isEmpty()) {
            final Path<Group> group = root.<Group>get("storageBin");
            predicates.add(group.in(searchStorageBin.getStorageBin()));
        }

        if (searchStorageBin.getFloorId() != null && !searchStorageBin.getFloorId().isEmpty()) {
            final Path<Group> group = root.<Group>get("floorId");
            predicates.add(group.in(searchStorageBin.getFloorId()));
        }

        if (searchStorageBin.getStorageSectionId() != null && !searchStorageBin.getStorageSectionId().isEmpty()) {
            final Path<Group> group = root.<Group>get("storageSectionId");
            predicates.add(group.in(searchStorageBin.getStorageSectionId()));
        }

        if (searchStorageBin.getRowId() != null && !searchStorageBin.getRowId().isEmpty()) {
            final Path<Group> group = root.<Group>get("rowId");
            predicates.add(group.in(searchStorageBin.getRowId()));
        }

        if (searchStorageBin.getAisleNumber() != null && !searchStorageBin.getAisleNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("aisleNumber");
            predicates.add(group.in(searchStorageBin.getAisleNumber()));
        }

        if (searchStorageBin.getLanguageId() != null && !searchStorageBin.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchStorageBin.getLanguageId()));
        }

        if (searchStorageBin.getCompanyCodeId() != null && !searchStorageBin.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchStorageBin.getCompanyCodeId()));
        }

        if (searchStorageBin.getPlantId() != null && !searchStorageBin.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchStorageBin.getPlantId()));
        }

        if (searchStorageBin.getSpanId() != null && !searchStorageBin.getSpanId().isEmpty()) {
            final Path<Group> group = root.<Group>get("spanId");
            predicates.add(group.in(searchStorageBin.getSpanId()));
        }

        if (searchStorageBin.getShelfId() != null && !searchStorageBin.getShelfId().isEmpty()) {
            final Path<Group> group = root.<Group>get("shelfId");
            predicates.add(group.in(searchStorageBin.getShelfId()));
        }

        if (searchStorageBin.getStartCreatedOn() != null && searchStorageBin.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchStorageBin.getStartCreatedOn(), searchStorageBin.getEndCreatedOn()));
        }

        if (searchStorageBin.getCreatedBy() != null && !searchStorageBin.getCreatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("createdBy");
            predicates.add(group.in(searchStorageBin.getCreatedBy()));
        }

        if (searchStorageBin.getStartUpdatedOn() != null && searchStorageBin.getEndUpdatedOn() != null) {
            predicates.add(cb.between(root.get("updatedOn"), searchStorageBin.getStartUpdatedOn(), searchStorageBin.getEndUpdatedOn()));
        }

        if (searchStorageBin.getUpdatedBy() != null && !searchStorageBin.getUpdatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("updatedBy");
            predicates.add(group.in(searchStorageBin.getUpdatedBy()));
        }

        if (searchStorageBin.getStatusId() != null && !searchStorageBin.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchStorageBin.getStatusId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}