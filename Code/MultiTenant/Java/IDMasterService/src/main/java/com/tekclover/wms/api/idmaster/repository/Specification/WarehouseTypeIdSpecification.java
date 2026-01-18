package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.warehousetypeid.FindWarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.WarehouseTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class WarehouseTypeIdSpecification implements Specification<WarehouseTypeId> {
    FindWarehouseTypeId findWarehouseTypeId;

    public WarehouseTypeIdSpecification(FindWarehouseTypeId inputSearchParams) {
        this.findWarehouseTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<WarehouseTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (findWarehouseTypeId.getWarehouseTypeId() != null && !findWarehouseTypeId.getWarehouseTypeId().isEmpty()) {
            predicates.add(root.get("warehouseTypeId").in(findWarehouseTypeId.getWarehouseTypeId()));
        }

        // companyCodeId (Single value)
        if (findWarehouseTypeId.getCompanyCodeId() != null && !findWarehouseTypeId.getCompanyCodeId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyCodeId"), findWarehouseTypeId.getCompanyCodeId()));
        }

        // warehouseId (List)
        if (findWarehouseTypeId.getWarehouseId() != null && !findWarehouseTypeId.getWarehouseId().isEmpty()) {
            predicates.add(root.get("warehouseId").in(findWarehouseTypeId.getWarehouseId()));
        }

        // plantId (Single value)
        if (findWarehouseTypeId.getPlantId() != null && !findWarehouseTypeId.getPlantId().isEmpty()) {
            predicates.add(cb.equal(root.get("plantId"), findWarehouseTypeId.getPlantId()));
        }

        // languageId (List)
        if (findWarehouseTypeId.getLanguageId() != null && !findWarehouseTypeId.getLanguageId().isEmpty()) {
            predicates.add(root.get("languageId").in(findWarehouseTypeId.getLanguageId()));
        }

        // deletionIndicator = 0
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
