package com.tekclover.wms.api.enterprise.repository.specification;

import com.tekclover.wms.api.enterprise.model.warehouse.SearchWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.Warehouse;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class WarehouseSpecification implements Specification<Warehouse> {

    SearchWarehouse searchWarehouse;

    public WarehouseSpecification(SearchWarehouse inputSearchParams) {
        this.searchWarehouse = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Warehouse> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchWarehouse.getCompanyId() != null && !searchWarehouse.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchWarehouse.getCompanyId()));
        }
        if (searchWarehouse.getModeOfImplementation() != null && !searchWarehouse.getModeOfImplementation().isEmpty()) {
            predicates.add(cb.equal(root.get("modeOfImplementation"), searchWarehouse.getModeOfImplementation()));
        }
        if (searchWarehouse.getWarehouseTypeId() != null && searchWarehouse.getWarehouseTypeId().longValue() != 0) {
            predicates.add(cb.equal(root.get("warehouseTypeId"), searchWarehouse.getWarehouseTypeId()));
        }

        if (searchWarehouse.getPlantId() != null && !searchWarehouse.getPlantId().isEmpty()) {
            predicates.add(cb.equal(root.get("plantId"), searchWarehouse.getPlantId()));
        }

        if (searchWarehouse.getLanguageId() != null && !searchWarehouse.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchWarehouse.getLanguageId()));
        }

        if (searchWarehouse.getWarehouseId() != null && !searchWarehouse.getWarehouseId().isEmpty()) {
            predicates.add(cb.equal(root.get("warehouseId"), searchWarehouse.getWarehouseId()));
        }

        if (searchWarehouse.getContactName() != null && !searchWarehouse.getContactName().isEmpty()) {
            predicates.add(cb.equal(root.get("contactName"), searchWarehouse.getContactName()));
        }

        if (searchWarehouse.getCreatedBy() != null && !searchWarehouse.getCreatedBy().isEmpty()) {
            predicates.add(cb.equal(root.get("createdBy"), searchWarehouse.getCreatedBy()));
        }

        if (searchWarehouse.getStartCreatedOn() != null && searchWarehouse.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchWarehouse.getStartCreatedOn(), searchWarehouse.getEndCreatedOn()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}