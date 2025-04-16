package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.threepl.pricelist.FindPriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PriceListSpecification implements Specification<PriceList> {
    FindPriceList findPriceList;

    public PriceListSpecification(FindPriceList inputSearchParams) {
        this.findPriceList = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<PriceList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findPriceList.getWarehouseId() != null && !findPriceList.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(findPriceList.getWarehouseId()));
        }

        if (findPriceList.getCompanyCodeId() != null && !findPriceList.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(findPriceList.getCompanyCodeId()));
        }

        if (findPriceList.getModuleId() != null && !findPriceList.getModuleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("moduleId");
            predicates.add(group.in(findPriceList.getModuleId()));
        }

        if (findPriceList.getPlantId() != null && !findPriceList.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(findPriceList.getPlantId()));
        }
        if (findPriceList.getPriceListId() != null && !findPriceList.getPriceListId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("priceListId");
            predicates.add(group.in(findPriceList.getPriceListId()));
        }
        if (findPriceList.getServiceTypeId() != null && !findPriceList.getServiceTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("serviceTypeId");
            predicates.add(group.in(findPriceList.getServiceTypeId()));
        }
        if (findPriceList.getStatusId() != null && !findPriceList.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("statusId");
            predicates.add(group.in(findPriceList.getStatusId()));
        }
        if (findPriceList.getLanguageId() != null && !findPriceList.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(findPriceList.getLanguageId()));
        }
        if (findPriceList.getChargeRangeId() != null && !findPriceList.getChargeRangeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("chargeRangeId");
            predicates.add(group.in(findPriceList.getChargeRangeId()));
        }
        if (findPriceList.getChargeRangeTo() != null && !findPriceList.getChargeRangeTo().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("chargeRangeTo");
            predicates.add(group.in(findPriceList.getChargeRangeTo()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
