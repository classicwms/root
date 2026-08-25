package com.tekclover.wms.api.inbound.orders.repository.specification;

import com.tekclover.wms.api.inbound.orders.model.report.StockReportOutput;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StockReportOutputSpecification implements Specification<StockReportOutput> {


	public StockReportOutputSpecification() {
	}

	@Override
	public Predicate toPredicate(Root<StockReportOutput> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
