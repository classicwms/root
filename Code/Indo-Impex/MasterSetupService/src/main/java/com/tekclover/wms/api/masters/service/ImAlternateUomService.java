package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imalternateuom.AddImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.ImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.SearchImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.UpdateImAlternateUom;
import com.tekclover.wms.api.masters.repository.ImAlternateUomRepository;
import com.tekclover.wms.api.masters.repository.specification.ImAlternateUomSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImAlternateUomService {

    @Autowired
    private ImAlternateUomRepository imalternateuomRepository;

    /**
     * getImAlternateUoms
     *
     * @return
     */
    public List<ImAlternateUom> getImAlternateUoms() {
        try {
            List<ImAlternateUom> imalternateuomList = imalternateuomRepository.findAll();
//		log.info("imalternateuomList : " + imalternateuomList);
            imalternateuomList = imalternateuomList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
            return imalternateuomList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getImAlternateUom
     *
     * @param alternateUom
     * @return
     */
    public List<ImAlternateUom> getImAlternateUom(String alternateUom, String companyCodeId, String plantId, String warehouseId, String itemCode, String uomId, String languageId) {
        try {
            List<ImAlternateUom> imalternateuom = imalternateuomRepository.
                    findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndDeletionIndicator(
                            alternateUom,
                            companyCodeId,
                            plantId,
                            warehouseId,
                            itemCode,
                            uomId,
                            languageId,
                            0L);
            if (imalternateuom.isEmpty()) {
                throw new BadRequestException("The given values:" +
                        "imAlternateUom " + alternateUom +
                        "itemCode" + itemCode +
                        "plantId" + plantId +
                        "companyCodeId" + companyCodeId + " doesn't exist.");
            }
            return imalternateuom;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findImAlternateUom
     *
     * @param searchImAlternateUom
     * @return
     */
    public List<ImAlternateUom> findImAlternateUom(SearchImAlternateUom searchImAlternateUom) {
        try {
            ImAlternateUomSpecification spec = new ImAlternateUomSpecification(searchImAlternateUom);
            List<ImAlternateUom> results = imalternateuomRepository.findAll(spec);
//		log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createImAlternateUom
     *
     * @param newImAlternateUom
     * @return
     */
    public List<ImAlternateUom> createImAlternateUom(List<AddImAlternateUom> newImAlternateUom, String loginUserID) {
        try {
            List<ImAlternateUom> dbImAlternateUomList = new ArrayList<>();

            for (AddImAlternateUom newAlternateUom : newImAlternateUom) {
                List<ImAlternateUom> duplicateImAlternateUom = imalternateuomRepository.
                        findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndDeletionIndicator(
                                newAlternateUom.getAlternateUom(), newAlternateUom.getCompanyCodeId(),
                                newAlternateUom.getPlantId(), newAlternateUom.getWarehouseId(),
                                newAlternateUom.getItemCode(), newAlternateUom.getUomId(),
                                newAlternateUom.getLanguageId(), 0L);

                if (!duplicateImAlternateUom.isEmpty()) {
                    throw new EntityNotFoundException("Record is Getting Duplicated");
                } else {
                    ImAlternateUom dbImAlternateUom = new ImAlternateUom();
                    BeanUtils.copyProperties(newAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(newAlternateUom));

                    dbImAlternateUom.setId(System.currentTimeMillis());
                    dbImAlternateUom.setDeletionIndicator(0L);
                    dbImAlternateUom.setCreatedBy(loginUserID);
                    dbImAlternateUom.setUpdatedBy(loginUserID);
                    dbImAlternateUom.setCreatedOn(new Date());
                    dbImAlternateUom.setUpdatedOn(new Date());
                    ImAlternateUom savedImAlternate = imalternateuomRepository.save(dbImAlternateUom);
                    dbImAlternateUomList.add(savedImAlternate);
                }
            }
            return dbImAlternateUomList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

//	}	 */
//	public List<ImAlternateUom> createImAlternateUom (List<AddImAlternateUom> newImAlternateUom, String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//			Optional<ImAlternateUom> duplicateImAlternateUom = imalternateuomRepository.findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndDeletionIndicator(newImAlternateUom.getAlternateUom(), newImAlternateUom.getCompanyCodeId(), newImAlternateUom.getPlantId(), newImAlternateUom.getWarehouseId(), newImAlternateUom.getItemCode(), newImAlternateUom.getUomId(), newImAlternateUom.getLanguageId(), 0L);
//			ImAlternateUom imAlternateUom=new ImAlternateUom();
//			if (!duplicateImAlternateUom.isEmpty()) {
//				throw new EntityNotFoundException("Record is Getting Duplicated");
//			} else {
//				ImAlternateUom dbImAlternateUom=new ImAlternateUom();
//				BeanUtils.copyProperties(newImAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(newImAlternateUom));
//				dbImAlternateUom.setDeletionIndicator(0L);
//				dbImAlternateUom.setCreatedBy(loginUserID);
//				dbImAlternateUom.setUpdatedBy(loginUserID);
//				dbImAlternateUom.setCreatedOn(new Date());
//				dbImAlternateUom.setUpdatedOn(new Date());
//				//return imalternateuomRepository.save(dbImAlternateUom);
////				ImAlternateUom savedImAlternateUom=imalternateuomRepository.save(dbImAlternateUom);
//
//			}
//
//
//	}

    /**
     * updateImAlternateUom
     *
     * @param alternateUom
     * @param updateImAlternateUom
     * @return
     */
    public List<ImAlternateUom> updateImAlternateUom(String alternateUom, String companyCodeId, String plantId,
                                                     String warehouseId, String itemCode, String uomId, String languageId,
                                                     List<UpdateImAlternateUom> updateImAlternateUom, String loginUserID) {
        try {
            List<ImAlternateUom> dbImAlternateUomList = new ArrayList<>();
            log.info("updateImAlternateUom : " + updateImAlternateUom + "|" + companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + uomId);

                List<ImAlternateUom> dbImAlternateUom = imalternateuomRepository.
                        findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndLanguageIdAndDeletionIndicator(
                            companyCodeId, plantId, warehouseId, itemCode, languageId, 0L);

                if (dbImAlternateUom != null && !dbImAlternateUom.isEmpty()) {
                    imalternateuomRepository.deleteAll(dbImAlternateUom);
                    log.info("altUom deleted successfully for update process");
                    }

            for (UpdateImAlternateUom newUpdateImAlternateUom : updateImAlternateUom) {
                    ImAlternateUom newImAlternateUom = new ImAlternateUom();
                        BeanUtils.copyProperties(newUpdateImAlternateUom, newImAlternateUom, CommonUtils.getNullPropertyNames(newUpdateImAlternateUom));
                        newImAlternateUom.setUomId(uomId);
                newImAlternateUom.setId(System.currentTimeMillis());
                        newImAlternateUom.setDeletionIndicator(0L);
                        newImAlternateUom.setCreatedBy(loginUserID);
                        newImAlternateUom.setUpdatedBy(loginUserID);
                        newImAlternateUom.setCreatedOn(new Date());
                        newImAlternateUom.setUpdatedOn(new Date());
                    ImAlternateUom created = imalternateuomRepository.save(newImAlternateUom);
                    log.info("created altUOM : " + created);
                    dbImAlternateUomList.add(created);
            }
            return dbImAlternateUomList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     //		 * updateImAlternateUom
     //		 * @param alternateUom
     //		 * @param updateImAlternateUom
     //		 * @return
     //		 * @throws IllegalAccessException
     //		 * @throws InvocationTargetException
     //		 */
//		public ImAlternateUom updateImAlternateUom (String alternateUom,String companyCodeId,String plantId,String warehouseId,String itemCode,String uomId,String languageId, UpdateImAlternateUom updateImAlternateUom, String loginUserID)
//            throws IllegalAccessException, InvocationTargetException {
//				ImAlternateUom dbImAlternateUom = getImAlternateUom(alternateUom, companyCodeId, plantId, warehouseId, itemCode, uomId, languageId);
//				BeanUtils.copyProperties(updateImAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(updateImAlternateUom));
//				dbImAlternateUom.setUpdatedBy(loginUserID);
//				dbImAlternateUom.setUpdatedOn(new Date());
//				return imalternateuomRepository.save(dbImAlternateUom);
//		}

    /**
     * deleteImAlternateUom
     *
     * @param alternateUom
     */
    public void deleteImAlternateUom(String alternateUom, String companyCodeId, String plantId, String warehouseId,
                                     String itemCode, String uomId, String languageId, String loginUserID) {
        try {
            List<ImAlternateUom> imalternateuom = getImAlternateUom(alternateUom, companyCodeId, plantId,
                    warehouseId, itemCode, uomId, languageId);
            if (imalternateuom != null) {
                for (ImAlternateUom dbImAlternateUom : imalternateuom) {
                    dbImAlternateUom.setDeletionIndicator(1L);
                    dbImAlternateUom.setUpdatedBy(loginUserID);
                    dbImAlternateUom.setUpdatedOn(new Date());
                    imalternateuomRepository.save(dbImAlternateUom);
                }
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + uomId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}