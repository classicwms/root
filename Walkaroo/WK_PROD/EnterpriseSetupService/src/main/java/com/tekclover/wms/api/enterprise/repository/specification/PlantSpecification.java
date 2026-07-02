package com.tekclover.wms.api.enterprise.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.enterprise.model.plant.Plant;
import com.tekclover.wms.api.enterprise.model.plant.SearchPlant;

@SuppressWarnings("serial")
public class PlantSpecification implements Specification<Plant> {
	
	SearchPlant searchPlant;
	
	public PlantSpecification(SearchPlant inputSearchParams) {
		this.searchPlant = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<Plant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchPlant.getCompanyId() != null && !searchPlant.getCompanyId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("companyId"), searchPlant.getCompanyId()));
         }
         
         if (searchPlant.getPlantId() != null && !searchPlant.getPlantId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("plantId"), searchPlant.getPlantId()));
         }
         
		 if (searchPlant.getContactName() != null && !searchPlant.getContactName().isEmpty()) {
        	 predicates.add(cb.equal(root.get("contactName"), searchPlant.getContactName()));
         }
        if (searchPlant.getLanguageId() != null && !searchPlant.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchPlant.getLanguageId()));
        }
		 if (searchPlant.getCity() != null && !searchPlant.getCity().isEmpty()) {
        	 predicates.add(cb.equal(root.get("city"), searchPlant.getCity()));
         }
		 
		 if (searchPlant.getState() != null && !searchPlant.getState().isEmpty()) {
        	 predicates.add(cb.equal(root.get("state"), searchPlant.getState()));
         }
		 
         if (searchPlant.getCountry() != null && !searchPlant.getCountry().isEmpty()) {
        	 predicates.add(cb.equal(root.get("country"), searchPlant.getCountry()));
         }         
             
         if (searchPlant.getCreatedBy() != null && !searchPlant.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchPlant.getCreatedBy()));
         }
         
         if (searchPlant.getStartCreatedOn() != null && searchPlant.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchPlant.getStartCreatedOn(), searchPlant.getEndCreatedOn()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
