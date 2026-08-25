package com.tekclover.wms.api.idmaster.repository.Specification;


import com.tekclover.wms.api.idmaster.model.currency.Currency;
import com.tekclover.wms.api.idmaster.model.currency.FindCurrency;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CurrencySpecification implements Specification<Currency> {
    FindCurrency findCurrency;

    public CurrencySpecification(FindCurrency inputSearchParams) {
        this.findCurrency = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Currency> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findCurrency.getCurrencyId() != null && !findCurrency.getCurrencyId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("currencyId");
            predicates.add(group.in(findCurrency.getCurrencyId()));
        }

        if (findCurrency.getLanguageId() != null && !findCurrency.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findCurrency.getLanguageId()));
        }


        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
