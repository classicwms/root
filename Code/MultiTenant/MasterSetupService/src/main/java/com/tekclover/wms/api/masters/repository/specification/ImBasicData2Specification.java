package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.imbasicdata2.ImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.SearchImBasicData2;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImBasicData2Specification implements Specification<ImBasicData2> {


    SearchImBasicData2 searchImBasicData2;

    public ImBasicData2Specification(SearchImBasicData2 inputSearchParams) {
        this.searchImBasicData2 = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ImBasicData2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchImBasicData2.getWarehouseId() != null && !searchImBasicData2.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("warehouseId");
            predicates.add(group.in(searchImBasicData2.getWarehouseId()));
        }
        if (searchImBasicData2.getItemBarcode() != null && !searchImBasicData2.getItemBarcode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemBarcode");
            predicates.add(group.in(searchImBasicData2.getItemBarcode()));
        }

        if (searchImBasicData2.getItemCode() != null && !searchImBasicData2.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("itemCode");
            predicates.add(group.in(searchImBasicData2.getItemCode()));
        }

        if (searchImBasicData2.getCompanyCodeId() != null && !searchImBasicData2.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("companyCodeId");
            predicates.add(group.in(searchImBasicData2.getCompanyCodeId()));
        }

        if (searchImBasicData2.getLanguageId() != null && !searchImBasicData2.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("languageId");
            predicates.add(group.in(searchImBasicData2.getLanguageId()));
        }

        if (searchImBasicData2.getPlantId() != null && !searchImBasicData2.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group> get("plantId");
            predicates.add(group.in(searchImBasicData2.getPlantId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
