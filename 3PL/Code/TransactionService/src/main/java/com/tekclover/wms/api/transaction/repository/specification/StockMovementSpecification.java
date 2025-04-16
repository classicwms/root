package com.tekclover.wms.api.transaction.repository.specification;


import com.tekclover.wms.api.transaction.model.threepl.stockmovement.FindStockMovement;
import com.tekclover.wms.api.transaction.model.threepl.stockmovement.StockMovement;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class StockMovementSpecification implements Specification<StockMovement> {

    FindStockMovement findStockMovement;

    public StockMovementSpecification(FindStockMovement inputSearchParams) {
        this.findStockMovement = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StockMovement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStockMovement.getWarehouseId() != null && !findStockMovement.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(findStockMovement.getWarehouseId()));
        }

        if (findStockMovement.getPlantId() != null && !findStockMovement.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(findStockMovement.getPlantId()));
        }

        if (findStockMovement.getCompanyCodeId() != null && !findStockMovement.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(findStockMovement.getCompanyCodeId()));
        }

        if (findStockMovement.getLanguageId() != null && !findStockMovement.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(findStockMovement.getLanguageId()));
        }

        if (findStockMovement.getMovementDocNo() != null && !findStockMovement.getMovementDocNo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("movementDocNo");
            predicates.add(group.in(findStockMovement.getMovementDocNo()));
        }



        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
