package com.tekclover.wms.api.inbound.transaction.service;


import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.transaction.repository.StorageBinV2Repository;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StorageBinService extends BaseService {

    @Autowired
    private StorageBinV2Repository storageBinV2Repository;


//    /**
//     * @param storageBinPutAway
//     * @return
//     */
//    public StorageBinV2 getaStorageBinV2(StorageBinPutAway storageBinPutAway) {
//
//        StorageBinV2 storagebin = null;
//
//        if (storageBinPutAway.getBinClassId() != null && storageBinPutAway.getStorageSectionIds() != null) {
//            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdAndStorageSectionIdNotInAndDeletionIndicator(
//                    storageBinPutAway.getCompanyCodeId(),
//                    storageBinPutAway.getPlantId(),
//                    storageBinPutAway.getLanguageId(),
//                    storageBinPutAway.getWarehouseId(),
//                    storageBinPutAway.getBin(),
//                    storageBinPutAway.getBinClassId(),
//                    storageBinPutAway.getStorageSectionIds(),
//                    0L);
//            return storagebin;
//        }
//        if (storageBinPutAway.getBinClassId() != null) {
//            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdAndDeletionIndicator(
//                    storageBinPutAway.getCompanyCodeId(),
//                    storageBinPutAway.getPlantId(),
//                    storageBinPutAway.getLanguageId(),
//                    storageBinPutAway.getWarehouseId(),
//                    storageBinPutAway.getBin(),
//                    storageBinPutAway.getBinClassId(),
//                    0L);
//            return storagebin;
//        }
//        if (storageBinPutAway.getBinClassId() == null && storageBinPutAway.getStorageSectionIds() != null) {
//            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndStorageSectionIdNotInAndDeletionIndicator(
//                    storageBinPutAway.getCompanyCodeId(),
//                    storageBinPutAway.getPlantId(),
//                    storageBinPutAway.getLanguageId(),
//                    storageBinPutAway.getWarehouseId(),
//                    storageBinPutAway.getBin(),
//                    storageBinPutAway.getStorageSectionIds(),
//                    0L);
//            return storagebin;
//        }
//        if (storageBinPutAway.getBinClassId() == null) {
//            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
//                    storageBinPutAway.getCompanyCodeId(),
//                    storageBinPutAway.getPlantId(),
//                    storageBinPutAway.getLanguageId(),
//                    storageBinPutAway.getWarehouseId(),
//                    storageBinPutAway.getBin(),
//                    0L);
//            return storagebin;
//        }
//        return null;
//    }
//
//    /**
//     * @param storageBinPutAway
//     * @return
//     * @throws Exception
//     */
//    public StorageBinV2 getStorageBinByBinClassIdV4(StorageBinPutAway storageBinPutAway) throws Exception {
//        try {
//            StorageBinV2 storagebin = null;
//            if (storageBinPutAway.getBinClassId() == 7) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getWarehouseId());
//            }
//            if (storagebin != null) {
//                log.info("BinClassId 7 StorageBin: " + storagebin.getStorageBin());
//                return storagebin;
//            } else {
//                log.info("BinClassId --> 7L StorageBin Unavailable --> Proposing reserve bin ---> BinClassId --> 2L");
//                return storageBinV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndBinClassIdAndDeletionIndicator(
//                        storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(), storageBinPutAway.getWarehouseId(),
//                        2L, 0L);
//            }
//        } catch (Exception e) {
//            log.error("Exception while Bin fetch : " + e.toString());
//            throw e;
//        }
//    }
//
//    /**
//     * checking for KNOWELL db routing
//     * @param storageBinPutAway
//     * @return
//     * @throws Exception
//     */
//    public StorageBinV2 getStorageBinByBinClassIdV7(StorageBinPutAway storageBinPutAway) throws Exception {
//        try {
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("KNOWELL");
//            StorageBinV2 storagebin = null;
//            if (storageBinPutAway.getBinClassId() == 7) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getWarehouseId());
//            }
//            if (storagebin != null) {
//                log.info("BinClassId 7 StorageBin: " + storagebin.getStorageBin());
//                return storagebin;
//            } else {
//                log.info("BinClassId --> 7L StorageBin Unavailable --> Proposing reserve bin ---> BinClassId --> 2L");
//                return storageBinV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndBinClassIdAndDeletionIndicator(
//                        storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(), storageBinPutAway.getWarehouseId(),
//                        2L, 0L);
//            }
//        } catch (Exception e) {
//            log.error("Exception while Bin fetch : " + e.toString());
//            throw e;
//        }
//    }
//
//    /**
//     * @param storageBinPutAway
//     * @return
//     */
//    public StorageBinV2 getExistingProposedStorageBinNonCBM(StorageBinPutAway storageBinPutAway) {
//
//        StorageBinV2 storagebin = null;
//        if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageSectionIds() != null) {
//            storagebin = storageBinV2Repository.getExistingStorageBinNonCBM(
//                    storageBinPutAway.getBinClassId(),
//                    storageBinPutAway.getStorageSectionIds(),
//                    storageBinPutAway.getCompanyCodeId(),
//                    storageBinPutAway.getPlantId(),
//                    storageBinPutAway.getLanguageId(),
//                    storageBinPutAway.getStorageBin(),
//                    storageBinPutAway.getWarehouseId());
//            log.info("Inventory Existing StorageBin: " + storagebin);
//            return storagebin;
//        }
//        if (storageBinPutAway.getBinClassId() != 7) {
//            storagebin = storageBinV2Repository.getExistingStorageBinNonCBM(
//                    storageBinPutAway.getBinClassId(),
//                    storageBinPutAway.getCompanyCodeId(),
//                    storageBinPutAway.getPlantId(),
//                    storageBinPutAway.getLanguageId(),
//                    storageBinPutAway.getStorageBin(),
//                    storageBinPutAway.getWarehouseId());
//            log.info("Inventory Existing StorageBin: " + storagebin);
//            return storagebin;
//        }
//        if (storageBinPutAway.getBinClassId() == 7) {
//            storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
//                    storageBinPutAway.getBinClassId(),
//                    storageBinPutAway.getCompanyCodeId(),
//                    storageBinPutAway.getPlantId(),
//                    storageBinPutAway.getLanguageId(),
//                    storageBinPutAway.getWarehouseId());
//            log.info("Inventory Existing StorageBin: " + storagebin);
//            return storagebin;
//        }
//        return null;
//    }
//
//    /**
//     * @param storageBinPutAway
//     * @return
//     */
//    public StorageBinV2 getProposedStorageBinNonCBM(StorageBinPutAway storageBinPutAway) {
//
//        try {
//            log.info("storageBinPutAway : " + storageBinPutAway);
//            StorageBinV2 storagebin = null;
//            if (storageBinPutAway.getBinClassId() != 7 && (storageBinPutAway.getStorageBin() != null && !storageBinPutAway.getStorageBin().isEmpty()) && storageBinPutAway.getStorageSectionIds() != null) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBM(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getStorageSectionIds(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getStatusId(),
//                        storageBinPutAway.getStorageBin(),
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageSectionIds() != null &&
//                    (storageBinPutAway.getStorageBin() == null || (storageBinPutAway.getStorageBin() != null && storageBinPutAway.getStorageBin().isEmpty()))) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBM(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getStorageSectionIds(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getStorageSectionId(),                //sending storageSectionId in statusId
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageBin() != null && !storageBinPutAway.getStorageBin().isEmpty()) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBM(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getStatusId(),
//                        storageBinPutAway.getStorageBin(),
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            if (storageBinPutAway.getBinClassId() != 7 &&
//                    (storageBinPutAway.getStorageBin() == null || (storageBinPutAway.getStorageBin() != null && storageBinPutAway.getStorageBin().isEmpty()))) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBM(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getStatusId(),
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            if (storageBinPutAway.getBinClassId() == 7) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Exception : " + e);
//        }
//    }
//
//    /**
//     * @param storageBinPutAway
//     * @return
//     */
//    public StorageBinV2 getProposedStorageBinNonCBMV7(StorageBinPutAway storageBinPutAway) {
//
//        try {
//            log.info("storageBinPutAway : " + storageBinPutAway);
//            StorageBinV2 storagebin = null;
//            if (storageBinPutAway.getBinClassId() != 7 && (storageBinPutAway.getStorageBin() != null && !storageBinPutAway.getStorageBin().isEmpty()) && storageBinPutAway.getStorageSectionIds() != null) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBMV7(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getStorageSectionIds(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getStatusId(),
//                        storageBinPutAway.getStorageBin(),
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageSectionIds() != null &&
//                    (storageBinPutAway.getStorageBin() == null || (storageBinPutAway.getStorageBin() != null && storageBinPutAway.getStorageBin().isEmpty()))) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBM(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getStorageSectionIds(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getStorageSectionId(),                //sending storageSectionId in statusId
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageBin() != null && !storageBinPutAway.getStorageBin().isEmpty()) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBM(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getStatusId(),
//                        storageBinPutAway.getStorageBin(),
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            if (storageBinPutAway.getBinClassId() != 7 &&
//                    (storageBinPutAway.getStorageBin() == null || (storageBinPutAway.getStorageBin() != null && storageBinPutAway.getStorageBin().isEmpty()))) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBM(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getStatusId(),
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            if (storageBinPutAway.getBinClassId() == 7) {
//                storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
//                        storageBinPutAway.getBinClassId(),
//                        storageBinPutAway.getCompanyCodeId(),
//                        storageBinPutAway.getPlantId(),
//                        storageBinPutAway.getLanguageId(),
//                        storageBinPutAway.getWarehouseId());
//                return storagebin;
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Exception : " + e);
//        }
//    }
//
//    /**
//     * @param warehouseId
//     * @param binClassId
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @return
//     */
//    public StorageBinV2 getStorageBinByBinClassIdV2(String warehouseId, Long binClassId, String companyCodeId, String plantId, String languageId) {
////        Optional<StorageBinV2> storagebin = storageBinV2Repository.findTopByBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
////                binClassId,
////                companyCodeId,
////                plantId,
////                warehouseId,
////                languageId, 0L);
////        if (storagebin.isEmpty()) {
////            throw new BadRequestException("The Given Values: " +
////                    "binClassId" + binClassId +
////                    "companyCodeId " + companyCodeId +
////                    "plantId " + plantId +
////                    "warehouseId " + warehouseId + " doesn't exist:");
////        }
////        return storagebin.get();
//
//        Optional<StorageBinV2> storageBin = storageBinV2Repository.getStorageBin(binClassId, companyCodeId, plantId, warehouseId, languageId);
//
//        if (storageBin.isEmpty()) {
//            throw new BadRequestException("The Given Values: " +
//                    "binClassId " + binClassId +
//                    "companyCodeId " + companyCodeId +
//                    "plantId " + plantId +
//                    "warehouseId " + warehouseId + " doesn't exist:");
//        }
//        return storageBin.get();
//    }

    /**
     * @param storageBin
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @return
     */
    public StorageBinV2 getStorageBinV2(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin) {
        Optional<StorageBinV2> storagebin = storageBinV2Repository.findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                storageBin,
                companyCodeId,
                plantId,
                warehouseId,
                languageId,
                0L);
        log.info("StorageBin---->:" + storagebin);
        if (storagebin.isEmpty()) {
            throw new BadRequestException("The Given Values: " +
                    "storageBin" + storageBin +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin.get();
    }

//    /**
//     * Modified for Knowell, JPA to Native Query
//     * Aakash Vinayak - 08/07/2025
//     *
//     * @param storageBin
//     * @param companyCodeId
//     * @param plantId
//     * @param warehouseId
//     * @param languageId
//     * @return
//     */
//    public StorageBinV2 getStorageBinV7(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin) {
//        StorageBinV2 storagebin = storageBinV2Repository.getStorageBinV7(
//                storageBin,
//                companyCodeId,
//                plantId,
//                languageId,
//                warehouseId);
//        log.info("StorageBin---->:" + storagebin);
//        if (storagebin == null) {
//            throw new BadRequestException("The Given Values: " +
//                    "storageBin" + storageBin +
//                    "companyCodeId " + companyCodeId +
//                    "plantId " + plantId +
//                    "warehouseId " + warehouseId + " doesn't exist:");
//        }
//        return storagebin;
//    }
//
//    /**
//     * Modified for Knowell Checking Conf_St_bin is available
//     * will be checking weather the remain_qty > 0 and status_id = 0
//     *
//     * Aakash Vinayak - 25/07/2025
//     *
//     * @param storageBin
//     * @param companyCodeId
//     * @param plantId
//     * @param warehouseId
//     * @param languageId
//     * @return
//     */
//    public StorageBinV2 getConfirmedStorageBinV7(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin) {
//        StorageBinV2 storagebin = storageBinV2Repository.getConfirmedStorageBinV7(
//                storageBin,
//                companyCodeId,
//                plantId,
//                languageId,
//                warehouseId);
//        log.info("ConfirmedStorageBin available ---->:" + storagebin);
//        if (storagebin == null) {
//            throw new BadRequestException("The ConfirmedStorageBin is not Available because either Remaining_Qty capacity is 0 or Bin is Occupied (status_id = 1)");
//        }
//        return storagebin;
//    }
//
//    /**
//     * @param storageBin
//     * @param companyCodeId
//     * @param plantId
//     * @param warehouseId
//     * @param languageId
//     * @return
//     */
//    public StorageBinV2 getStorageBin5(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin) {
//        DataBaseContextHolder.setCurrentDb("REEFERON");
//        Optional<StorageBinV2> storagebin = storageBinV2Repository.getStorageBinV5(storageBin, companyCodeId, plantId, languageId, warehouseId);
//
//        log.info("StorageBin------>" + storagebin);
//        if (storagebin == null) {
//            throw new BadRequestException("The Given Values: " +
//                    "storageBin" + storageBin +
//                    "companyCodeId " + companyCodeId +
//                    "plantId " + plantId +
//                    "warehouseId " + warehouseId + " doesn't exist:");
//        }
//        return storagebin.get();
//    }
//
//    /**
//     * @param storageBin
//     * @param companyCodeId
//     * @param plantId
//     * @param warehouseId
//     * @param languageId
//     * @return
//     */
//    public StorageBinV2 getStorageBinEmptyCrate(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin) {
//        DataBaseContextHolder.setCurrentDb("REEFERON");
//        Optional<StorageBinV2> storagebin = storageBinV2Repository.getStorageBinEmptyCrate(storageBin, companyCodeId, plantId, languageId, warehouseId);
//        if (storagebin == null) {
//            throw new BadRequestException("The Given Values: " +
//                    "storageBin" + storageBin +
//                    "companyCodeId " + companyCodeId +
//                    "plantId " + plantId +
//                    "warehouseId " + warehouseId + " doesn't exist:");
//        }
//        return storagebin.get();
//    }

//    /**
//     * updateStorageBin
//     *
//     * @param storageBin
//     * @param updateStorageBin
//     * @return
//     * @throws IllegalAccessException
//     * @throws InvocationTargetException
//     */
//    public StorageBinV2 updateStorageBinV2(String storageBin, StorageBinV2 updateStorageBin, String companyCodeId, String plantId,
//                                           String languageId, String warehouseId, String loginUserID) {
//        StorageBinV2 dbStorageBin = getStorageBinV2(companyCodeId, plantId, languageId, warehouseId, storageBin);
//        log.info("dbstorageBin: " + dbStorageBin);
//        BeanUtils.copyProperties(updateStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(updateStorageBin));
//        dbStorageBin.setUpdatedBy(loginUserID);
//        dbStorageBin.setUpdatedOn(new Date());
////        storageBinV2Repository.delete(dbStorageBin);
//        return storageBinV2Repository.save(dbStorageBin);
//    }
//
//    /**
//     * updateStorageBin
//     *
//     * @param storageBin
//     * @param updateStorageBin
//     * @return
//     * @throws IllegalAccessException
//     * @throws InvocationTargetException
//     */
//    public StorageBinV2 updateStorageBinV6(String storageBin, StorageBinV2 updateStorageBin, String companyCodeId, String plantId,
//                                           String languageId, String warehouseId, String loginUserID) {
//        StorageBinV2 dbStorageBin = getStorageBinV2(companyCodeId, plantId, languageId, warehouseId, storageBin);
//        log.info("dbstorageBin: " + dbStorageBin);
//        BeanUtils.copyProperties(updateStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(updateStorageBin));
//        dbStorageBin.setUpdatedBy(loginUserID);
//        dbStorageBin.setUpdatedOn(new Date());
//        storageBinV2Repository.delete(dbStorageBin);
//        return storageBinV2Repository.save(dbStorageBin);
//    }
//
//    /**
//     * updateStorageBin
//     *
//     * @param storageBin
//     * @param updateStorageBin
//     * @return
//     * @throws IllegalAccessException
//     * @throws InvocationTargetException
//     */
//    public StorageBinV2 updateStorageBinV7(String storageBin, StorageBinV2 updateStorageBin, String companyCodeId, String plantId,
//                                           String languageId, String warehouseId, String loginUserID) {
//        StorageBinV2 dbStorageBin = getStorageBinV2(companyCodeId, plantId, languageId, warehouseId, storageBin);
//        log.info("dbstorageBin Knowell: " + dbStorageBin);
//        BeanUtils.copyProperties(updateStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(updateStorageBin));
//        dbStorageBin.setUpdatedBy(loginUserID);
//        dbStorageBin.setUpdatedOn(new Date());
//        dbStorageBin.setCapacityCheck(false);
//        storageBinV2Repository.delete(dbStorageBin);
//        return storageBinV2Repository.save(dbStorageBin);
//    }
//
//    /**
//     * updateStorageBin
//     *
//     * @param storageBin
//     * @param updateStorageBin
//     * @return
//     * @throws IllegalAccessException
//     * @throws InvocationTargetException
//     */
//    public StorageBinV2 updateStorageBinV5(String storageBin, StorageBinV2 updateStorageBin, String companyCodeId, String plantId,
//                                           String languageId, String warehouseId, String loginUserID) {
//        StorageBinV2 dbStorageBin = getStorageBinV2(companyCodeId, plantId, languageId, warehouseId, storageBin);
//        log.info("dbstorageBin: " + dbStorageBin);
//        BeanUtils.copyProperties(updateStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(updateStorageBin));
//        dbStorageBin.setUpdatedBy(loginUserID);
//        dbStorageBin.setUpdatedOn(new Date());
//        storageBinV2Repository.delete(dbStorageBin);
//        return storageBinV2Repository.save(dbStorageBin);
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param binClassIds
//     * @param storageBin
//     * @return
//     */
//    public boolean isStorageBinExists(String companyCodeId, String plantId, String languageId, String warehouseId, List<Long> binClassIds, String storageBin) {
//        return storageBinV2Repository.existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdInAndDeletionIndicator(
//                companyCodeId, plantId, languageId, warehouseId, storageBin, binClassIds, 0L);
//
//    }
}
