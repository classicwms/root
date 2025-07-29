package com.tekclover.wms.api.inbound.transaction.repository.specification;

import com.tekclover.wms.api.inbound.transaction.model.inbound.inboundquality.InboundQualityHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inboundquality.SearchInboundQualityHeader;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InboundQualityHeaderSpecification implements Specification<InboundQualityHeader> {

	SearchInboundQualityHeader searchInboundQualityHeader;

	public InboundQualityHeaderSpecification(SearchInboundQualityHeader inputSearchParams) {
		this.searchInboundQualityHeader = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<InboundQualityHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<>();

         if (searchInboundQualityHeader.getCompanyCodeId() != null && !searchInboundQualityHeader.getCompanyCodeId().isEmpty()) {
        	 final Path<Group> group = root.get("companyCodeId");
        	 predicates.add(group.in(searchInboundQualityHeader.getCompanyCodeId()));
         }

		 if (searchInboundQualityHeader.getPlantId() != null && !searchInboundQualityHeader.getPlantId().isEmpty()) {
        	 final Path<Group> group = root.get("plantId");
        	 predicates.add(group.in(searchInboundQualityHeader.getPlantId()));
         }

		 if (searchInboundQualityHeader.getLanguageId() != null && !searchInboundQualityHeader.getLanguageId().isEmpty()) {
        	 final Path<Group> group = root.get("languageId");
        	 predicates.add(group.in(searchInboundQualityHeader.getLanguageId()));
         }

		 if (searchInboundQualityHeader.getWarehouseId() != null && !searchInboundQualityHeader.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.get("warehouseId");
        	 predicates.add(group.in(searchInboundQualityHeader.getWarehouseId()));
         }
         
         if (searchInboundQualityHeader.getRefDocNumber() != null && !searchInboundQualityHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.get("refDocNumber");
        	 predicates.add(group.in(searchInboundQualityHeader.getRefDocNumber()));
         }

		 if (searchInboundQualityHeader.getPreInboundNo() != null && !searchInboundQualityHeader.getPreInboundNo().isEmpty()) {
        	 final Path<Group> group = root.get("preInboundNo");
        	 predicates.add(group.in(searchInboundQualityHeader.getPreInboundNo()));
         }
         
		 if (searchInboundQualityHeader.getInboundQualityNumber() != null && !searchInboundQualityHeader.getInboundQualityNumber().isEmpty()) {
        	 final Path<Group> group = root.get("inboundQualityNumber");
        	 predicates.add(group.in(searchInboundQualityHeader.getInboundQualityNumber()));
         }

		 if (searchInboundQualityHeader.getItemCode() != null && !searchInboundQualityHeader.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.get("itemCode");
        	 predicates.add(group.in(searchInboundQualityHeader.getItemCode()));
         }

		 if (searchInboundQualityHeader.getBatchSerialNumber() != null && !searchInboundQualityHeader.getBatchSerialNumber().isEmpty()) {
        	 final Path<Group> group = root.get("batchSerialNumber");
        	 predicates.add(group.in(searchInboundQualityHeader.getBatchSerialNumber()));
         }

		 if (searchInboundQualityHeader.getStorageSectionId() != null && !searchInboundQualityHeader.getStorageSectionId().isEmpty()) {
        	 final Path<Group> group = root.get("storageSectionId");
        	 predicates.add(group.in(searchInboundQualityHeader.getStorageSectionId()));
         }

		 if (searchInboundQualityHeader.getStatusId() != null && !searchInboundQualityHeader.getStatusId().isEmpty()) {
        	 final Path<Group> group = root.get("statusId");
        	 predicates.add(group.in(searchInboundQualityHeader.getStatusId()));
         }	
		 
		 if (searchInboundQualityHeader.getStartCreatedOn()!= null && searchInboundQualityHeader.getEndCreatedOn() != null) {
			 predicates.add(cb.between(root.get("createdOn"), searchInboundQualityHeader.getStartCreatedOn(),
        			 searchInboundQualityHeader.getEndCreatedOn()));
         }

		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}