package com.almailem.ams.api.connector.repository.specification;

import com.almailem.ams.api.connector.model.stockadjustment.FindStockAdjustment;
import com.almailem.ams.api.connector.model.stockadjustment.StockAdjustment;
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
public class StockAdjustmentSpecification implements Specification<StockAdjustment> {

    FindStockAdjustment findStockAdjustment;

    public StockAdjustmentSpecification(FindStockAdjustment inputSearchParams) {
        this.findStockAdjustment = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StockAdjustment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStockAdjustment.getStockAdjustmentId() != null && !findStockAdjustment.getStockAdjustmentId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("stockAdjustmentId");
            predicates.add(group.in(findStockAdjustment.getStockAdjustmentId()));
        }
        if (findStockAdjustment.getCompanyCode() != null && !findStockAdjustment.getCompanyCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCode");
            predicates.add(group.in(findStockAdjustment.getCompanyCode()));
        }
        if (findStockAdjustment.getBranchCode() != null && !findStockAdjustment.getBranchCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("branchCode");
            predicates.add(group.in(findStockAdjustment.getBranchCode()));
        }
        if (findStockAdjustment.getItemCode() != null && !findStockAdjustment.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findStockAdjustment.getItemCode()));
        }
        if (findStockAdjustment.getUnitOfMeasure() != null && !findStockAdjustment.getUnitOfMeasure().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("unitOfMeasure");
            predicates.add(group.in(findStockAdjustment.getUnitOfMeasure()));
        }
        if (findStockAdjustment.getManufacturerCode() != null && !findStockAdjustment.getManufacturerCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("manufacturerCode");
            predicates.add(group.in(findStockAdjustment.getManufacturerCode()));
        }
        if (findStockAdjustment.getFromOrderReceivedOn() != null && findStockAdjustment.getToOrderReceivedOn() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), findStockAdjustment.getFromOrderReceivedOn(),
                    findStockAdjustment.getToOrderReceivedOn()));
        }

        if (findStockAdjustment.getFromOrderProcessedOn() != null && findStockAdjustment.getToOrderProcessedOn() != null) {
            predicates.add(cb.between(root.get("orderProcessedOn"), findStockAdjustment.getFromOrderProcessedOn(),
                    findStockAdjustment.getToOrderProcessedOn()));
        }

        if (findStockAdjustment.getProcessedStatusId() != null && !findStockAdjustment.getProcessedStatusId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("processedStatusId");
            predicates.add(group.in(findStockAdjustment.getProcessedStatusId()));
        }



        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
