package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.binclassid.BinClassId;
import com.tekclover.wms.api.idmaster.model.binclassid.FindBinClassId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BinClassIdSpecification implements Specification<BinClassId> {

    FindBinClassId findBinClassId;

    public BinClassIdSpecification(FindBinClassId inputSearchParams) {
        this.findBinClassId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<BinClassId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findBinClassId.getCompanyCodeId() != null && !findBinClassId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findBinClassId.getCompanyCodeId()));
        }

        if (findBinClassId.getPlantId() != null && !findBinClassId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findBinClassId.getPlantId()));
        }

        if (findBinClassId.getWarehouseId() != null && !findBinClassId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findBinClassId.getWarehouseId()));
        }

        if (findBinClassId.getBinClass() != null && !findBinClassId.getBinClass().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("binClass");
            predicates.add(group.in(findBinClassId.getBinClass()));
        }
        if (findBinClassId.getBinClassId() != null && !findBinClassId.getBinClassId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("binClassId");
            predicates.add(group.in(findBinClassId.getBinClassId()));
        }
        if (findBinClassId.getLanguageId() != null && !findBinClassId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findBinClassId.getLanguageId()));
        }


        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
