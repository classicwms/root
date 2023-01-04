package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.paymentvoucher.*;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PaymentVoucherSpecification implements Specification<PaymentVoucher> {

	FindPaymentVoucher findPaymentVoucher;

	public PaymentVoucherSpecification(FindPaymentVoucher inputSearchParams) {
		this.findPaymentVoucher = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<PaymentVoucher> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findPaymentVoucher.getVoucherId() != null && !findPaymentVoucher.getVoucherStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("voucherStatus");
			predicates.add(group.in(findPaymentVoucher.getVoucherStatus()));
		}

		if (findPaymentVoucher.getCodeId() != null && !findPaymentVoucher.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findPaymentVoucher.getCodeId()));
		}

		if (findPaymentVoucher.getCustomerName() != null && !findPaymentVoucher.getCustomerName().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerName");
			predicates.add(group.in(findPaymentVoucher.getCustomerName()));
		}

		if (findPaymentVoucher.getServiceType() != null && !findPaymentVoucher.getServiceType().isEmpty()) {
			final Path<Group> group = root.<Group>get("serviceType");
			predicates.add(group.in(findPaymentVoucher.getServiceType()));
		}

		if (findPaymentVoucher.getStoreNumber() != null && !findPaymentVoucher.getStoreNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("storeNumber");
			predicates.add(group.in(findPaymentVoucher.getStoreNumber()));
		}

		if (findPaymentVoucher.getContractNumber() != null && !findPaymentVoucher.getContractNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("contractNumber");
			predicates.add(group.in(findPaymentVoucher.getContractNumber()));
		}

		if (findPaymentVoucher.getModeOfPayment() != null && !findPaymentVoucher.getModeOfPayment().isEmpty()) {
			final Path<Group> group = root.<Group>get("modeOfPayment");
			predicates.add(group.in(findPaymentVoucher.getModeOfPayment()));
		}

		if (findPaymentVoucher.getStatus() != null && !findPaymentVoucher.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findPaymentVoucher.getStatus()));
		}

		if (findPaymentVoucher.getVoucherStatus() != null && !findPaymentVoucher.getVoucherStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("voucherStatus");
			predicates.add(group.in(findPaymentVoucher.getVoucherStatus()));
		}

		if (findPaymentVoucher.getStartDate() != null && findPaymentVoucher.getEndDate() != null) {
			predicates.add(cb.between(root.get("createdOn"),
					findPaymentVoucher.getStartDate(), findPaymentVoucher.getEndDate()));
		}

		if(findPaymentVoucher.getIsActive() != null) {
			predicates.add(cb.equal(root.get("isActive"), findPaymentVoucher.getIsActive()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
