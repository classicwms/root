package com.tekclover.wms.api.inbound.orders.repository;


import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.cyclecount.periodic.v2.PeriodicLineTempV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PeriodicLineTempV2Repository extends JpaRepository<PeriodicLineTempV2, Long> {

    @Query(value = "SELECT SUM(CTD_QTY) inventoryQty, itm_code itemCode, mfr_name manufacturerName, \n"
            + "ref_no referenceCycleCountNo, sc_line_no lineNumber FROM tblperiodictempline WHERE WH_ID = :warehouseId AND CC_NO = :cycleCountNo AND\r\n"
            + "C_ID = :companyCode AND PLANT_ID = :plantId AND LANG_ID = :languageId AND IS_DELETED = 0 \r\n"
            + "GROUP BY itm_code,mfr_name,ref_no,sc_line_no", nativeQuery = true)
    public List<IKeyValuePair> getPeriodicHeader(@Param("warehouseId") String warehouseId,
                                                 @Param("companyCode") String companyCode,
                                                 @Param("plantId") String plantId,
                                                 @Param("cycleCountNo") String cycleCountNo,
                                                 @Param("languageId") String languageId);


    //Truncate Table
    @Modifying
    @Query(value = "truncate table tblperiodictempline", nativeQuery = true)
    public void truncateTblperiodictempline();

}
