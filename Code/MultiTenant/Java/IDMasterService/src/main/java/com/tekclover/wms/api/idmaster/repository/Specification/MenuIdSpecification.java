package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.levelid.FindLevelId;
import com.tekclover.wms.api.idmaster.model.levelid.LevelId;
import com.tekclover.wms.api.idmaster.model.menuid.FindMenuId;
import com.tekclover.wms.api.idmaster.model.menuid.MenuId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MenuIdSpecification implements Specification<MenuId> {

    FindMenuId findMenuId;

    public MenuIdSpecification(FindMenuId inputSearchParams) {
        this.findMenuId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<MenuId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findMenuId.getCompanyCodeId() != null && !findMenuId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findMenuId.getCompanyCodeId()));
        }
        if (findMenuId.getWarehouseId() != null && !findMenuId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findMenuId.getWarehouseId()));
        }
        if (findMenuId.getMenuId() != null && !findMenuId.getMenuId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("menuId");
            predicates.add(group.in(findMenuId.getMenuId()));
        }
        if (findMenuId.getPlantId() != null && !findMenuId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findMenuId.getPlantId()));
        }
        if (findMenuId.getSubMenuId() != null && !findMenuId.getSubMenuId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("subMenuId");
            predicates.add(group.in(findMenuId.getSubMenuId()));
        }
        if (findMenuId.getAuthorizationObjectId() != null && !findMenuId.getAuthorizationObjectId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("authorizationObjectId");
            predicates.add(group.in(findMenuId.getAuthorizationObjectId()));
        }
        if (findMenuId.getLanguageId() != null && !findMenuId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findMenuId.getLanguageId()));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
