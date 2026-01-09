package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.BarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.FindBarcodeSubTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BarcodeSubTypeIdSpecification implements Specification<BarcodeSubTypeId> {

    FindBarcodeSubTypeId findBarcodeSubTypeId;

    public BarcodeSubTypeIdSpecification(FindBarcodeSubTypeId inputSearchParams) {
        this.findBarcodeSubTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BarcodeSubTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findBarcodeSubTypeId.getCompanyCodeId() != null && !findBarcodeSubTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findBarcodeSubTypeId.getCompanyCodeId()));
        }

        if (findBarcodeSubTypeId.getPlantId() != null && !findBarcodeSubTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findBarcodeSubTypeId.getPlantId()));
        }

        if (findBarcodeSubTypeId.getWarehouseId() != null && !findBarcodeSubTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findBarcodeSubTypeId.getWarehouseId()));
        }

        if (findBarcodeSubTypeId.getBarcodeSubType() != null && !findBarcodeSubTypeId.getBarcodeSubType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("barcodeSubType");
            predicates.add(group.in(findBarcodeSubTypeId.getBarcodeSubType()));
        }

        if (findBarcodeSubTypeId.getBarcodeSubTypeId() != null && !findBarcodeSubTypeId.getBarcodeSubTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("barcodeSubTypeId");
            predicates.add(group.in(findBarcodeSubTypeId.getBarcodeSubTypeId()));
        }

        if (findBarcodeSubTypeId.getBarcodeTypeId() != null && !findBarcodeSubTypeId.getBarcodeTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("barcodeTypeId");
            predicates.add(group.in(findBarcodeSubTypeId.getBarcodeTypeId()));
        }
        if (findBarcodeSubTypeId.getLanguageId() != null && !findBarcodeSubTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findBarcodeSubTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
