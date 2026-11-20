package com.tekclover.wms.api.inbound.transaction.repository;


import com.tekclover.wms.api.inbound.transaction.model.dto.HHTUser;
import com.tekclover.wms.api.inbound.transaction.model.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface PreOutboundHeaderV2Repository extends JpaRepository<PreOutboundHeaderV2, Long>,
        JpaSpecificationExecutor<PreOutboundHeaderV2>, StreamableJpaSpecificationRepository<PreOutboundHeaderV2> {

    @Query(value ="select usr_id \n"+
            " from tblhhtuser \n" +
            " where \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
            "(getdate() between :startDate and :endDate) and \n" +
            " is_deleted = 0 ",nativeQuery = true)
    public List<String> getHhtUserAttendance(@Param("companyCodeId") String companyCodeId,
                                             @Param("languageId") String languageId,
                                             @Param("plantId") String plantId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("userId") String userId,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);

    @Query(value = "select \n" +
            "ht.USR_ID userId,\n" +
            "ht.LANG_ID languageId,\n" +
            "ht.C_ID companyCodeId,\n" +
            "ht.PLANT_ID plantId,\n" +
            "ht.WH_ID warehouseId,\n" +
            "ht.LEVEL_ID levelId,\n" +
            "ht.PASSWORD password,\n" +
            "ht.USER_NM userName,\n" +
            "ht.START_DATE startDate,\n" +
            "ht.END_DATE endDate,\n" +
            "ht.USR_PRESENT userPresent,\n" +
            "ht.NO_OF_DAYS_LEAVE noOfDaysLeave \n" +
            "from tblhhtuser ht \n" +
            "join tblusermanagement um on um.usr_id=ht.usr_id and um.c_id=ht.c_id and um.plant_id=ht.plant_id and um.wh_id=ht.wh_id \n" +
            "where \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "um.usr_typ_id in (:userTypeId) and \n" +
            "ht.is_deleted = 0 ", nativeQuery = true)
    public List<HHTUser> getHHTUserListV3(@Param("companyCodeId") String companyCodeId,
                                          @Param("plantId") String plantId,
                                          @Param("languageId") String languageId,
                                          @Param("warehouseId") String warehouseId,
                                          @Param("userTypeId") Long userTypeId);
}
