package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.driver.Driver;
import com.tekclover.wms.api.masters.model.driver.SearchDriver;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DriverSpecification implements Specification<Driver> {

    SearchDriver searchDriver;

    public DriverSpecification(SearchDriver inputSearchParams) {
        this.searchDriver = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Driver> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchDriver.getWarehouseId() != null && !searchDriver.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchDriver.getWarehouseId()));
        }

        if (searchDriver.getCompanyCodeId() != null && !searchDriver.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchDriver.getCompanyCodeId()));
        }

        if (searchDriver.getPlantId() != null && !searchDriver.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchDriver.getPlantId()));
        }
        if (searchDriver.getLanguageId() != null && !searchDriver.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchDriver.getLanguageId()));
        }
        if (searchDriver.getUserId() != null && !searchDriver.getUserId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userId");
            predicates.add(group.in(searchDriver.getUserId()));
        }
        if (searchDriver.getDriverId() != null && !searchDriver.getDriverId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("driverId");
            predicates.add(group.in(searchDriver.getDriverId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}