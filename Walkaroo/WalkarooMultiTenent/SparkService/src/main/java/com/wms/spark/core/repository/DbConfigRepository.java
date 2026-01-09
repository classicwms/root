package com.wms.spark.core.repository;



import com.wms.spark.core.model.common.DbConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DbConfigRepository extends JpaRepository<DbConfig, Long> {


    @Query("SELECT dbName from DbConfig where companyCode IN ?1 and plantId IN ?2 and warehouseId IN ?3")
    String getDbName(List<String> companyCodeId, List<String> plantId, List<String> warehouseId);


//    @Query("SELECT dbName from User where dbId =?1")
//    String getDbName(Long id);
}
