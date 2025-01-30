package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.enterprise.transaction.model.mnc.InhouseTransferLine;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface InhouseTransferLineRepository extends JpaRepository<InhouseTransferLine, Long>,
        JpaSpecificationExecutor<InhouseTransferLine>, StreamableJpaSpecificationRepository<InhouseTransferLine> {

    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    List<InhouseTransferLine> findAll();

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param transferNumber
     * @param sourceItemCode
     * @param deletionIndicator
     * @return
     */
    Optional<InhouseTransferLine>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferNumberAndSourceItemCodeAndDeletionIndicator(
            String languageId,
            String companyCodeId,
            String plantId,
            String warehouseId,
            String transferNumber,
            String sourceItemCode,
            Long deletionIndicator);

    // Description
    @Query(value = "Select \n" +
            "tc.c_text as companyDesc,\n" +
            "tp.plant_text as plantDesc,\n" +
            "tw.wh_text as warehouseDesc from \n" +
            "tblcompanyid tc\n" +
            "join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id \n" +
            "join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id = tc.lang_id and tw.plant_id = tp.plant_id \n" +
            "where\n" +
            "tc.lang_id IN (:languageId) and \n" +
            "tc.c_id IN (:companyCodeId) and \n" +
            "tp.plant_id IN(:plantId) and \n" +
            "tw.wh_id IN (:warehouseId) and \n" +
            "tc.is_deleted = 0 and \n" +
            "tp.is_deleted = 0 and \n" +
            "tw.is_deleted = 0 ", nativeQuery = true)
    IKeyValuePair getDescription(@Param(value = "languageId") String languageId,
                                 @Param(value = "companyCodeId") String companyCodeId,
                                 @Param(value = "plantId") String plantId,
                                 @Param(value = "warehouseId") String warehouseId);
}