package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.variant.SearchVariant;
import com.tekclover.wms.api.masters.model.variant.Variant;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class VariantSpecification implements Specification<Variant> {
	
	SearchVariant searchVariant;
	
	public VariantSpecification(SearchVariant inputSearchParams) {
		this.searchVariant = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<Variant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchVariant.getWarehouseId() != null && !searchVariant.getWarehouseId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("warehouseId"), searchVariant.getWarehouseId()));
         }
        if (searchVariant.getCompanyId() != null && !searchVariant.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchVariant.getCompanyId()));
        }
        if (searchVariant.getPlantId() != null && !searchVariant.getPlantId().isEmpty()) {
            predicates.add(cb.equal(root.get("plantId"), searchVariant.getPlantId()));
        }
        if (searchVariant.getVariantSubId() != null && !searchVariant.getVariantSubId().isEmpty()) {
            predicates.add(cb.equal(root.get("variantSubId"), searchVariant.getVariantSubId()));
        }
         if (searchVariant.getVariantId() != null && !searchVariant.getVariantId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("variantId"), searchVariant.getVariantId()));
         }
        if (searchVariant.getLanguageId() != null && !searchVariant.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchVariant.getLanguageId()));
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
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
