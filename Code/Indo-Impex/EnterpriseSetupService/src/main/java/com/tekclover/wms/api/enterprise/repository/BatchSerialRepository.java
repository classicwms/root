package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.batchserial.BatchSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface BatchSerialRepository extends JpaRepository<BatchSerial, Long>, JpaSpecificationExecutor<BatchSerial> {

    public List<BatchSerial> findAll();

    List<BatchSerial> findByStorageMethodAndPlantIdAndCompanyIdAndLanguageIdAndWarehouseIdAndLevelIdAndLevelReferencesAndMaintenanceAndDeletionIndicator(
            String storageMethod, String plantId, String companyId, String languageId, String warehouseId, Long levelId, String levelReferences, String maintenance, Long deletionIndicator);

//	List<BatchSerial> findByStorageMethodAndPlantIdAndCompanyIdAndLanguageIdAndWarehouseIdAndLevelIdAndDeletionIndicator(
//			String storageMethod, String plantId, String companyId, String languageId, String warehouseId, Long levelId, Long deletionIndicator);

    List<BatchSerial> findByStorageMethodAndPlantIdAndCompanyIdAndLanguageIdAndWarehouseIdAndLevelIdAndMaintenanceAndDeletionIndicator(
            String storageMethod, String plantId, String companyId, String languageId, String warehouseId, Long levelId, String maintenance, Long deletionIndicator);

    BatchSerial findByStorageMethodAndPlantIdAndCompanyIdAndLanguageIdAndWarehouseIdAndLevelIdAndMaintenanceAndIdAndDeletionIndicator(
            String storageMethod, String plantId, String companyId, String languageId, String warehouseId, Long levelId, String maintenance, Long id, Long deletionIndicator);

    @Query(value = "select max(ID)+1 \n" +
            " from tblbatchserial ", nativeQuery = true)
    public Long getId();

    @Query(value = "select  tl.level_id AS levelId,tl.level AS description\n" +
            " from tbllevelid tl \n" +
            "WHERE \n" +
            "tl.level_id IN (:levelId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IkeyValuePair getLevelIdAndDescription(@Param(value = "levelId") String levelId,
                                                  @Param(value = "languageId") String languageId,
                                                  @Param(value = "companyCodeId") String companyCodeId,
                                                  @Param(value = "plantId") String plantId,
                                                  @Param(value = "warehouseId") String warehouseId);

    @Query(value = "CREATE TABLE #DESC \n" +
            "(C_ID NVARCHAR(10), \n" +
            "PLANT_ID NVARCHAR(10), \n" +
            "WH_ID NVARCHAR(10), \n" +
            "LANG_ID NVARCHAR(10), \n" +
            "LEVEL_ID NVARCHAR(10), \n" +
//			"VAR_ID NVARCHAR(10), \n"+
            "C_TEXT NVARCHAR(100), \n" +
            "PLANT_TEXT NVARCHAR(100), \n" +
            "WH_TEXT NVARCHAR(100), \n" +
            "LEVEL NVARCHAR(100) \n" +
//			"VAR_ID_TEXT NVARCHAR(100) \n"+
            "PRIMARY KEY(C_ID,LANG_ID)); \n" +


            "INSERT INTO #DESC(C_ID,LANG_ID,PLANT_ID,WH_ID,LEVEL_ID) VALUES \n" +
            "(:companyCodeId,:languageId,:plantId,:warehouseId,:levelId) \n" +

            "UPDATE TH SET TH.C_TEXT = X.C_TEXT FROM #DESC TH INNER JOIN \n" +
            "(SELECT C_ID,C_TEXT,LANG_ID FROM TBLCOMPANYID WHERE  \n" +
            "C_ID=:companyCodeId AND LANG_ID=:languageId) X ON  \n" +
            "X.C_ID=TH.C_ID AND X.LANG_ID=TH.LANG_ID \n" +

            "UPDATE TH SET TH.PLANT_TEXT = X.PLANT_TEXT FROM #DESC TH INNER JOIN \n" +
            "(SELECT C_ID,LANG_ID,PLANT_ID,PLANT_TEXT FROM TBLPLANTID WHERE  \n" +
            "C_ID=:companyCodeId AND LANG_ID=:languageId AND PLANT_ID=:plantId) X ON  \n" +
            "X.C_ID=TH.C_ID AND X.LANG_ID=TH.LANG_ID AND X.PLANT_ID=TH.PLANT_ID \n" +

            "UPDATE TH SET TH.WH_TEXT = X.WH_TEXT FROM #DESC TH INNER JOIN \n" +
            "(SELECT C_ID,LANG_ID,PLANT_ID,WH_ID,WH_TEXT FROM TBLWAREHOUSEID WHERE  \n" +
            "C_ID=:companyCodeId AND LANG_ID=:languageId AND PLANT_ID=:plantId AND WH_ID=:warehouseId) X ON  \n" +
            "X.C_ID=TH.C_ID AND X.LANG_ID=TH.LANG_ID AND X.PLANT_ID=TH.PLANT_ID AND X.WH_ID=TH.WH_ID \n" +

            "UPDATE TH SET TH.LEVEL = X.LEVEL FROM #DESC TH INNER JOIN \n" +
            "(SELECT C_ID,LANG_ID,PLANT_ID,WH_ID,LEVEL_ID,LEVEL FROM TBLLEVELID WHERE  \n" +
            "C_ID=:companyCodeId AND LANG_ID=:languageId AND PLANT_ID=:plantId AND WH_ID=:warehouseId AND LEVEL_ID = :levelId) X ON  \n" +
            "X.C_ID=TH.C_ID AND X.LANG_ID=TH.LANG_ID AND X.PLANT_ID=TH.PLANT_ID AND X.WH_ID=TH.WH_ID AND X.LEVEL_ID=TH.LEVEL_ID \n" +

//			"UPDATE TH SET TH.VAR_ID_TEXT = X.VAR_ID_TEXT FROM #DESC TH INNER JOIN \n"+
//			"(SELECT C_ID,LANG_ID,PLANT_ID,WH_ID,VAR_ID,VAR_ID_TEXT FROM TBLVARIANTID WHERE  \n"+
//			"C_ID=:companyCodeId AND LANG_ID=:languageId AND PLANT_ID=:plantId AND WH_ID=:warehouseId AND VAR_ID = :variantId) X ON  \n"+
//			"X.C_ID=TH.C_ID AND X.LANG_ID=TH.LANG_ID AND X.PLANT_ID=TH.PLANT_ID AND X.WH_ID=TH.WH_ID AND X.VAR_ID=TH.VAR_ID \n"+

            "SELECT c_id companyCodeId,\n" +
            "plant_id plantId,\n" +
            "wh_id warehouseId,\n" +
            "level_id levelId,\n" +
//			"var_id variantId,\n"+
            "c_text companyDescription,\n" +
            "plant_text plantDescription,\n" +
            "wh_text warehouseDescription,\n" +
            "level levelDescription \n" +
//			"var_id_text variantDescription\n"+
            "FROM #DESC", nativeQuery = true)
    public IkeyValuePair findDescription(@Param(value = "companyCodeId") String companyCodeId,
                                         @Param(value = "plantId") String plantId,
                                         @Param(value = "warehouseId") String warehouseId,
                                         @Param(value = "languageId") String languageId,
                                         @Param(value = "levelId") Long levelId);
//										 @Param(value="variantId") String variantId

    @Query(value = "SELECT x1.c_id companyCodeId,\n" +
            "x2.plant_id plantId,\n" +
            "x3.wh_id warehouseId,\n" +
            "x4.level_id levelId,\n" +
            "x1.c_text companyDescription,\n" +
            "x2.plant_text plantDescription,\n" +
            "x3.wh_text warehouseDescription,\n" +
            "x4.level levelDescription\n" +
            "FROM tblcompanyid x1\n" +
            "join tblplantid x2 on x2.c_id=x1.c_id and x2.lang_id=x1.lang_id\n" +
            "join tblwarehouseid x3 on x3.c_id=x1.c_id and x3.lang_id=x1.lang_id and x3.plant_id=x2.plant_id\n" +
            "join tbllevelid x4 on x4.c_id=x1.c_id and x4.lang_id=x1.lang_id and x4.plant_id=x2.plant_id and x4.wh_id=x3.wh_id\n" +
            "where x1.c_id=:companyCodeId and x1.lang_id = :languageId and x2.plant_id=:plantId \n" +
            "and x3.wh_id=:warehouseId and x4.level_id=:levelId and\n" +
            "x1.is_deleted=0 and x2.is_deleted=0 and x3.is_deleted=0 and x4.is_deleted=0", nativeQuery = true)
    public IkeyValuePair getDescription(@Param(value = "companyCodeId") String companyCodeId,
                                        @Param(value = "plantId") String plantId,
                                        @Param(value = "warehouseId") String warehouseId,
                                        @Param(value = "languageId") String languageId,
                                        @Param(value = "levelId") Long levelId);

    @Query(value = "SELECT concat(x1.c_id,'-',x1.c_text companyDescription,\n" +
            "concat(x2.plant_id,'-',x2.plant_text) plantDescription,\n" +
            "concat(x3.wh_id,'-',x3.wh_text) warehouseDescription,\n" +
            "concat(x4.level_id,'-',x4.level) levelDescription\n" +
            "FROM tblcompanyid x1\n" +
            "join tblplantid x2 on x2.c_id=x1.c_id and x2.lang_id=x1.lang_id\n" +
            "join tblwarehouseid x3 on x3.c_id=x1.c_id and x3.lang_id=x1.lang_id and x3.plant_id=x2.plant_id\n" +
            "join tbllevelid x4 on x4.c_id=x1.c_id and x4.lang_id=x1.lang_id and x4.plant_id=x2.plant_id and x4.wh_id=x3.wh_id\n" +
            "where x1.c_id=:companyCodeId and x1.lang_id = :languageId and x2.plant_id=:plantId \n" +
            "and x3.wh_id=:warehouseId and x4.level_id=:levelId and\n" +
            "x1.is_deleted=0 and x2.is_deleted=0 and x3.is_deleted=0 and x4.is_deleted=0", nativeQuery = true)
    public IkeyValuePair getIdDescription(@Param(value = "companyCodeId") String companyCodeId,
                                          @Param(value = "plantId") String plantId,
                                          @Param(value = "languageId") String languageId,
                                          @Param(value = "warehouseId") String warehouseId,
                                          @Param(value = "levelId") Long levelId);


}