package com.tekclover.wms.api.inbound.transaction.repository.specification;

import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.SearchPutAwayHeaderV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PutAwayHeaderV2Specification implements Specification<PutAwayHeaderV2> {

	SearchPutAwayHeaderV2 searchPutAwayHeader;

	public PutAwayHeaderV2Specification(SearchPutAwayHeaderV2 inputSearchParams) {
		this.searchPutAwayHeader = inputSearchParams;
	}

	@Override
    public Predicate toPredicate(Root<PutAwayHeaderV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchPutAwayHeader.getCompanyCodeId() != null && !searchPutAwayHeader.getCompanyCodeId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("companyCodeId");
        	 predicates.add(group.in(searchPutAwayHeader.getCompanyCodeId()));
         }

		 if (searchPutAwayHeader.getPlantId() != null && !searchPutAwayHeader.getPlantId().isEmpty()) {
				 final Path<Group> group = root.<Group> get("plantId");
				 predicates.add(group.in(searchPutAwayHeader.getPlantId()));
		 }

		 if (searchPutAwayHeader.getLanguageId() != null && !searchPutAwayHeader.getLanguageId().isEmpty()) {
				 final Path<Group> group = root.<Group> get("languageId");
				 predicates.add(group.in(searchPutAwayHeader.getLanguageId()));
		 }

		 if (searchPutAwayHeader.getWarehouseId() != null && !searchPutAwayHeader.getWarehouseId().isEmpty()) {
				 final Path<Group> group = root.<Group> get("warehouseId");
				 predicates.add(group.in(searchPutAwayHeader.getWarehouseId()));
		 }

		 if (searchPutAwayHeader.getRefDocNumber() != null && !searchPutAwayHeader.getRefDocNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("refDocNumber");
        	 predicates.add(group.in(searchPutAwayHeader.getRefDocNumber()));
         }
		 if (searchPutAwayHeader.getItemCode() != null && !searchPutAwayHeader.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("referenceField5");
        	 predicates.add(group.in(searchPutAwayHeader.getItemCode()));
         }

		 if (searchPutAwayHeader.getPackBarcodes() != null && !searchPutAwayHeader.getPackBarcodes().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("packBarcodes");
        	 predicates.add(group.in(searchPutAwayHeader.getPackBarcodes()));
         }

		  if (searchPutAwayHeader.getPutAwayNumber() != null && !searchPutAwayHeader.getPutAwayNumber().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("putAwayNumber");
        	 predicates.add(group.in(searchPutAwayHeader.getPutAwayNumber()));
         }

          if (searchPutAwayHeader.getProposedStorageBin() != null && !searchPutAwayHeader.getProposedStorageBin().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("proposedStorageBin");
        	 predicates.add(group.in(searchPutAwayHeader.getProposedStorageBin()));
         }

          if (searchPutAwayHeader.getProposedHandlingEquipment() != null && !searchPutAwayHeader.getProposedHandlingEquipment().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("proposedHandlingEquipment");
        	 predicates.add(group.in(searchPutAwayHeader.getProposedHandlingEquipment()));
         }

         if (searchPutAwayHeader.getStatusId() != null && !searchPutAwayHeader.getStatusId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("statusId");
        	 predicates.add(group.in(searchPutAwayHeader.getStatusId()));
         }

		  if (searchPutAwayHeader.getCreatedBy() != null && !searchPutAwayHeader.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchPutAwayHeader.getCreatedBy()));
         }

         if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchPutAwayHeader.getStartCreatedOn(), searchPutAwayHeader.getEndCreatedOn()));
         }

		if (searchPutAwayHeader.getBarcodeId() != null && !searchPutAwayHeader.getBarcodeId().isEmpty()) {
			final Path<Group> group = root.<Group> get("barcodeId");
			predicates.add(group.in(searchPutAwayHeader.getBarcodeId()));
		}

		if (searchPutAwayHeader.getManufacturerCode() != null && !searchPutAwayHeader.getManufacturerCode().isEmpty()) {
			final Path<Group> group = root.<Group> get("manufacturerCode");
			predicates.add(group.in(searchPutAwayHeader.getManufacturerCode()));
		}

		if (searchPutAwayHeader.getManufacturerName() != null && !searchPutAwayHeader.getManufacturerName().isEmpty()) {
			final Path<Group> group = root.<Group> get("manufacturerName");
			predicates.add(group.in(searchPutAwayHeader.getManufacturerName()));
		}

		if (searchPutAwayHeader.getOrigin() != null && !searchPutAwayHeader.getOrigin().isEmpty()) {
			final Path<Group> group = root.<Group> get("origin");
			predicates.add(group.in(searchPutAwayHeader.getOrigin()));
		}

		if (searchPutAwayHeader.getBrand() != null && !searchPutAwayHeader.getBrand().isEmpty()) {
			final Path<Group> group = root.<Group> get("brand");
			predicates.add(group.in(searchPutAwayHeader.getBrand()));
		}

		if (searchPutAwayHeader.getApprovalStatus() != null && !searchPutAwayHeader.getApprovalStatus().isEmpty()) {
			final Path<Group> group = root.<Group> get("approvalStatus");
			predicates.add(group.in(searchPutAwayHeader.getApprovalStatus()));
		}

		predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
