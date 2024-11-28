package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.storageclass.StorageClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StorageClassRepository extends JpaRepository<StorageClass, Long>, JpaSpecificationExecutor<StorageClass> {

    public List<StorageClass> findAll();

    public Optional<StorageClass> findByStorageClassId(Long storageClassId);

    public Optional<StorageClass>
    findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndDeletionIndicator(
            String languageId, String companyId, String plantId, String warehouseId, Long storageClassId,
            Long deletionIndicator);

    @Query(value = "select  tl.st_cl_id AS storageClassId,tl.st_cl_text AS description\n" +
            " from tblstorageclassid tl \n" +
            "WHERE \n" +
            "tl.st_cl_id IN (:storageClassId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IkeyValuePair getStorageClassIdAndDescription(@Param(value = "storageClassId") String storageClassId,
                                                         @Param(value = "languageId") String languageId,
                                                         @Param(value = "companyCodeId") String companyCodeId,
                                                         @Param(value = "plantId") String plantId,
                                                         @Param(value = "warehouseId") String warehouseId);
}