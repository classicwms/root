package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.vehicle.SearchVehicle;
import com.tekclover.wms.api.masters.model.vehicle.Vehicle;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("serial")
public class VehicleSpecification implements Specification<Vehicle> {

    SearchVehicle searchVehicle;

    public VehicleSpecification(SearchVehicle inputSearchParams) {
        this.searchVehicle = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Vehicle> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchVehicle.getWarehouseId() != null && !searchVehicle.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchVehicle.getWarehouseId()));
        }

        if (searchVehicle.getCompanyCodeId() != null && !searchVehicle.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchVehicle.getCompanyCodeId()));
        }

        if (searchVehicle.getVehicleNumber() != null && !searchVehicle.getVehicleNumber().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("vehicleNumber");
            predicates.add(group.in(searchVehicle.getVehicleNumber()));
        }

        if (searchVehicle.getPlantId() != null && !searchVehicle.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchVehicle.getPlantId()));
        }

        if (searchVehicle.getLanguageId() != null && !searchVehicle.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchVehicle.getLanguageId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
