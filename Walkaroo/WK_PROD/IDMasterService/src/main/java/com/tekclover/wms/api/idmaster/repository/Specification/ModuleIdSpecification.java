package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.languageid.FindLanguageId;
import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import com.tekclover.wms.api.idmaster.model.moduleid.FindModuleId;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ModuleIdSpecification implements Specification<ModuleId> {
    FindModuleId findModuleId;

    public ModuleIdSpecification(FindModuleId inputSearchParams) {
        this.findModuleId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ModuleId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findModuleId.getCompanyCodeId() != null && !findModuleId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findModuleId.getCompanyCodeId()));
        }
        if (findModuleId.getWarehouseId() != null && !findModuleId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findModuleId.getWarehouseId()));
        }
        if (findModuleId.getModuleId() != null && !findModuleId.getModuleId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("moduleId");
            predicates.add(group.in(findModuleId.getModuleId()));
        }
        if (findModuleId.getPlantId() != null && !findModuleId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findModuleId.getPlantId()));
        }
        if (findModuleId.getLanguageId() != null && !findModuleId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findModuleId.getLanguageId()));
        }
        if (findModuleId.getMenuId() != null && !findModuleId.getMenuId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("menuId");
            predicates.add(group.in(findModuleId.getMenuId()));
        }
        if (findModuleId.getSubMenuId() != null && !findModuleId.getSubMenuId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("subMenuId");
            predicates.add(group.in(findModuleId.getSubMenuId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
