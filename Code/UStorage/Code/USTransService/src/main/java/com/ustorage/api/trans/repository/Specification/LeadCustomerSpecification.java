package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.leadcustomer.FindLeadCustomer;
import com.ustorage.api.trans.model.leadcustomer.LeadCustomer;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class LeadCustomerSpecification implements Specification<LeadCustomer> {

	FindLeadCustomer findLeadCustomer;

	public LeadCustomerSpecification(FindLeadCustomer inputSearchParams) {
		this.findLeadCustomer = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<LeadCustomer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findLeadCustomer.getCustomerCode() != null && !findLeadCustomer.getCustomerCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerCode");
			predicates.add(group.in(findLeadCustomer.getCustomerCode()));
		}

		if (findLeadCustomer.getCodeId() != null && !findLeadCustomer.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findLeadCustomer.getCodeId()));
		}

		if (findLeadCustomer.getCivilId() != null && !findLeadCustomer.getCivilId().isEmpty()) {
			final Path<Group> group = root.<Group>get("civilId");
			predicates.add(group.in(findLeadCustomer.getCivilId()));
		}


		if (findLeadCustomer.getCustomerName() != null && !findLeadCustomer.getCustomerName().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerName");
			predicates.add(group.in(findLeadCustomer.getCustomerName()));
		}

		if (findLeadCustomer.getType() != null && !findLeadCustomer.getType().isEmpty()) {
			final Path<Group> group = root.<Group>get("type");
			predicates.add(group.in(findLeadCustomer.getType()));
		}

		if (findLeadCustomer.getStatus() != null && !findLeadCustomer.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findLeadCustomer.getStatus()));
		}

		if (findLeadCustomer.getMobileNumber() != null && !findLeadCustomer.getMobileNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("mobileNumber");
			predicates.add(group.in(findLeadCustomer.getMobileNumber()));
		}

		if (findLeadCustomer.getPhoneNumber() != null && !findLeadCustomer.getPhoneNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("phoneNumber");
			predicates.add(group.in(findLeadCustomer.getPhoneNumber()));
		}

		if(findLeadCustomer.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findLeadCustomer.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
