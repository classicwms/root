package com.tekclover.wms.api.masters.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.SearchBusinessPartner;

@SuppressWarnings("serial")
public class BusinessPartnerSpecification implements Specification<BusinessPartner> {
	
	SearchBusinessPartner searchBusinessPartner;
	
	public BusinessPartnerSpecification(SearchBusinessPartner inputSearchParams) {
		this.searchBusinessPartner = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<BusinessPartner> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchBusinessPartner.getWarehouseId() != null && !searchBusinessPartner.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchBusinessPartner.getWarehouseId()));
         }
         
         if (searchBusinessPartner.getBusinessPartnerType() != null && !searchBusinessPartner.getBusinessPartnerType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("businessPartnerType");
        	 predicates.add(group.in(searchBusinessPartner.getBusinessPartnerType()));
         }
         
         if (searchBusinessPartner.getPartnerCode() != null && !searchBusinessPartner.getPartnerCode().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("partnerCode");
        	 predicates.add(group.in(searchBusinessPartner.getPartnerCode()));
         }	
		 
		 if (searchBusinessPartner.getPartnerName() != null && !searchBusinessPartner.getPartnerName().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("partnerName");
        	 predicates.add(group.in(searchBusinessPartner.getPartnerName()));
         }	
		 
		 if (searchBusinessPartner.getStatusId() != null && !searchBusinessPartner.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchBusinessPartner.getStatusId()));
         }	
		 
         if (searchBusinessPartner.getStartCreatedOn() != null && searchBusinessPartner.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdon"), searchBusinessPartner.getStartCreatedOn(), searchBusinessPartner.getEndCreatedOn()));
         }
         
         if (searchBusinessPartner.getCreatedBy() != null && !searchBusinessPartner.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchBusinessPartner.getCreatedBy()));
         }
		 
		 if (searchBusinessPartner.getStartUpdatedOn() != null && searchBusinessPartner.getEndUpdatedOn() != null) {
             predicates.add(cb.between(root.get("updatedOn"), searchBusinessPartner.getStartUpdatedOn(), searchBusinessPartner.getEndUpdatedOn()));
         }
         
         if (searchBusinessPartner.getUpdatedBy() != null && !searchBusinessPartner.getUpdatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("updatedBy");
        	 predicates.add(group.in(searchBusinessPartner.getUpdatedBy()));
         }
                 
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
