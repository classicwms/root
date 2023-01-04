package com.ustorage.api.trans.repository.Specification;

import com.ustorage.api.trans.model.invoice.Invoice;
import com.ustorage.api.trans.model.invoice.FindInvoice;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InvoiceSpecification implements Specification<Invoice> {

	FindInvoice findInvoice;

	public InvoiceSpecification(FindInvoice inputSearchParams) {
		this.findInvoice = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findInvoice.getInvoiceNumber() != null && !findInvoice.getInvoiceNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("invoiceNumber");
			predicates.add(group.in(findInvoice.getInvoiceNumber()));
		}

		if (findInvoice.getCodeId() != null && !findInvoice.getCodeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("codeId");
			predicates.add(group.in(findInvoice.getCodeId()));
		}

		if (findInvoice.getCustomerId() != null && !findInvoice.getCustomerId().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerId");
			predicates.add(group.in(findInvoice.getCustomerId()));
		}

		if (findInvoice.getDocumentNumber() != null && !findInvoice.getDocumentNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("documentNumber");
			predicates.add(group.in(findInvoice.getDocumentNumber()));
		}

		if (findInvoice.getStatus() != null && !findInvoice.getStatus().isEmpty()) {
			final Path<Group> group = root.<Group>get("status");
			predicates.add(group.in(findInvoice.getStatus()));
		}

		if (findInvoice.getDocumentStartDate() != null && findInvoice.getDocumentEndDate() !=null) {
			predicates.add(cb.between(root.get("invoiceDate"),
					findInvoice.getDocumentStartDate(), findInvoice.getDocumentEndDate()));
		}


		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
