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

import com.tekclover.wms.api.transaction.model.inbound.gr.GrLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.SearchGrLine;

@SuppressWarnings("serial")
public class GrLineSpecification implements Specification<GrLine> {
	
	SearchGrLine searchGrLine;
	
	public GrLineSpecification(SearchGrLine inputSearchParams) {
		this.searchGrLine = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<GrLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchGrLine.getPreInboundNo() != null && !searchGrLine.getPreInboundNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("preInboundNo");
        	 predicates.add(group.in(searchGrLine.getPreInboundNo()));
         }
		 
		 if (searchGrLine.getRefDocNumber() != null && !searchGrLine.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchGrLine.getRefDocNumber()));
         }
		 
		 if (searchGrLine.getPackBarcodes() != null && !searchGrLine.getPackBarcodes().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("packBarcodes");
        	 predicates.add(group.in(searchGrLine.getPackBarcodes()));
         }
		 
		 if (searchGrLine.getLineNo() != null && !searchGrLine.getLineNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("lineNo");
        	 predicates.add(group.in(searchGrLine.getLineNo()));
         }
		 
		 if (searchGrLine.getItemCode() != null && !searchGrLine.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchGrLine.getItemCode()));
         }
		 
		 if (searchGrLine.getCaseCode() != null && !searchGrLine.getCaseCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("caseCode");
        	 predicates.add(group.in(searchGrLine.getCaseCode()));
         }
		 
		 if (searchGrLine.getStatusId() != null && !searchGrLine.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchGrLine.getStatusId()));
         }	
		 
		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));

         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
