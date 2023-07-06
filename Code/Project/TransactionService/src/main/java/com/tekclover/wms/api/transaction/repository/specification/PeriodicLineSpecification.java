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
         
     	if (searchPeriodicLine.getPlantId() != null && !searchPeriodicLine.getPlantId().isEmpty()) {
     		predicates.add(cb.equal(root.get("plantId"), searchPeriodicLine.getPlantId()));
     	}
		
     	if (searchPeriodicLine.getWarehouseId() != null && !searchPeriodicLine.getWarehouseId().isEmpty()) {
	       	 final Path<Group> group = root.<Group> get("warehouseId");
	       	 predicates.add(group.in(searchPeriodicLine.getWarehouseId()));
        }
     	
		if (searchPeriodicLine.getCycleCounterId() != null && !searchPeriodicLine.getCycleCounterId().isEmpty()) {
			final Path<Group> group = root.<Group> get("cycleCounterId");
			predicates.add(group.in(searchPeriodicLine.getCycleCounterId()));
		}
		 
		if (searchPeriodicLine.getLineStatusId() != null && !searchPeriodicLine.getLineStatusId().isEmpty()) {	
			final Path<Group> group = root.<Group> get("statusId");
        	predicates.add(group.in(searchPeriodicLine.getLineStatusId()));
        }
		
		if (searchPeriodicLine.getCycleCountNo() != null && !searchPeriodicLine.getCycleCountNo().isEmpty()) {
			final Path<Group> group = root.<Group> get("cycleCountNo");
        	predicates.add(group.in(searchPeriodicLine.getCycleCountNo()));
        }
			
		if (searchPeriodicLine.getStartCreatedOn() != null && searchPeriodicLine.getEndCreatedOn() != null) {
			predicates.add(cb.between(root.get("createdOn"), searchPeriodicLine.getStartCreatedOn(), 
					searchPeriodicLine.getEndCreatedOn()));
        }
		
        return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
