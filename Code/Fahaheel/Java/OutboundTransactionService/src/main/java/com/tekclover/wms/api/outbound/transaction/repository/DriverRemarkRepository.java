package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.driverremark.DriverRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DriverRemarkRepository extends JpaRepository<DriverRemark, Long>, JpaSpecificationExecutor<DriverRemark> {

    public List<DriverRemark> findAll();

    Optional<DriverRemark> findByPreOutboundNoAndRefDocNumber(String preOutboundNo, String refDocNumber);


    // Updating outboundHeader Table fields
    @Modifying
    @Query(value = "UPDATE tbloutboundheader SET REMARK = :remarks, DRIVER_NAME = :driverName, REF_FIELD_5 = :referenceField5, \n" +
            "VEHICLE_NO = :vehicleNO WHERE PRE_OB_NO = :preOutboundNo and REF_DOC_NO = :refDocNumber and \n" +
            "IS_DELETED = 0", nativeQuery = true)
    void updateDriverRemarks(@Param("preOutboundNo") String preOutboundNo,
                             @Param("refDocNumber") String refDocNumber,
                             @Param("driverName") String driverName,
                             @Param("remarks") String remarks,
                             @Param("vehicleNO") String vehicleNO,
                             @Param("referenceField5") String referenceField5);


    // Updating outboundHeader Table fields
    @Modifying
    @Query(value = "UPDATE tbloutboundline SET REMARKS = :remark, DRIVER_NAME = :driverName,  REF_FIELD_5 = :referenceField5, \n" +
            "VEHICLE_NO = :vehicleNO WHERE PRE_OB_NO = :preOutboundNo and REF_DOC_NO = :refDocNumber and \n" +
            "IS_DELETED = 0", nativeQuery = true)
    void updateDriverRemarkModify(@Param("preOutboundNo") String preOutboundNo,
                             @Param("refDocNumber") String refDocNumber,
                             @Param("driverName") String driverName,
                             @Param("remark") String remark,
                             @Param("vehicleNO") String vehicleNO,
                             @Param("referenceField5") String referenceField5);
}


