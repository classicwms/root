package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.notification.FindNotification;
import com.tekclover.wms.api.idmaster.model.notification.Notification;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class NotificationSpecification implements Specification<Notification> {

    FindNotification findNotification;

    public NotificationSpecification(FindNotification inputSearchParams) {
        this.findNotification = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findNotification.getLanguageId() != null && !findNotification.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findNotification.getLanguageId()));
        }
        if (findNotification.getCompanyId() != null && !findNotification.getCompanyId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyId");
            predicates.add(group.in(findNotification.getCompanyId()));
        }
        if (findNotification.getPlantId() != null && !findNotification.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(findNotification.getPlantId()));
        }
        if (findNotification.getWarehouseId() != null && !findNotification.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(findNotification.getWarehouseId()));
        }
        if (findNotification.getNotificationId() != null && !findNotification.getNotificationId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("notificationId");
            predicates.add(group.in(findNotification.getNotificationId()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
