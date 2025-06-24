package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.languageid.FindLanguageId;
import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class LanguageIdSpecification implements Specification<LanguageId> {
    FindLanguageId findLanguageId;

    public LanguageIdSpecification(FindLanguageId inputSearchParams) {
        this.findLanguageId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<LanguageId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findLanguageId.getLanguageId() != null && !findLanguageId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findLanguageId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
