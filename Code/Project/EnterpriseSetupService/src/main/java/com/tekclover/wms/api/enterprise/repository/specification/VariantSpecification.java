package com.tekclover.wms.api.enterprise.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.enterprise.model.variant.SearchVariant;
import com.tekclover.wms.api.enterprise.model.variant.Variant;

@SuppressWarnings("serial")
public class VariantSpecification implements Specification<Variant> {
	
	SearchVariant searchVariant;
	
	public VariantSpecification(SearchVariant inputSearchParams) {
		this.searchVariant = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<Variant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchVariant.getWarehouseID() != null && !searchVariant.getWarehouseID().isEmpty()) {
        	 predicates.add(cb.equal(root.get("warehouseID"), searchVariant.getWarehouseID()));
         }
         
         if (searchVariant.getVariantId() != null && !searchVariant.getVariantId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("variantId"), searchVariant.getVariantId()));
         }
         
         if (searchVariant.getLevelId() != null && searchVariant.getLevelId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("levelId"), searchVariant.getLevelId()));
         }
         
         if (searchVariant.getCreatedBy() != null && !searchVariant.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchVariant.getCreatedBy()));
         }
         
         if (searchVariant.getStartCreatedOn() != null && searchVariant.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchVariant.getStartCreatedOn(), searchVariant.getEndCreatedOn()));
         }
         
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
