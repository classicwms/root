package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.quote.Quote;
import com.ustorage.api.trans.model.quote.FindQuote;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class QuoteSpecification implements Specification<Quote> {

	FindQuote findQuote;

	public QuoteSpecification(FindQuote inputSearchParams) {
		this.findQuote = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<Quote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findQuote.getQuoteId() != null && !findQuote.getQuoteId().isEmpty()) {
			final Path<Group> group = root.<Group>get("quoteId");
			predicates.add(group.in(findQuote.getQuoteId()));
		}

		if (findQuote.getCodeId() != null && !findQuote.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findQuote.getCodeId()));
		}

		if (findQuote.getEnquiryReferenceNumber() != null && !findQuote.getEnquiryReferenceNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("enquiryReferenceNumber");
			predicates.add(group.in(findQuote.getEnquiryReferenceNumber()));
		}

		if (findQuote.getCustomerName() != null && !findQuote.getCustomerName().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerName");
			predicates.add(group.in(findQuote.getCustomerName()));
		}

		if (findQuote.getMobileNumber() != null && !findQuote.getMobileNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("mobileNumber");
			predicates.add(group.in(findQuote.getMobileNumber()));
		}

		if (findQuote.getEmail() != null && !findQuote.getEmail().isEmpty()) {
			final Path<Group> group = root.<Group>get("email");
			predicates.add(group.in(findQuote.getEmail()));
		}

		if (findQuote.getStatus() != null && !findQuote.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findQuote.getStatus()));
		}

		if (findQuote.getCustomerCode() != null && !findQuote.getCustomerCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerCode");
			predicates.add(group.in(findQuote.getCustomerCode()));
		}

		if (findQuote.getCustomerGroup() != null && !findQuote.getCustomerGroup().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerGroup");
			predicates.add(group.in(findQuote.getCustomerGroup()));
		}

		if(findQuote.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findQuote.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
