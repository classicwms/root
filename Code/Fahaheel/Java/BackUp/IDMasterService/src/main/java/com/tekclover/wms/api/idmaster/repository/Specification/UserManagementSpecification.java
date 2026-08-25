package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.user.FindUserManagement;
import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class UserManagementSpecification implements Specification<UserManagement> {

    FindUserManagement findUserManagement;

    public UserManagementSpecification(FindUserManagement inputSearchParams) {
        this.findUserManagement = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<UserManagement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findUserManagement.getCompanyCode() != null && !findUserManagement.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findUserManagement.getCompanyCode()));
        }

        if (findUserManagement.getPlantId() != null && !findUserManagement.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findUserManagement.getPlantId()));
        }

        if (findUserManagement.getWarehouseId() != null && !findUserManagement.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findUserManagement.getWarehouseId()));
        }

        if (findUserManagement.getUserTypeId() != null && !findUserManagement.getUserTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userTypeId");
            predicates.add(group.in(findUserManagement.getUserTypeId()));
        }
        if (findUserManagement.getLanguageId() != null && !findUserManagement.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findUserManagement.getLanguageId()));
        }
        if (findUserManagement.getUserId() != null && !findUserManagement.getUserId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userId");
            predicates.add(group.in(findUserManagement.getUserId()));
        }
        if (findUserManagement.getUserRoleId() != null && !findUserManagement.getUserRoleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userRoleId");
            predicates.add(group.in(findUserManagement.getUserRoleId()));
        }
        if (findUserManagement.getHhtLoggedIn() != null) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("hhtLoggedIn");
            predicates.add(group.in(findUserManagement.getHhtLoggedIn()));
        }
        if (findUserManagement.getPortalLoggedIn() != null) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("portalLoggedIn");
            predicates.add(group.in(findUserManagement.getPortalLoggedIn()));
        }


        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
