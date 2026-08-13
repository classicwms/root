package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.transaction.repository.StorageBinV2Repository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class StorageBinService extends BaseService {

    @Autowired
    private StorageBinV2Repository storageBinV2Repository;

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getaStorageBinV2(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;

        if (storageBinPutAway.getBinClassId() != null && storageBinPutAway.getStorageSectionIds() != null) {
            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdAndStorageSectionIdNotInAndDeletionIndicator(
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId(),
                    storageBinPutAway.getBin(),
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getStorageSectionIds(),
                    0L);
            return storagebin;
        }
        if (storageBinPutAway.getBinClassId() != null) {
            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdAndDeletionIndicator(
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId(),
                    storageBinPutAway.getBin(),
                    storageBinPutAway.getBinClassId(),
                    0L);
            return storagebin;
        }
        if (storageBinPutAway.getBinClassId() == null && storageBinPutAway.getStorageSectionIds() != null) {
            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndStorageSectionIdNotInAndDeletionIndicator(
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId(),
                    storageBinPutAway.getBin(),
                    storageBinPutAway.getStorageSectionIds(),
                    0L);
            return storagebin;
        }
        if (storageBinPutAway.getBinClassId() == null) {
            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId(),
                    storageBinPutAway.getBin(),
                    0L);
            return storagebin;
        }
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getProposedStorageBinNonCBM(StorageBinPutAway storageBinPutAway) {

        try {
            log.info("storageBinPutAway : " + storageBinPutAway);
            StorageBinV2 storagebin = null;
            if (storageBinPutAway.getBinClassId() != 7 && (storageBinPutAway.getStorageBin() != null && !storageBinPutAway.getStorageBin().isEmpty()) && storageBinPutAway.getStorageSectionIds() != null) {
                storagebin = storageBinV2Repository.getStorageBinNonCBM(
                        storageBinPutAway.getBinClassId(),
                        storageBinPutAway.getStorageSectionIds(),
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getStatusId(),
                        storageBinPutAway.getStorageBin(),
                        storageBinPutAway.getWarehouseId());
                return storagebin;
            }
            if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageSectionIds() != null &&
                    (storageBinPutAway.getStorageBin() == null  || (storageBinPutAway.getStorageBin() != null && storageBinPutAway.getStorageBin().isEmpty()))) {
                storagebin = storageBinV2Repository.getStorageBinNonCBM(
                        storageBinPutAway.getBinClassId(),
                        storageBinPutAway.getStorageSectionIds(),
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getStorageSectionId(),                //sending storageSectionId in statusId
                        storageBinPutAway.getWarehouseId());
                return storagebin;
            }
            if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageBin() != null && !storageBinPutAway.getStorageBin().isEmpty()) {
                storagebin = storageBinV2Repository.getStorageBinNonCBM(
                        storageBinPutAway.getBinClassId(),
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getStatusId(),
                        storageBinPutAway.getStorageBin(),
                        storageBinPutAway.getWarehouseId());
                return storagebin;
            }
            if (storageBinPutAway.getBinClassId() != 7 &&
                    (storageBinPutAway.getStorageBin() == null  || (storageBinPutAway.getStorageBin() != null && storageBinPutAway.getStorageBin().isEmpty()))) {
                storagebin = storageBinV2Repository.getStorageBinNonCBM(
                        storageBinPutAway.getBinClassId(),
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getStatusId(),
                        storageBinPutAway.getWarehouseId());
                return storagebin;
            }
            if (storageBinPutAway.getBinClassId() == 7) {
                storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
                        storageBinPutAway.getBinClassId(),
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getWarehouseId());
                return storagebin;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getProposedStorageBinNonCBMLastPicked(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;
        if (storageBinPutAway.getBinClassId() != 7 && !storageBinPutAway.getStorageBin().isEmpty()) {
            storagebin = storageBinV2Repository.getStorageBinNonCBMLastPick(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getStatusId(),
                    storageBinPutAway.getStorageBin(),
                    storageBinPutAway.getWarehouseId());
        }
        log.info("ProposedStorageBinNonCBMLastPicked: " + storagebin);
        if (storagebin != null) {
            log.info("ProposedStorageBinNonCBMLastPickedBin: " + storagebin.getStorageBin());
            return storagebin;
        }
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getProposedStorageBinCBMLastPicked(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;
        if (storageBinPutAway.getBinClassId() != 7 && !storageBinPutAway.getStorageBin().isEmpty()) {
            storagebin = storageBinV2Repository.getStorageBinLastPickCBM(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getCbm(),
                    storageBinPutAway.getStatusId(),
                    storageBinPutAway.getStorageBin(),
                    storageBinPutAway.getWarehouseId());
        }
        log.info("ProposedStorageBinCBMLastPicked: " + storagebin);
        if (storagebin != null) {
            log.info("ProposedStorageBinCBMLastPickedBin: " + storagebin.getStorageBin());
            return storagebin;
        }
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getProposedStorageBinCBMPerQtyLastPicked(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;
        if (storageBinPutAway.getBinClassId() != 7 && !storageBinPutAway.getStorageBin().isEmpty()) {
            storagebin = storageBinV2Repository.getStorageBinCbmPerQtyLastPick(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getCbmPerQty(),
                    storageBinPutAway.getStatusId(),
                    storageBinPutAway.getStorageBin(),
                    storageBinPutAway.getWarehouseId());
        }
        log.info("ProposedStorageBinCBMPerQtyLastPicked: " + storagebin);
        if (storagebin != null) {
            log.info("ProposedStorageBinCBMPerQtyLastPickedBin: " + storagebin.getStorageBin());
            return storagebin;
        }
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getExistingProposedStorageBinNonCBM(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;
        if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageSectionIds() != null) {
            storagebin = storageBinV2Repository.getExistingStorageBinNonCBM(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getStorageSectionIds(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getStorageBin(),
                    storageBinPutAway.getWarehouseId());
            log.info("Inventory Existing StorageBin: " + storagebin);
            return storagebin;
        }
        if (storageBinPutAway.getBinClassId() != 7) {
            storagebin = storageBinV2Repository.getExistingStorageBinNonCBM(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getStorageBin(),
                    storageBinPutAway.getWarehouseId());
            log.info("Inventory Existing StorageBin: " + storagebin);
            return storagebin;
        }
        if (storageBinPutAway.getBinClassId() == 7) {
            storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId());
            log.info("Inventory Existing StorageBin: " + storagebin);
            return storagebin;
        }
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getStorageBinBinClassId(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
                storageBinPutAway.getBinClassId(),
                storageBinPutAway.getCompanyCodeId(),
                storageBinPutAway.getPlantId(),
                storageBinPutAway.getLanguageId(),
                storageBinPutAway.getWarehouseId());

        if (storagebin != null) {
            log.info("Reserve StorageBin: " + storagebin.getStorageBin());
            return storagebin;
        }
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getProposedStorageBinCBM(StorageBinPutAway storageBinPutAway) {
        StorageBinV2 storagebin =
                storageBinV2Repository.getStorageBinCBM(
                        storageBinPutAway.getBinClassId(),
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getCbm(),
                        storageBinPutAway.getStatusId(),
                        storageBinPutAway.getWarehouseId());
        if (storagebin != null) {
            return storagebin;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param binClassId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public StorageBinV2 getStorageBinByBinClassIdV2(String warehouseId, Long binClassId, String companyCodeId, String plantId, String languageId) {
        Optional<StorageBinV2> storagebin = storageBinV2Repository.findTopByBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                binClassId,
                companyCodeId,
                plantId,
                warehouseId,
                languageId, 0L);
        if (storagebin.isEmpty()) {
            throw new BadRequestException("The Given Values: " +
                    "binClassId" + binClassId +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin.get();
    }
    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param binClassId
     * @param storageBin
     * @return
     */
    public StorageBinV2 getStorageBinByBinClassIdV2(String companyCodeId, String plantId, String languageId, String warehouseId, Long binClassId, String storageBin) {
        Optional<StorageBinV2> storagebin = storageBinV2Repository.findTopByBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndStorageBinAndDeletionIndicator(
                binClassId,
                companyCodeId,
                plantId,
                warehouseId,
                languageId,
                storageBin,
                0L);
        if (storagebin.isEmpty()) {
            throw new BadRequestException("The Given Values: " +
                    "binClassId" + binClassId +
                    "storageBin" + storageBin +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin.get();
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getProposedStorageBinCbmPerQty(StorageBinPutAway storageBinPutAway) {
        StorageBinV2 storagebin =
                storageBinV2Repository.getStorageBinCbmPerQty(
                        storageBinPutAway.getBinClassId(),
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getCbmPerQty(),
                        storageBinPutAway.getStatusId(),
                        storageBinPutAway.getWarehouseId());
        if (storagebin != null) {
            return storagebin;
        }
        return null;
    }

    /**
     * updateStorageBin
     *
     * @param storageBin
     * @param updateStorageBin
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StorageBinV2 updateStorageBinV2(String storageBin, StorageBinV2 updateStorageBin, String companyCodeId, String plantId,
                                           String languageId, String warehouseId, String loginUserID) {
        StorageBinV2 dbStorageBin = getStorageBinV2(companyCodeId, plantId, languageId, warehouseId, storageBin);
//        log.info("dbstorageBin: " + dbStorageBin);
        BeanUtils.copyProperties(updateStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(updateStorageBin));
        dbStorageBin.setUpdatedBy(loginUserID);
        dbStorageBin.setUpdatedOn(new Date());
        return storageBinV2Repository.save(dbStorageBin);
    }

    /**
     * @param storageBin
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, 
    		LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public StorageBinV2 getStorageBinV2(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin) {
        Optional<StorageBinV2> storagebin = storageBinV2Repository.findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                storageBin,
                companyCodeId,
                plantId,
                warehouseId,
                languageId,
                0L);
        if (storagebin.isEmpty()) {
            throw new BadRequestException("The Given Values: " +
                    "storageBin" + storageBin +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin.get();
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param cbmPerQty
     * @return
     */
    public List<StorageBinV2> getStorageBinWithCbmPerQty(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String itemCode, String manufacturerName, Long binClassId, Double cbmPerQty) {
        List<StorageBinV2> storageBinList = storageBinV2Repository.storageBinWithCbmPerQty(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, cbmPerQty);
        return storageBinList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param cbmPerQty
     * @return
     */
    public List<StorageBinV2> lastPickedStorageBinWithCbmPerQty(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                String itemCode, String manufacturerName, Long binClassId, Double cbmPerQty) {
        List<StorageBinV2> storageBinList = storageBinV2Repository.lastPickedStorageBinWithCbmPerQty(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, cbmPerQty);
        return storageBinList;
    }

    /**
     *
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getStorageBinBinClassId7(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;
        if (storageBinPutAway.getBinClassId() == 7) {
            storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId());
        }
        if (storagebin != null) {
            log.info("BinClassId 7 StorageBin: " + storagebin.getStorageBin());
            return storagebin;
        }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param storageSectionId
     * @param binClassId
     * @return
     */
    public StorageBinV2 getStorageBinV2(String companyCodeId, String plantId, String languageId, String warehouseId, String storageSectionId, Long binClassId) {
        Optional<StorageBinV2> storagebin = storageBinV2Repository.findTopByStorageSectionIdAndBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                storageSectionId,
                binClassId,
                companyCodeId,
                plantId,
                warehouseId,
                languageId,
                0L);
        if (storagebin.isEmpty()) {
            throw new BadRequestException("The Given Values: " +
                    "storageSectionId " + storageSectionId +
                    "binClassId " + binClassId +
                    "statusId : 0" +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin.get();
    }

    //=========================================================Impex-V4================================================================

    /**
     * propose returnBin else reserveBin if it is unavailable
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getStorageBinByBinClassIdV4(StorageBinPutAway storageBinPutAway) throws Exception {
        try {
            StorageBinV2 storagebin = null;
            if (storageBinPutAway.getBinClassId() == 7) {
                storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
                        storageBinPutAway.getBinClassId(),
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getWarehouseId());
            }
            if (storagebin != null) {
                log.info("BinClassId 7 StorageBin: " + storagebin.getStorageBin());
                return storagebin;
            } else {
                log.info("BinClassId --> 7L StorageBin Unavailable --> Proposing reserve bin ---> BinClassId --> 2L");
                return storageBinV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndBinClassIdAndDeletionIndicator(
                        storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(), storageBinPutAway.getWarehouseId(),
                        2L, 0L);
            }
        } catch (Exception e) {
            log.error("Exception while Bin fetch : " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param binClassIds
     * @param storageBin
     * @return
     */
    public boolean isStorageBinExists(String companyCodeId, String plantId, String languageId, String warehouseId, List<Long> binClassIds, String storageBin) {
        return storageBinV2Repository.existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdInAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, storageBin, binClassIds, 0L);
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param storageBin
     * @return
     */
    public boolean isStorageBinExists(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin) {
        return storageBinV2Repository.existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, storageBin, 0L);
    }
}