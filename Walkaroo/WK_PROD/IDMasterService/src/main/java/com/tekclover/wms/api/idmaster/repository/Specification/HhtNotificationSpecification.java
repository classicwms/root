package com.tekclover.wms.api.idmaster.repository.Specification;

import com.tekclover.wms.api.idmaster.model.hhtnotification.FindHhtNotification;
import com.tekclover.wms.api.idmaster.model.hhtnotification.HhtNotification;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class HhtNotificationSpecification implements Specification<HhtNotification> {
    FindHhtNotification findHhtNotification;

    public HhtNotificationSpecification(FindHhtNotification inputSearchParams) {
        this.findHhtNotification = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<HhtNotification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findHhtNotification.getCompanyCodeId() != null && !findHhtNotification.getCompanyCodeId().isEmpty()) {
            final Path<Group> group = root.<Group>get("companyId");
            predicates.add(group.in(findHhtNotification.getCompanyCodeId()));
        }

        if (findHhtNotification.getPlantId() != null && !findHhtNotification.getPlantId().isEmpty()) {
            final Path<Group> group = root.<Group>get("plantId");
            predicates.add(group.in(findHhtNotification.getPlantId()));
        }

        if (findHhtNotification.getUserId() != null && !findHhtNotification.getUserId().isEmpty()) {
            final Path<Group> group = root.<Group>get("userId");
            predicates.add(group.in(findHhtNotification.getUserId()));
        }

        if (findHhtNotification.getWarehouseId() != null && !findHhtNotification.getWarehouseId().isEmpty()) {
            final Path<Group> group = root.<Group>get("warehouseId");
            predicates.add(group.in(findHhtNotification.getWarehouseId()));
        }
        if (findHhtNotification.getLanguageId() != null && !findHhtNotification.getLanguageId().isEmpty()) {
            final Path<Group> group = root.<Group>get("languageId");
            predicates.add(group.in(findHhtNotification.getLanguageId()));
        }
        if (findHhtNotification.getDeviceId() != null && !findHhtNotification.getDeviceId().isEmpty()) {
            final Path<Group> group = root.<Group>get("deviceId");
            predicates.add(group.in(findHhtNotification.getDeviceId()));
        }
        if (findHhtNotification.getIsLoggedIn() != null) {
            final Path<Group> group = root.<Group>get("isLoggedIn");
            predicates.add(group.in(findHhtNotification.getIsLoggedIn()));
        }
        if (findHhtNotification.getPortalUser() != null) {
            final Path<Group> group = root.<Group>get("portalUser");
            predicates.add(group.in(findHhtNotification.getPortalUser()));
        }
        if (findHhtNotification.getStartDate() != null && findHhtNotification.getEndDate() != null) {
            predicates.add(cb.between(root.get("createdOn"),
                    findHhtNotification.getStartDate(), findHhtNotification.getEndDate()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
}
