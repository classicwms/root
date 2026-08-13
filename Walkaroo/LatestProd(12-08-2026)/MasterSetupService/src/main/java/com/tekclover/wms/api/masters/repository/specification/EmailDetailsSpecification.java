package com.tekclover.wms.api.masters.repository.specification;
import com.tekclover.wms.api.masters.model.email.*;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class EmailDetailsSpecification implements Specification<EMailDetails> {

    FindEmailDetails findEmailDetails;

    public EmailDetailsSpecification(FindEmailDetails inputSearchParams) {
        this.findEmailDetails = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<EMailDetails> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findEmailDetails.getCompanyCodeId() != null && !findEmailDetails.getCompanyCodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findEmailDetails.getCompanyCodeId()));
        }

        if (findEmailDetails.getPlantId() != null && !findEmailDetails.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findEmailDetails.getPlantId()));
        }

        if (findEmailDetails.getWarehouseId() != null && !findEmailDetails.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findEmailDetails.getWarehouseId()));
        }

        if (findEmailDetails.getLanguageId() != null && !findEmailDetails.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findEmailDetails.getLanguageId()));
        }

        if (findEmailDetails.getCreatedBy() != null && !findEmailDetails.getCreatedBy().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("createdBy");
            predicates.add(group.in(findEmailDetails.getCreatedBy()));
        }

        if (findEmailDetails.getSenderName() != null && !findEmailDetails.getSenderName().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("senderName");
            predicates.add(group.in(findEmailDetails.getSenderName()));
        }

        if (findEmailDetails.getFromAddress() != null && !findEmailDetails.getFromAddress().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("fromAddress");
            predicates.add(group.in(findEmailDetails.getFromAddress()));
        }

        if (findEmailDetails.getToAddress() != null && !findEmailDetails.getToAddress().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("toAddress");
            predicates.add(group.in(findEmailDetails.getToAddress()));
        }
        if (findEmailDetails.getCcAddress() != null && !findEmailDetails.getCcAddress().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("ccAddress");
            predicates.add(group.in(findEmailDetails.getCcAddress()));
        }

        if (findEmailDetails.getStartDate()!= null && findEmailDetails.getEndDate() != null) {
            predicates.add(cb.between(root.get("createdOn"), findEmailDetails.getStartDate(), findEmailDetails.getEndDate()));
        }

        if (findEmailDetails.getDeletionIndicator() != null && !findEmailDetails.getDeletionIndicator().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("deletionIndicator");
            predicates.add(group.in(findEmailDetails.getDeletionIndicator()));
        }

        if(findEmailDetails.getDeletionIndicator() == null || findEmailDetails.getDeletionIndicator().isEmpty()) {
            predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
