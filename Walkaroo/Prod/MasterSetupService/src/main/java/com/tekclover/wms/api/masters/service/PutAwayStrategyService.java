package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.putawaystrategy.AddPutAwayStrategy;
import com.tekclover.wms.api.masters.model.putawaystrategy.FindPutAwayStrategy;
import com.tekclover.wms.api.masters.model.putawaystrategy.PutAwayStrategy;
import com.tekclover.wms.api.masters.model.putawaystrategy.UpdatePutAwayStrategy;
import com.tekclover.wms.api.masters.repository.PutAwayStrategyRepository;
import com.tekclover.wms.api.masters.repository.specification.PutAwayStrategySpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PutAwayStrategyService {

    @Autowired
    private PutAwayStrategyRepository putAwayStrategyRepository;

    /**
     * Get PutAwayStrategy
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param brand
     * @param article
     * @param category
     * @return
     */
    public PutAwayStrategy getPutAwayStrategy (String  languageId, String companyCodeId, String plantId, String warehouseId, String brand, String article, String category) {
        Optional<PutAwayStrategy> dbPutAwayStrategy = putAwayStrategyRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndBrandAndArticleAndCategoryAndDeletionIndicator(
                languageId,
                companyCodeId,
                plantId,
                warehouseId,
                brand,
                article,
                category,
                0L
        );
        if(dbPutAwayStrategy.isEmpty()) {
            throw new BadRequestException("The given Values : " +
                    " languageId " + languageId +
                    " companyCodeId " + companyCodeId +
                    " plantId " + plantId +
                    " warehouseId " + warehouseId +
                    " brand " + brand +
                    " article " + article +
                    " category " + category +" doesn't exist.");
        }
        return dbPutAwayStrategy.get();
    }


    /**
     *
     * @param findPutAwayStrategy
     * @return
     * @throws Exception
     */
    public List<PutAwayStrategy> findPutAwayStrategy(FindPutAwayStrategy findPutAwayStrategy)
            throws Exception {
        PutAwayStrategySpecification spec = new PutAwayStrategySpecification(findPutAwayStrategy);
        List<PutAwayStrategy> results = putAwayStrategyRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     *
     * @param newPutAwayStrategy
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public PutAwayStrategy createPutAwayStrategy (AddPutAwayStrategy newPutAwayStrategy, String loginUserID)
            throws Exception {
        try {
            PutAwayStrategy dbPutAwayStrategy = new PutAwayStrategy();
            Optional<PutAwayStrategy> duplicatePutAwayStrategy = putAwayStrategyRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndBrandAndArticleAndCategoryAndDeletionIndicator(
                    newPutAwayStrategy.getLanguageId(),
                    newPutAwayStrategy.getCompanyCodeId(),
                    newPutAwayStrategy.getPlantId(),
                    newPutAwayStrategy.getWarehouseId(),
                    newPutAwayStrategy.getBrand(),
                    newPutAwayStrategy.getArticle(),
                    newPutAwayStrategy.getCategory(),
                    0L);
            if (!duplicatePutAwayStrategy.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newPutAwayStrategy, dbPutAwayStrategy, CommonUtils.getNullPropertyNames(newPutAwayStrategy));
                IKeyValuePair iKeyValuePair = putAwayStrategyRepository.getDescription(dbPutAwayStrategy.getLanguageId(), dbPutAwayStrategy.getCompanyCodeId(), dbPutAwayStrategy.getPlantId(), dbPutAwayStrategy.getWarehouseId());
                if (iKeyValuePair != null) {
                    dbPutAwayStrategy.setLanguageIdAndDescription(iKeyValuePair.getLanguageId());
                    dbPutAwayStrategy.setCompanyIdAndDescription(iKeyValuePair.getCompanyDesc());
                    dbPutAwayStrategy.setPlantIdAndDescription(iKeyValuePair.getPlantDesc());
                    dbPutAwayStrategy.setWarehouseIdAndDescription(iKeyValuePair.getWarehouseDesc());
                }
                dbPutAwayStrategy.setDeletionIndicator(0L);
                dbPutAwayStrategy.setCreatedBy(loginUserID);
                dbPutAwayStrategy.setUpdatedBy(loginUserID);
                dbPutAwayStrategy.setCreatedOn(new Date());
                dbPutAwayStrategy.setUpdatedOn(new Date());
                return putAwayStrategyRepository.save(dbPutAwayStrategy);
            }
        } catch (Exception e) {
            throw new BadRequestException("PutAwayStrategy Create Error" + e);
        }
    }

    /**
     *
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param brand
     * @param article
     * @param category
     * @param updatePutAwayStrategy
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public PutAwayStrategy updatePutAwayStrategy (String languageId, String companyCodeId, String plantId, String warehouseId,
                                          String brand, String article, String category, UpdatePutAwayStrategy updatePutAwayStrategy, String loginUserID)
            throws Exception{
        try {
            PutAwayStrategy dbPutAwayStrategy = getPutAwayStrategy(languageId, companyCodeId, plantId, warehouseId, brand, article, category);
            BeanUtils.copyProperties(updatePutAwayStrategy, dbPutAwayStrategy, CommonUtils.getNullPropertyNames(updatePutAwayStrategy));
            dbPutAwayStrategy.setUpdatedBy(loginUserID);
            dbPutAwayStrategy.setUpdatedOn(new Date());
            return putAwayStrategyRepository.save(dbPutAwayStrategy);
        } catch (Exception e) {
            throw new BadRequestException("PutAwayStrategy Update Error" + e);
        }
    }

    /**
     *
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param brand
     * @param article
     * @param category
     * @param loginUserID
     * @throws ParseException
     */
    public void deletePutAwayStrategy (String languageId, String companyCodeId, String plantId, String warehouseId,
                               String brand, String article, String category, String loginUserID) throws ParseException {
        PutAwayStrategy dbPutAwayStrategy = getPutAwayStrategy(languageId, companyCodeId, plantId, warehouseId, brand, article, category);
        if ( dbPutAwayStrategy != null) {
            dbPutAwayStrategy.setDeletionIndicator (1L);
            dbPutAwayStrategy.setUpdatedBy(loginUserID);
            dbPutAwayStrategy.setUpdatedOn(new Date());
            putAwayStrategyRepository.save(dbPutAwayStrategy);
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" +brand);
        }
    }
}
