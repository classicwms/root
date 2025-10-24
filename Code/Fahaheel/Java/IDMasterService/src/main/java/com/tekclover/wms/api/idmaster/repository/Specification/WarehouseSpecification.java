package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.warehouseid.FindWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.FindWarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.WarehouseTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class WarehouseSpecification implements Specification<Warehouse> {
    FindWarehouse findWarehouse;

    public WarehouseSpecification(FindWarehouse inputSearchParams) {
        this.findWarehouse = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Warehouse> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findWarehouse.getWarehouseId() != null && !findWarehouse.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findWarehouse.getWarehouseId()));
        }

        if (findWarehouse.getPlantId() != null && !findWarehouse.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findWarehouse.getPlantId()));
        }

        if (findWarehouse.getCompanyCodeId() != null && !findWarehouse.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findWarehouse.getCompanyCodeId()));
        }
        if (findWarehouse.getLanguageId() != null && !findWarehouse.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findWarehouse.getLanguageId()));
        }
        if (findWarehouse.getNoOfUsers() != null) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("noOfUsers");
            predicates.add(group.in(findWarehouse.getNoOfUsers()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
