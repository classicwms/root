package com.tekclover.wms.api.idmaster.repository.Specification;


import com.tekclover.wms.api.idmaster.model.statusmessagesid.FindStatusMessagesId;
import com.tekclover.wms.api.idmaster.model.statusmessagesid.StatusMessagesId;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StatusMessagesIdSpecification implements Specification<StatusMessagesId> {
    FindStatusMessagesId findStatusMessageId;

    public StatusMessagesIdSpecification(FindStatusMessagesId inputSearchParams) {
        this.findStatusMessageId = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<StatusMessagesId> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (findStatusMessageId.getMessageId() != null && !findStatusMessageId.getMessageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("messageId");
            predicates.add(group.in(findStatusMessageId.getMessageId()));
        }

        if (findStatusMessageId.getMessageType() != null && !findStatusMessageId.getMessageType().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("messageType");
            predicates.add(group.in(findStatusMessageId.getMessageType()));
        }
        if (findStatusMessageId.getLanguageId() != null && !findStatusMessageId.getLanguageId().isEmpty()) {
            final Path<DeferredImportSelector.Group> group = root.<DeferredImportSelector.Group>get("languageId");
            predicates.add(group.in(findStatusMessageId.getLanguageId()));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

}
