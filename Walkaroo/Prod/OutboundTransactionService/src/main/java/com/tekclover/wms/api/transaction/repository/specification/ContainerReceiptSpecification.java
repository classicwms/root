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

import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.ContainerReceipt;
import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.SearchContainerReceipt;

@SuppressWarnings("serial")
public class ContainerReceiptSpecification implements Specification<ContainerReceipt> {
	
	SearchContainerReceipt searchContainerReceipt;
	
	public ContainerReceiptSpecification(SearchContainerReceipt inputSearchParams) {
		this.searchContainerReceipt = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<ContainerReceipt> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchContainerReceipt.getWarehouseId() != null && !searchContainerReceipt.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchContainerReceipt.getWarehouseId()));
         }
         
         if (searchContainerReceipt.getContainerReceiptNo() != null && !searchContainerReceipt.getContainerReceiptNo().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("containerReceiptNo");
        	 predicates.add(group.in(searchContainerReceipt.getContainerReceiptNo()));
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
		               
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
