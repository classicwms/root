package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.packing.PackingHeader;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.TemporalType;

@Repository
@Transactional
public interface PackingHeaderRepository extends JpaRepository<PackingHeader, Long>, JpaSpecificationExecutor<PackingHeader> {

    public List<PackingHeader> findAll();

    public Optional<PackingHeader>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndQualityInspectionNoAndPackingNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String qualityInspectionNo, String packingNo, Long deletionIndicator);

    public Optional<PackingHeader> findByPackingNo(String packingNo);


    @Modifying
    @Query(value = "Update tblpackingheader set status_id = :statusId, status_desc = :statusText where is_deleted = 0 and ref_doc_no = :refDocNo", nativeQuery = true)
    void updatePackingHeader(@Param("statusId") Long statusId,
                             @Param("statusText") String statusText,
                             @Param("refDocNo") String refDocNo);


    @Query(value = "select  SUM(TPL_CBM) totalCbm, sum(RATE) totalRate, min(currency) currency from tblpackingline " +
            "where  c_id = :companyId and plant_id = :plantId " +
            "and wh_id = :warehouseId and partner_code = :partnerCode And (PROFORMA_INVOICE_NO is null or PROFORMA_INVOICE_NO =0) " +
            "AND (:startCreatedOn IS NULL OR PACK_CNF_ON >= :startCreatedOn) " +
            "AND (:endCreatedOn IS NULL OR PACK_CNF_ON <= :endCreatedOn)",
            nativeQuery = true)
    IKeyValuePair getCbm(@Param("companyId") String companyId,
                         @Param("plantId") String plantId,
                         @Param("warehouseId") String warehouseId,
                         @Param("partnerCode") String partnerCode,
                         @Param("startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
                         @Param("endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn);
}