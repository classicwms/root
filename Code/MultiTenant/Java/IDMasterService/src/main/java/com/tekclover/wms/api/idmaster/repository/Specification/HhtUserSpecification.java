package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.handlingunitid.FindHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.HandlingUnitId;
import com.tekclover.wms.api.idmaster.model.hhtuser.FindHhtUser;
import com.tekclover.wms.api.idmaster.model.hhtuser.HhtUser;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class HhtUserSpecification implements Specification<HhtUser> {
    FindHhtUser findHhtUser;

    public HhtUserSpecification(FindHhtUser inputSearchParams) {
        this.findHhtUser = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<HhtUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findHhtUser.getCompanyCodeId() != null && !findHhtUser.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findHhtUser.getCompanyCodeId()));
        }

        if (findHhtUser.getPlantId() != null && !findHhtUser.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findHhtUser.getPlantId()));
        }

        if (findHhtUser.getUserId() != null && !findHhtUser.getUserId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userId");
            predicates.add(group.in(findHhtUser.getUserId()));
        }

        if (findHhtUser.getWarehouseId() != null && !findHhtUser.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findHhtUser.getWarehouseId()));
        }
        if (findHhtUser.getLanguageId() != null && !findHhtUser.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findHhtUser.getLanguageId()));
        }
        if (findHhtUser.getUserPresent() != null && !findHhtUser.getUserPresent().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userPresent");
            predicates.add(group.in(findHhtUser.getUserPresent()));
        }
        if (findHhtUser.getNoOfDaysLeave() != null && !findHhtUser.getNoOfDaysLeave().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("noOfDaysLeave");
            predicates.add(group.in(findHhtUser.getNoOfDaysLeave()));
        }
        if (findHhtUser.getLevelId() != null && !findHhtUser.getLevelId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("levelId");
            predicates.add(group.in(findHhtUser.getLevelId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
