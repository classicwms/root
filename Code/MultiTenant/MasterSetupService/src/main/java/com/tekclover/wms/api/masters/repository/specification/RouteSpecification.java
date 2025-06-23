package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.route.Route;
import com.tekclover.wms.api.masters.model.route.SearchRoute;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class RouteSpecification implements Specification<Route> {

    SearchRoute searchRoute;

    public RouteSpecification(SearchRoute inputSearchParams) {
        this.searchRoute = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Route> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchRoute.getWarehouseId() != null && !searchRoute.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchRoute.getWarehouseId()));
        }

        if (searchRoute.getRouteId() != null && !searchRoute.getRouteId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("routeId");
            predicates.add(group.in(searchRoute.getRouteId()));
        }

        if (searchRoute.getCompanyCodeId() != null && !searchRoute.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchRoute.getCompanyCodeId()));
        }

        if (searchRoute.getLanguageId() != null && !searchRoute.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchRoute.getLanguageId()));
        }

        if (searchRoute.getPlantId() != null && !searchRoute.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchRoute.getPlantId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
