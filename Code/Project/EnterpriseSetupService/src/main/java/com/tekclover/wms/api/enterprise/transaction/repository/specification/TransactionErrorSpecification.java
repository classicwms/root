package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.dto.TransactionError;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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