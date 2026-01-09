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

    FindNotificationMessage searchNotification;

    public NotificationMessageSpecification(FindNotificationMessage inputSearchParams) {
        this.searchNotification = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<NotificationMessage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchNotification.getPlantId() != null && !searchNotification.getPlantId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("plantId");
            predicates.add(group.in(searchNotification.getPlantId()));
        }

        if (searchNotification.getCompanyId() != null && !searchNotification.getCompanyId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("companyId");
            predicates.add(group.in(searchNotification.getCompanyId()));
        }

        if (searchNotification.getLanguageId() != null && !searchNotification.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(searchNotification.getLanguageId()));
        }
        if (searchNotification.getWarehouseId() != null && !searchNotification.getWarehouseId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("warehouseId");
            predicates.add(group.in(searchNotification.getWarehouseId()));
        }

        if (searchNotification.getUserId() != null && !searchNotification.getUserId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("userId");
            predicates.add(group.in(searchNotification.getUserId()));
        }

        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        predicates.add(cb.equal(root.get("status"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
