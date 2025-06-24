package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.storagetypeid.FindStorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.FindUserTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.UserTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class UserTypeIdSpecification implements Specification<UserTypeId> {

    FindUserTypeId findUserTypeId;

    public UserTypeIdSpecification(FindUserTypeId inputSearchParams) {
        this.findUserTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<UserTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findUserTypeId.getCompanyCodeId() != null && !findUserTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findUserTypeId.getCompanyCodeId()));
        }

        if (findUserTypeId.getPlantId() != null && !findUserTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findUserTypeId.getPlantId()));
        }

        if (findUserTypeId.getWarehouseId() != null && !findUserTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findUserTypeId.getWarehouseId()));
        }

        if (findUserTypeId.getUserTypeId() != null && !findUserTypeId.getUserTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userTypeId");
            predicates.add(group.in(findUserTypeId.getUserTypeId()));
        }
        if (findUserTypeId.getLanguageId() != null && !findUserTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findUserTypeId.getLanguageId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
