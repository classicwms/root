package com.tekclover.wms.api.enterprise.transaction.repository;//package com.tekclover.wms.api.enterprise.transaction.repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.tekclover.wms.api.enterprise.transaction.model.outbound.packing.PackingLine;
//
//@Repository
//@Transactional
//public interface PackingLineRepository extends JpaRepository<PackingLine,Long>, JpaSpecificationExecutor<PackingLine> {
//	
//	public List<PackingLine> findAll();
//	
//	public Optional<PackingLine> 
//		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPackingNoAndItemCodeAndDeletionIndicator(
//				String languageId, Long companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String packingNo, String itemCode, Long deletionIndicator);
//	
//	public Optional<PackingLine> findByItemCode(String itemCode);
//}