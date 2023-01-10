package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.storenumber.*;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StoreNumberSpecification implements Specification<StoreNumber> {

	FindStoreNumber findStoreNumber;

	public StoreNumberSpecification(FindStoreNumber inputSearchParams) {
		this.findStoreNumber = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<StoreNumber> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findStoreNumber.getStoreNumber() != null && !findStoreNumber.getStoreNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("storeNumber");
			predicates.add(group.in(findStoreNumber.getStoreNumber()));
		}

		return cb.and(predicates.toArray(new Predicate[] {}));


	}
}
