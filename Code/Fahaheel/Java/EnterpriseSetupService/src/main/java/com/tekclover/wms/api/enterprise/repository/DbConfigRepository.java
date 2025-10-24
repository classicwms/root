package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.common.DbConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DbConfigRepository extends JpaRepository<DbConfig, Long> {


    @Query("SELECT dbName from DbConfig where companyCode =?1 and plantId=?2 and warehouseId =?3")
    String getDbName(String companyCodeId, String plantId, String warehouseId);

    @Query("SELECT dbName from DbConfig where companyCode IN ?1 and plantId IN ?2 and warehouseId IN ?3")
    String getDbName1(List<String> companyCodeId, List<String> plantId, List<String> warehouseId);

    @Query("SELECT dbName from DbConfig where companyCode =?1 and plantId=?2")
    String getDbName2(String companyCodeId, String plantId);

//    @Query("SELECT dbName from User where dbId =?1")
//    String getDbName(Long id);
}
