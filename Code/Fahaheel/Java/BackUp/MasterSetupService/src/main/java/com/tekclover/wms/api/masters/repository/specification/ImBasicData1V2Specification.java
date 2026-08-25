package com.tekclover.wms.api.masters.repository.specification;

import com.tekclover.wms.api.masters.model.imbasicdata1.SearchImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImBasicData1V2Specification implements Specification<ImBasicData1V2> {

	SearchImBasicData1 searchImBasicData1;

	public ImBasicData1V2Specification(SearchImBasicData1 inputSearchParams) {
		this.searchImBasicData1 = inputSearchParams;
	}
	 
	@Override
     public Predicate toPredicate(Root<ImBasicData1V2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

         List<Predicate> predicates = new ArrayList<Predicate>();

         if (searchImBasicData1.getWarehouseId() != null && !searchImBasicData1.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchImBasicData1.getWarehouseId()));
         }
         
         if (searchImBasicData1.getItemCode() != null && !searchImBasicData1.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchImBasicData1.getItemCode()));
         }
         
         if (searchImBasicData1.getDescription() != null && !searchImBasicData1.getDescription().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("description");
        	 predicates.add(group.in(searchImBasicData1.getDescription()));
         }
         
         if (searchImBasicData1.getItemType() != null && !searchImBasicData1.getItemType().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemType");
        	 predicates.add(group.in(searchImBasicData1.getItemType()));
         }
         
         if (searchImBasicData1.getItemGroup() != null && !searchImBasicData1.getItemGroup().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemGroup");
        	 predicates.add(group.in(searchImBasicData1.getItemGroup()));
         }
		 
		 if (searchImBasicData1.getSubItemGroup() != null && !searchImBasicData1.getSubItemGroup().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("subItemGroup");
        	 predicates.add(group.in(searchImBasicData1.getSubItemGroup()));
         }

         if (searchImBasicData1.getCompanyCodeId() != null && !searchImBasicData1.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group> get("companyCodeId");
            predicates.add(group.in(searchImBasicData1.getCompanyCodeId()));
        }

        if (searchImBasicData1.getManufacturerPartNo() != null && !searchImBasicData1.getManufacturerPartNo().isEmpty()) {
            final Path<Group> group = root.<Group> get("manufacturerPartNo");
            predicates.add(group.in(searchImBasicData1.getManufacturerPartNo()));
        }

        if (searchImBasicData1.getPlantId() != null && !searchImBasicData1.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group> get("plantId");
            predicates.add(group.in(searchImBasicData1.getPlantId()));
        }

        if (searchImBasicData1.getLanguageId() != null && !searchImBasicData1.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group> get("languageId");
            predicates.add(group.in(searchImBasicData1.getLanguageId()));
        }

        if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
             predicates.add(cb.between(root.get("createdOn"), searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn()));
         }
         
         if (searchImBasicData1.getCreatedBy() != null && !searchImBasicData1.getCreatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("createdBy");
        	 predicates.add(group.in(searchImBasicData1.getCreatedBy()));
         }
		 
		 if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
             predicates.add(cb.between(root.get("updatedOn"), searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn()));
         }
         
         if (searchImBasicData1.getUpdatedBy() != null && !searchImBasicData1.getUpdatedBy().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("updatedBy");
        	 predicates.add(group.in(searchImBasicData1.getUpdatedBy()));
         }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}
