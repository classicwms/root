package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.workorder.*;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class WorkOrderSpecification implements Specification<WorkOrder> {

	FindWorkOrder findWorkOrder;

	public WorkOrderSpecification(FindWorkOrder inputSearchParams) {
		this.findWorkOrder = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<WorkOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findWorkOrder.getWorkOrderId() != null && !findWorkOrder.getWorkOrderId().isEmpty()) {
			final Path<Group> group = root.<Group>get("workOrderId");
			predicates.add(group.in(findWorkOrder.getWorkOrderId()));
		}

		if (findWorkOrder.getCodeId() != null && !findWorkOrder.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findWorkOrder.getCodeId()));
		}

		if (findWorkOrder.getCustomerId() != null && !findWorkOrder.getCustomerId().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerId");
			predicates.add(group.in(findWorkOrder.getCustomerId()));
		}

		if (findWorkOrder.getJobCard() != null && !findWorkOrder.getJobCard().isEmpty()) {
			final Path<Group> group = root.<Group>get("jobCard");
			predicates.add(group.in(findWorkOrder.getJobCard()));
		}

		if (findWorkOrder.getCreated() != null && !findWorkOrder.getCreated().isEmpty()) {
			final Path<Group> group = root.<Group>get("created");
			predicates.add(group.in(findWorkOrder.getCreated()));
		}

		if (findWorkOrder.getJobCardType() != null && !findWorkOrder.getJobCardType().isEmpty()) {
			final Path<Group> group = root.<Group>get("jobCardType");
			predicates.add(group.in(findWorkOrder.getJobCardType()));
		}

		if (findWorkOrder.getWorkOrderNumber() != null && !findWorkOrder.getWorkOrderNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("workOrderNumber");
			predicates.add(group.in(findWorkOrder.getWorkOrderNumber()));
		}

		if (findWorkOrder.getWorkOrderSbu() != null && !findWorkOrder.getWorkOrderSbu().isEmpty()) {
			final Path<Group> group = root.<Group>get("workOrderSbu");
			predicates.add(group.in(findWorkOrder.getWorkOrderSbu()));
		}

		if (findWorkOrder.getStatus() != null && !findWorkOrder.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findWorkOrder.getStatus()));
		}

		if (findWorkOrder.getStartDate() != null && findWorkOrder.getEndDate() !=null) {
			predicates.add(cb.between(root.get("workOrderDate"),
					findWorkOrder.getStartDate(), findWorkOrder.getEndDate()));
		}

		if(findWorkOrder.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findWorkOrder.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
