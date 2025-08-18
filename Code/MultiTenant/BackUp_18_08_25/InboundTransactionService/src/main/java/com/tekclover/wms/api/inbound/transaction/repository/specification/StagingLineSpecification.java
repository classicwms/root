package com.tekclover.wms.api.inbound.transaction.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.SearchStagingLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("serial")
public class StagingLineSpecification implements Specification<StagingLineEntityV2> {

	SearchStagingLineV2 searchStagingLine;

	public StagingLineSpecification(SearchStagingLineV2 inputSearchParams) {
		this.searchStagingLine = inputSearchParams;
	}

	@Override
	public Predicate toPredicate(Root<StagingLineEntityV2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (searchStagingLine.getWarehouseId() != null && !searchStagingLine.getWarehouseId().isEmpty()) {
			final Path<Group> group = root.<Group>get("warehouseId");
			predicates.add(group.in(searchStagingLine.getWarehouseId()));
		}

		if (searchStagingLine.getPreInboundNo() != null && !searchStagingLine.getPreInboundNo().isEmpty()) {
			final Path<Group> group = root.<Group>get("preInboundNo");
			predicates.add(group.in(searchStagingLine.getPreInboundNo()));
		}

		if (searchStagingLine.getRefDocNumber() != null && !searchStagingLine.getRefDocNumber().isEmpty()) {
			final Path<Group> group = root.<Group>get("refDocNumber");
			predicates.add(group.in(searchStagingLine.getRefDocNumber()));
		}

		if (searchStagingLine.getStagingNo() != null && !searchStagingLine.getStagingNo().isEmpty()) {
			final Path<Group> group = root.<Group>get("stagingNo");
			predicates.add(group.in(searchStagingLine.getStagingNo()));
		}

		if (searchStagingLine.getPalletCode() != null && !searchStagingLine.getPalletCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("palletCode");
			predicates.add(group.in(searchStagingLine.getPalletCode()));
		}

		if (searchStagingLine.getCaseCode() != null && !searchStagingLine.getCaseCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("caseCode");
			predicates.add(group.in(searchStagingLine.getCaseCode()));
		}

		if (searchStagingLine.getLineNo() != null && !searchStagingLine.getLineNo().isEmpty()) {
			final Path<Group> group = root.<Group>get("lineNo");
			predicates.add(group.in(searchStagingLine.getLineNo()));
		}

		if (searchStagingLine.getItemCode() != null && !searchStagingLine.getItemCode().isEmpty()) {
			final Path<Group> group = root.<Group>get("itemCode");
			predicates.add(group.in(searchStagingLine.getItemCode()));
		}

		if (searchStagingLine.getStatusId() != null && !searchStagingLine.getStatusId().isEmpty()) {
			final Path<Group> group = root.<Group>get("statusId");
			predicates.add(group.in(searchStagingLine.getStatusId()));
		}

		if (searchStagingLine.getPartner_item_barcode() != null && !searchStagingLine.getPartner_item_barcode().isEmpty()) {
			final Path<Group> group = root.<Group>get("partner_item_barcode");
			predicates.add(group.in(searchStagingLine.getPartner_item_barcode()));
		}

		return cb.and(predicates.toArray(new Predicate[] {}));
	}
}
