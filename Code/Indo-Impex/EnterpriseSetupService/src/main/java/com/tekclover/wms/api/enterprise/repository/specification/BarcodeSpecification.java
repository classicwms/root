package com.tekclover.wms.api.enterprise.repository.specification;

import com.tekclover.wms.api.enterprise.model.barcode.Barcode;
import com.tekclover.wms.api.enterprise.model.barcode.SearchBarcode;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BarcodeSpecification implements Specification<Barcode> {

    SearchBarcode searchBarcode;

    public BarcodeSpecification(SearchBarcode inputSearchParams) {
        this.searchBarcode = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Barcode> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchBarcode.getWarehouseID() != null && !searchBarcode.getWarehouseID().isEmpty()) {
            predicates.add(cb.equal(root.get("warehouseID"), searchBarcode.getWarehouseID()));
        }

        if (searchBarcode.getMethod() != null && !searchBarcode.getMethod().isEmpty()) {
            predicates.add(cb.equal(root.get("method"), searchBarcode.getMethod()));
        }

        if (searchBarcode.getBarcodeTypeId() != null && searchBarcode.getBarcodeTypeId().longValue() != 0) {
            predicates.add(cb.equal(root.get("barcodeTypeId"), searchBarcode.getBarcodeTypeId()));
        }

        if (searchBarcode.getProcessId() != null && searchBarcode.getProcessId().longValue() != 0) {
            predicates.add(cb.equal(root.get("processId"), searchBarcode.getProcessId()));
        }

        if (searchBarcode.getBarcodeSubTypeId() != null && searchBarcode.getBarcodeSubTypeId().longValue() != 0) {
            predicates.add(cb.equal(root.get("barcodeSubTypeId"), searchBarcode.getBarcodeSubTypeId()));
        }

        if (searchBarcode.getLevelId() != null && searchBarcode.getLevelId().longValue() != 0) {
            predicates.add(cb.equal(root.get("levelId"), searchBarcode.getLevelId()));
        }
        if (searchBarcode.getCreatedBy() != null && !searchBarcode.getCreatedBy().isEmpty()) {
            predicates.add(cb.equal(root.get("createdBy"), searchBarcode.getCreatedBy()));
        }

        if (searchBarcode.getStartCreatedOn() != null && searchBarcode.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchBarcode.getStartCreatedOn(), searchBarcode.getEndCreatedOn()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}