package com.tekclover.wms.api.outbound.transaction.repository.specification;

import com.tekclover.wms.api.outbound.transaction.model.outbound.SearchOutboundLineReportV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.OutboundLineV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OutboundLineReportV2Specification implements Specification<OutboundLineV2> {

	SearchOutboundLineReportV2 searchOutboundLineReport;

	public OutboundLineReportV2Specification(SearchOutboundLineReportV2 inputSearchParams) {
		this.searchOutboundLineReport = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<OutboundLineV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
		 if (searchOutboundLineReport.getCompanyCodeId() != null && !searchOutboundLineReport.getCompanyCodeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("companyCodeId");
        	 predicates.add(group.in(searchOutboundLineReport.getCompanyCodeId()));
         }
		 if (searchOutboundLineReport.getPlantId() != null && !searchOutboundLineReport.getPlantId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("plantId");
        	 predicates.add(group.in(searchOutboundLineReport.getPlantId()));
         }
		 if (searchOutboundLineReport.getWarehouseId() != null && !searchOutboundLineReport.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchOutboundLineReport.getWarehouseId()));
         }

		 if (searchOutboundLineReport.getRefDocNumber() != null) {
        	 predicates.add(cb.equal(root.get("refDocNumber"), searchOutboundLineReport.getRefDocNumber()));
         }
		 if (searchOutboundLineReport.getPreOutboundNo() != null) {
        	 predicates.add(cb.equal(root.get("preOutboundNo"), searchOutboundLineReport.getPreOutboundNo()));
         }
		 
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
