package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.report.StockMovementReport1;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StockMovementReportNewSpecification implements Specification<StockMovementReport1> {
	public StockMovementReportNewSpecification() {

	}
	 
	@Override
    public Predicate toPredicate(Root<StockMovementReport1> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

		         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
