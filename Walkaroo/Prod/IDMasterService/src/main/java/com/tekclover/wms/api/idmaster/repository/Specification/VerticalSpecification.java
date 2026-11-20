package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.vertical.FindVertical;
import com.tekclover.wms.api.idmaster.model.vertical.Vertical;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class VerticalSpecification implements Specification<Vertical> {
 FindVertical findVertical;
    public VerticalSpecification(FindVertical inputSearchParams) {
        this.findVertical = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Vertical> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findVertical.getVerticalId() != null && !findVertical.getVerticalId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("verticalId");
            predicates.add(group.in(findVertical.getVerticalId()));
        }

        if (findVertical.getLanguageId() != null && !findVertical.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findVertical.getLanguageId()));
        }

        if (findVertical.getVerticalName() != null && !findVertical.getVerticalName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("verticalName");
            predicates.add(group.in(findVertical.getVerticalName()));
        }

        if (findVertical.getRemark() != null && !findVertical.getRemark().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("remark");
            predicates.add(group.in(findVertical.getRemark()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
