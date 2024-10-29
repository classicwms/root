package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.SearchOutboundLineReport;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OutboundLineReportSpecification implements Specification<OutboundLine> {
	
	SearchOutboundLineReport searchOutboundLineReport;
	
	public OutboundLineReportSpecification(SearchOutboundLineReport inputSearchParams) {
		this.searchOutboundLineReport = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<OutboundLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
         List<Predicate> predicates = new ArrayList<Predicate>();
         
         if (searchOutboundLineReport.getWarehouseId() != null) {
        	 predicates.add(cb.equal(root.get("warehouseId"), searchOutboundLineReport.getWarehouseId()));
         }

		 if (searchOutboundLineReport.getStartConfirmedOn() != null && searchOutboundLineReport.getEndConfirmedOn() != null) {
             predicates.add(cb.between(root.get("deliveryConfirmedOn"), 
            		 searchOutboundLineReport.getStartConfirmedOn(), searchOutboundLineReport.getEndConfirmedOn()));
         }
		 
		 if (searchOutboundLineReport.getPartnerCode() != null) {
        	 predicates.add(cb.equal(root.get("partnerCode"), searchOutboundLineReport.getPartnerCode()));
         }
		 
		 // SO Type - Ref_Field_1
		 if (searchOutboundLineReport.getSoTypeRefField1() != null && !searchOutboundLineReport.getSoTypeRefField1().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField1");
        	 predicates.add(group.in(searchOutboundLineReport.getSoTypeRefField1()));
         }	
		 
		 if (searchOutboundLineReport.getRefDocNumber() != null) {
        	 predicates.add(cb.equal(root.get("refDocNumber"), searchOutboundLineReport.getRefDocNumber()));
         }
		 
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}