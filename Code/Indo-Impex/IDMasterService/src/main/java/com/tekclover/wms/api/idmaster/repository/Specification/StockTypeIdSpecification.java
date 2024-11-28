package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.statusmessagesid.FindStatusMessagesId;
import com.tekclover.wms.api.idmaster.model.statusmessagesid.StatusMessagesId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.FindStockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.StockTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StockTypeIdSpecification implements Specification<StockTypeId> {
    FindStockTypeId findStockTypeId;

    public StockTypeIdSpecification(FindStockTypeId inputSearchParams) {
        this.findStockTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StockTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStockTypeId.getPlantId() != null && !findStockTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findStockTypeId.getPlantId()));
        }

        if (findStockTypeId.getCompanyCodeId() != null && !findStockTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findStockTypeId.getCompanyCodeId()));
        }
        if (findStockTypeId.getWarehouseId() != null && !findStockTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findStockTypeId.getWarehouseId()));
        }
        if (findStockTypeId.getStockTypeId() != null && !findStockTypeId.getStockTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("stockTypeId");
            predicates.add(group.in(findStockTypeId.getStockTypeId()));
        }
        if (findStockTypeId.getLanguageId() != null && !findStockTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findStockTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
