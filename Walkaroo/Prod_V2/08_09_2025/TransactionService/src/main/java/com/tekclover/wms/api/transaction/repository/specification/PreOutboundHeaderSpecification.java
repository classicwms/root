package com.tekclover.wms.api.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundHeader;

@SuppressWarnings("serial")
public class PreOutboundHeaderSpecification implements Specification<PreOutboundHeader> {

	SearchPreOutboundHeader searchPreOutboundHeader;

	public PreOutboundHeaderSpecification(SearchPreOutboundHeader inputSearchParams) {
		this.searchPreOutboundHeader = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<PreOutboundHeader> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (searchPreOutboundHeader.getWarehouseId() != null && !searchPreOutboundHeader.getWarehouseId().isEmpty()) {
			final Path<Group> group = root.<Group>get("warehouseId");
			predicates.add(group.in(searchPreOutboundHeader.getWarehouseId()));
		}

		if (searchPreOutboundHeader.getPreOutboundNo() != null
				&& !searchPreOutboundHeader.getPreOutboundNo().isEmpty()) {
			final Path<Group> group = root.<Group>get("preOutboundNo");
			predicates.add(group.in(searchPreOutboundHeader.getPreOutboundNo()));
		}

		if (searchPreOutboundHeader.getOutboundOrderTypeId() != null
				&& !searchPreOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
			final Path<Group> group = root.<Group>get("outboundOrderTypeId");
			predicates.add(group.in(searchPreOutboundHeader.getOutboundOrderTypeId()));
		}

		if (searchPreOutboundHeader.getSoType() != null && !searchPreOutboundHeader.getSoType().isEmpty()) {
			final Path<Group> group = root.<Group>get("referenceField1");
			predicates.add(group.in(searchPreOutboundHeader.getSoType()));
		}

		if (searchPreOutboundHeader.getSoNumber() != null && !searchPreOutboundHeader.getSoNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("refDocNumber");
			predicates.add(group.in(searchPreOutboundHeader.getSoNumber()));
		}

		if (searchPreOutboundHeader.getPartnerCode() != null && !searchPreOutboundHeader.getPartnerCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("partnerCode");
			predicates.add(group.in(searchPreOutboundHeader.getPartnerCode()));
		}

		if (searchPreOutboundHeader.getStatusId() != null && !searchPreOutboundHeader.getStatusId().isEmpty()) {
			final Path<Group> group = root.<Group>get("statusId");
			predicates.add(group.in(searchPreOutboundHeader.getStatusId()));
		}

		if (searchPreOutboundHeader.getCreatedBy() != null && !searchPreOutboundHeader.getCreatedBy().isEmpty()) {
			final Path<Group> group = root.<Group>get("createdBy");
			predicates.add(group.in(searchPreOutboundHeader.getCreatedBy()));
		}

		if (searchPreOutboundHeader.getStartRequiredDeliveryDate() != null
				&& searchPreOutboundHeader.getEndRequiredDeliveryDate() != null) {
			predicates.add(
					cb.between(root.get("requiredDeliveryDate"), searchPreOutboundHeader.getStartRequiredDeliveryDate(),
							searchPreOutboundHeader.getEndRequiredDeliveryDate()));
		}

		if (searchPreOutboundHeader.getStartOrderDate() != null && searchPreOutboundHeader.getEndOrderDate() != null) {
			predicates.add(cb.between(root.get("refDocDate"), searchPreOutboundHeader.getStartOrderDate(),
					searchPreOutboundHeader.getEndOrderDate()));
		}

		if (searchPreOutboundHeader.getStartCreatedOn() != null && searchPreOutboundHeader.getEndCreatedOn() != null) {
			predicates.add(cb.between(root.get("createdOn"), searchPreOutboundHeader.getStartCreatedOn(),
					searchPreOutboundHeader.getEndCreatedOn()));
		}

		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
