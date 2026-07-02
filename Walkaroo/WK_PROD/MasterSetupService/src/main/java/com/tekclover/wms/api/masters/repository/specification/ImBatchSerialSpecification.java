package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.imbatchserial.ImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.SearchImBatchSerial;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImBatchSerialSpecification implements Specification<ImBatchSerial> {

SearchImBatchSerial searchImBatchSerial;

    public ImBatchSerialSpecification(SearchImBatchSerial inputSearchParams) {
        this.searchImBatchSerial = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImBatchSerial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImBatchSerial.getWarehouseId() != null && !searchImBatchSerial.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchImBatchSerial.getWarehouseId()));
        }
        if (searchImBatchSerial.getStorageMethod() != null && !searchImBatchSerial.getStorageMethod().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("storageMethod");
            predicates.add(group.in(searchImBatchSerial.getStorageMethod()));
        }

        if (searchImBatchSerial.getItemCode() != null && !searchImBatchSerial.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemCode");
            predicates.add(group.in(searchImBatchSerial.getItemCode()));
        }

        if (searchImBatchSerial.getCompanyCodeId() != null && !searchImBatchSerial.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchImBatchSerial.getCompanyCodeId()));
        }

        if (searchImBatchSerial.getLanguageId() != null && !searchImBatchSerial.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchImBatchSerial.getLanguageId()));
        }

        if (searchImBatchSerial.getPlantId() != null && !searchImBatchSerial.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchImBatchSerial.getPlantId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
