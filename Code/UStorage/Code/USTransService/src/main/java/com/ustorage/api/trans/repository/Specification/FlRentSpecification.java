package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.flrent.FindFlRent;
import com.ustorage.api.trans.model.flrent.FlRent;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FlRentSpecification implements Specification<FlRent> {

	FindFlRent findFlRent;

	public FlRentSpecification(FindFlRent inputSearchParams) {
		this.findFlRent = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<FlRent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findFlRent.getItemCode() != null && !findFlRent.getItemCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemCode");
			predicates.add(group.in(findFlRent.getItemCode()));
		}

		if (findFlRent.getCodeId() != null && !findFlRent.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findFlRent.getCodeId()));
		}

		if (findFlRent.getItemGroup() != null && !findFlRent.getItemGroup().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemGroup");
			predicates.add(group.in(findFlRent.getItemGroup()));
		}

		if (findFlRent.getItemType() != null && !findFlRent.getItemType().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemType");
			predicates.add(group.in(findFlRent.getItemType()));
		}

		if (findFlRent.getSaleUnitOfMeasure() != null && !findFlRent.getSaleUnitOfMeasure().isEmpty()) {
			final Path<Group> group = root.<Group>get("saleUnitOfMeasure");
			predicates.add(group.in(findFlRent.getSaleUnitOfMeasure()));
		}

		if (findFlRent.getStatus() != null && !findFlRent.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findFlRent.getStatus()));
		}

		if(findFlRent.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findFlRent.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
