package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.inbound.inboundquality.InboundQualityLine;
import com.tekclover.wms.api.transaction.model.inbound.inboundquality.SearchInboundQualityLine;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InboundQualityLineSpecification implements Specification<InboundQualityLine> {

	SearchInboundQualityLine searchInboundQualityLine;

	public InboundQualityLineSpecification(SearchInboundQualityLine inputSearchParams) {
		this.searchInboundQualityLine = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<InboundQualityLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<>();

         if (searchInboundQualityLine.getCompanyCodeId() != null && !searchInboundQualityLine.getCompanyCodeId().isEmpty()) {
        	 final Path<Group> group = root.get("companyCodeId");
        	 predicates.add(group.in(searchInboundQualityLine.getCompanyCodeId()));
         }

		 if (searchInboundQualityLine.getPlantId() != null && !searchInboundQualityLine.getPlantId().isEmpty()) {
        	 final Path<Group> group = root.get("plantId");
        	 predicates.add(group.in(searchInboundQualityLine.getPlantId()));
         }

		 if (searchInboundQualityLine.getLanguageId() != null && !searchInboundQualityLine.getLanguageId().isEmpty()) {
        	 final Path<Group> group = root.get("languageId");
        	 predicates.add(group.in(searchInboundQualityLine.getLanguageId()));
         }

		 if (searchInboundQualityLine.getWarehouseId() != null && !searchInboundQualityLine.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.get("warehouseId");
        	 predicates.add(group.in(searchInboundQualityLine.getWarehouseId()));
         }
         
         if (searchInboundQualityLine.getRefDocNumber() != null && !searchInboundQualityLine.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.get("refDocNumber");
        	 predicates.add(group.in(searchInboundQualityLine.getRefDocNumber()));
         }

		 if (searchInboundQualityLine.getPreInboundNo() != null && !searchInboundQualityLine.getPreInboundNo().isEmpty()) {
        	 final Path<Group> group = root.get("preInboundNo");
        	 predicates.add(group.in(searchInboundQualityLine.getPreInboundNo()));
         }
         
		 if (searchInboundQualityLine.getInboundQualityNumber() != null && !searchInboundQualityLine.getInboundQualityNumber().isEmpty()) {
        	 final Path<Group> group = root.get("inboundQualityNumber");
        	 predicates.add(group.in(searchInboundQualityLine.getInboundQualityNumber()));
         }

		 if (searchInboundQualityLine.getItemCode() != null && !searchInboundQualityLine.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.get("itemCode");
        	 predicates.add(group.in(searchInboundQualityLine.getItemCode()));
         }

		 if (searchInboundQualityLine.getBatchSerialNumber() != null && !searchInboundQualityLine.getBatchSerialNumber().isEmpty()) {
        	 final Path<Group> group = root.get("batchSerialNumber");
        	 predicates.add(group.in(searchInboundQualityLine.getBatchSerialNumber()));
         }

		 if (searchInboundQualityLine.getStorageSectionId() != null && !searchInboundQualityLine.getStorageSectionId().isEmpty()) {
        	 final Path<Group> group = root.get("storageSectionId");
        	 predicates.add(group.in(searchInboundQualityLine.getStorageSectionId()));
         }

		 if (searchInboundQualityLine.getStatusId() != null && !searchInboundQualityLine.getStatusId().isEmpty()) {
        	 final Path<Group> group = root.get("statusId");
        	 predicates.add(group.in(searchInboundQualityLine.getStatusId()));
         }
		 if (searchInboundQualityLine.getLineNo() != null && !searchInboundQualityLine.getLineNo().isEmpty()) {
        	 final Path<Group> group = root.get("lineNo");
        	 predicates.add(group.in(searchInboundQualityLine.getLineNo()));
         }
		 
		 if (searchInboundQualityLine.getStartCreatedOn()!= null && searchInboundQualityLine.getEndCreatedOn() != null) {
			 predicates.add(cb.between(root.get("createdOn"), searchInboundQualityLine.getStartCreatedOn(),
        			 searchInboundQualityLine.getEndCreatedOn()));
         }

		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}