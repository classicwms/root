package com.tekclover.wms.api.enterprise.transaction.repository.specification;

import com.tekclover.wms.api.enterprise.transaction.model.mnc.InhouseTransferLine;
import com.tekclover.wms.api.enterprise.transaction.model.mnc.SearchInhouseTransferLine;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InhouseTransferLineSpecification implements Specification<InhouseTransferLine> {

    SearchInhouseTransferLine searchInhouseTransferLine;

    public InhouseTransferLineSpecification(SearchInhouseTransferLine inputSearchParams) {
        this.searchInhouseTransferLine = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InhouseTransferLine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchInhouseTransferLine.getLanguageId() != null && !searchInhouseTransferLine.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(searchInhouseTransferLine.getLanguageId()));
        }

        if (searchInhouseTransferLine.getCompanyCodeId() != null && !searchInhouseTransferLine.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyCodeId");
            predicates.add(group.in(searchInhouseTransferLine.getCompanyCodeId()));
        }

        if (searchInhouseTransferLine.getPlantId() != null && !searchInhouseTransferLine.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(searchInhouseTransferLine.getPlantId()));
        }

        if (searchInhouseTransferLine.getWarehouseId() != null && !searchInhouseTransferLine.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(searchInhouseTransferLine.getWarehouseId()));
        }

        if (searchInhouseTransferLine.getTransferNumber() != null && !searchInhouseTransferLine.getTransferNumber().isEmpty()) {
            final Path<Group> group = root.<Group>get("transferNumber");
            predicates.add(group.in(searchInhouseTransferLine.getTransferNumber()));
        }

        if (searchInhouseTransferLine.getSourceItemCode() != null && !searchInhouseTransferLine.getSourceItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("sourceItemCode");
            predicates.add(group.in(searchInhouseTransferLine.getSourceItemCode()));
        }

        if (searchInhouseTransferLine.getSourceStockTypeId() != null && !searchInhouseTransferLine.getSourceStockTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("sourceStockTypeId");
            predicates.add(group.in(searchInhouseTransferLine.getSourceStockTypeId()));
        }

        if (searchInhouseTransferLine.getSourceStorageBin() != null && !searchInhouseTransferLine.getSourceStorageBin().isEmpty()) {
            final Path<Group> group = root.<Group>get("sourceStorageBin");
            predicates.add(group.in(searchInhouseTransferLine.getSourceStorageBin()));
        }

        if (searchInhouseTransferLine.getTargetItemCode() != null && !searchInhouseTransferLine.getTargetItemCode().isEmpty()) {
            final Path<Group> group = root.<Group>get("targetItemCode");
            predicates.add(group.in(searchInhouseTransferLine.getTargetItemCode()));
        }

        if (searchInhouseTransferLine.getTargetStockTypeId() != null && !searchInhouseTransferLine.getTargetStockTypeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("targetStockTypeId");
            predicates.add(group.in(searchInhouseTransferLine.getTargetStockTypeId()));
        }

        if (searchInhouseTransferLine.getTargetStorageBin() != null && !searchInhouseTransferLine.getTargetStorageBin().isEmpty()) {
            final Path<Group> group = root.<Group>get("targetStorageBin");
            predicates.add(group.in(searchInhouseTransferLine.getTargetStorageBin()));
        }

        if (searchInhouseTransferLine.getTransferConfirmedQty() != null && !searchInhouseTransferLine.getTransferConfirmedQty().isEmpty()) {
            final Path<Group> group = root.<Group>get("transferConfirmedQty");
            predicates.add(group.in(searchInhouseTransferLine.getTransferConfirmedQty()));
        }

        if (searchInhouseTransferLine.getAvailableQty() != null && !searchInhouseTransferLine.getAvailableQty().isEmpty()) {
            final Path<Group> group = root.<Group>get("availableQty");
            predicates.add(group.in(searchInhouseTransferLine.getAvailableQty()));
        }

        if (searchInhouseTransferLine.getStatusId() != null && !searchInhouseTransferLine.getStatusId().isEmpty()) {
            final Path<Group> group = root.<Group>get("statusId");
            predicates.add(group.in(searchInhouseTransferLine.getStatusId()));
        }

        if (searchInhouseTransferLine.getRemarks() != null && !searchInhouseTransferLine.getRemarks().isEmpty()) {
            final Path<Group> group = root.<Group>get("remarks");
            predicates.add(group.in(searchInhouseTransferLine.getRemarks()));
        }

        if (searchInhouseTransferLine.getStartCreatedOn() != null && searchInhouseTransferLine.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchInhouseTransferLine.getStartCreatedOn(), searchInhouseTransferLine.getEndCreatedOn()));
        }

        if (searchInhouseTransferLine.getCreatedBy() != null && !searchInhouseTransferLine.getCreatedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("createdBy");
            predicates.add(group.in(searchInhouseTransferLine.getCreatedBy()));
        }

        if (searchInhouseTransferLine.getStartConfirmedOn() != null && searchInhouseTransferLine.getEndConfirmedOn() != null) {
            predicates.add(cb.between(root.get("confirmedOn"), searchInhouseTransferLine.getStartConfirmedOn(), searchInhouseTransferLine.getEndConfirmedOn()));
        }

        if (searchInhouseTransferLine.getConfirmedBy() != null && !searchInhouseTransferLine.getConfirmedBy().isEmpty()) {
            final Path<Group> group = root.<Group>get("confirmedBy");
            predicates.add(group.in(searchInhouseTransferLine.getConfirmedBy()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}