package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.v2.OutboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface OutboundOrderV2Repository extends JpaRepository<OutboundOrderV2, Long>,
        JpaSpecificationExecutor<OutboundOrderV2>,
        StreamableJpaSpecificationRepository<OutboundOrderV2> {

    @Query(value = "select \n"
            + "tc.c_text AS companyDesc,\n"
            + "tp.plant_text AS plantDesc,\n"
            + "tw.wh_text AS warehouseDesc \n"
            + "from tblcompanyid tc \n"
            + "join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id \n"
            + "join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id = tc.lang_id and tw.plant_id = tp.plant_id \n"
            + "where \n"
            + "tc.c_id IN (:companyCodeId) and \n"
            + "tp.plant_id IN (:plantId) and \n"
            + "tw.wh_id IN (:warehouseId) \n", nativeQuery = true)
    IKeyValuePair getV2Description(@Param(value = "companyCodeId") String companyCodeId,
                                   @Param(value = "plantId") String plantId,
                                   @Param(value = "warehouseId") String warehouseId);


}
