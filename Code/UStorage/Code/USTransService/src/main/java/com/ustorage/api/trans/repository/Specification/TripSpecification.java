package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.trip.FindTrip;
import com.ustorage.api.trans.model.trip.Trip;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TripSpecification implements Specification<Trip> {

	FindTrip findTrip;

	public TripSpecification(FindTrip inputSearchParams) {
		this.findTrip = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<Trip> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findTrip.getItemCode() != null && !findTrip.getItemCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemCode");
			predicates.add(group.in(findTrip.getItemCode()));
		}

		if (findTrip.getCodeId() != null && !findTrip.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findTrip.getCodeId()));
		}

		if (findTrip.getItemGroup() != null && !findTrip.getItemGroup().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemGroup");
			predicates.add(group.in(findTrip.getItemGroup()));
		}

		if (findTrip.getItemType() != null && !findTrip.getItemType().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemType");
			predicates.add(group.in(findTrip.getItemType()));
		}

		if (findTrip.getSaleUnitOfMeasure() != null && !findTrip.getSaleUnitOfMeasure().isEmpty()) {
			final Path<Group> group = root.<Group>get("saleUnitOfMeasure");
			predicates.add(group.in(findTrip.getSaleUnitOfMeasure()));
		}

		if (findTrip.getStatus() != null && !findTrip.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findTrip.getStatus()));
		}

		if(findTrip.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findTrip.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
