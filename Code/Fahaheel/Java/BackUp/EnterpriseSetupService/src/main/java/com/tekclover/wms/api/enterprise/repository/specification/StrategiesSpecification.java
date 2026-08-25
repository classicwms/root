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
        if (searchStrategies.getLanguageId() != null && !searchStrategies.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStrategies.getLanguageId()));
        }
        if (searchStrategies.getCompanyId() != null && !searchStrategies.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchStrategies.getCompanyId()));
        }
         if (searchStrategies.getStrategyTypeId() != null && searchStrategies.getStrategyTypeId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("strategyTypeId"), searchStrategies.getStrategyTypeId()));
         }

        if (searchStrategies.getLanguageId() != null && searchStrategies.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchStrategies.getLanguageId()));
        }
         
         if (searchStrategies.getSequenceIndicator() != null && searchStrategies.getSequenceIndicator().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("sequenceIndicator"), searchStrategies.getSequenceIndicator()));
         }
         
		 if (searchStrategies.getPriority1() != null && searchStrategies.getPriority1().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("priority1"), searchStrategies.getPriority1()));
         }

        if (searchStrategies.getPriority2() != null && searchStrategies.getPriority2().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority2"), searchStrategies.getPriority2()));
        }

        if (searchStrategies.getPriority3() != null && searchStrategies.getPriority3().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority3"), searchStrategies.getPriority3()));
        }

        if (searchStrategies.getPriority4() != null && searchStrategies.getPriority4().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority4"), searchStrategies.getPriority4()));
        }

        if (searchStrategies.getPriority5() != null && searchStrategies.getPriority5().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority5"), searchStrategies.getPriority5()));
        }

        if (searchStrategies.getPriority6() != null && searchStrategies.getPriority6().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority6"), searchStrategies.getPriority6()));
        }

        if (searchStrategies.getPriority7() != null && searchStrategies.getPriority7().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority7"), searchStrategies.getPriority7()));
        }

        if (searchStrategies.getPriority8() != null && searchStrategies.getPriority8().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority8"), searchStrategies.getPriority8()));
        }

        if (searchStrategies.getPriority9() != null && searchStrategies.getPriority9().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority9"), searchStrategies.getPriority9()));
        }

        if (searchStrategies.getPriority10() != null && searchStrategies.getPriority10().longValue() != 0) {
            predicates.add(cb.equal(root.get("priority10"), searchStrategies.getPriority10()));
        }

         if (searchStrategies.getCreatedBy() != null && !searchStrategies.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchStrategies.getCreatedBy()));
         }
         
         if (searchStrategies.getStartCreatedOn() != null && searchStrategies.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchStrategies.getStartCreatedOn(), searchStrategies.getEndCreatedOn()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
