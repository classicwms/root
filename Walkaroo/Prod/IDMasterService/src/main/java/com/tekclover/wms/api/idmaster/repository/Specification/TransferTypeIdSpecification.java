package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.transfertypeid.FindTransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.TransferTypeId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TransferTypeIdSpecification implements Specification<TransferTypeId> {
    FindTransferTypeId findTransferTypeId;

    public TransferTypeIdSpecification(FindTransferTypeId inputSearchParams) {
        this.findTransferTypeId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<TransferTypeId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findTransferTypeId.getCompanyCodeId() != null && !findTransferTypeId.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findTransferTypeId.getCompanyCodeId()));
        }

        if (findTransferTypeId.getWarehouseId() != null && !findTransferTypeId.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findTransferTypeId.getWarehouseId()));
        }

        if (findTransferTypeId.getPlantId() != null && !findTransferTypeId.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findTransferTypeId.getPlantId()));
        }
        if (findTransferTypeId.getTransferTypeId() != null && !findTransferTypeId.getTransferTypeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("transferTypeId");
            predicates.add(group.in(findTransferTypeId.getTransferTypeId()));
        }
        if (findTransferTypeId.getLanguageId() != null && !findTransferTypeId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findTransferTypeId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
