package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.consumables.*;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ConsumablesSpecification implements Specification<Consumables> {

	FindConsumables findConsumables;

	public ConsumablesSpecification(FindConsumables inputSearchParams) {
		this.findConsumables = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<Consumables> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findConsumables.getItemCode() != null && !findConsumables.getItemCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemCode");
			predicates.add(group.in(findConsumables.getItemCode()));
		}

		if (findConsumables.getCodeId() != null && !findConsumables.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findConsumables.getCodeId()));
		}

		if (findConsumables.getItemType() != null && !findConsumables.getItemType().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemType");
			predicates.add(group.in(findConsumables.getItemType()));
		}

		if (findConsumables.getItemGroup() != null && !findConsumables.getItemGroup().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemGroup");
			predicates.add(group.in(findConsumables.getItemGroup()));
		}

		if (findConsumables.getWarehouse() != null && !findConsumables.getWarehouse().isEmpty()) {
			final Path<Group> group = root.<Group>get("warehouse");
			predicates.add(group.in(findConsumables.getWarehouse()));
		}

		if (findConsumables.getStatus() != null && !findConsumables.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findConsumables.getStatus()));
		}

		if (findConsumables.getInStock() != null && !findConsumables.getInStock().isEmpty()) {
			final Path<Group> group = root.<Group>get("inStock");
			predicates.add(group.in(findConsumables.getInStock()));
		}

		if (findConsumables.getLastReceipt() != null && !findConsumables.getLastReceipt().isEmpty()) {
			final Path<Group> group = root.<Group>get("lastReceipt");
			predicates.add(group.in(findConsumables.getLastReceipt()));
		}

		if(findConsumables.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findConsumables.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
