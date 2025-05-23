package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.variant.LevelReferenceVariant;
import com.tekclover.wms.api.enterprise.repository.LevelReferenceVariantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LevelReferenceVariantService {

    @Autowired
    private LevelReferenceVariantRepository levelReferenceVariantRepository;

    public List<LevelReferenceVariant> getAllLevelReference() {
        try {
            List<LevelReferenceVariant> LevelReferenceList = levelReferenceVariantRepository.findAll();
            LevelReferenceList = LevelReferenceList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            return LevelReferenceList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getReferenceField3
     *
     * @param variantId
     * @return
     */
    public List<LevelReferenceVariant> getLevelReferenceVariant(String variantId) {
        try {
            List<LevelReferenceVariant> levelReferences = levelReferenceVariantRepository.findByVariantIdAndDeletionIndicator(variantId, 0L);
            if (levelReferences.isEmpty()) {
                return null;
            }
            //levelReferences = levelReferences.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            return levelReferences;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * deleteLevelReferences
     */
    public void deleteLevelReferences(String variantId, String loginUserID) {
        try {
            List<LevelReferenceVariant> levelReferences = getLevelReferenceVariant(variantId);
            if (levelReferences != null) {
                for (LevelReferenceVariant newLevelReference : levelReferences) {
                    newLevelReference.setDeletionIndicator(1L);
                    newLevelReference.setUpdatedBy(loginUserID);
                    newLevelReference.setUpdatedOn(new Date());
                    levelReferenceVariantRepository.save(newLevelReference);
                }
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + variantId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}