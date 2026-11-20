package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.putawaystrategy.FindPutAwayStrategy;
import com.tekclover.wms.api.masters.model.putawaystrategy.PutAwayStrategy;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class PutAwayStrategySpecification implements Specification<PutAwayStrategy> {
    FindPutAwayStrategy findPutAwayStrategy;

    public PutAwayStrategySpecification(FindPutAwayStrategy inputSearchParams) {
        this.findPutAwayStrategy = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PutAwayStrategy> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPutAwayStrategy.getLanguageId() != null && !findPutAwayStrategy.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(findPutAwayStrategy.getLanguageId()));
        }
        if (findPutAwayStrategy.getCompanyCodeId() != null && !findPutAwayStrategy.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(findPutAwayStrategy.getCompanyCodeId()));
        }
        if (findPutAwayStrategy.getPlantId() != null && !findPutAwayStrategy.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(findPutAwayStrategy.getPlantId()));
        }
        if (findPutAwayStrategy.getWarehouseId() != null && !findPutAwayStrategy.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(findPutAwayStrategy.getWarehouseId()));
        }
        if (findPutAwayStrategy.getBrand() != null && !findPutAwayStrategy.getBrand().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("brand");
            predicates.add(group.in(findPutAwayStrategy.getBrand()));
        }
        if (findPutAwayStrategy.getArticle() != null && !findPutAwayStrategy.getArticle().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("article");
            predicates.add(group.in(findPutAwayStrategy.getArticle()));
        }
        if (findPutAwayStrategy.getCategory() != null && !findPutAwayStrategy.getCategory().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("category");
            predicates.add(group.in(findPutAwayStrategy.getCategory()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
