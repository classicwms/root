package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.dto.LikeSearchInput;
import com.tekclover.wms.api.masters.model.exceptionlog.ExceptionLog;
import com.tekclover.wms.api.masters.model.impl.StorageBinListImpl;
import com.tekclover.wms.api.masters.model.storagebin.*;
import com.tekclover.wms.api.masters.model.storagebin.v2.StorageBinV2;
import com.tekclover.wms.api.masters.repository.ExceptionLogRepository;
import com.tekclover.wms.api.masters.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.masters.repository.StorageBinRepository;
import com.tekclover.wms.api.masters.repository.StorageBinV2Repository;
import com.tekclover.wms.api.masters.repository.specification.StorageBinSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class StorageBinService {
    @Autowired
    private ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    private StorageBinRepository storagebinRepository;

    @Autowired
    private StorageBinV2Repository storageBinV2Repository;

    @Autowired
    private ExceptionLogRepository exceptionLogRepo;

    /**
     * getStorageBins
     *
     * @return
     */
    public List<StorageBin> getStorageBins() {
        List<StorageBin> storagebinList = storagebinRepository.findAll();
//		log.info("storagebinList : " + storagebinList);
        storagebinList = storagebinList.stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return storagebinList;
    }

    /**
     * getStorageBin
     *
     * @param storageBin
     * @return
     */
    public StorageBin getStorageBin(String storageBin, String companyCodeId, String plantId, String warehouseId, String languageId) {
        Optional<StorageBin> storagebin = storagebinRepository.findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                storageBin,
                companyCodeId,
                plantId,
                warehouseId,
                languageId,
                0L);
        if (storagebin.isEmpty()) {
            // Exception Log
            createStorageBinLog1(storageBin, languageId, companyCodeId, plantId, warehouseId,
                    "Storage Bin with given values and binClassId - " + storageBin + " doesn't exists.");
            throw new BadRequestException("The Given Values:" +
                    "storageBin" + storageBin +
                    "companyCodeId" + companyCodeId +
                    "plantId" + plantId +
                    "warehouseId" + warehouseId + "doesn't exist:");
        }
        return storagebin.get();
    }

    //V2
    public StorageBinV2 getStorageBinV2(String storageBin, String companyCodeId, String plantId, String warehouseId, String languageId) {
        Optional<StorageBinV2> storagebin = storageBinV2Repository.findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                storageBin,
                companyCodeId,
                plantId,
                warehouseId,
                languageId,
                0L);
        if (storagebin.isEmpty()) {
            // Exception Log
            createStorageBinLog1(storageBin, languageId, companyCodeId, plantId, warehouseId,
                    "Storage Bin with given values and storageBin - " + storageBin + " doesn't exists.");
            throw new BadRequestException("The Given Values: " +
                    "storageBin" + storageBin +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin.get();
    }

    /**
     * @param warehouseId
     * @param binClassId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public StorageBinV2 getStorageBinByBinClassId(String warehouseId, Long binClassId, String companyCodeId, String plantId, String languageId) {
        StorageBinV2 storagebin = storageBinV2Repository.getStorageBinByBinClassId(
                binClassId,
                companyCodeId,
                plantId,
                warehouseId,
                languageId);
        if (storagebin == null) {
            // Exception Log
            createStorageBinLog(binClassId, languageId, companyCodeId, plantId, warehouseId,
                    "Storage Bin with given values and binClassId - " + binClassId + " doesn't exists.");
            throw new BadRequestException("The Given Values: " +
                    "binClassId" + binClassId +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin;
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
            // Exception Log
//            createStorageBinLog(binClassId, languageId, companyCodeId, plantId, warehouseId,
//                    "Storage Bin with given values and binClassId-" + binClassId + " doesn't exists.");
            throw new BadRequestException("The Given Values: " +
                    "binClassId" + binClassId +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin.get();
    }

    /**
     * @param warehouseId
     * @param binClassId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public StorageBinV2 getStorageBinByBinClassIdV5(String warehouseId, Long binClassId, String companyCodeId, String plantId, String languageId) {
        Optional<StorageBinV2> storagebin = storageBinV2Repository.findByBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                binClassId,
                companyCodeId,
                plantId,
                warehouseId,
                languageId, 0L);
        if (storagebin.isEmpty()) {
            // Exception Log
//            createStorageBinLog(binClassId, languageId, companyCodeId, plantId, warehouseId,
//                    "Storage Bin with given values and binClassId-" + binClassId + " doesn't exists.");
            throw new BadRequestException("The Given Values: " +
                    "binClassId" + binClassId +
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
    public List<StorageBin> getStorageBin(StorageBinPutAway storageBinPutAway) {
        List<StorageBin> storagebinList =
                storagebinRepository.findByWarehouseIdAndStorageBinInAndStorageSectionIdInAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
                        storageBinPutAway.getWarehouseId(),
                        storageBinPutAway.getStorageBin(),
                        storageBinPutAway.getStorageSectionIds(),
                        0,
                        0,
                        0L);
        if (!storagebinList.isEmpty()) {
            return storagebinList;
        }
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Storage Bin with given values doesn't exists.");
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public List<StorageBinV2> getStorageBinV2(StorageBinPutAway storageBinPutAway) {
        List<StorageBinV2> storagebinList =
                storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinInAndBinClassIdAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
                        storageBinPutAway.getCompanyCodeId(),
                        storageBinPutAway.getPlantId(),
                        storageBinPutAway.getLanguageId(),
                        storageBinPutAway.getWarehouseId(),
                        storageBinPutAway.getStorageBin(),
                        storageBinPutAway.getBinClassId(),
                        0,
                        0,
                        0L);
        if (!storagebinList.isEmpty()) {
            return storagebinList;
        }
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Storage Bin with given values doesn't exists.");
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getaStorageBinV2(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;

        if (storageBinPutAway.getBinClassId() != null) {
            log.info("StorageBin Inputs if BinClass != null : companyCodeId ----> " + storageBinPutAway.getCompanyCodeId() + ", plantId -----> " + storageBinPutAway.getPlantId() + ", " +
                    "languageId ----> " + storageBinPutAway.getLanguageId() + ", warehouseId -----> " + storageBinPutAway.getWarehouseId() + ", bin -------> " + storageBinPutAway.getBin() + ", binClassID ----> " +
                    storageBinPutAway.getBinClassId());
            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdAndDeletionIndicator(
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId(),
                    storageBinPutAway.getBin(),
                    storageBinPutAway.getBinClassId(),
                    0L);
        }
        if (storageBinPutAway.getBinClassId() == null) {
            log.info("StorageBin Inputs if BinClass == null : companyCodeId ----> " + storageBinPutAway.getCompanyCodeId() + ", plantId -----> " + storageBinPutAway.getPlantId() + ", " +
                    "languageId ----> " + storageBinPutAway.getLanguageId() + ", warehouseId -----> " + storageBinPutAway.getWarehouseId() + ", bin ---> " + storageBinPutAway.getBin());
            storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId(),
                    storageBinPutAway.getBin(),
                    0L);
        }
        log.info("storageBin -----> {}", storagebin);
        if (storagebin != null) {
            return storagebin;
        }
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Storage Bin with given values doesn't exists.");
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getProposedStorageBinNonCBM(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;
        if (storageBinPutAway.getBinClassId() != 7 && !storageBinPutAway.getStorageBin().isEmpty()) {
            storagebin = storageBinV2Repository.getStorageBinNonCBM(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getStatusId(),
                    storageBinPutAway.getStorageBin(),
                    storageBinPutAway.getWarehouseId());
        }
        if (storageBinPutAway.getBinClassId() != 7 && storageBinPutAway.getStorageBin().isEmpty()) {
            storagebin = storageBinV2Repository.getStorageBinNonCBM(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getStatusId(),
                    storageBinPutAway.getWarehouseId());
        }
        if (storageBinPutAway.getBinClassId() == 7) {
            storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId());
        }
        if (storagebin != null) {
            return storagebin;
        }
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Proposed Storage Bin NonCbm doesn't exists.");
        return null;
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
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Proposed Storage Bin NonCbm LastPicked doesn't exists.");
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
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Proposed Storage Bin Cbm LastPicked doesn't exists.");
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
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Proposed Storage Bin CbmPerQty LastPicked doesn't exists.");
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getExistingProposedStorageBinNonCBM(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;
        if (storageBinPutAway.getBinClassId() != 7) {
            storagebin = storageBinV2Repository.getExistingStorageBinNonCBM(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getStorageBin(),
                    storageBinPutAway.getWarehouseId());
        }
        if (storageBinPutAway.getBinClassId() == 7) {
            storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId());
        }
        if (storagebin != null) {
            log.info("Inventory Existing StorageBin: " + storagebin.getStorageBin());
            return storagebin;
        }
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Proposed Storage Bin NonCbm doesn't exists.");
        return null;
    }

    /**
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
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Storage Bin with BinClassId-7 not available!");
        return null;
    }

    /**
     * @param storageBinPutAway
     * @return
     */
    public StorageBinV2 getStorageBinBinClassId1(StorageBinPutAway storageBinPutAway) {

        StorageBinV2 storagebin = null;

        if (storageBinPutAway.getBinClassId() == 1) {
            storagebin = storageBinV2Repository.getStorageBinNonCBMBinClassId1(
                    storageBinPutAway.getBinClassId(),
                    storageBinPutAway.getCompanyCodeId(),
                    storageBinPutAway.getPlantId(),
                    storageBinPutAway.getLanguageId(),
                    storageBinPutAway.getWarehouseId());
        }
        if (storagebin != null) {
            log.info("BinClassId 1 StorageBin: " + storagebin.getStorageBin());
            return storagebin;
        }
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Storage Bin with BinClassId-1 not available!");
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
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Proposed Storage Bin Cbm doesn't exists.");
        return null;
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
        // Exception Log
        createStorageBinLog2(storageBinPutAway, "Proposed Storage Bin CbmPerQty doesn't exists.");
        return null;
    }

    /**
     * @param warehouseId
     * @param statusId
     * @return
     */
    public List<StorageBin> getStorageBinByStatus(String warehouseId, Long statusId) {
        List<StorageBin> storagebin = storagebinRepository.findByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, statusId, 0L);
        if (storagebin != null) {
            return storagebin;
        } else {
            throw new BadRequestException("The given StorageBin ID : " + warehouseId + ", statusId: " + statusId + " doesn't exist.");
        }
    }

    //V2
    public List<StorageBinV2> getStorageBinByStatusV2(String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId) {
        List<StorageBinV2> storagebin = storageBinV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, statusId, 0L);
        if (storagebin != null) {
            return storagebin;
        } else {
            return null;
//            throw new BadRequestException("The given StorageBin ID : " + warehouseId + ", statusId: " + statusId + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param statusId
     * @return
     */
    public List<StorageBin> getStorageBinByStatusNotEqual(String warehouseId, Long statusId) {
        List<StorageBin> storagebin =
                storagebinRepository.findByWarehouseIdAndStatusIdNotAndDeletionIndicator(warehouseId,
                        statusId, 0L);
        if (storagebin != null) {
            return storagebin;
        } else {
            throw new BadRequestException("The given StorageBinByStatusNotEqual : " + warehouseId + ", statusId: " + statusId + " doesn't exist.");
        }
    }

    /**
     * @param likeSearchByStorageBinNDesc
     * @return
     */
    public List<StorageBinListImpl> findStorageBinLikeSearch(String likeSearchByStorageBinNDesc) {
        if (likeSearchByStorageBinNDesc != null && !likeSearchByStorageBinNDesc.trim().isEmpty()) {
            List<StorageBinListImpl> data = storagebinRepository.getStorageBinListBySearch(likeSearchByStorageBinNDesc.trim(),
                    likeSearchByStorageBinNDesc.trim());
            return data;
        } else {
            throw new BadRequestException("Search string must not be empty");
        }
    }

    /**
     * @param likeSearchByStorageBinNDesc
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public List<StorageBinListImpl> findStorageBinLikeSearchNew(String likeSearchByStorageBinNDesc, String companyCodeId,
                                                                String plantId, String languageId, String warehouseId) {
        if (likeSearchByStorageBinNDesc != null && !likeSearchByStorageBinNDesc.trim().isEmpty()) {
            List<StorageBinListImpl> data = storagebinRepository.getStorageBinListBySearchNew(likeSearchByStorageBinNDesc.trim(),
                    likeSearchByStorageBinNDesc.trim(),
                    likeSearchByStorageBinNDesc.trim(),
                    companyCodeId,
                    plantId,
                    languageId,
                    warehouseId);
            return data;
        } else {
            throw new BadRequestException("Search string must not be empty");
        }
    }

    /**
     * @param likeSearchInput
     * @return
     */
    public List<StorageBinListImpl> findStorageBinLikeSearchV2(LikeSearchInput likeSearchInput) {
        if (likeSearchInput.getLikeSearchByDesc() != null && !likeSearchInput.getLikeSearchByDesc().trim().isEmpty()) {
            List<StorageBinListImpl> data = storagebinRepository.getStorageBinListBySearchV2(
                    likeSearchInput.getLikeSearchByDesc().trim(),
                    likeSearchInput.getLikeSearchByDesc().trim(),
                    likeSearchInput.getLikeSearchByDesc().trim(),
                    likeSearchInput.getCompanyCodeId(),
                    likeSearchInput.getPlantId(),
                    likeSearchInput.getLanguageId(),
                    likeSearchInput.getWarehouseId());
            return data;
        } else {
            throw new BadRequestException("Search string must not be empty");
        }
    }

    /**
     * getStorageBin
     *
     * @param warehouseId
     * @param binClassId
     * @return
     */
    public StorageBin getStorageBin(String warehouseId, Long binClassId) {
        StorageBin storagebin = storagebinRepository.findByWarehouseIdAndBinClassIdAndDeletionIndicator(
                warehouseId, binClassId, 0L);
        if (storagebin != null) {
            return storagebin;
        } else {
            throw new BadRequestException("The given values : "
                    + " warehouseId:" + warehouseId
                    + ", binClassId:" + binClassId
                    + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param storageBin
     * @return
     */
    public StorageBin getStorageBin(String warehouseId, String storageBin) {
        StorageBin storagebin = storagebinRepository.findByWarehouseIdAndStorageBinAndDeletionIndicator(warehouseId, storageBin, 0L);
        log.info("Storage bin==========>: " + storagebin);
        if (storagebin != null && storagebin.getDeletionIndicator() != null && storagebin.getDeletionIndicator() == 0) {
            return storagebin;
        } else {
            throw new BadRequestException("The given StorageBin ID : " + storageBin + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param storageBin
     * @return
     */
    public StorageBinV2 getStorageBinV2(String warehouseId, String storageBin) {
        StorageBinV2 storagebin = storageBinV2Repository.findByWarehouseIdAndStorageBinAndDeletionIndicator(warehouseId, storageBin, 0L);
        log.info("Storage bin==========>: " + storagebin);
        if (storagebin != null && storagebin.getDeletionIndicator() != null && storagebin.getDeletionIndicator() == 0) {
            return storagebin;
        } else {
            // Exception Log
            createStorageBinLog3(storageBin, warehouseId, "Storage Bin with Id-" + storageBin + " and warehouseId-" + warehouseId + " doesn't exists.");
            throw new BadRequestException("The given StorageBin ID : " + storageBin + " doesn't exist.");
        }
    }

    /**
     * @param stSectionIds
     * @return
     */
    public List<StorageBin> getStorageBin(String warehouseId, List<String> stSectionIds) {
        List<StorageBin> storagebin = storagebinRepository.findByWarehouseIdAndStorageSectionIdIn(warehouseId, stSectionIds);
        if (storagebin != null) {
            return storagebin;
        } else {
            throw new BadRequestException("The given values : "
                    + " stSectionIds:" + stSectionIds
                    + " doesn't exist.");
        }
    }

    /**
     * @param searchStorageBin
     * @return
     * @throws Exception
     */
    public List<StorageBin> findStorageBin(SearchStorageBin searchStorageBin) throws Exception {
        if (searchStorageBin.getStartCreatedOn() != null && searchStorageBin.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageBin.getStartCreatedOn(), searchStorageBin.getEndCreatedOn());
            searchStorageBin.setStartCreatedOn(dates[0]);
            searchStorageBin.setEndCreatedOn(dates[1]);
        }

        if (searchStorageBin.getStartUpdatedOn() != null && searchStorageBin.getEndUpdatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageBin.getStartUpdatedOn(), searchStorageBin.getEndUpdatedOn());
            searchStorageBin.setStartUpdatedOn(dates[0]);
            searchStorageBin.setEndUpdatedOn(dates[1]);
        }

        StorageBinSpecification spec = new StorageBinSpecification(searchStorageBin);
        List<StorageBin> results = storagebinRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    //Streaming
    public Stream<StorageBin> findStorageBinStream(SearchStorageBin searchStorageBin) throws Exception {
        if (searchStorageBin.getStartCreatedOn() != null && searchStorageBin.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageBin.getStartCreatedOn(), searchStorageBin.getEndCreatedOn());
            searchStorageBin.setStartCreatedOn(dates[0]);
            searchStorageBin.setEndCreatedOn(dates[1]);
        }

        if (searchStorageBin.getStartUpdatedOn() != null && searchStorageBin.getEndUpdatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageBin.getStartUpdatedOn(), searchStorageBin.getEndUpdatedOn());
            searchStorageBin.setStartUpdatedOn(dates[0]);
            searchStorageBin.setEndUpdatedOn(dates[1]);
        }

        StorageBinSpecification spec = new StorageBinSpecification(searchStorageBin);
        Stream<StorageBin> results = storagebinRepository.stream(spec, StorageBin.class);
        return results;
    }

    /**
     * createStorageBin
     *
     * @param newStorageBin
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StorageBin createStorageBin(AddStorageBin newStorageBin, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        StorageBin dbStorageBin = new StorageBin();
        Optional<StorageBin> duplicateStorageBin = storagebinRepository.findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(newStorageBin.getStorageBin(), newStorageBin.getCompanyCodeId(), newStorageBin.getPlantId(), newStorageBin.getWarehouseId(), newStorageBin.getLanguageId(), 0L);
        if (!duplicateStorageBin.isEmpty()) {
            throw new BadRequestException("Record is Getting Duplicate");
        } else {
            BeanUtils.copyProperties(newStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(newStorageBin));
            dbStorageBin.setDeletionIndicator(0L);
            dbStorageBin.setCreatedBy(loginUserID);
            dbStorageBin.setUpdatedBy(loginUserID);
            dbStorageBin.setCreatedOn(new Date());
            dbStorageBin.setUpdatedOn(new Date());
            return storagebinRepository.save(dbStorageBin);
        }
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
    public StorageBin updateStorageBin(String storageBin, String companyCodeId, String plantId, String warehouseId, String languageId, UpdateStorageBin updateStorageBin, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        StorageBin dbStorageBin = getStorageBin(storageBin, companyCodeId, plantId, warehouseId, languageId);
        BeanUtils.copyProperties(updateStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(updateStorageBin));
        dbStorageBin.setUpdatedBy(loginUserID);
        dbStorageBin.setUpdatedOn(new Date());
        storagebinRepository.delete(dbStorageBin);
        return storagebinRepository.save(dbStorageBin);
    }

    /**
     * createStorageBin
     *
     * @param newStorageBin
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StorageBinV2 createStorageBinV2(StorageBinV2 newStorageBin, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        StorageBinV2 dbStorageBin = new StorageBinV2();
        Optional<StorageBinV2> duplicateStorageBin = storageBinV2Repository.
                findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                        newStorageBin.getStorageBin(), newStorageBin.getCompanyCodeId(),
                        newStorageBin.getPlantId(), newStorageBin.getWarehouseId(),
                        newStorageBin.getLanguageId(), 0L);
        if (!duplicateStorageBin.isEmpty()) {
            throw new BadRequestException("Record is Getting Duplicate");
        } else {
            BeanUtils.copyProperties(newStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(newStorageBin));

            IKeyValuePair description = imBasicData1V2Repository.getDescription(newStorageBin.getCompanyCodeId(),
                    newStorageBin.getLanguageId(),
                    newStorageBin.getPlantId(),
                    newStorageBin.getWarehouseId());

            if (description != null) {
                dbStorageBin.setCompanyDescription(description.getCompanyDesc());
                dbStorageBin.setPlantDescription(description.getPlantDesc());
                dbStorageBin.setWarehouseDescription(description.getWarehouseDesc());
            }

            dbStorageBin.setDeletionIndicator(0L);
            dbStorageBin.setCapacityCheck(false);
            dbStorageBin.setCreatedBy(loginUserID);
            dbStorageBin.setUpdatedBy(loginUserID);
            dbStorageBin.setCreatedOn(new Date());
            dbStorageBin.setUpdatedOn(new Date());
            return storageBinV2Repository.save(dbStorageBin);
        }
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
    public StorageBinV2 updateStorageBinV2(String storageBin, String companyCodeId,
                                           String plantId, String warehouseId,
                                           String languageId, StorageBinV2 updateStorageBin, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
//        StorageBinV2 dbStorageBin = storageBinV2Repository.getStorageBin(storageBin, companyCodeId, plantId, warehouseId, languageId);
//        StorageBinV2 dbStorageBin = getStorageBinV2(storageBin, companyCodeId, plantId, warehouseId, languageId);
        StorageBinV2 dbStorageBin = storageBinV2Repository.getStorageBin(storageBin, companyCodeId, plantId, languageId, warehouseId);
//        Optional<StorageBinV2> dbStorageBin = storageBinV2Repository.findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(storageBin, companyCodeId, plantId, warehouseId, languageId, 0L);
        log.info("dbstorageBin: " + dbStorageBin);
        BeanUtils.copyProperties(updateStorageBin, dbStorageBin, CommonUtils.getNullPropertyNames(updateStorageBin));
      if(dbStorageBin != null){
          Date date = new Date();
          storageBinV2Repository.updateStorageBin(loginUserID,date,storageBin, companyCodeId, plantId, languageId, warehouseId);
          log.info("storage bin updated : ");
        }

      return dbStorageBin;
    }

    /**
     * Modified for MT - 15/07/2025
     * Aakash Vinayak
     *
     * deleteStorageBin
     *
     * @param storageBin
     */
    public void deleteStorageBin(String storageBin, String companyCodeId, String plantId, String warehouseId, String languageId, String loginUserID) throws ParseException {
        StorageBin storagebin = getStorageBin(storageBin, companyCodeId, plantId, warehouseId, languageId);
        if (storagebin != null) {
//            storagebin.setDeletionIndicator(1L);
//            storagebin.setUpdatedBy(loginUserID);
//            storagebin.setUpdatedOn(new Date());
//            storagebinRepository.save(storagebin);
            storageBinV2Repository.softDeleteStorageBin(companyCodeId, plantId, languageId, warehouseId, storageBin);
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + storageBin);
        }
    }

    /**
     * GET STORAGE BIN V2
     *
     * @param storageBin
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @return
     */
    public StorageBinV2 getStoreBinV2(String storageBin, String companyCodeId, String plantId, String warehouseId, String languageId) {
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
     * @param storageBin
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param storageBinV2
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StorageBinV2 updateStoreBinV2(String storageBin, String companyCodeId, String plantId, String warehouseId, String languageId, StorageBinV2 storageBinV2, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        StorageBinV2 dbStorageBin = getStoreBinV2(storageBin, companyCodeId, plantId, warehouseId, languageId);
        BeanUtils.copyProperties(storageBinV2, dbStorageBin, CommonUtils.getNullPropertyNames(storageBinV2));
        dbStorageBin.setUpdatedBy(loginUserID);
        dbStorageBin.setUpdatedOn(new Date());
        return storagebinRepository.save(dbStorageBin);
    }

    /**
     * DELETE STORAGE BIN V2
     *
     * @param storageBin
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param loginUserID
     */
    public void deleteStoreBinV2(String storageBin, String companyCodeId, String plantId, String warehouseId,
                                 String languageId, String loginUserID) throws ParseException {
        StorageBinV2 storeBinV2 = getStoreBinV2(storageBin, companyCodeId, plantId, warehouseId, languageId);
        if (storeBinV2 != null) {
            storeBinV2.setDeletionIndicator(1L);
            storeBinV2.setUpdatedBy(loginUserID);
            storeBinV2.setUpdatedOn(new Date());
            storagebinRepository.save(storeBinV2);
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + storageBin);
        }
    }

    //========================================StorageBin_ExceptionLog==================================================
    private void createStorageBinLog(Long binClassId, String languageId, String companyCodeId,
                                     String plantId, String warehouseId, String error) {

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setOrderTypeId(String.valueOf(binClassId));
        exceptionLog.setOrderDate(new Date());
        exceptionLog.setLanguageId(languageId);
        exceptionLog.setCompanyCodeId(companyCodeId);
        exceptionLog.setPlantId(plantId);
        exceptionLog.setWarehouseId(warehouseId);
        exceptionLog.setReferenceField1(String.valueOf(binClassId));
        exceptionLog.setErrorMessage(error);
        exceptionLog.setCreatedBy("MSD_API");
        exceptionLog.setCreatedOn(new Date());
        exceptionLogRepo.save(exceptionLog);
    }

    private void createStorageBinLog1(String storageBin, String languageId, String companyCodeId,
                                      String plantId, String warehouseId, String error) {

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setOrderTypeId(storageBin);
        exceptionLog.setOrderDate(new Date());
        exceptionLog.setLanguageId(languageId);
        exceptionLog.setCompanyCodeId(companyCodeId);
        exceptionLog.setPlantId(plantId);
        exceptionLog.setWarehouseId(warehouseId);
        exceptionLog.setReferenceField1(storageBin);
        exceptionLog.setErrorMessage(error);
        exceptionLog.setCreatedBy("MSD_API");
        exceptionLog.setCreatedOn(new Date());
        exceptionLogRepo.save(exceptionLog);
    }

    private void createStorageBinLog2(StorageBinPutAway storageBinPutAway, String error) {

        for (String dbStorageBin : storageBinPutAway.getStorageBin()) {
            ExceptionLog exceptionLog = new ExceptionLog();

            exceptionLog.setOrderTypeId(dbStorageBin);
            exceptionLog.setOrderDate(new Date());
            exceptionLog.setLanguageId(storageBinPutAway.getLanguageId());
            exceptionLog.setCompanyCodeId(storageBinPutAway.getCompanyCodeId());
            exceptionLog.setPlantId(storageBinPutAway.getPlantId());
            exceptionLog.setWarehouseId(storageBinPutAway.getWarehouseId());
            exceptionLog.setReferenceField1(storageBinPutAway.getBin());
            exceptionLog.setReferenceField2(String.valueOf(storageBinPutAway.getBinClassId()));
            exceptionLog.setReferenceField3(String.valueOf(storageBinPutAway.getStatusId()));
            exceptionLog.setErrorMessage(error);
            exceptionLog.setCreatedBy("MSD_API");
            exceptionLog.setCreatedOn(new Date());
            exceptionLogRepo.save(exceptionLog);
        }
    }

    private void createStorageBinLog3(String storageBin, String warehouseId, String error) {

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setOrderTypeId(storageBin);
        exceptionLog.setOrderDate(new Date());
        exceptionLog.setWarehouseId(warehouseId);
        exceptionLog.setErrorMessage(error);
        exceptionLog.setCreatedBy("MSD_API");
        exceptionLog.setCreatedOn(new Date());
        exceptionLogRepo.save(exceptionLog);
    }

    public List<StorageBinV2> storageBinUpload(List<StorageBinV2> storageBinList) {

        List<StorageBinV2> saveStorageBin = new ArrayList<>();
        for (StorageBinV2 storageBin : storageBinList) {
            StorageBinV2 dbStorageBin = new StorageBinV2();
            Optional<StorageBin> duplicateStorageBin = storagebinRepository.findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(storageBin.getStorageBin(), storageBin.getCompanyCodeId(), storageBin.getPlantId(), storageBin.getWarehouseId(), storageBin.getLanguageId(), 0L);
            if (!duplicateStorageBin.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicate");
            } else {
                BeanUtils.copyProperties(storageBin, dbStorageBin, CommonUtils.getNullPropertyNames(storageBin));
                dbStorageBin.setDeletionIndicator(0L);
                dbStorageBin.setCreatedOn(new Date());
                dbStorageBin.setUpdatedOn(new Date());
                storageBinV2Repository.save(dbStorageBin);
                saveStorageBin.add(dbStorageBin);
            }
        }
        return saveStorageBin;
    }

    /**
     * @param warehouseId
     * @param binClassId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public StorageBinV2 getStorageBinByBinClassIdV9(String warehouseId, Long binClassId, String companyCodeId, String plantId, String languageId) {
        Optional<StorageBinV2> storagebin = storageBinV2Repository.findByBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                binClassId,
                companyCodeId,
                plantId,
                warehouseId,
                languageId, 0L);
        if (storagebin.isEmpty()) {
            // Exception Log
//            createStorageBinLog(binClassId, languageId, companyCodeId, plantId, warehouseId,
//                    "Storage Bin with given values and binClassId-" + binClassId + " doesn't exists.");
            throw new BadRequestException("The Given Values: " +
                    "binClassId" + binClassId +
                    "companyCodeId " + companyCodeId +
                    "plantId " + plantId +
                    "warehouseId " + warehouseId + " doesn't exist:");
        }
        return storagebin.get();
    }


}
