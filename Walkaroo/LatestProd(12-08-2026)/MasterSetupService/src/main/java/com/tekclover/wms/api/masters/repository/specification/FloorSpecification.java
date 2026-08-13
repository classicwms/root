package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.floor.Floor;
import com.tekclover.wms.api.masters.model.floor.SearchFloor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FloorSpecification implements Specification<Floor> {
	
	SearchFloor searchFloor;
	
	public FloorSpecification(SearchFloor inputSearchParams) {
		this.searchFloor = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<Floor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchFloor.getCompanyId() != null && !searchFloor.getCompanyId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("companyId"), searchFloor.getCompanyId()));
         }
         
         if (searchFloor.getPlantId() != null && !searchFloor.getPlantId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("plantId"), searchFloor.getPlantId()));
         }
         
         if (searchFloor.getWarehouseId() != null && !searchFloor.getWarehouseId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("warehouseId"), searchFloor.getWarehouseId()));
         }
        if (searchFloor.getLanguageId() != null && !searchFloor.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchFloor.getLanguageId()));
        }
         
         if (searchFloor.getFloorId() != null && searchFloor.getFloorId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("floorId"), searchFloor.getFloorId()));
         }
         
         if (searchFloor.getCreatedBy() != null && !searchFloor.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchFloor.getCreatedBy()));
         }
         
         if (searchFloor.getStartCreatedOn() != null && searchFloor.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchFloor.getStartCreatedOn(), searchFloor.getEndCreatedOn()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
