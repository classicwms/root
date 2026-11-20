package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.state.FindState;
import com.tekclover.wms.api.idmaster.model.state.State;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StateSpecification implements Specification<State> {

   FindState findState;

    public StateSpecification(FindState inputSearchParams) {
        this.findState = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<State> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findState.getStateId() != null && !findState.getStateId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("stateId");
            predicates.add(group.in(findState.getStateId()));
        }

        if (findState.getStateName() != null && !findState.getStateName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("stateName");
            predicates.add(group.in(findState.getStateName()));
        }

        if (findState.getCountryId() != null && !findState.getCountryId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("countryId");
            predicates.add(group.in(findState.getCountryId()));
        }
        if (findState.getLanguageId() != null && !findState.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findState.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
