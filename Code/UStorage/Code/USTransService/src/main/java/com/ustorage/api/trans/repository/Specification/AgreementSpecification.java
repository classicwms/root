package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.agreement.*;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AgreementSpecification implements Specification<Agreement> {

	FindAgreement findAgreement;

	public AgreementSpecification(FindAgreement inputSearchParams) {
		this.findAgreement = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<Agreement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findAgreement.getAgreementNumber() != null && !findAgreement.getAgreementNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("agreementNumber");
			predicates.add(group.in(findAgreement.getAgreementNumber()));
		}

		if (findAgreement.getQuoteNumber() != null && !findAgreement.getQuoteNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("quoteNumber");
			predicates.add(group.in(findAgreement.getQuoteNumber()));
		}

		if (findAgreement.getCustomerName() != null && !findAgreement.getCustomerName().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerName");
			predicates.add(group.in(findAgreement.getCustomerName()));
		}

		if (findAgreement.getNationality() != null && !findAgreement.getNationality().isEmpty()) {
			final Path<Group> group = root.<Group>get("nationality");
			predicates.add(group.in(findAgreement.getNationality()));
		}

		if (findAgreement.getEmail() != null && !findAgreement.getEmail().isEmpty()) {
			final Path<Group> group = root.<Group>get("email");
			predicates.add(group.in(findAgreement.getEmail()));
		}

		if (findAgreement.getAgreementType() != null && !findAgreement.getAgreementType().isEmpty()) {
			final Path<Group> group = root.<Group>get("agreementType");
			predicates.add(group.in(findAgreement.getAgreementType()));
		}

		if (findAgreement.getDeposit() != null && !findAgreement.getDeposit().isEmpty()) {
			final Path<Group> group = root.<Group>get("deposit");
			predicates.add(group.in(findAgreement.getDeposit()));
		}

		if (findAgreement.getStatus() != null && !findAgreement.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findAgreement.getStatus()));
		}

		if (findAgreement.getCodeId() != null && !findAgreement.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findAgreement.getCodeId()));
		}

		if (findAgreement.getCodeId() != null && !findAgreement.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findAgreement.getCodeId()));
		}

		if (findAgreement.getStartDate() != null && findAgreement.getEndDate() != null) {
			predicates.add(cb.between(root.get("createdOn"),
					findAgreement.getStartDate(), findAgreement.getEndDate()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));


	}
}
