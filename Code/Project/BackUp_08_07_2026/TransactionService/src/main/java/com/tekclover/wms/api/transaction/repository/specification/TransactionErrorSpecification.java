package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.dto.TransactionError;
import com.tekclover.wms.api.transaction.model.report.SearchTransactionError;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TransactionErrorSpecification implements Specification<TransactionError> {



	public TransactionErrorSpecification() {

	}
	 
	@Override
    public Predicate toPredicate(Root<TransactionError> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();
		         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
