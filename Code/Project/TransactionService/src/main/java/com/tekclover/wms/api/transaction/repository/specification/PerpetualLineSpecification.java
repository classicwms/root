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

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.SearchPerpetualLine;

@SuppressWarnings("serial")
public class PerpetualLineSpecification implements Specification<PerpetualLine> {
	
	SearchPerpetualLine searchPerpetualLine;
	
	public PerpetualLineSpecification(SearchPerpetualLine inputSearchParams) {
		this.searchPerpetualLine = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<PerpetualLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();
		
         if (searchPerpetualLine.getCycleCounterId() != null && !searchPerpetualLine.getCycleCounterId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("cycleCounterId");
        	 predicates.add(group.in(searchPerpetualLine.getCycleCounterId()));
         }
		   
		 if (searchPerpetualLine.getLineStatusId() != null && !searchPerpetualLine.getLineStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchPerpetualLine.getLineStatusId()));
         }
			
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
