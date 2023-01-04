package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.enquiry.Enquiry;
import com.ustorage.api.trans.model.enquiry.FindEnquiry;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class EnquirySpecification implements Specification<Enquiry> {

	FindEnquiry findEnquiry;

	public EnquirySpecification(FindEnquiry inputSearchParams) {
		this.findEnquiry = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<Enquiry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findEnquiry.getEnquiryId() != null && !findEnquiry.getEnquiryId().isEmpty()) {
			final Path<Group> group = root.<Group>get("enquiryId");
			predicates.add(group.in(findEnquiry.getEnquiryId()));
		}

		if (findEnquiry.getCodeId() != null && !findEnquiry.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findEnquiry.getCodeId()));
		}

		if (findEnquiry.getEnquiryName() != null && !findEnquiry.getEnquiryName().isEmpty()) {
			final Path<Group> group = root.<Group>get("enquiryName");
			predicates.add(group.in(findEnquiry.getEnquiryName()));
		}

		if (findEnquiry.getEnquiryMobileNumber() != null && !findEnquiry.getEnquiryMobileNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("enquiryMobileNumber");
			predicates.add(group.in(findEnquiry.getEnquiryMobileNumber()));
		}

		if (findEnquiry.getEnquiryStatus() != null && !findEnquiry.getEnquiryStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("enquiryStatus");
			predicates.add(group.in(findEnquiry.getEnquiryStatus()));
		}

		if (findEnquiry.getRentType() != null && !findEnquiry.getRentType().isEmpty()) {
			final Path<Group> group = root.<Group>get("rentType");
			predicates.add(group.in(findEnquiry.getRentType()));
		}

		if (findEnquiry.getEmail() != null && !findEnquiry.getEmail().isEmpty()) {
			final Path<Group> group = root.<Group>get("email");
			predicates.add(group.in(findEnquiry.getEmail()));
		}

		if (findEnquiry.getCustomerGroup() != null && !findEnquiry.getCustomerGroup().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerGroup");
			predicates.add(group.in(findEnquiry.getCustomerGroup()));
		}

		if (findEnquiry.getSbu() != null && !findEnquiry.getSbu().isEmpty()) {
			final Path<Group> group = root.<Group>get("sbu");
			predicates.add(group.in(findEnquiry.getSbu()));
		}

		if (findEnquiry.getRequirementDetail() != null && !findEnquiry.getRequirementDetail().isEmpty()) {
			final Path<Group> group = root.<Group>get("requirementDetail");
			predicates.add(group.in(findEnquiry.getRequirementDetail()));
		}

		if(findEnquiry.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findEnquiry.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
