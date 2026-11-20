package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.interimbarcode.FindInterimBarcode;
import com.tekclover.wms.api.idmaster.model.interimbarcode.InterimBarcode;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InterimBarcodeSpecification implements Specification<InterimBarcode> {

    FindInterimBarcode findInterimBarcode;

    public InterimBarcodeSpecification(FindInterimBarcode inputSearchParams) {
        this.findInterimBarcode = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<InterimBarcode> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findInterimBarcode.getBarcode() != null && !findInterimBarcode.getBarcode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("barcode");
            predicates.add(group.in(findInterimBarcode.getBarcode()));
        }

        if (findInterimBarcode.getInterimBarcodeId() != null && !findInterimBarcode.getInterimBarcodeId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("interimBarcodeId");
            predicates.add(group.in(findInterimBarcode.getInterimBarcodeId()));
        }

        if (findInterimBarcode.getItemCode() != null && !findInterimBarcode.getItemCode().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("itemCode");
            predicates.add(group.in(findInterimBarcode.getItemCode()));
        }

        if (findInterimBarcode.getReferenceField1() != null && !findInterimBarcode.getReferenceField1().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("referenceField1");
            predicates.add(group.in(findInterimBarcode.getReferenceField1()));
        }

        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
