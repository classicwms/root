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

import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityHeader;

@SuppressWarnings("serial")
public class QualityHeaderSpecification implements Specification<QualityHeader> {
	
	SearchQualityHeader searchQualityHeader;
	
	public QualityHeaderSpecification(SearchQualityHeader inputSearchParams) {
		this.searchQualityHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<QualityHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();
		
		 if (searchQualityHeader.getRefDocNumber() != null && !searchQualityHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchQualityHeader.getRefDocNumber()));
         }
		 
		 if (searchQualityHeader.getPartnerCode() != null && !searchQualityHeader.getPartnerCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("partnerCode");
        	 predicates.add(group.in(searchQualityHeader.getPartnerCode()));
         }
		 
         if (searchQualityHeader.getQualityInspectionNo() != null && !searchQualityHeader.getQualityInspectionNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("qualityInspectionNo");
        	 predicates.add(group.in(searchQualityHeader.getQualityInspectionNo()));
         }
		   
		 if (searchQualityHeader.getActualHeNo() != null && !searchQualityHeader.getActualHeNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("actualHeNo");
        	 predicates.add(group.in(searchQualityHeader.getActualHeNo()));
         }
         
		 if (searchQualityHeader.getOutboundOrderTypeId() != null && !searchQualityHeader.getOutboundOrderTypeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("outboundOrderTypeId");
        	 predicates.add(group.in(searchQualityHeader.getOutboundOrderTypeId()));
         }
		 
		 if (searchQualityHeader.getStatusId() != null && !searchQualityHeader.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchQualityHeader.getStatusId()));
         }	
		 
		 if (searchQualityHeader.getSoType() != null && !searchQualityHeader.getSoType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField1");
        	 predicates.add(group.in(searchQualityHeader.getSoType()));
         }   		 
	
		 if (searchQualityHeader.getStartQualityCreatedOn() != null && searchQualityHeader.getEndQualityCreatedOn() != null) {
        	 predicates.add(cb.between(root.get("qualityCreatedOn"), searchQualityHeader.getStartQualityCreatedOn(), searchQualityHeader.getEndQualityCreatedOn()));
         }


		if (searchQualityHeader.getWarehouseId() != null && !searchQualityHeader.getWarehouseId().isEmpty()) {
			final Path<Group> group = root.<Group> get("warehouseId");
			predicates.add(group.in(searchQualityHeader.getWarehouseId()));
		}

		predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
			
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
