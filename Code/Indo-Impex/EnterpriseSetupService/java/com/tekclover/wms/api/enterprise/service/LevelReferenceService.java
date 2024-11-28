package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.model.batchserial.LevelReference;
import com.tekclover.wms.api.enterprise.repository.LevelReferenceRepository;
import com.tekclover.wms.api.enterprise.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class LevelReferenceService {
@Autowired
private LevelReferenceRepository levelReferenceRepository;
    public List<LevelReference> getLevelReference () {
        List<LevelReference> LevelReferenceList =  levelReferenceRepository.findAll();
        LevelReferenceList = LevelReferenceList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return LevelReferenceList;
    }

    /**
     * getReferenceField3
     * @param storageMethod
     * @return
     */
    public List<LevelReference> getLevelReferences (String storageMethod) {
        List<LevelReference> levelReferences = levelReferenceRepository.findByStorageMethodAndDeletionIndicator(storageMethod, 0L);
        if (levelReferences.isEmpty()) {
            return null;
        }
        //levelReferences = levelReferences.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return levelReferences;
    }


    /**
     * deleteLevelReferences
     */
    public void deleteLevelReferences (String storageMethod, String loginUserID) throws ParseException {
        List<LevelReference> levelReferences= getLevelReferences(storageMethod);
        if (levelReferences != null) {
            for(LevelReference newLevelReference: levelReferences){
                    newLevelReference.setDeletionIndicator(1L);
                    newLevelReference.setUpdatedBy(loginUserID);
                    newLevelReference.setUpdatedOn(new Date());
                    levelReferenceRepository.save(newLevelReference);
            }
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + storageMethod);
        }
    }
}
