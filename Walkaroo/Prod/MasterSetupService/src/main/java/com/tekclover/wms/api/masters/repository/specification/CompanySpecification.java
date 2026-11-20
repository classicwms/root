package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.company.Company;
import com.tekclover.wms.api.masters.model.company.SearchCompany;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CompanySpecification implements Specification<Company> {
	
	SearchCompany searchCompany;
	
	public CompanySpecification(SearchCompany inputSearchParams) {
		this.searchCompany = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<Company> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchCompany.getCompanyId() != null && !searchCompany.getCompanyId().isEmpty()) {
        	 predicates.add(cb.equal(root.get("companyId"), searchCompany.getCompanyId()));
         }
         
         if (searchCompany.getVerticalId() != null && searchCompany.getVerticalId().longValue()!=0) {
        	 predicates.add(cb.equal(root.get("verticalId"), searchCompany.getVerticalId()));
         }

        if (searchCompany.getLanguageId() != null && !searchCompany.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchCompany.getLanguageId()));
        }

        if (searchCompany.getCountry() != null && !searchCompany.getCountry().isEmpty()) {
        	 predicates.add(cb.equal(root.get("country"), searchCompany.getCountry()));
         }
         
         if (searchCompany.getContactName() != null && !searchCompany.getContactName().isEmpty()) {
        	 predicates.add(cb.equal(root.get("contactName"), searchCompany.getContactName()));
         }
         
         if (searchCompany.getCreatedBy() != null && !searchCompany.getCreatedBy().isEmpty()) {
        	 predicates.add(cb.equal(root.get("createdBy"), searchCompany.getCreatedBy()));
         }
         
         if (searchCompany.getStartCreatedOn() != null && searchCompany.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchCompany.getStartCreatedOn(), searchCompany.getEndCreatedOn()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
