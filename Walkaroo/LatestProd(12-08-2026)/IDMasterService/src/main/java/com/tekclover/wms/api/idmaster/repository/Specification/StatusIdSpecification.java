package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.statusid.FindStatusId;
import com.tekclover.wms.api.idmaster.model.statusid.StatusId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StatusIdSpecification implements Specification<StatusId> {
    FindStatusId findStatusId;

    public StatusIdSpecification(FindStatusId inputSearchParams) {
        this.findStatusId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StatusId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStatusId.getStatusId() != null && !findStatusId.getStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("statusId");
            predicates.add(group.in(findStatusId.getStatusId()));
        }

        if (findStatusId.getStatus() != null && !findStatusId.getStatus().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("status");
            predicates.add(group.in(findStatusId.getStatus()));
        }
        if (findStatusId.getLanguageId() != null && !findStatusId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findStatusId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
