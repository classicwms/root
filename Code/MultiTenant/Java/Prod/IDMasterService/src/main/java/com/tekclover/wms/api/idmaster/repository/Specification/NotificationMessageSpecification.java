package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.notificationmessage.FindNotificationMessage;
import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMessage;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class NotificationMessageSpecification implements Specification<NotificationMessage> {

    FindNotificationMessage findNotificationMessage;

    public NotificationMessageSpecification(FindNotificationMessage inputSearchParams) {
        this.findNotificationMessage = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<NotificationMessage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findNotificationMessage.getNotificationId() != null && !findNotificationMessage.getNotificationId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("notificationId");
            predicates.add(group.in(findNotificationMessage.getNotificationId()));
        }
        if (findNotificationMessage.getLanguageId() != null && !findNotificationMessage.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findNotificationMessage.getLanguageId()));
        }
        if (findNotificationMessage.getCompanyId() != null && !findNotificationMessage.getCompanyId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyCodeId");
            predicates.add(group.in(findNotificationMessage.getCompanyId()));
        }
        if (findNotificationMessage.getPlantId() != null && !findNotificationMessage.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findNotificationMessage.getPlantId()));
        }
        if (findNotificationMessage.getWarehouseId() != null && !findNotificationMessage.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findNotificationMessage.getWarehouseId()));
        }
        if (findNotificationMessage.getUserId() != null && !findNotificationMessage.getUserId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userId");
            predicates.add(group.in(findNotificationMessage.getUserId()));
        }
        if (findNotificationMessage.getOrderType() != null && !findNotificationMessage.getOrderType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("orderType");
            predicates.add(group.in(findNotificationMessage.getOrderType()));
        }
        if (findNotificationMessage.getStartDate() != null && findNotificationMessage.getEndDate() != null) {
            predicates.add(cb.between(root.get("createdOn"), findNotificationMessage.getStartDate(), findNotificationMessage.getEndDate()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}