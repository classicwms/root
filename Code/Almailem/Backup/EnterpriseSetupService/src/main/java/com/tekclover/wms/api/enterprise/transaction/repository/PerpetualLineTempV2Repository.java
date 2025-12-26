package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.enterprise.transaction.model.cyclecount.perpetual.v2.PerpetualLineTempV2;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PerpetualLineTempV2Repository extends JpaRepository<PerpetualLineTempV2, Long>,
        JpaSpecificationExecutor<PerpetualLineTempV2>, StreamableJpaSpecificationRepository<PerpetualLineTempV2> {

    @Query(value = "SELECT SUM(CTD_QTY) inventoryQty, itm_code itemCode, mfr_name manufacturerName, \n"
            + "ref_no referenceCycleCountNo, sc_line_no lineNumber FROM tblperpetualtempline \n"
            + "WHERE WH_ID = :warehouseId AND CC_NO = :cycleCountNo AND\r\n"
            + "C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND IS_DELETED = 0 \r\n"
            + "GROUP BY itm_code,mfr_name,ref_no,sc_line_no", nativeQuery = true)
    public List<IKeyValuePair> getPickupLineCount(@Param("warehouseId") String warehouseId,
                                                  @Param("companyCodeId") String companyCodeId,
                                                  @Param("plantId") String plantId,
                                                  @Param("cycleCountNo") String cycleCountNo,
                                                  @Param("languageId") String languageId);

    //Truncate Table
    @Modifying
    @Query(value = "truncate table tblperpetualtempline", nativeQuery = true)
    public void truncateTblperpetualtempline();
}