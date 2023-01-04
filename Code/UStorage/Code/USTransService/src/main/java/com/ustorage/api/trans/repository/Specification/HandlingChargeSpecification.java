package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.handlingcharge.FindHandlingCharge;
import com.ustorage.api.trans.model.handlingcharge.HandlingCharge;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class HandlingChargeSpecification implements Specification<HandlingCharge> {

	FindHandlingCharge findHandlingCharge;

	public HandlingChargeSpecification(FindHandlingCharge inputSearchParams) {
		this.findHandlingCharge = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<HandlingCharge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findHandlingCharge.getItemCode() != null && !findHandlingCharge.getItemCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemCode");
			predicates.add(group.in(findHandlingCharge.getItemCode()));
		}

		if (findHandlingCharge.getCodeId() != null && !findHandlingCharge.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findHandlingCharge.getCodeId()));
		}

		if (findHandlingCharge.getItemGroup() != null && !findHandlingCharge.getItemGroup().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemGroup");
			predicates.add(group.in(findHandlingCharge.getItemGroup()));
		}

		if (findHandlingCharge.getItemType() != null && !findHandlingCharge.getItemType().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemType");
			predicates.add(group.in(findHandlingCharge.getItemType()));
		}

		if (findHandlingCharge.getSaleUnitOfMeasure() != null && !findHandlingCharge.getSaleUnitOfMeasure().isEmpty()) {
			final Path<Group> group = root.<Group>get("saleUnitOfMeasure");
			predicates.add(group.in(findHandlingCharge.getSaleUnitOfMeasure()));
		}

		if (findHandlingCharge.getStatus() != null && !findHandlingCharge.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findHandlingCharge.getStatus()));
		}

		if(findHandlingCharge.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findHandlingCharge.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
