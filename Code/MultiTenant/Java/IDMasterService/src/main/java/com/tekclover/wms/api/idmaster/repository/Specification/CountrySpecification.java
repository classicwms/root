package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.country.Country;
import com.tekclover.wms.api.idmaster.model.country.FindCountry;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CountrySpecification implements Specification<Country> {
    FindCountry findCountry;

    public CountrySpecification(FindCountry inputSearchParams) {
        this.findCountry = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Country> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findCountry.getCountryId() != null && !findCountry.getCountryId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("countryId");
            predicates.add(group.in(findCountry.getCountryId()));
        }

        if (findCountry.getCountryName() != null && !findCountry.getCountryName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("countryName");
            predicates.add(group.in(findCountry.getCountryName()));
        }
        if (findCountry.getLanguageId() != null && !findCountry.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findCountry.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
