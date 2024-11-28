package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.plant.Plant;
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
public interface PlantRepository extends JpaRepository<Plant, Long>, JpaSpecificationExecutor<Plant> {

    public List<Plant> findAll();

    Optional<Plant> findByPlantId(String plantId);

    public Optional<Plant> findByLanguageIdAndCompanyIdAndPlantIdAndDeletionIndicator(
            String languageId, String companyId, String plantId, Long deletionIndicator);

    @Query(value = "select  tl.plant_id AS plantId,tl.plant_text AS description\n" +
            " from tblplantid tl \n" +
            "WHERE \n" +
            "tl.plant_id IN (:plantId)and tl.lang_id IN (:languageId) and tl.c_id IN(:companyCodeId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IkeyValuePair getPlantIdAndDescription(@Param(value = "plantId") String plantId,
                                                  @Param(value = "languageId") String languageId,
                                                  @Param(value = "companyCodeId") String companyCodeId);
}