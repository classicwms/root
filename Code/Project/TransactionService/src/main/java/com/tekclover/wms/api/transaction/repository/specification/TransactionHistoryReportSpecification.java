package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.report.TransactionHistoryReport;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TransactionHistoryReportSpecification implements Specification<TransactionHistoryReport> {


	public TransactionHistoryReportSpecification() {
	}

	@Override
	public Predicate toPredicate(Root<TransactionHistoryReport> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
