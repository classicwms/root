package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.enterprise.transaction.model.report.FindImBasicData1;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ImBasicData1Specification implements Specification<ImBasicData1> {
	
	FindImBasicData1 searchImBasicData1;
	
	public ImBasicData1Specification(FindImBasicData1 inputSearchParams) {
		this.searchImBasicData1 = inputSearchParams;
	}
	 
	@Override
    public Predicate toPredicate(Root<ImBasicData1> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
         List<Predicate> predicates = new ArrayList<Predicate>();

		 if (searchImBasicData1.getWarehouseId() != null && !searchImBasicData1.getWarehouseId().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("warehouseId");
        	 predicates.add(group.in(searchImBasicData1.getWarehouseId()));
         }

		 if (searchImBasicData1.getItemCode() != null && !searchImBasicData1.getItemCode().isEmpty()) {
        	 final Path<Group> group = root.<Group> get("itemCode");
        	 predicates.add(group.in(searchImBasicData1.getItemCode()));
         }
		 
//		 if (searchImBasicData1.getFromCreatedOn() != null && searchImBasicData1.getToCreatedOn() != null) {
//             predicates.add(cb.between(root.get("createdOn"),
//            		 searchImBasicData1.getFromCreatedOn(), searchImBasicData1.getToCreatedOn()));
//         }
		 
		 predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
         return cb.and(predicates.toArray(new Predicate[] {}));
     }
}