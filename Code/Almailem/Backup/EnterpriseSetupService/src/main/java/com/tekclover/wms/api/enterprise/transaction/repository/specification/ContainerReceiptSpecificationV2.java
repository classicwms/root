package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.containerreceipt.v2.ContainerReceiptV2;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.containerreceipt.v2.SearchContainerReceiptV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ContainerReceiptSpecificationV2 implements Specification<ContainerReceiptV2> {

	SearchContainerReceiptV2 searchContainerReceipt;

	public ContainerReceiptSpecificationV2(SearchContainerReceiptV2 inputSearchParams) {
		this.searchContainerReceipt = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<ContainerReceiptV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchContainerReceipt.getCompanyCodeId() != null && !searchContainerReceipt.getCompanyCodeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("companyCodeId");
        	 predicates.add(group.in(searchContainerReceipt.getCompanyCodeId()));
         }

		 if (searchContainerReceipt.getPlantId() != null && !searchContainerReceipt.getPlantId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("plantId");
        	 predicates.add(group.in(searchContainerReceipt.getPlantId()));
         }

		 if (searchContainerReceipt.getLanguageId() != null && !searchContainerReceipt.getLanguageId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("languageId");
        	 predicates.add(group.in(searchContainerReceipt.getLanguageId()));
         }

		 if (searchContainerReceipt.getWarehouseId() != null && !searchContainerReceipt.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchContainerReceipt.getWarehouseId()));
         }
         
         if (searchContainerReceipt.getContainerReceiptNo() != null && !searchContainerReceipt.getContainerReceiptNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("containerReceiptNo");
        	 predicates.add(group.in(searchContainerReceipt.getContainerReceiptNo()));
         }

		 if (searchContainerReceipt.getRefDocNumber() != null && !searchContainerReceipt.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchContainerReceipt.getRefDocNumber()));
         }
         
		 if (searchContainerReceipt.getContainerNo() != null && !searchContainerReceipt.getContainerNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("containerNo");
        	 predicates.add(group.in(searchContainerReceipt.getContainerNo()));
         }
		 
		 if (searchContainerReceipt.getStartContainerReceivedDate() != null && searchContainerReceipt.getEndContainerReceivedDate() != null) {
        	 predicates.add(cb.between(root.get("containerReceivedDate"), 
        			 searchContainerReceipt.getStartContainerReceivedDate(), searchContainerReceipt.getEndContainerReceivedDate()));
         }
		 
		 if (searchContainerReceipt.getPartnerCode() != null && !searchContainerReceipt.getPartnerCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("partnerCode");
        	 predicates.add(group.in(searchContainerReceipt.getPartnerCode()));
         }
		 
		 if (searchContainerReceipt.getUnloadedBy() != null && !searchContainerReceipt.getUnloadedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField1");
        	 predicates.add(group.in(searchContainerReceipt.getUnloadedBy()));
         }
		 
		 if (searchContainerReceipt.getStatusId() != null && !searchContainerReceipt.getStatusId().isEmpty()) {	
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchContainerReceipt.getStatusId()));
         }	
		 
		 // ----Dashboard-----
		 if (searchContainerReceipt.getFromCreatedOn()!= null && searchContainerReceipt.getToCreatedOn() != null) {	
			 predicates.add(cb.between(root.get("createdOn"), searchContainerReceipt.getFromCreatedOn(), 
        			 searchContainerReceipt.getToCreatedOn()));
         }

		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}