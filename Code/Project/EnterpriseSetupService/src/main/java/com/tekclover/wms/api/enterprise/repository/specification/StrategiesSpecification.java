package com.tekclover.wms.api.enterprise.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.enterprise.model.strategies.SearchStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.Strategies;

@SuppressWarnings("serial")
public class StrategiesSpecification implements Specification<Strategies> {
	
	SearchStrategies searchStrategies;
	
	public StrategiesSpecification(SearchStrategies inputSearchParams) {
		this.searchStrategies = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<Strategies> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchStrategies.getWarehouseId() != null && !searchStrategies.getWarehouseId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("warehouseId"), searchStrategies.getWarehouseId()));
         }
         
         if (searchStrategies.getStrategyTypeId() != null && searchStrategies.getStrategyTypeId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("strategyTypeId"), searchStrategies.getStrategyTypeId()));
         }
         
         if (searchStrategies.getSequenceIndicator() != null && searchStrategies.getSequenceIndicator().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("sequenceIndicator"), searchStrategies.getSequenceIndicator()));
         }
         
         if (searchStrategies.getStrategyNo() != null && !searchStrategies.getStrategyNo().isEmpty()) {
        	 predicates.add(cb.equal(root.get("strategyNo"), searchStrategies.getStrategyNo()));
         }
         
		 if (searchStrategies.getPriority() != null && searchStrategies.getPriority().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("priority"), searchStrategies.getPriority()));
         }
		 
         if (searchStrategies.getCreatedBy() != null && !searchStrategies.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchStrategies.getCreatedBy()));
         }
         
         if (searchStrategies.getStartCreatedOn() != null && searchStrategies.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchStrategies.getStartCreatedOn(), searchStrategies.getEndCreatedOn()));
         }
         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
