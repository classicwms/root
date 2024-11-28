package com.tekclover.wms.api.masters.repository.specification;


import com.tekclover.wms.api.masters.model.drivervehicleassignment.DriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.SearchDriverVehicleAssignment;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DriverVehicleAssignmentSpecification implements Specification<DriverVehicleAssignment> {

    SearchDriverVehicleAssignment searchDriverVehicleAssignment;

    public DriverVehicleAssignmentSpecification(SearchDriverVehicleAssignment inputSearchParams) {
        this.searchDriverVehicleAssignment = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DriverVehicleAssignment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchDriverVehicleAssignment.getWarehouseId() != null && !searchDriverVehicleAssignment.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchDriverVehicleAssignment.getWarehouseId()));
        }

        if (searchDriverVehicleAssignment.getCompanyCodeId() != null && !searchDriverVehicleAssignment.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(searchDriverVehicleAssignment.getCompanyCodeId()));
        }

        if (searchDriverVehicleAssignment.getPlantId() != null && !searchDriverVehicleAssignment.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchDriverVehicleAssignment.getPlantId()));
        }
        if (searchDriverVehicleAssignment.getLanguageId() != null && !searchDriverVehicleAssignment.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchDriverVehicleAssignment.getLanguageId()));
        }
        if (searchDriverVehicleAssignment.getDriverId() != null && !searchDriverVehicleAssignment.getDriverId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("driverId");
            predicates.add(group.in(searchDriverVehicleAssignment.getDriverId()));
        }
        if (searchDriverVehicleAssignment.getVehicleNumber() != null && !searchDriverVehicleAssignment.getVehicleNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("vehicleNumber");
            predicates.add(group.in(searchDriverVehicleAssignment.getVehicleNumber()));
        }
        if (searchDriverVehicleAssignment.getRouteId() != null && !searchDriverVehicleAssignment.getRouteId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("routeId");
            predicates.add(group.in(searchDriverVehicleAssignment.getRouteId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}