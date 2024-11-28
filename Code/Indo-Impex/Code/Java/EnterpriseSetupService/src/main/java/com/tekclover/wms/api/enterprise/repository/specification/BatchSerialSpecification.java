package com.tekclover.wms.api.enterprise.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.enterprise.model.batchserial.BatchSerial;
import com.tekclover.wms.api.enterprise.model.batchserial.SearchBatchSerial;

@SuppressWarnings("serial")
public class BatchSerialSpecification implements Specification<BatchSerial> {

    SearchBatchSerial searchBatchSerial;

    public BatchSerialSpecification(SearchBatchSerial inputSearchParams) {
        this.searchBatchSerial = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BatchSerial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchBatchSerial.getWarehouseId() != null && !searchBatchSerial.getWarehouseId().isEmpty()) {
            predicates.add(cb.equal(root.get("warehouseId"), searchBatchSerial.getWarehouseId()));
        }
        if (searchBatchSerial.getCompanyId() != null && !searchBatchSerial.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchBatchSerial.getCompanyId()));
        }

        if (searchBatchSerial.getPlantId() != null && !searchBatchSerial.getPlantId().isEmpty()) {
            predicates.add(cb.equal(root.get("plantId"), searchBatchSerial.getPlantId()));
        }

        if (searchBatchSerial.getStorageMethod() != null && !searchBatchSerial.getStorageMethod().isEmpty()) {
            predicates.add(cb.equal(root.get("storageMethod"), searchBatchSerial.getStorageMethod()));
        }

        if (searchBatchSerial.getId() != null && !searchBatchSerial.getId().isEmpty()) {
            predicates.add(cb.equal(root.get("Id"), searchBatchSerial.getId()));
        }

        if (searchBatchSerial.getMaintenance() != null && !searchBatchSerial.getMaintenance().isEmpty()) {
            predicates.add(cb.equal(root.get("maintenance"), searchBatchSerial.getMaintenance()));
        }

        if (searchBatchSerial.getLanguageId() != null && !searchBatchSerial.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchBatchSerial.getLanguageId()));
        }

        if (searchBatchSerial.getLevelId() != null && !searchBatchSerial.getLevelId().isEmpty()) {
            predicates.add(cb.equal(root.get("levelId"), searchBatchSerial.getLevelId()));
        }

        if (searchBatchSerial.getCreatedBy() != null && !searchBatchSerial.getCreatedBy().isEmpty()) {
            predicates.add(cb.equal(root.get("createdBy"), searchBatchSerial.getCreatedBy()));
        }

        if (searchBatchSerial.getStartCreatedOn() != null && searchBatchSerial.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchBatchSerial.getStartCreatedOn(), searchBatchSerial.getEndCreatedOn()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}