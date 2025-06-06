package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.packing.PackingLine;

@Repository
@Transactional
public interface PackingLineRepository extends JpaRepository<PackingLine,String>, JpaSpecificationExecutor<PackingLine> {


	public Optional<PackingLine>
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPackingNoAndItemCodeAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String packingNo, String itemCode, Long deletionIndicator);

	public Optional<PackingLine> findByItemCode(String itemCode);
}