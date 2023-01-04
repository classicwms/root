package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.storageunit.*;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StorageUnitSpecification implements Specification<StorageUnit> {

	FindStorageUnit findStorageUnit;

	public StorageUnitSpecification(FindStorageUnit inputSearchParams) {
		this.findStorageUnit = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<StorageUnit> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findStorageUnit.getItemCode() != null && !findStorageUnit.getItemCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemCode");
			predicates.add(group.in(findStorageUnit.getItemCode()));
		}

		if (findStorageUnit.getCodeId() != null && !findStorageUnit.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findStorageUnit.getCodeId()));
		}

		if (findStorageUnit.getItemType() != null && !findStorageUnit.getItemType().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemType");
			predicates.add(group.in(findStorageUnit.getItemType()));
		}

		if (findStorageUnit.getItemGroup() != null && !findStorageUnit.getItemGroup().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemGroup");
			predicates.add(group.in(findStorageUnit.getItemGroup()));
		}

		if (findStorageUnit.getDoorType() != null && !findStorageUnit.getDoorType().isEmpty()) {
			final Path<Group> group = root.<Group>get("doorType");
			predicates.add(group.in(findStorageUnit.getDoorType()));
		}

		if (findStorageUnit.getBin() != null && !findStorageUnit.getBin().isEmpty()) {
			final Path<Group> group = root.<Group>get("bin");
			predicates.add(group.in(findStorageUnit.getBin()));
		}

		if (findStorageUnit.getStorageType() != null && !findStorageUnit.getStorageType().isEmpty()) {
			final Path<Group> group = root.<Group>get("storageType");
			predicates.add(group.in(findStorageUnit.getStorageType()));
		}

		if (findStorageUnit.getPriceMeterSquare() != null && !findStorageUnit.getPriceMeterSquare().isEmpty()) {
			final Path<Group> group = root.<Group>get("priceMeterSquare");
			predicates.add(group.in(findStorageUnit.getPriceMeterSquare()));
		}

		if (findStorageUnit.getPhase() != null && !findStorageUnit.getPhase().isEmpty()) {
			final Path<Group> group = root.<Group>get("phase");
			predicates.add(group.in(findStorageUnit.getPhase()));
		}

		if (findStorageUnit.getStatus() != null && !findStorageUnit.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findStorageUnit.getStatus()));
		}

		if (findStorageUnit.getAvailability() != null && !findStorageUnit.getAvailability().isEmpty()) {
			final Path<Group> group = root.<Group>get("availability");
			predicates.add(group.in(findStorageUnit.getAvailability()));
		}

		if(findStorageUnit.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findStorageUnit.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
