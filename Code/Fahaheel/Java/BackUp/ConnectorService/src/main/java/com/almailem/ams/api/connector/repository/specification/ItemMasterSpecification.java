package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.master.FindItemMaster;
import com.almailem.ams.api.connector.model.master.ItemMaster;
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
public class ItemMasterSpecification implements Specification<ItemMaster> {

    FindItemMaster findItemMaster;

    public ItemMasterSpecification(FindItemMaster inputSearchParams) {
        this.findItemMaster = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<ItemMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findItemMaster.getItemMasterId() != null && !findItemMaster.getItemMasterId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemMasterId");
            predicates.add(group.in(findItemMaster.getItemMasterId()));
        }
        if (findItemMaster.getCompanyCode() != null && !findItemMaster.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findItemMaster.getCompanyCode()));
        }
        if (findItemMaster.getBranchCode() != null && !findItemMaster.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findItemMaster.getBranchCode()));
        }
        if (findItemMaster.getItemCode() != null && !findItemMaster.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findItemMaster.getItemCode()));
        }
        if (findItemMaster.getManufacturerCode() != null && !findItemMaster.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findItemMaster.getManufacturerCode()));
        }
        if (findItemMaster.getFromOrderReceivedOn() != null && findItemMaster.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findItemMaster.getFromOrderReceivedOn(),
                    findItemMaster.getToOrderReceivedOn()));
        }

        if (findItemMaster.getFromOrderProcessedOn() != null && findItemMaster.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findItemMaster.getFromOrderProcessedOn(),
                    findItemMaster.getToOrderProcessedOn()));
        }

        if (findItemMaster.getProcessedStatusId() != null && !findItemMaster.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findItemMaster.getProcessedStatusId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
