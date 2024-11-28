package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.roleaccess.FindRoleAccess;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class RoleAccessIdSpecification implements Specification<RoleAccess> {

    FindRoleAccess findRoleAccess;

    public RoleAccessIdSpecification(FindRoleAccess inputSearchParams) {
        this.findRoleAccess = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<RoleAccess> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findRoleAccess.getCompanyCodeId() != null && !findRoleAccess.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findRoleAccess.getCompanyCodeId()));
        }

        if (findRoleAccess.getPlantId() != null && !findRoleAccess.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findRoleAccess.getPlantId()));
        }

        if (findRoleAccess.getRoleId() != null && !findRoleAccess.getRoleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("roleId");
            predicates.add(group.in(findRoleAccess.getRoleId()));
        }

        if (findRoleAccess.getMenuId() != null && !findRoleAccess.getMenuId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("menuId");
            predicates.add(group.in(findRoleAccess.getMenuId()));
        }

        if (findRoleAccess.getSubMenuId() != null && !findRoleAccess.getSubMenuId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("subMenuId");
            predicates.add(group.in(findRoleAccess.getSubMenuId()));
        }

        if (findRoleAccess.getWarehouseId() != null && !findRoleAccess.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findRoleAccess.getWarehouseId()));
        }
        if (findRoleAccess.getLanguageId() != null && !findRoleAccess.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findRoleAccess.getLanguageId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
