//package com.tekclover.wms.api.transaction.repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.tekclover.wms.api.transaction.model.outbound.packing.PackingHeader;
//
//@Repository
//@Transactional
//public interface PackingHeaderRepository extends JpaRepository<PackingHeader,Long>, JpaSpecificationExecutor<PackingHeader> {
//	
//	public List<PackingHeader> findAll();
//	public Optional<PackingHeader> 
//		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndQualityInspectionNoAndPackingNoAndDeletionIndicator(
//				String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String qualityInspectionNo, String packingNo, Long deletionIndicator);
//	public Optional<PackingHeader> findByPackingNo(String packingNo);
//}