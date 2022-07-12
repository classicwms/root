package com.tekclover.wms.api.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.SearchPeriodicLine;

@SuppressWarnings("serial")
public class PeriodicLineSpecification implements Specification<PeriodicLine> {
	
	SearchPeriodicLine searchPeriodicLine;
	
	public PeriodicLineSpecification(SearchPeriodicLine inputSearchParams) {
		this.searchPeriodicLine = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<PeriodicLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();
		
		 if (searchPeriodicLine.getCycleCounterId() != null && !searchPeriodicLine.getCycleCounterId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("cycleCounterId");
        	 predicates.add(group.in(searchPeriodicLine.getCycleCounterId()));
         }
		 
		 if (searchPeriodicLine.getLineStatusId() != null && !searchPeriodicLine.getLineStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchPeriodicLine.getLineStatusId()));
         }	
			
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
