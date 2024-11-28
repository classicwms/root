package com.tekclover.wms.api.enterprise.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.enterprise.model.itemtype.ItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.SearchItemType;

@SuppressWarnings("serial")
public class ItemTypeSpecification implements Specification<ItemType> {
	
	SearchItemType searchItemType;
	
	public ItemTypeSpecification(SearchItemType inputSearchParams) {
		this.searchItemType = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<ItemType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchItemType.getWarehouseId() != null && !searchItemType.getWarehouseId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("warehouseId"), searchItemType.getWarehouseId()));
         }
        if (searchItemType.getCompanyId() != null && !searchItemType.getCompanyId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyId"), searchItemType.getCompanyId()));
        }
        if (searchItemType.getLanguageId() != null && !searchItemType.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchItemType.getLanguageId()));
        }

         if (searchItemType.getItemTypeId() != null && searchItemType.getItemTypeId().longValue() != 0) {
        	 predicates.add(cb.equal(root.get("itemTypeId"), searchItemType.getItemTypeId()));
         }
         
         if (searchItemType.getDescription() != null && !searchItemType.getDescription().isEmpty()) {
        	 predicates.add(cb.equal(root.get("description"), searchItemType.getDescription()));
         }
        
         if (searchItemType.getCreatedBy() != null && !searchItemType.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchItemType.getCreatedBy()));
         }
         
         if (searchItemType.getStartCreatedOn() != null && searchItemType.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchItemType.getStartCreatedOn(), searchItemType.getEndCreatedOn()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
