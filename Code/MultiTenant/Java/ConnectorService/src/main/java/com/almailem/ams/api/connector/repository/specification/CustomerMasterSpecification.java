package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.master.CustomerMaster;
import com.almailem.ams.api.connector.model.master.FindCustomerMaster;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CustomerMasterSpecification implements Specification<CustomerMaster> {

    FindCustomerMaster findCustomerMaster;

    public CustomerMasterSpecification(FindCustomerMaster inputSearchParams) {
        this.findCustomerMaster = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<CustomerMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findCustomerMaster.getCustomerMasterId() != null && !findCustomerMaster.getCustomerMasterId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("customerMasterId");
            predicates.add(group.in(findCustomerMaster.getCustomerMasterId()));
        }
        if (findCustomerMaster.getCompanyCode() != null && !findCustomerMaster.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findCustomerMaster.getCompanyCode()));
        }
        if (findCustomerMaster.getBranchCode() != null && !findCustomerMaster.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findCustomerMaster.getBranchCode()));
        }
        if (findCustomerMaster.getCustomerCode() != null && !findCustomerMaster.getCustomerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("customerCode");
            predicates.add(group.in(findCustomerMaster.getCustomerCode()));
        }
        if (findCustomerMaster.getFromOrderReceivedOn() != null && findCustomerMaster.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findCustomerMaster.getFromOrderReceivedOn(),
                    findCustomerMaster.getToOrderReceivedOn()));
        }

        if (findCustomerMaster.getFromOrderProcessedOn() != null && findCustomerMaster.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findCustomerMaster.getFromOrderProcessedOn(),
                    findCustomerMaster.getToOrderProcessedOn()));
        }

        if (findCustomerMaster.getProcessedStatusId() != null && !findCustomerMaster.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findCustomerMaster.getProcessedStatusId()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
