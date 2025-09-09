package com.tekclover.wms.api.transaction.repository.specification;

import com.tekclover.wms.api.transaction.model.deliveryconfirmation.DeliveryConfirmation;
import com.tekclover.wms.api.transaction.model.deliveryconfirmation.SearchDeliveryConfirmation;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DeliveryConfirmationSpecification implements Specification<DeliveryConfirmation> {

    SearchDeliveryConfirmation searchDeliveryConfirmation;

    public DeliveryConfirmationSpecification(SearchDeliveryConfirmation inputSearchParams) {
        this.searchDeliveryConfirmation = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<DeliveryConfirmation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchDeliveryConfirmation.getCompanyCodeId() != null && !searchDeliveryConfirmation.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group> get("companyCodeId");
            predicates.add(group.in(searchDeliveryConfirmation.getCompanyCodeId()));
        }

        if (searchDeliveryConfirmation.getLanguageId() != null && !searchDeliveryConfirmation.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group> get("languageId");
            predicates.add(group.in(searchDeliveryConfirmation.getLanguageId()));
        }

        if (searchDeliveryConfirmation.getPlantId() != null && !searchDeliveryConfirmation.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group> get("plantId");
            predicates.add(group.in(searchDeliveryConfirmation.getPlantId()));
        }

        if (searchDeliveryConfirmation.getWarehouseId() != null && !searchDeliveryConfirmation.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group> get("warehouseId");
            predicates.add(group.in(searchDeliveryConfirmation.getWarehouseId()));
        }

        if (searchDeliveryConfirmation.getLoginUserId() != null && !searchDeliveryConfirmation.getLoginUserId().isEmpty()) {
            final Path<Group> group = root.<Group> get("loginUserId");
            predicates.add(group.in(searchDeliveryConfirmation.getLoginUserId()));
        }

        if (searchDeliveryConfirmation.getProcessedStatusId() != null && !searchDeliveryConfirmation.getProcessedStatusId().isEmpty()) {
            final Path<Group> group = root.<Group> get("processedStatusId");
            predicates.add(group.in(searchDeliveryConfirmation.getProcessedStatusId()));
        }

        if (searchDeliveryConfirmation.getOutbound() != null && !searchDeliveryConfirmation.getOutbound().isEmpty()) {
            final Path<Group> group = root.<Group> get("outbound");
            predicates.add(group.in(searchDeliveryConfirmation.getOutbound()));
        }

        if (searchDeliveryConfirmation.getSkuCode() != null && !searchDeliveryConfirmation.getSkuCode().isEmpty()) {
            final Path<Group> group = root.<Group> get("skuCode");
            predicates.add(group.in(searchDeliveryConfirmation.getSkuCode()));
        }

        if (searchDeliveryConfirmation.getHuSerialNo() != null && !searchDeliveryConfirmation.getHuSerialNo().isEmpty()) {
            final Path<Group> group = root.<Group> get("huSerialNo");
            predicates.add(group.in(searchDeliveryConfirmation.getHuSerialNo()));
        }

        if (searchDeliveryConfirmation.getGender() != null && !searchDeliveryConfirmation.getGender().isEmpty()) {
            final Path<Group> group = root.<Group> get("gender");
            predicates.add(group.in(searchDeliveryConfirmation.getGender()));
        }

        if (searchDeliveryConfirmation.getArticleNumber() != null && !searchDeliveryConfirmation.getArticleNumber().isEmpty()) {
            final Path<Group> group = root.<Group> get("articleNumber");
            predicates.add(group.in(searchDeliveryConfirmation.getArticleNumber()));
        }

        if (searchDeliveryConfirmation.getDeliveryId() != null && !searchDeliveryConfirmation.getDeliveryId().isEmpty()) {
            final Path<Group> group = root.<Group> get("deliveryId");
            predicates.add(group.in(searchDeliveryConfirmation.getDeliveryId()));
        }

        if (searchDeliveryConfirmation.getFromDate() != null && searchDeliveryConfirmation.getToDate() != null) {
            predicates.add(cb.between(root.get("orderReceivedOn"), searchDeliveryConfirmation.getFromDate(), searchDeliveryConfirmation.getToDate()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
