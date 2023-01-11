package com.ustorage.api.master.repository.Specification;

import com.ustorage.api.master.model.documentstorage.*;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DocumentStorageSpecification implements Specification<DocumentStorage> {

	FindDocumentStorage findDocumentStorage;

	public DocumentStorageSpecification(FindDocumentStorage inputSearchParams) {
		this.findDocumentStorage = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<DocumentStorage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findDocumentStorage.getDocumentNumber() != null && !findDocumentStorage.getDocumentNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("documentNumber");
			predicates.add(group.in(findDocumentStorage.getDocumentNumber()));
		}

		if (findDocumentStorage.getCustomerName() != null && !findDocumentStorage.getCustomerName().isEmpty()) {
			final Path<Group> group = root.<Group>get("customerName");
			predicates.add(group.in(findDocumentStorage.getCustomerName()));
		}

		if (findDocumentStorage.getFileDescription() != null && !findDocumentStorage.getFileDescription().isEmpty()) {
			final Path<Group> group = root.<Group>get("fileDescription");
			predicates.add(group.in(findDocumentStorage.getFileDescription()));
		}

		if (findDocumentStorage.getUploadedBy() != null && !findDocumentStorage.getUploadedBy().isEmpty()) {
			final Path<Group> group = root.<Group>get("uploadedBy");
			predicates.add(group.in(findDocumentStorage.getUploadedBy()));
		}

		if (findDocumentStorage.getStartDate() != null && findDocumentStorage.getEndDate() !=null) {
			predicates.add(cb.between(root.get("uploadedOn"),
					findDocumentStorage.getStartDate(), findDocumentStorage.getEndDate()));
		}

		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
