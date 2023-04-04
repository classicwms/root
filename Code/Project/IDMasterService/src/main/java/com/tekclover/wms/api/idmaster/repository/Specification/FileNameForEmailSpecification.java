package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.email.*;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FileNameForEmailSpecification implements Specification<FileNameForEmail> {

	FindFileNameForEmail findFileNameForEmail;

	public FileNameForEmailSpecification(FindFileNameForEmail inputSearchParams) {
		this.findFileNameForEmail = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<FileNameForEmail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (findFileNameForEmail.getReportDate() != null && !findFileNameForEmail.getReportDate().isEmpty()) {
			final Path<Group> group = root.<Group>get("reportDate");
			predicates.add(group.in(findFileNameForEmail.getReportDate()));
		}
		if (findFileNameForEmail.getFileNameId() != null) {
			final Path<Group> group = root.<Group>get("fileNameId");
			predicates.add(group.in(findFileNameForEmail.getFileNameId()));
		}
		predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
