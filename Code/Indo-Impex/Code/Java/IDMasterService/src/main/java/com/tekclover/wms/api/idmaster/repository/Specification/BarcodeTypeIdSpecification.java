package com.tekclover.wms.api.idmaster.repository.Specification;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.FindBarcodeTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BarcodeTypeIdSpecification implements Specification<BarcodeTypeId> {
    FindBarcodeTypeId findBarcodeTypeId;

    public BarcodeTypeIdSpecification(FindBarcodeTypeId inputSearchParams) {
        this.findBarcodeTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BarcodeTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findBarcodeTypeId.getCompanyCodeId() != null && !findBarcodeTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findBarcodeTypeId.getCompanyCodeId()));
        }

        if (findBarcodeTypeId.getPlantId() != null && !findBarcodeTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findBarcodeTypeId.getPlantId()));
        }

        if (findBarcodeTypeId.getWarehouseId() != null && !findBarcodeTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findBarcodeTypeId.getWarehouseId()));
        }

        if (findBarcodeTypeId.getBarcodeTypeId() != null && !findBarcodeTypeId.getBarcodeTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("barcodeTypeId");
            predicates.add(group.in(findBarcodeTypeId.getBarcodeTypeId()));
        }

        if (findBarcodeTypeId.getBarcodeType() != null && !findBarcodeTypeId.getBarcodeType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("barcodeType");
            predicates.add(group.in(findBarcodeTypeId.getBarcodeType()));
        }
        if (findBarcodeTypeId.getLanguageId() != null && !findBarcodeTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findBarcodeTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
