package com.tekclover.wms.api.idmaster.repository.Specification;


import com.tekclover.wms.api.idmaster.model.companyid.CompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.FindCompanyId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CompanyIdSpecification implements Specification<CompanyId> {
    FindCompanyId findCompanyId;

    public CompanyIdSpecification(FindCompanyId inputSearchParams) {
        this.findCompanyId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<CompanyId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findCompanyId.getCompanyCodeId() != null && !findCompanyId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findCompanyId.getCompanyCodeId()));
        }

        if (findCompanyId.getLanguageId() != null && !findCompanyId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findCompanyId.getLanguageId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
