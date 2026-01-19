package com.tekclover.wms.api.inbound.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataSourceConfig;
import com.tekclover.wms.api.inbound.transaction.model.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ReversalLineV3;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ReversalV3;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.HHTUser;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1;

import com.tekclover.wms.api.inbound.transaction.model.dto.PutAwayPalletGroupResponse;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.inbound.transaction.model.impl.PutAwayHeaderImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.GrLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.PreInboundHeaderEntity;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.AddPutAwayHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.SearchPutAwayHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.UpdatePutAwayHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.InboundReversalInput;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutawayHeaderInt;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.SearchPutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.transaction.repository.specification.PutAwayHeaderSpecification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.PutAwayHeaderV2Specification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PutAwayHeaderService extends BaseService {
    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    StorageBinV2Repository storageBinV2Repository;

    @Autowired
    StorageBinService storageBinService;

    @Autowired
    private GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    private GrLineV2Repository grLineV2Repository;

    @Autowired
    private PutAwayHeaderRepository putAwayHeaderRepository;

    @Autowired
    private PutAwayLineRepository putAwayLineRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private InboundLineRepository inboundLineRepository;

    @Autowired
    private InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    private PutAwayLineService putAwayLineService;

    @Autowired
    private InboundLineService inboundLineService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private GrLineService grLineService;

    @Autowired
    private PickupLineService pickupLineService;

    @Autowired
    private PutAwayHeaderService putAwayHeaderService;

    //-------------------------------------------------------------------------------------------------
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    private PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    private PutAwayLineV2Repository putAwayLineV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private StorageBinRepository storageBinRepository;

    @Autowired
    private StagingLineService stagingLineService;

    @Autowired
    private GrHeaderService grHeaderService;

    @Autowired
    private PreInboundHeaderService preInboundHeaderService;

    @Autowired
    private PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    private InboundHeaderService inboundHeaderService;

    @Autowired
    private InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    private PreInboundLineService preInboundLineService;

    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    private StagingHeaderService stagingHeaderService;

    @Autowired
    private StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    private InventoryTransRepository inventoryTransRepository;

    String statusDescription = null;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    PutAwayHeaderIntRepository putAwayHeaderIntRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    //-------------------------------------------------------------------------------------------------

    /**
     * getPutAwayHeaders
     *
     * @return
     */
    public List<PutAwayHeader> getPutAwayHeaders() {
        List<PutAwayHeader> putAwayHeaderList = putAwayHeaderRepository.findAll();
        putAwayHeaderList = putAwayHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return putAwayHeaderList;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param putAwayNumber
     * @param proposedStorageBin
     * @return
     */
    public PutAwayHeader getPutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                                          String palletCode, String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin) {
        Optional<PutAwayHeader> putAwayHeader =
                putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndPutAwayNumberAndProposedStorageBinAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        goodsReceiptNo,
                        palletCode,
                        caseCode,
                        packBarcodes,
                        putAwayNumber,
                        proposedStorageBin,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",goodsReceiptNo: " + goodsReceiptNo +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",packBarcodes: " + packBarcodes +
                    ",putAwayNumber: " + putAwayNumber +
                    ",proposedStorageBin: " + proposedStorageBin +
                    " doesn't exist.");
        }
        return putAwayHeader.get();
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @return
     */
    public List<PutAwayHeader> getPutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber, String putAwayNumber) {
        List<PutAwayHeader> putAwayHeader =
                putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        putAwayNumber,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",putAwayNumber: " + putAwayNumber +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public List<PutAwayHeader> getPutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber) {
        List<PutAwayHeader> putAwayHeader =
                putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getPutawayHeaderByStatusId(String warehouseId, String preInboundNo, String refDocNumber) {
        long putAwayHeaderStatusIdCount = putAwayHeaderRepository.getPutawayHeaderCountByStatusId(getCompanyCode(), getPlantId(), warehouseId, preInboundNo, refDocNumber);
        return putAwayHeaderStatusIdCount;
    }

    /**
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<PutAwayHeader> getPutAwayHeader(String refDocNumber, String packBarcodes) {
        List<PutAwayHeader> putAwayHeader =
                putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        refDocNumber,
                        packBarcodes,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     * @param refDocNumber
     * @return
     */
    public List<PutAwayHeader> getPutAwayHeader(String refDocNumber) {
        List<Long> statusIds = Arrays.asList(19L, 20L);
        List<PutAwayHeader> putAwayHeader =
                putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        refDocNumber,
                        statusIds,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<PutAwayHeader> getPutAwayHeaderCount(String companyCodeId, String plantId, String languageId,
                                                     String warehouseId, List<Long> orderTypeId) {
        List<PutAwayHeader> header =
                putAwayHeaderRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndInboundOrderTypeIdInAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, 19L, orderTypeId, 0L);
        return header;
    }

    /**
     * @param searchPutAwayHeader
     * @return
     * @throws Exception
     */
    public List<PutAwayHeader> findPutAwayHeader(SearchPutAwayHeader searchPutAwayHeader)
            throws Exception {
        if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayHeader.getStartCreatedOn(),
                    searchPutAwayHeader.getEndCreatedOn());
            searchPutAwayHeader.setStartCreatedOn(dates[0]);
            searchPutAwayHeader.setEndCreatedOn(dates[1]);
        }

        PutAwayHeaderSpecification spec = new PutAwayHeaderSpecification(searchPutAwayHeader);
        List<PutAwayHeader> results = putAwayHeaderRepository.findAll(spec);
        return results;
    }

    /**
     * @param searchPutAwayHeader
     * @return
     * @throws Exception
     */
    //Stream
    public Stream<PutAwayHeader> findPutAwayHeaderNew(SearchPutAwayHeader searchPutAwayHeader)
            throws Exception {
        if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayHeader.getStartCreatedOn(),
                    searchPutAwayHeader.getEndCreatedOn());
            searchPutAwayHeader.setStartCreatedOn(dates[0]);
            searchPutAwayHeader.setEndCreatedOn(dates[1]);
        }

        PutAwayHeaderSpecification spec = new PutAwayHeaderSpecification(searchPutAwayHeader);
        Stream<PutAwayHeader> results = putAwayHeaderRepository.stream(spec, PutAwayHeader.class).parallel();
        return results;
    }

    /**
     * createPutAwayHeader
     *
     * @param newPutAwayHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PutAwayHeader createPutAwayHeader(AddPutAwayHeader newPutAwayHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayHeader dbPutAwayHeader = new PutAwayHeader();
        log.info("newPutAwayHeader : " + newPutAwayHeader);
        BeanUtils.copyProperties(newPutAwayHeader, dbPutAwayHeader, CommonUtils.getNullPropertyNames(newPutAwayHeader));
        dbPutAwayHeader.setDeletionIndicator(0L);
        dbPutAwayHeader.setCreatedBy(loginUserID);
        dbPutAwayHeader.setUpdatedBy(loginUserID);
        dbPutAwayHeader.setCreatedOn(new Date());
        dbPutAwayHeader.setUpdatedOn(new Date());
        return putAwayHeaderRepository.save(dbPutAwayHeader);
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param putAwayNumber
     * @param proposedStorageBin
     * @param loginUserID
     * @param updatePutAwayHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PutAwayHeader updatePutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                                             String palletCode, String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin,
                                             String loginUserID, UpdatePutAwayHeader updatePutAwayHeader)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayHeader dbPutAwayHeader = getPutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo,
                palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin);
        BeanUtils.copyProperties(updatePutAwayHeader, dbPutAwayHeader, CommonUtils.getNullPropertyNames(updatePutAwayHeader));
        dbPutAwayHeader.setUpdatedBy(loginUserID);
        dbPutAwayHeader.setUpdatedOn(new Date());
        return putAwayHeaderRepository.save(dbPutAwayHeader);
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<PutAwayHeader> putAwayHeaders = getPutAwayHeaders();
        putAwayHeaders.forEach(p -> p.setReferenceField1(asnNumber));
        putAwayHeaderRepository.saveAll(putAwayHeaders);
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PutAwayHeader> updatePutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber, Long statusId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<PutAwayHeader> dbPutAwayHeaderList = getPutAwayHeader(warehouseId, preInboundNo, refDocNumber);
        List<PutAwayHeader> updatedPutAwayHeaderList = new ArrayList<>();
        for (PutAwayHeader dbPutAwayHeader : dbPutAwayHeaderList) {
            dbPutAwayHeader.setStatusId(statusId);
            dbPutAwayHeader.setUpdatedBy(loginUserID);
            dbPutAwayHeader.setUpdatedOn(new Date());
            updatedPutAwayHeaderList.add(putAwayHeaderRepository.save(dbPutAwayHeader));
        }
        return updatedPutAwayHeaderList;
    }

    /**
     * @param refDocNumber
     * @param packBarcodes
     * @param loginUserID
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<PutAwayHeader> updatePutAwayHeader(String refDocNumber, String packBarcodes, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        String warehouseId = null;
        String caseCode = null;
        String palletCode = null;
        String storageBin = null;

        /*
         * Pass WH_ID/REF_DOC_NO/PACK_BARCODE values in PUTAWAYHEADER table and fetch STATUS_ID value and PA_NO
         * 1. If STATUS_ID=20, then
         */
        List<PutAwayHeader> putAwayHeaderList = getPutAwayHeader(refDocNumber, packBarcodes);
        List<GrLine> grLineList = grLineService.getGrLine(refDocNumber, packBarcodes);

        // Fetching Item Code
        String itemCode = null;
        if (grLineList != null) {
            itemCode = grLineList.get(0).getItemCode();
        }

        for (PutAwayHeader dbPutAwayHeader : putAwayHeaderList) {
            warehouseId = dbPutAwayHeader.getWarehouseId();
            palletCode = dbPutAwayHeader.getPalletCode();
            caseCode = dbPutAwayHeader.getCaseCode();
            storageBin = dbPutAwayHeader.getProposedStorageBin();

            log.info("dbPutAwayHeader---------> : " + dbPutAwayHeader.getWarehouseId() + "," + refDocNumber + "," + dbPutAwayHeader.getPutAwayNumber());
            if (dbPutAwayHeader.getStatusId() == 20L) {
                /*
                 * Checking whether Line Items have updated with STATUS_ID = 22.
                 */
                long STATUS_ID_22_COUNT = putAwayLineService.getPutAwayLineByStatusId(warehouseId, dbPutAwayHeader.getPutAwayNumber(), refDocNumber, 22L);
                log.info("putAwayLine---STATUS_ID_22_COUNT---> : " + STATUS_ID_22_COUNT);
                if (STATUS_ID_22_COUNT > 0) {
                    throw new BadRequestException("Pallet_ID : " + dbPutAwayHeader.getPalletCode() + " is already reversed.");
                }

                /*
                 * Pass WH_ID/REF_DOC_NO/PA_NO values in PUTAWAYLINE table and fetch PA_CNF_QTY values and QTY_TYPE values and
                 * update Status ID as 22, PA_UTD_BY = USR_ID and PA_UTD_ON=Server time
                 */
                List<PutAwayLine> putAwayLineList =
                        putAwayLineService.getPutAwayLine(dbPutAwayHeader.getWarehouseId(), refDocNumber, dbPutAwayHeader.getPutAwayNumber());
                log.info("putAwayLineList : " + putAwayLineList);
                for (PutAwayLine dbPutAwayLine : putAwayLineList) {
                    log.info("dbPutAwayLine---------> : " + dbPutAwayLine);

                    itemCode = dbPutAwayLine.getItemCode();

                    /*
                     * On Successful reversal, update INVENTORY table as below
                     * Pass WH_ID/PACK_BARCODE/ITM_CODE values in Inventory table and delete the records
                     */
                    boolean isDeleted = inventoryService.deleteInventory(warehouseId, packBarcodes, itemCode);
                    log.info("deleteInventory deleted.." + isDeleted);

                    if (isDeleted) {
                        dbPutAwayLine.setStatusId(22L);
                        dbPutAwayLine.setConfirmedBy(loginUserID);
                        dbPutAwayLine.setUpdatedBy(loginUserID);
                        dbPutAwayLine.setConfirmedOn(new Date());
                        dbPutAwayLine.setUpdatedOn(new Date());
                        dbPutAwayLine = putAwayLineRepository.save(dbPutAwayLine);
                        log.info("dbPutAwayLine updated: " + dbPutAwayLine);
                    }

                    /*
                     * Pass WH_ID/REF_DOC_NO/IB_LINE_NO/ ITM_CODE values in Inboundline table and update
                     * If QTY_TYPE = A, update ACCEPT_QTY as (ACCEPT_QTY-PA_CNF_QTY)
                     * if QTY_TYPE= D, update DAMAGE_QTY as (DAMAGE_QTY-PA_CNF_QTY)
                     */
                    InboundLine inboundLine = inboundLineService.getInboundLine(dbPutAwayHeader.getWarehouseId(), refDocNumber, dbPutAwayHeader.getPreInboundNo(),
                            dbPutAwayLine.getLineNo(), dbPutAwayLine.getItemCode());
                    if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                        Double acceptedQty = inboundLine.getAcceptedQty() - dbPutAwayLine.getPutawayConfirmedQty();
                        inboundLine.setAcceptedQty(acceptedQty);
                    }

                    if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                        Double damageQty = inboundLine.getDamageQty() - dbPutAwayLine.getPutawayConfirmedQty();
                        inboundLine.setAcceptedQty(damageQty);
                    }

                    if (isDeleted) {
                        // Updating InboundLine only if Inventory got deleted
                        InboundLine updatedInboundLine = inboundLineRepository.save(inboundLine);
                        log.info("updatedInboundLine : " + updatedInboundLine);
                    }
                }
            }

            /*
             * 3. For STATUS_ID=19 and 20 , below tables to be updated
             * Pass the selected REF_DOC_NO/PACK_BARCODE values  and PUTAWAYHEADER tables and update Status ID as 22 and
             * PA_UTD_BY = USR_ID and PA_UTD_ON=Server time and fetch CASE_CODE
             */
            if (dbPutAwayHeader.getStatusId() == 19L) {
                log.info("---#---deleteInventory: " + warehouseId + "," + packBarcodes + "," + itemCode);
                boolean isDeleted = inventoryService.deleteInventory(warehouseId, packBarcodes, itemCode);
                log.info("---#---deleteInventory deleted.." + isDeleted);

                if (isDeleted) {
                    dbPutAwayHeader.setStatusId(22L);
                    dbPutAwayHeader.setUpdatedBy(loginUserID);
                    dbPutAwayHeader.setUpdatedOn(new Date());
                    PutAwayHeader updatedPutAwayHeader = putAwayHeaderRepository.save(dbPutAwayHeader);
                    log.info("updatedPutAwayHeader : " + updatedPutAwayHeader);
                }
            }
        }

        // Insert a record into INVENTORYMOVEMENT table as below
        if(grLineList != null && !grLineList.isEmpty()) {
            for (GrLine grLine : grLineList) {
                createInventoryMovement(grLine, caseCode, palletCode, storageBin);
            }
        }

        return putAwayHeaderList;
    }

    /**
     * @param grLine
     * @param caseCode
     * @param storageBin
     */
    private void createInventoryMovement(GrLine grLine, String caseCode, String palletCode, String storageBin) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(grLine, inventoryMovement, CommonUtils.getNullPropertyNames(grLine));

        inventoryMovement.setCompanyCodeId(grLine.getCompanyCode());

        // CASE_CODE
        inventoryMovement.setCaseCode(caseCode);

        // PAL_CODE
        inventoryMovement.setPalletCode(palletCode);

        // MVT_TYP_ID
        inventoryMovement.setMovementType(1L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(3L);

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(grLine.getRefDocNumber());

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY
        inventoryMovement.setMovementQty(grLine.getGoodReceiptQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue("N");

        // BAL_OH_QTY
        // PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
        List<Inventory> inventoryList = inventoryService.getInventory(grLine.getWarehouseId(), grLine.getItemCode(), 1L);
        double sumOfInvQty = inventoryList.stream().mapToDouble(a -> a.getInventoryQuantity()).sum();
        inventoryMovement.setBalanceOHQty(sumOfInvQty);

        // MVT_UOM
        inventoryMovement.setInventoryUom(grLine.getGrUom());

        // PACK_BARCODES
        inventoryMovement.setPackBarcodes(grLine.getPackBarcodes());

        // ITEM_CODE
        inventoryMovement.setItemCode(grLine.getItemCode());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(grLine.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(grLine.getCreatedOn());
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("inventoryMovement created: " + inventoryMovement);
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param statusId
     * @param loginUserID
     */
    public void updatePutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber, Long lineNo,
                                    String itemCode, Long statusId, String loginUserID) {
        List<PutAwayHeader> putAwayHeaderList = getPutAwayHeader(warehouseId, preInboundNo, refDocNumber);
        for (PutAwayHeader dbPutAwayHeader : putAwayHeaderList) {
            dbPutAwayHeader.setStatusId(statusId);
            dbPutAwayHeader.setUpdatedBy(loginUserID);
            dbPutAwayHeader.setUpdatedOn(new Date());
            putAwayHeaderRepository.save(dbPutAwayHeader);
        }

        // Line
        List<PutAwayLine> putAwayLineList =
                putAwayLineService.getPutAwayLine(warehouseId, preInboundNo, refDocNumber, lineNo, itemCode);
        for (PutAwayLine dbPutAwayLine : putAwayLineList) {
            dbPutAwayLine.setStatusId(statusId);
            dbPutAwayLine.setUpdatedBy(loginUserID);
            dbPutAwayLine.setUpdatedOn(new Date());
            putAwayLineRepository.save(dbPutAwayLine);
        }
        log.info("PutAwayHeader & Line updated..");
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param putAwayNumber
     * @param proposedStorageBin
     * @param loginUserID
     */
    public void deletePutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                                    String palletCode, String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin, String loginUserID) {
        PutAwayHeader putAwayHeader = getPutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo,
                palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin);
        if (putAwayHeader != null) {
            putAwayHeader.setDeletionIndicator(1L);
            putAwayHeader.setUpdatedBy(loginUserID);
            putAwayHeaderRepository.save(putAwayHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + putAwayNumber);
        }
    }

    //==============================================================V2==========================================================================

    /**
     * @param putAwayNumber
     * @return
     */
    public PutAwayHeaderV2 getPutawayHeaderV2(String putAwayNumber) {
        PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.getPutAwayHeaderV2(putAwayNumber);
        return dbPutAwayHeader;
    }

    public PutAwayHeaderV2 getPutawayHeaderV2(String companyId, String plantId, String warehouseId, String languageId, String putAwayNumber) {
        PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPutAwayNumberAndDeletionIndicator(
                companyId, plantId, warehouseId, languageId, putAwayNumber, 0L);
        return dbPutAwayHeader;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<PutAwayHeaderV2> getPutawayHeaderExistingBinItemCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String itemCode, String manufacturerName) {
        List<PutAwayHeaderV2> dbPutAwayHeader = putAwayHeaderV2Repository.
                findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
                        companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 19L, 0L);
        return dbPutAwayHeader;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @return
     */
    public PutAwayHeaderV2 getPutawayHeaderExistingBinItemCheckV2(String companyCodeId, String plantId, String languageId,
                                                                  String warehouseId, String itemCode) {
        PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.
                findTopByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndStatusIdAndDeletionIndicatorOrderByCreatedOn(
                        companyCodeId, plantId, warehouseId, languageId, itemCode, 19L, 0L);
        return dbPutAwayHeader;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public PutAwayHeaderV2 getPutAwayHeaderExistingItemCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                               String itemCode, String manufacturerName) {
        PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.
                findTopByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
                        companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 19L, 0L);
        if (dbPutAwayHeader != null) {
            return dbPutAwayHeader;
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public List<String> getPutawayHeaderExistingBinCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId) {
        List<PutAwayHeaderV2> dbPutAwayHeader = putAwayHeaderV2Repository.
                findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndStatusIdAndDeletionIndicator(
                        companyCodeId, plantId, warehouseId, languageId, 19L, 0L);
        if (dbPutAwayHeader != null) {
            List<String> storageBin = dbPutAwayHeader.stream().map(PutAwayHeaderV2::getProposedStorageBin).collect(Collectors.toList());
            return storageBin;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getPutawayHeaderByStatusIdV2(String companyId, String plantId, String warehouseId,
                                             String preInboundNo, String refDocNumber) {
        long putAwayHeaderStatusIdCount = putAwayHeaderV2Repository.getPutawayHeaderCountByStatusId(companyId, plantId, warehouseId, preInboundNo, refDocNumber);
        return putAwayHeaderStatusIdCount;
    }

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getPutawayHeaderForInboundConfirmV2(String companyId, String plantId, String warehouseId, String preInboundNo,
                                                    String refDocNumber, String itemCode, String manufacturerName, Long inboundLineNumber) {
        long putAwayHeaderStatusIdCount = putAwayHeaderRepository.getPutawayHeaderForInboundConfirm(companyId, plantId, warehouseId, preInboundNo, refDocNumber, itemCode, manufacturerName, inboundLineNumber);
        return putAwayHeaderStatusIdCount;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderV2(String warehouseId, String preInboundNo,
                                                    String refDocNumber, String putAwayNumber,
                                                    String companyCodeId, String plantId,
                                                    String languageId) {
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        putAwayNumber,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",putAwayNumber: " + putAwayNumber +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     *
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public PutAwayHeaderV2 getPutAwayHeaderV2ForPutAwayLine(String warehouseId, String preInboundNo, String refDocNumber, String putAwayNumber,
                                                            String companyCodeId, String plantId, String languageId) {
        PutAwayHeaderV2 putAwayHeader =
                putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        languageId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        putAwayNumber,
                        0L
                );
        if (putAwayHeader == null) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",putAwayNumber: " + putAwayNumber +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNumber
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderV2ForPutAwayLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                  String preInboundNo, String refDocNumber, String lineNumber,
                                                                  String itemCode, String manufacturerName) {
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndReferenceField5AndManufacturerNameAndReferenceField9AndStatusIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        languageId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        19L,
                        0L
                );
        if (putAwayHeader == null) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",itemCode: " + itemCode +
                    ",manufacturerName: " + manufacturerName +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param putAwayNumber
     * @param proposedStorageBin
     * @return
     */
    public PutAwayHeaderV2 getPutAwayHeaderV2(String companyCode, String plantId, String languageId,
                                              String warehouseId, String preInboundNo, String refDocNumber,
                                              String goodsReceiptNo, String palletCode, String caseCode,
                                              String packBarcodes, String putAwayNumber, String proposedStorageBin) {
        Optional<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndPutAwayNumberAndProposedStorageBinAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        goodsReceiptNo,
                        palletCode,
                        caseCode,
                        packBarcodes,
                        putAwayNumber,
                        proposedStorageBin,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",goodsReceiptNo: " + goodsReceiptNo +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",packBarcodes: " + packBarcodes +
                    ",putAwayNumber: " + putAwayNumber +
                    ",proposedStorageBin: " + proposedStorageBin +
                    " doesn't exist.");
        }
        return putAwayHeader.get();
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String packBarcodes) {
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        packBarcodes,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param putawayNumber
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderForReversalV2(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String putawayNumber) {
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        putawayNumber,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    "companyCode: " + companyCode + "," +
                    "plantId: " + plantId + "," +
                    "languageId: " + languageId + "," +
                    "warehouseId: " + warehouseId + "," +
                    "refDocNumber: " + refDocNumber + "," +
                    "putawayNumber: " + putawayNumber + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNumber
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderForPutAwayConfirm(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNumber) {
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNumber,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            return null;
        }
        return putAwayHeader;
    }


    /**
     * @param searchPutAwayHeader
     * @return
     * @throws Exception
     */
    public List<PutAwayHeaderV2> findPutAwayHeaderV2(SearchPutAwayHeaderV2 searchPutAwayHeader) throws Exception {
        if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayHeader.getStartCreatedOn(),
                    searchPutAwayHeader.getEndCreatedOn());
            searchPutAwayHeader.setStartCreatedOn(dates[0]);
            searchPutAwayHeader.setEndCreatedOn(dates[1]);
        }
        log.info("searchPutAwayHeader V2: " + searchPutAwayHeader);
        if(searchPutAwayHeader.getBarcodeId() != null && !searchPutAwayHeader.getBarcodeId().isEmpty()) {
            List<String> trimmedBarcodes = searchPutAwayHeader.getBarcodeId().stream().filter(n -> n != null && !n.isBlank()).map(String::trim).distinct().collect(Collectors.toList());
            searchPutAwayHeader.setBarcodeId(trimmedBarcodes);
        }
        log.info("Trimmed searchPutAwayHeader V2: " + searchPutAwayHeader);
        PutAwayHeaderV2Specification spec = new PutAwayHeaderV2Specification(searchPutAwayHeader);
        List<PutAwayHeaderV2> results = putAwayHeaderV2Repository.findAll(spec);
        log.info("putAwayHeader results:" + results.size());
        return results;
    }

    /**
     * SQL - Method - to set Inventory Qty
     * @param searchPutAwayHeader
     * @return
     * @throws Exception
     */
    public List<PutAwayHeaderImpl> findPutAwayHeaderSQLV2(SearchPutAwayHeaderV2 searchPutAwayHeader)
            throws Exception {
        if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayHeader.getStartCreatedOn(),
                    searchPutAwayHeader.getEndCreatedOn());
            searchPutAwayHeader.setStartCreatedOn(dates[0]);
            searchPutAwayHeader.setEndCreatedOn(dates[1]);
        }
        log.info("searchPutAwayHeader V2: " + searchPutAwayHeader);

        if(searchPutAwayHeader.getCompanyCodeId() == null || searchPutAwayHeader.getCompanyCodeId().isEmpty()){
            searchPutAwayHeader.setCompanyCodeId(null);
        }
        if (searchPutAwayHeader.getPlantId() == null || searchPutAwayHeader.getPlantId().isEmpty()) {
            searchPutAwayHeader.setPlantId(null);
        }
        if (searchPutAwayHeader.getLanguageId() == null || searchPutAwayHeader.getLanguageId().isEmpty()) {
            searchPutAwayHeader.setLanguageId(null);
        }
        if (searchPutAwayHeader.getWarehouseId() == null || searchPutAwayHeader.getWarehouseId().isEmpty()) {
            searchPutAwayHeader.setWarehouseId(null);
        }
        if (searchPutAwayHeader.getItemCode() == null || searchPutAwayHeader.getItemCode().isEmpty()) {
            searchPutAwayHeader.setItemCode(null);
        }
        if (searchPutAwayHeader.getManufacturerName() == null || searchPutAwayHeader.getManufacturerName().isEmpty()) {
            searchPutAwayHeader.setManufacturerName(null);
        }
        if (searchPutAwayHeader.getManufacturerCode() == null || searchPutAwayHeader.getManufacturerCode().isEmpty()) {
            searchPutAwayHeader.setManufacturerCode(null);
        }
        if (searchPutAwayHeader.getRefDocNumber() == null || searchPutAwayHeader.getRefDocNumber().isEmpty()) {
            searchPutAwayHeader.setRefDocNumber(null);
        }
        if (searchPutAwayHeader.getPreInboundNo() == null || searchPutAwayHeader.getPreInboundNo().isEmpty()) {
            searchPutAwayHeader.setPreInboundNo(null);
        }
        if (searchPutAwayHeader.getPackBarcodes() == null || searchPutAwayHeader.getPackBarcodes().isEmpty()) {
            searchPutAwayHeader.setPackBarcodes(null);
        }
        if (searchPutAwayHeader.getPutAwayNumber() == null || searchPutAwayHeader.getPutAwayNumber().isEmpty()) {
            searchPutAwayHeader.setPutAwayNumber(null);
        }
        if (searchPutAwayHeader.getProposedHandlingEquipment() == null || searchPutAwayHeader.getProposedHandlingEquipment().isEmpty()) {
            searchPutAwayHeader.setProposedHandlingEquipment(null);
        }
        if (searchPutAwayHeader.getProposedStorageBin() == null || searchPutAwayHeader.getProposedStorageBin().isEmpty()) {
            searchPutAwayHeader.setProposedStorageBin(null);
        }
        if (searchPutAwayHeader.getCreatedBy() == null || searchPutAwayHeader.getCreatedBy().isEmpty()) {
            searchPutAwayHeader.setCreatedBy(null);
        }
        if (searchPutAwayHeader.getBarcodeId() == null || searchPutAwayHeader.getBarcodeId().isEmpty()) {
            searchPutAwayHeader.setBarcodeId(null);
        }
        if (searchPutAwayHeader.getOrigin() == null || searchPutAwayHeader.getOrigin().isEmpty()) {
            searchPutAwayHeader.setOrigin(null);
        }
        if (searchPutAwayHeader.getBrand() == null || searchPutAwayHeader.getBrand().isEmpty()) {
            searchPutAwayHeader.setBrand(null);
        }
        if (searchPutAwayHeader.getApprovalStatus() == null || searchPutAwayHeader.getApprovalStatus().isEmpty()) {
            searchPutAwayHeader.setApprovalStatus(null);
        }
        if (searchPutAwayHeader.getStatusId() == null || searchPutAwayHeader.getStatusId().isEmpty()) {
            searchPutAwayHeader.setStatusId(null);
        }
        if (searchPutAwayHeader.getStartCreatedOn() == null || searchPutAwayHeader.getEndCreatedOn() == null) {
            searchPutAwayHeader.setStartCreatedOn(null);
        }
        if(searchPutAwayHeader.getMaterialNo() != null && searchPutAwayHeader.getMaterialNo().isEmpty()){
            searchPutAwayHeader.setMaterialNo(null);
        }
        if(searchPutAwayHeader.getPriceSegment() != null && searchPutAwayHeader.getPriceSegment().isEmpty()){
            searchPutAwayHeader.setPriceSegment(null);
        }
        if(searchPutAwayHeader.getArticleNo() != null && searchPutAwayHeader.getArticleNo().isEmpty()){
            searchPutAwayHeader.setArticleNo(null);
        }
        if(searchPutAwayHeader.getGender() != null && searchPutAwayHeader.getGender().isEmpty()){
            searchPutAwayHeader.setGender(null);
        }
        if(searchPutAwayHeader.getColor() != null && searchPutAwayHeader.getColor().isEmpty()){
            searchPutAwayHeader.setColor(null);
        }
        if(searchPutAwayHeader.getSize() != null && searchPutAwayHeader.getSize().isEmpty()){
            searchPutAwayHeader.setSize(null);
        }
        if(searchPutAwayHeader.getNoPairs() != null && searchPutAwayHeader.getNoPairs().isEmpty()) {
            searchPutAwayHeader.setNoPairs(null);
        }
        if(searchPutAwayHeader.getBarcodeId() != null && searchPutAwayHeader.getBarcodeId().isEmpty()) {
            searchPutAwayHeader.setBarcodeId(null);
        }
        if(searchPutAwayHeader.getPalletId() != null && searchPutAwayHeader.getPalletId().isEmpty()) {
            searchPutAwayHeader.setPalletId(null);
        }
        if(searchPutAwayHeader.getBarcodeId() != null && !searchPutAwayHeader.getBarcodeId().isEmpty()) {
            List<String> trimmedBarcodes = searchPutAwayHeader.getBarcodeId().stream().filter(n -> n != null && !n.isBlank()).map(String::trim).distinct().collect(Collectors.toList());
            searchPutAwayHeader.setBarcodeId(trimmedBarcodes);
        }
        log.info("SQL Trimmed searchPutAwayHeader V2: " + searchPutAwayHeader);
        List<PutAwayHeaderImpl> results = putAwayHeaderV2Repository.findPutAwayHeader(
                searchPutAwayHeader.getCompanyCodeId(),
                searchPutAwayHeader.getPlantId(),
                searchPutAwayHeader.getLanguageId(),
                searchPutAwayHeader.getWarehouseId(),
                searchPutAwayHeader.getItemCode(),
                searchPutAwayHeader.getManufacturerName(),
                searchPutAwayHeader.getRefDocNumber(),
                searchPutAwayHeader.getPreInboundNo(),
                searchPutAwayHeader.getPackBarcodes(),
                searchPutAwayHeader.getPutAwayNumber(),
                searchPutAwayHeader.getProposedStorageBin(),
                searchPutAwayHeader.getProposedHandlingEquipment(),
                searchPutAwayHeader.getCreatedBy(),
                searchPutAwayHeader.getBarcodeId(),
                searchPutAwayHeader.getManufacturerCode(),
                searchPutAwayHeader.getOrigin(),
                searchPutAwayHeader.getBrand(),
                searchPutAwayHeader.getApprovalStatus(),
                searchPutAwayHeader.getStatusId(),
                searchPutAwayHeader.getInboundOrderTypeId(),
                searchPutAwayHeader.getMaterialNo(),
                searchPutAwayHeader.getPriceSegment(),
                searchPutAwayHeader.getArticleNo(),
                searchPutAwayHeader.getGender(),
                searchPutAwayHeader.getColor(),
                searchPutAwayHeader.getSize(),
                searchPutAwayHeader.getNoPairs(),
                searchPutAwayHeader.getPalletId(),
                searchPutAwayHeader.getStartCreatedOn(),
                searchPutAwayHeader.getEndCreatedOn());
        log.info("putAwayHeader results:" + results.size());
        return results;
    }

    public List<PutAwayHeaderV2> getPutAwayHeaderforUpdateV2(String companyCode, String plantId, String languageId,
                                                             String warehouseId, String preInboundNo, String refDocNumber) {
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param refDocNumber
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber) {
        List<Long> statusIds = Arrays.asList(19L, 20L);
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        statusIds,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderForCancellationV2(String companyCode, String plantId, String languageId,
                                                                   String warehouseId, String refDocNumber, String preInboundNo) {
        List<Long> statusIds = Arrays.asList(19L, 20L);
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndStatusIdInAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        statusIds,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderCountV2(String warehouseId, List<Long> orderTypeId) {
        List<PutAwayHeaderV2> header =
                putAwayHeaderV2Repository.findByWarehouseIdAndStatusIdAndInboundOrderTypeIdInAndDeletionIndicator(
                        warehouseId, 19L, orderTypeId, 0L);
        return header;
    }

    /**
     * @param newPutAwayHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PutAwayHeaderV2 createPutAwayHeaderV2(PutAwayHeaderV2 newPutAwayHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PutAwayHeaderV2 dbPutAwayHeader = new PutAwayHeaderV2();
        log.info("newPutAwayHeader : " + newPutAwayHeader);
        BeanUtils.copyProperties(newPutAwayHeader, dbPutAwayHeader, CommonUtils.getNullPropertyNames(newPutAwayHeader));

        IKeyValuePair description = stagingLineV2Repository.getDescription(newPutAwayHeader.getCompanyCodeId(),
                newPutAwayHeader.getLanguageId(),
                newPutAwayHeader.getPlantId(),
                newPutAwayHeader.getWarehouseId());

        dbPutAwayHeader.setCompanyDescription(description.getCompanyDesc());
        dbPutAwayHeader.setPlantDescription(description.getPlantDesc());
        dbPutAwayHeader.setWarehouseDescription(description.getWarehouseDesc());

        dbPutAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(
                newPutAwayHeader.getCompanyCodeId(),
                newPutAwayHeader.getPlantId(),
                newPutAwayHeader.getLanguageId(),
                newPutAwayHeader.getWarehouseId(),
                newPutAwayHeader.getInboundOrderTypeId()));

        dbPutAwayHeader.setDeletionIndicator(0L);
        dbPutAwayHeader.setCreatedBy(loginUserID);
        dbPutAwayHeader.setUpdatedBy(loginUserID);
        dbPutAwayHeader.setCreatedOn(new Date());
        dbPutAwayHeader.setUpdatedOn(new Date());
        return putAwayHeaderV2Repository.save(dbPutAwayHeader);
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param putAwayNumber
     * @param proposedStorageBin
     * @param loginUserID
     * @param updatePutAwayHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PutAwayHeaderV2 updatePutAwayHeaderV2(String companyCode, String plantId, String languageId,
                                                 String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                                                 String palletCode, String caseCode, String packBarcodes, String putAwayNumber,
                                                 String proposedStorageBin, String loginUserID, PutAwayHeaderV2 updatePutAwayHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PutAwayHeaderV2 dbPutAwayHeader = getPutAwayHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, goodsReceiptNo,
                palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin);
        BeanUtils.copyProperties(updatePutAwayHeader, dbPutAwayHeader, CommonUtils.getNullPropertyNames(updatePutAwayHeader));
        dbPutAwayHeader.setUpdatedBy(loginUserID);
        dbPutAwayHeader.setUpdatedOn(new Date());
        return putAwayHeaderV2Repository.save(dbPutAwayHeader);
    }

    //new api for Assign HHT in the putaway Header 22-12-2023

    public List<PutAwayHeaderV2> updatePutAwayHeaderBatchV2(String loginUserID, List<PutAwayHeaderV2> updatePutAwayHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        List<PutAwayHeaderV2> putAwayHeaderV2List = new ArrayList<>();

        for (PutAwayHeaderV2 putAwayHeaderV2 : updatePutAwayHeader) {
            PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.findByPutAwayNumberAndDeletionIndicator(putAwayHeaderV2.getPutAwayNumber(), 0L);
            if (dbPutAwayHeader != null) {
                BeanUtils.copyProperties(putAwayHeaderV2, dbPutAwayHeader, CommonUtils.getNullPropertyNames(putAwayHeaderV2));
                dbPutAwayHeader.setUpdatedBy(loginUserID);
                dbPutAwayHeader.setUpdatedOn(new Date());
                PutAwayHeaderV2 savePutAway = putAwayHeaderV2Repository.save(dbPutAwayHeader);
                putAwayHeaderV2List.add(savePutAway);
            }
        }
        return putAwayHeaderV2List;
    }

    @Transactional
    public List<PutAwayHeaderV2> updatePutAwayHeaderMobileApp(String loginUserID, List<PutAwayHeaderV2> updatePutAwayHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        List<PutAwayHeaderV2> putAwayHeaderV2List = new ArrayList<>();

        for (PutAwayHeaderV2 putAwayHeaderV2 : updatePutAwayHeader) {
            List<PutAwayHeaderV2> dbPutAwayHeader = putAwayHeaderV2Repository.findByDeletionIndicatorAndPutAwayNumber(
                    0L, putAwayHeaderV2.getPutAwayNumber());

            dbPutAwayHeader.forEach(putaway -> {
                if (dbPutAwayHeader != null) {
                    // Copy only non-null values
                    BeanUtils.copyProperties(putAwayHeaderV2, dbPutAwayHeader, CommonUtils.getNullPropertyNames(putAwayHeaderV2));

                    // Fetch HHT users
                    List<HHTUser> hhtUserList = putAwayHeaderV2Repository.getHHtDetails(loginUserID);
                    log.info("hhtUserList -----> {}", hhtUserList);

                    if (hhtUserList.get(0).getTeamMember1() != null) {
                        putaway.setTeamMember1(hhtUserList.get(0).getTeamMember1());
                    }
                    if (hhtUserList.get(0).getTeamMember2() != null) {
                        putaway.setTeamMember2(hhtUserList.get(0).getTeamMember2());
                    }
                    if (hhtUserList.get(0).getTeamMember3() != null) {
                        putaway.setTeamMember3(hhtUserList.get(0).getTeamMember3());
                    }
                    if (hhtUserList.get(0).getTeamMember4() != null) {
                        putaway.setTeamMember4(hhtUserList.get(0).getTeamMember4());
                    }
                    if (hhtUserList.get(0).getTeamMember5() != null) {
                        putaway.setTeamMember5(hhtUserList.get(0).getTeamMember5());
                    }

                    putaway.setAssignedUserId(loginUserID);
                    putaway.setUpdatedBy(loginUserID);
                    putaway.setUpdatedOn(new Date());

                    // Save updated entity
                    PutAwayHeaderV2 savePutAway = putAwayHeaderV2Repository.save(putaway);
                    putAwayHeaderV2List.add(savePutAway);
                }
            });
        }

        return putAwayHeaderV2List;
    }

    @Transactional
    public void updatePutAwayFieldsDynamic(String refDocNo, String palletId, String userId, List<HHTUser> hhtUsers) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE tblputawayheader SET ass_user_id = :userId");

        // Dynamically append based on the size of hhtUsers
        for (int i = 0; i < 5; i++) {
            queryBuilder.append(", TEAM_MEM_").append(i + 1).append(" = :teamMember").append(i + 1);
        }

        queryBuilder.append(" WHERE ref_doc_no = :refDocNo AND pal_code = :palletId AND IS_DELETED = 0");

        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        query.setParameter("userId", userId);
        query.setParameter("refDocNo", refDocNo);
        query.setParameter("palletId", palletId);

        // Set parameters safely (nulls for extra slots)
        if (hhtUsers != null && !hhtUsers.isEmpty()) {
            HHTUser hhtUser = hhtUsers.get(0); // Use only the first one

            query.setParameter("teamMember1", hhtUser.getTeamMember1());
            query.setParameter("teamMember2", hhtUser.getTeamMember2());
            query.setParameter("teamMember3", hhtUser.getTeamMember3());
            query.setParameter("teamMember4", hhtUser.getTeamMember4());
            query.setParameter("teamMember5", hhtUser.getTeamMember5());
        } else {
            // fallback to nulls
            for (int i = 1; i <= 5; i++) {
                query.setParameter("teamMember" + i, null);
            }
        }

        query.executeUpdate();
    }

//    /**
//     * @param searchPutAwayHeader
//     * @return
//     * @throws Exception
//     */
//    public List<PutAwayPalletGroupResponse> findPutAwayHeaderMobApp(SearchPutAwayHeaderV2 searchPutAwayHeader) throws Exception {
//
//        if (searchPutAwayHeader.getFromConfirmedDate() != null && searchPutAwayHeader.getToConfirmedDate() != null) {
//            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayHeader.getFromConfirmedDate(), searchPutAwayHeader.getToConfirmedDate());
//            searchPutAwayHeader.setFromConfirmedDate(dates[0]);
//            searchPutAwayHeader.setToConfirmedDate(dates[1]);
//        }
//
//        searchPutAwayHeader.setStatusId(Arrays.asList(19L));
//
//        log.info("searchPutAwayHeader -------> {}", searchPutAwayHeader);
//
//        PutAwayHeaderV2Specification spec = new PutAwayHeaderV2Specification(searchPutAwayHeader);
//        List<PutAwayHeaderV2> results = putAwayHeaderV2Repository.findAll(spec);
//
//        // Grouping by custom key: refDocNumber, palletId, proposedStorageBin
//        Map<PutAwayGroupKey, List<PutAwayHeaderV2>> groupedMap = results.stream()
//                .collect(Collectors.groupingBy(p -> new PutAwayGroupKey(p.getPalletId(), p.getProposedStorageBin())));
//
//        List<PutAwayPalletGroupResponse> palletGroupResponses = new ArrayList<>();
//        // Set group count
//        for (Map.Entry<PutAwayGroupKey, List<PutAwayHeaderV2>> entry : groupedMap.entrySet()) {
//            List<PutAwayHeaderV2> groupList = entry.getValue();
//            long count = groupList.size();
//            PutAwayHeaderV2 item = groupList.get(0); // pick one representative item
//
//            PutAwayPalletGroupResponse response = new PutAwayPalletGroupResponse();
//            log.info("item -----> {}", item);
//            response.setGroupCount(count);
//            response.setCreatedOn(item.getCreatedOn());
//            response.setCreatedBy(item.getAssignedUserId());
//            response.setPalletId(item.getPalletId());
//            response.setProposedStorageBin(item.getProposedStorageBin());
//            response.setRefDocNumber(item.getRefDocNumber());
//            response.setAssignedUserId(item.getAssignedUserId());
//            response.setPutAwayNumber(item.getPutAwayNumber());
//
//            palletGroupResponses.add(response);
//        }
//
//
//        return palletGroupResponses;
//    }




    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PutAwayHeaderV2> updatePutAwayHeaderV2(String companyCode, String plantId, String languageId,
                                                       String warehouseId, String preInboundNo, String refDocNumber,
                                                       Long statusId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<PutAwayHeaderV2> dbPutAwayHeaderList = getPutAwayHeaderforUpdateV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
        List<PutAwayHeaderV2> updatedPutAwayHeaderList = new ArrayList<>();
        for (PutAwayHeaderV2 dbPutAwayHeader : dbPutAwayHeaderList) {
            dbPutAwayHeader.setStatusId(statusId);
            if (statusId != null) {
                statusDescription = stagingLineV2Repository.getStatusDescription(statusId, languageId);
                dbPutAwayHeader.setStatusDescription(statusDescription);
            }
            dbPutAwayHeader.setUpdatedBy(loginUserID);
            dbPutAwayHeader.setUpdatedOn(new Date());
            updatedPutAwayHeaderList.add(putAwayHeaderV2Repository.save(dbPutAwayHeader));
        }
        return updatedPutAwayHeaderList;
    }

    /**
     *
     * @param inboundReversalInputList
     * @param loginUserID
     * @return
     * @throws ParseException
     */
    @Transactional
    public List<PutAwayHeaderV2> batchPutAwayReversal(List<InboundReversalInput> inboundReversalInputList, String loginUserID) throws ParseException {
        log.info("PutAway Reversal Input: " + inboundReversalInputList);
        if(inboundReversalInputList != null && !inboundReversalInputList.isEmpty()) {
            for (InboundReversalInput inboundReversalInput : inboundReversalInputList){
                updatePutAwayHeaderReversalBatch(
                        inboundReversalInput.getCompanyCodeId(),
                        inboundReversalInput.getPlantId(),
                        inboundReversalInput.getLanguageId(),
                        inboundReversalInput.getWarehouseId(),
                        inboundReversalInput.getRefDocNumber(),
                        inboundReversalInput.getPackBarcodes(),
                        inboundReversalInput.getPutAwayNumber(),
                        loginUserID);
            }
        }
        return null;
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param packBarcodes
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PutAwayHeaderV2> updatePutAwayHeaderReversalBatch(String companyCode, String plantId, String languageId, String warehouseId,
                                                                  String refDocNumber, String packBarcodes, String putAwayNumber, String loginUserID) throws ParseException {

        log.info("Inbound Reversal Initiated : order Number, putaway Number ----> " + refDocNumber + ", " + putAwayNumber);
        String caseCode = null;
        String palletCode = null;
        String storageBin = null;
        String preInboundNo = null;

        boolean capacityCheck = false;
        boolean storageBinCapacityCheck = false;

        Double invQty = 0D;
        Double itemLength = 0D;
        Double itemWidth = 0D;
        Double itemHeight = 0D;
        Double itemVolume = 0D;
        Double remainingVolume = 0D;
        Double occupiedVolume = 0D;
        Double totalVolume = 0D;
        Double reversalVolume = 0D;
        /*
         * Pass WH_ID/REF_DOC_NO/PACK_BARCODE values in PUTAWAYHEADER table and fetch STATUS_ID value and PA_NO
         * 1. If STATUS_ID=20, then
         */
        List<PutAwayHeaderV2> putAwayHeaderList = getPutAwayHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber, putAwayNumber);
//        List<GrLineV2> grLineList = grLineService.getGrLineV2ForReversal(companyCode, languageId, plantId, warehouseId, refDocNumber, packBarcodes);
        List<GrLineV2> grLineList = null;

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

        // Fetching Item Code
        String itemCode = null;
        String manufactureName = null;
//        if (grLineList != null && !grLineList.isEmpty()) {
//            itemCode = grLineList.get(0).getItemCode();
//            manufactureName = grLineList.get(0).getManufacturerName();
//        }

        for (PutAwayHeaderV2 dbPutAwayHeader : putAwayHeaderList) {
            warehouseId = dbPutAwayHeader.getWarehouseId();
            palletCode = dbPutAwayHeader.getPalletCode();
            caseCode = dbPutAwayHeader.getCaseCode();
            storageBin = dbPutAwayHeader.getProposedStorageBin();
            preInboundNo = dbPutAwayHeader.getPreInboundNo();
            itemCode = dbPutAwayHeader.getReferenceField5();
            manufactureName = dbPutAwayHeader.getManufacturerName();

            if(dbPutAwayHeader.getStatusId() == 24L) {
                throw new BadRequestException("Putaway already confirmed cannot be reversed: " + dbPutAwayHeader.getPutAwayNumber());
            }

            log.info("dbPutAwayHeader---------> : " + dbPutAwayHeader.getWarehouseId() + "," + refDocNumber + "," + dbPutAwayHeader.getPutAwayNumber());
            if (dbPutAwayHeader.getStatusId() == 20L) {
                /*
                 * Checking whether Line Items have updated with STATUS_ID = 22.
                 */
                long STATUS_ID_22_COUNT = putAwayLineService.getPutAwayLineByStatusIdV2(companyCode, plantId, languageId, warehouseId,
                        dbPutAwayHeader.getPutAwayNumber(), refDocNumber, 22L);
                log.info("putAwayLine---STATUS_ID_22_COUNT---> : " + STATUS_ID_22_COUNT);
                if (STATUS_ID_22_COUNT > 0) {
                    throw new BadRequestException("Pallet_ID : " + dbPutAwayHeader.getPalletCode() + " is already reversed.");
                }

                /*
                 * Pass WH_ID/REF_DOC_NO/PA_NO values in PUTAWAYLINE table and fetch PA_CNF_QTY values and QTY_TYPE values and
                 * update Status ID as 22, PA_UTD_BY = USR_ID and PA_UTD_ON=Server time
                 */
                List<PutAwayLineV2> putAwayLineList =
                        putAwayLineService.getPutAwayLineV2ForReversal(dbPutAwayHeader.getCompanyCodeId(),
                                dbPutAwayHeader.getPlantId(),
                                dbPutAwayHeader.getLanguageId(),
                                dbPutAwayHeader.getWarehouseId(),
                                refDocNumber,
                                dbPutAwayHeader.getPutAwayNumber());
                log.info("putAwayLineList : " + putAwayLineList);
                if(putAwayLineList != null && !putAwayLineList.isEmpty()) {
                    for (PutAwayLineV2 dbPutAwayLine : putAwayLineList) {
                        log.info("dbPutAwayLine---------> : " + dbPutAwayLine);

                        itemCode = dbPutAwayLine.getItemCode();
                        manufactureName = dbPutAwayLine.getManufacturerName();

                        ImBasicData imBasicData = new ImBasicData();
                        imBasicData.setCompanyCodeId(companyCode);
                        imBasicData.setPlantId(plantId);
                        imBasicData.setLanguageId(languageId);
                        imBasicData.setWarehouseId(warehouseId);
                        imBasicData.setItemCode(itemCode);
                        imBasicData.setManufacturerName(manufactureName);
                        ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                        log.info("ImbasicData1 : " + itemCodeCapacityCheck);
                        if(itemCodeCapacityCheck != null) {
                            if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                                capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                                log.info("capacity Check: " + capacityCheck);
                            }
                        }

                        InventoryV2 updateStorageBin =
                                inventoryService.getInventoryForReversalV2(companyCode, plantId, languageId, warehouseId, "99999", itemCode, manufactureName);
                        log.info("Inventory for Delete: " + updateStorageBin);
//                    createInventoryMovement = inventoryService.getInventoryForInvMmt(companyCode, plantId, languageId, warehouseId, itemCode, manufactureName, 1L);

                        /*
                         * On Successful reversal, update INVENTORY table as below
                         * Pass WH_ID/PACK_BARCODE/ITM_CODE values in Inventory table and delete the records
                         */
                        boolean isDeleted = false;
                        if(updateStorageBin != null) {
                            InventoryV2 deleteInventory = new InventoryV2();
                            BeanUtils.copyProperties(updateStorageBin, deleteInventory, CommonUtils.getNullPropertyNames(updateStorageBin));
                            deleteInventory.setInventoryQuantity(updateStorageBin.getInventoryQuantity() - dbPutAwayHeader.getPutAwayQuantity());
                            deleteInventory.setReferenceField4(deleteInventory.getInventoryQuantity());         //Allocated Qty is always 0 for BinClassId 3
//                        deleteInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                            InventoryV2 createInventory = inventoryV2Repository.save(deleteInventory);
                            log.info("Delete Inventory Inserted: " + createInventory);
                            isDeleted = true;
                        }
                        log.info("deleteInventory deleted.." + isDeleted);

                        StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                dbPutAwayLine.getPreInboundNo(), dbPutAwayLine.getItemCode(), dbPutAwayLine.getManufacturerName(), dbPutAwayLine.getLineNo());

                        if (dbStagingLineEntity != null) {

                            Double rec_accept_qty = 0D;
                            Double rec_damage_qty = 0D;
                            if(dbPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                                rec_accept_qty = (dbStagingLineEntity.getRec_accept_qty() != null ? dbStagingLineEntity.getRec_accept_qty() : 0) - (dbPutAwayLine.getPutawayConfirmedQty() != null ? dbPutAwayLine.getPutawayConfirmedQty() : 0);
                            }
                            if(dbPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                                rec_damage_qty = (dbStagingLineEntity.getRec_damage_qty() != null ? dbStagingLineEntity.getRec_damage_qty() : 0) - (dbPutAwayLine.getPutawayConfirmedQty() != null ? dbPutAwayLine.getPutawayConfirmedQty() : 0);
                            }

                            dbStagingLineEntity.setRec_accept_qty(rec_accept_qty);
                            dbStagingLineEntity.setRec_damage_qty(rec_damage_qty);
                            dbStagingLineEntity.setStatusId(14L);
                            statusDescription = stagingLineV2Repository.getStatusDescription(14L, languageId);
                            dbStagingLineEntity.setStatusDescription(statusDescription);
                            stagingLineV2Repository.save(dbStagingLineEntity);
                            log.info("stagingLineEntity rec_accept_damage_qty and status updated: " + dbStagingLineEntity);
                        }

                        if (isDeleted) {
                            StorageBinV2 dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 3L, updateStorageBin.getStorageBin());
                            log.info("dbStorageBin: " + dbstorageBin);

                            if (dbstorageBin != null) {

                                storageBinCapacityCheck = dbstorageBin.isCapacityCheck();
                                log.info("storageBinCapacityCheck: " + storageBinCapacityCheck);

                                if (capacityCheck && storageBinCapacityCheck) {
                                    if (updateStorageBin.getInventoryQuantity() != null) {
                                        invQty = updateStorageBin.getInventoryQuantity();
                                    }
                                    if (itemCodeCapacityCheck.getLength() != null) {
                                        itemLength = itemCodeCapacityCheck.getLength();
                                    }
                                    if (itemCodeCapacityCheck.getWidth() != null) {
                                        itemWidth = itemCodeCapacityCheck.getWidth();
                                    }
                                    if (itemCodeCapacityCheck.getHeight() != null) {
                                        itemHeight = itemCodeCapacityCheck.getHeight();
                                    }

                                    itemVolume = itemLength * itemWidth * itemHeight;
                                    reversalVolume = itemVolume * invQty;

                                    log.info("item Length, Width, Height: " + itemLength + ", " + itemWidth + "," + itemHeight);
                                    log.info("item volume, invQty, reversalVolume: " + itemVolume + ", " + invQty + "," + reversalVolume);

                                    if (dbstorageBin.getRemainingVolume() != null) {
                                        remainingVolume = Double.valueOf(dbstorageBin.getRemainingVolume());
                                        log.info("remainingVolume: " + dbstorageBin.getRemainingVolume());
                                    }
                                    if (dbstorageBin.getOccupiedVolume() != null) {
                                        occupiedVolume = Double.valueOf(dbstorageBin.getOccupiedVolume());
                                    }
                                    if (dbstorageBin.getTotalVolume() != null) {
                                        totalVolume = Double.valueOf(dbstorageBin.getTotalVolume());
                                    }

                                    log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                                    remainingVolume = remainingVolume + reversalVolume;
                                    occupiedVolume = occupiedVolume - reversalVolume;

                                    log.info("after reversal remainingVolume, occupiedVolume, totalVolume: " + remainingVolume + ", " + occupiedVolume + ", " + totalVolume);

                                    if (remainingVolume.equals(totalVolume) && (occupiedVolume == 0D || occupiedVolume == 0 || occupiedVolume == 0.0)) {
                                        dbstorageBin.setStatusId(0L);
                                        log.info("status Id: 0 [Storage Bin Emptied]");
                                    }
                                    dbstorageBin.setRemainingVolume(String.valueOf(remainingVolume));
                                    dbstorageBin.setOccupiedVolume(String.valueOf(occupiedVolume));
                                    dbstorageBin.setCapacityCheck(true);

                                    StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(dbstorageBin.getStorageBin(),
                                            dbstorageBin,
                                            companyCode,
                                            plantId,
                                            languageId,
                                            warehouseId,
                                            loginUserID,
                                            authTokenForMastersService.getAccess_token());

                                    if (updateStorageBinV2 != null) {
                                        log.info("Storage Bin Volume Updated successfully ");
                                    }
                                }
                            }
                            //End - CBM StorageBin Update

//                        dbPutAwayLine.setStatusId(22L);
//                        statusDescription = stagingLineV2Repository.getStatusDescription(22L, languageId);
//                        dbPutAwayLine.setStatusDescription(statusDescription);

                            //delete code
                            dbPutAwayLine.setDeletionIndicator(1L);

                            dbPutAwayLine.setConfirmedBy(loginUserID);
                            dbPutAwayLine.setUpdatedBy(loginUserID);
                            dbPutAwayLine.setConfirmedOn(new Date());
                            dbPutAwayLine.setUpdatedOn(new Date());
                            dbPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                            log.info("dbPutAwayLine updated: " + dbPutAwayLine);
                        }

                        /*
                         * Pass WH_ID/REF_DOC_NO/IB_LINE_NO/ ITM_CODE values in Inboundline table and update
                         * If QTY_TYPE = A, update ACCEPT_QTY as (ACCEPT_QTY-PA_CNF_QTY)
                         * if QTY_TYPE= D, update DAMAGE_QTY as (DAMAGE_QTY-PA_CNF_QTY)
                         */
                        InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(
                                dbPutAwayHeader.getCompanyCodeId(), dbPutAwayHeader.getPlantId(),
                                dbPutAwayHeader.getLanguageId(), dbPutAwayHeader.getWarehouseId(),
                                refDocNumber, dbPutAwayHeader.getPreInboundNo(),
                                dbPutAwayLine.getLineNo(), dbPutAwayLine.getItemCode());
                        if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                            Double acceptedQty = inboundLine.getAcceptedQty() - dbPutAwayLine.getPutawayConfirmedQty();
                            log.info("Accepted Qty: " + acceptedQty);
                            inboundLine.setAcceptedQty(acceptedQty);
                            Double VAR_QTY = 0D;
                            if(inboundLine.getVarianceQty() != null) {
                                VAR_QTY = inboundLine.getVarianceQty() - acceptedQty;
                            }
                            inboundLine.setVarianceQty(VAR_QTY);
                        }

                        if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                            Double damageQty = inboundLine.getDamageQty() - dbPutAwayLine.getPutawayConfirmedQty();
                            log.info("Damage Qty: " + damageQty);
                            inboundLine.setDamageQty(damageQty);
                            Double VAR_QTY = 0D;
                            if(inboundLine.getVarianceQty() != null) {
                                VAR_QTY = inboundLine.getVarianceQty() - damageQty;
                            }
                            inboundLine.setVarianceQty(VAR_QTY);
                        }

                        if (isDeleted) {
                            // Updating InboundLine only if Inventory got deleted
                            InboundLineV2 updatedInboundLine = inboundLineV2Repository.save(inboundLine);
                            log.info("updatedInboundLine : " + updatedInboundLine);
                        }
                    }
                }
            }

            /*
             * 3. For STATUS_ID=19 and 20 , below tables to be updated
             * Pass the selected REF_DOC_NO/PACK_BARCODE values  and PUTAWAYHEADER tables and update Status ID as 22 and
             * PA_UTD_BY = USR_ID and PA_UTD_ON=Server time and fetch CASE_CODE
             */
            if (dbPutAwayHeader.getStatusId() == 19L) {

//                List<InventoryV2> updateStorageBinList = inventoryService.getInventoryForDeleteV2(companyCode, plantId, languageId, warehouseId, packBarcodes, itemCode, manufactureName);
                InventoryV2 updateStorageBin = inventoryService.getInventoryForReversalV2(companyCode, plantId, languageId, warehouseId, "99999", itemCode, manufactureName);
                log.info("Inventory for Delete: " + updateStorageBin);
//                createInventoryMovement = inventoryService.getInventoryForInvMmt(companyCode, plantId, languageId, warehouseId, itemCode, manufactureName, 1L);

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(companyCode);
                imBasicData.setPlantId(plantId);
                imBasicData.setLanguageId(languageId);
                imBasicData.setWarehouseId(warehouseId);
                imBasicData.setItemCode(itemCode);
                imBasicData.setManufacturerName(manufactureName);
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + itemCodeCapacityCheck);
                if(itemCodeCapacityCheck != null) {
                    if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                        capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                        log.info("capacity Check: " + capacityCheck);
                    }
                }

                log.info("---#---deleteInventory: " + warehouseId + "," + packBarcodes + "," + itemCode);
                boolean isDeleted = false;
                if(updateStorageBin != null){
                    InventoryV2 deleteInventory = new InventoryV2();
                    BeanUtils.copyProperties(updateStorageBin, deleteInventory, CommonUtils.getNullPropertyNames(updateStorageBin));
                    deleteInventory.setInventoryQuantity(updateStorageBin.getInventoryQuantity() - dbPutAwayHeader.getPutAwayQuantity());
                    deleteInventory.setReferenceField4(deleteInventory.getInventoryQuantity());         //Allocated Qty is always 0 for BinClassId 3
                    deleteInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                    InventoryV2 createInventory = inventoryV2Repository.save(deleteInventory);
                    log.info("Delete Inventory Inserted: " + createInventory);
                    isDeleted = true;
                }

                log.info("---#---deleteInventory deleted.." + isDeleted);

                Long lineNo = dbPutAwayHeader.getReferenceField9() != null ? Long.valueOf(dbPutAwayHeader.getReferenceField9()) : 0;

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                        dbPutAwayHeader.getPreInboundNo(), dbPutAwayHeader.getReferenceField5(), dbPutAwayHeader.getManufacturerName(), lineNo);

                if (dbStagingLineEntity != null) {
                    Double rec_accept_qty = 0D;
                    Double rec_damage_qty = 0D;
                    if(dbPutAwayHeader.getQuantityType().equalsIgnoreCase("A")) {
                        rec_accept_qty = (dbStagingLineEntity.getRec_accept_qty() != null ? dbStagingLineEntity.getRec_accept_qty() : 0) - (dbPutAwayHeader.getPutAwayQuantity() != null ? dbPutAwayHeader.getPutAwayQuantity() : 0);
                    }
                    if(dbPutAwayHeader.getQuantityType().equalsIgnoreCase("D")) {
                        rec_damage_qty = (dbStagingLineEntity.getRec_damage_qty() != null ? dbStagingLineEntity.getRec_damage_qty() : 0) - (dbPutAwayHeader.getPutAwayQuantity() != null ? dbPutAwayHeader.getPutAwayQuantity() : 0);
                    }

                    dbStagingLineEntity.setRec_accept_qty(rec_accept_qty);
                    dbStagingLineEntity.setRec_damage_qty(rec_damage_qty);
                    dbStagingLineEntity.setStatusId(14L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(14L, languageId);
                    dbStagingLineEntity.setStatusDescription(statusDescription);
                    stagingLineV2Repository.save(dbStagingLineEntity);
                    log.info("stagingLineEntity rec_accept_damage_qty and status updated: " + dbStagingLineEntity);
                }

                if (isDeleted) {
                    StorageBinV2 dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 1L, updateStorageBin.getStorageBin());
                    log.info("dbStorageBin: " + dbstorageBin);

                    if (dbstorageBin == null) {
                        dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 1L, storageBin);
                    }

                    if (dbstorageBin != null) {

                        storageBinCapacityCheck = dbstorageBin.isCapacityCheck();
                        log.info("storageBinCapacityCheck: " + storageBinCapacityCheck);

                        if (capacityCheck && storageBinCapacityCheck) {
                            if (updateStorageBin.getInventoryQuantity() != null) {
                                invQty = updateStorageBin.getInventoryQuantity();
                            }
                            if (itemCodeCapacityCheck.getLength() != null) {
                                itemLength = itemCodeCapacityCheck.getLength();
                            }
                            if (itemCodeCapacityCheck.getWidth() != null) {
                                itemWidth = itemCodeCapacityCheck.getWidth();
                            }
                            if (itemCodeCapacityCheck.getHeight() != null) {
                                itemHeight = itemCodeCapacityCheck.getHeight();
                            }

                            itemVolume = itemLength * itemWidth * itemHeight;
                            reversalVolume = itemVolume * invQty;

                            log.info("item Length, Width, Height: " + itemLength + ", " + itemWidth + "," + itemHeight);
                            log.info("item volume, invQty, reversalVolume: " + itemVolume + ", " + invQty + "," + reversalVolume);

                            if (dbstorageBin.getRemainingVolume() != null) {
                                remainingVolume = Double.valueOf(dbstorageBin.getRemainingVolume());
                                log.info("remainingVolume: " + dbstorageBin.getRemainingVolume());
                            }
                            if (dbstorageBin.getOccupiedVolume() != null) {
                                occupiedVolume = Double.valueOf(dbstorageBin.getOccupiedVolume());
                            }
                            if (dbstorageBin.getTotalVolume() != null) {
                                totalVolume = Double.valueOf(dbstorageBin.getTotalVolume());
                            }

                            log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            remainingVolume = remainingVolume + reversalVolume;
                            occupiedVolume = occupiedVolume - reversalVolume;

                            log.info("after reversal remainingVolume, occupiedVolume, totalVolume: " + remainingVolume + ", " + occupiedVolume + ", " + totalVolume);

                            if (remainingVolume.equals(totalVolume) && (occupiedVolume == 0D || occupiedVolume == 0 || occupiedVolume == 0.0)) {
                                dbstorageBin.setStatusId(0L);
                                log.info("status Id: 0 [Storage Bin Emptied]");
                            }
                            dbstorageBin.setRemainingVolume(String.valueOf(remainingVolume));
                            dbstorageBin.setOccupiedVolume(String.valueOf(occupiedVolume));
                            dbstorageBin.setCapacityCheck(true);

                            StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(dbstorageBin.getStorageBin(),
                                    dbstorageBin,
                                    companyCode,
                                    plantId,
                                    languageId,
                                    warehouseId,
                                    loginUserID,
                                    authTokenForMastersService.getAccess_token());

                            if (updateStorageBinV2 != null) {
                                log.info("Storage Bin Volume Updated successfully ");
                            }
                        }
                    }
                    //End - CBM StorageBin Update
                }
            }
            //delete code
            dbPutAwayHeader.setStatusId(22L);
            statusDescription = stagingLineV2Repository.getStatusDescription(22L, languageId);
            dbPutAwayHeader.setStatusDescription(statusDescription);
            dbPutAwayHeader.setUpdatedBy(loginUserID);
            dbPutAwayHeader.setUpdatedOn(new Date());
            PutAwayHeaderV2 updatedPutAwayHeader = putAwayHeaderV2Repository.save(dbPutAwayHeader);
            log.info("updatedPutAwayHeader : " + updatedPutAwayHeader);

            Long lineNumber = dbPutAwayHeader.getReferenceField9() != null ? Long.valueOf(dbPutAwayHeader.getReferenceField9()) : 0;
            grLineList = grLineService.getGrLineV2ForReversal(companyCode, languageId, plantId, warehouseId, refDocNumber,
                    packBarcodes, dbPutAwayHeader.getReferenceField5(), dbPutAwayHeader.getManufacturerName(), lineNumber, preInboundNo);
            log.info("Grline Reversal: " + grLineList);

            //update the statusId to complete reversal process
            reversalProcess(grLineList, preInboundNo, companyCode, plantId, languageId, warehouseId, refDocNumber, loginUserID);
        }

        // Insert a record into INVENTORYMOVEMENT table as below
//        if(grLineList != null && !grLineList.isEmpty()) {
//            for (GrLineV2 grLine : grLineList) {
//                createInventoryMovementV2(grLine, createInventoryMovement, caseCode, palletCode, storageBin);
//            }
//        }

//        //update the statusId to complete reversal process
//        reversalProcess(grLineList, preInboundNo, companyCode, plantId, languageId, warehouseId, refDocNumber, loginUserID);
        reversalProcessUpdateHeader(preInboundNo, companyCode, plantId, languageId, warehouseId, refDocNumber, loginUserID);

        return putAwayHeaderList;
    }

    @Transactional
    public List<PutAwayHeaderV2> updatePutAwayHeaderV2(String companyCode, String plantId, String languageId,
                                                       String warehouseId, String refDocNumber, String packBarcodes, String loginUserID)
            throws ParseException {

        String caseCode = null;
        String palletCode = null;
        String storageBin = null;
        String preInboundNo = null;

        boolean capacityCheck = false;
        boolean storageBinCapacityCheck = false;

        Double invQty = 0D;
        Double itemLength = 0D;
        Double itemWidth = 0D;
        Double itemHeight = 0D;
        Double itemVolume = 0D;
        Double remainingVolume = 0D;
        Double occupiedVolume = 0D;
        Double totalVolume = 0D;
        Double reversalVolume = 0D;
        /*
         * Pass WH_ID/REF_DOC_NO/PACK_BARCODE values in PUTAWAYHEADER table and fetch STATUS_ID value and PA_NO
         * 1. If STATUS_ID=20, then
         */
        List<PutAwayHeaderV2> putAwayHeaderList = getPutAwayHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, packBarcodes);
        List<GrLineV2> grLineList = grLineService.getGrLineV2(companyCode, languageId, plantId, warehouseId, refDocNumber, packBarcodes);
        List<IInventoryImpl> createInventoryMovement = null;

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

        // Fetching Item Code
        String itemCode = null;
        String manufactureName = null;
        if (grLineList != null) {
            itemCode = grLineList.get(0).getItemCode();
            manufactureName = grLineList.get(0).getManufacturerName();
        }

        for (PutAwayHeaderV2 dbPutAwayHeader : putAwayHeaderList) {
            warehouseId = dbPutAwayHeader.getWarehouseId();
            palletCode = dbPutAwayHeader.getPalletCode();
            caseCode = dbPutAwayHeader.getCaseCode();
            storageBin = dbPutAwayHeader.getProposedStorageBin();
            preInboundNo = dbPutAwayHeader.getPreInboundNo();

            log.info("dbPutAwayHeader---------> : " + dbPutAwayHeader.getWarehouseId() + "," + refDocNumber + "," + dbPutAwayHeader.getPutAwayNumber());
            if (dbPutAwayHeader.getStatusId() == 20L) {
                /*
                 * Checking whether Line Items have updated with STATUS_ID = 22.
                 */
                long STATUS_ID_22_COUNT = putAwayLineService.getPutAwayLineByStatusIdV2(companyCode, plantId, languageId, warehouseId,
                        dbPutAwayHeader.getPutAwayNumber(), refDocNumber, 22L);
                log.info("putAwayLine---STATUS_ID_22_COUNT---> : " + STATUS_ID_22_COUNT);
                if (STATUS_ID_22_COUNT > 0) {
                    throw new BadRequestException("Pallet_ID : " + dbPutAwayHeader.getPalletCode() + " is already reversed.");
                }

                /*
                 * Pass WH_ID/REF_DOC_NO/PA_NO values in PUTAWAYLINE table and fetch PA_CNF_QTY values and QTY_TYPE values and
                 * update Status ID as 22, PA_UTD_BY = USR_ID and PA_UTD_ON=Server time
                 */
                List<PutAwayLineV2> putAwayLineList =
                        putAwayLineService.getPutAwayLineV2(dbPutAwayHeader.getCompanyCodeId(),
                                dbPutAwayHeader.getPlantId(),
                                dbPutAwayHeader.getLanguageId(),
                                dbPutAwayHeader.getWarehouseId(),
                                refDocNumber,
                                dbPutAwayHeader.getPutAwayNumber());
                log.info("putAwayLineList : " + putAwayLineList);
                for (PutAwayLineV2 dbPutAwayLine : putAwayLineList) {
                    log.info("dbPutAwayLine---------> : " + dbPutAwayLine);

                    itemCode = dbPutAwayLine.getItemCode();

                    ImBasicData imBasicData = new ImBasicData();
                    imBasicData.setCompanyCodeId(companyCode);
                    imBasicData.setPlantId(plantId);
                    imBasicData.setLanguageId(languageId);
                    imBasicData.setWarehouseId(warehouseId);
                    imBasicData.setItemCode(itemCode);
                    imBasicData.setManufacturerName(dbPutAwayLine.getManufacturerName());
                    ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                    log.info("ImbasicData1 : " + itemCodeCapacityCheck);
                    if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                        capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                        log.info("capacity Check: " + capacityCheck);
                    }

//                    List<IInventoryImpl> updateStorageBinList =
//                            inventoryService.getInventoryForDelete(companyCode, plantId, languageId, warehouseId, packBarcodes, itemCode, manufactureName);
                    InventoryV2 updateStorageBin =
                            inventoryService.getInventoryForReversalV2(companyCode, plantId, languageId, warehouseId, "99999", itemCode, manufactureName);
                    log.info("Inventory for Delete: " + updateStorageBin);
                    createInventoryMovement = inventoryService.getInventoryForInvMmt(companyCode, plantId, languageId, warehouseId, itemCode, manufactureName, 1L);

                    /*
                     * On Successful reversal, update INVENTORY table as below
                     * Pass WH_ID/PACK_BARCODE/ITM_CODE values in Inventory table and delete the records
                     */
//                    boolean isDeleted = inventoryService.deleteInventoryV2(companyCode, plantId, languageId, warehouseId, packBarcodes, itemCode, manufactureName);
                    boolean isDeleted = false;
//                    for (IInventoryImpl dbInventory : updateStorageBinList) {
                    if(updateStorageBin != null) {
                        InventoryV2 deleteInventory = new InventoryV2();
                        BeanUtils.copyProperties(updateStorageBin, deleteInventory, CommonUtils.getNullPropertyNames(updateStorageBin));
                        deleteInventory.setInventoryQuantity(updateStorageBin.getInventoryQuantity() - dbPutAwayHeader.getPutAwayQuantity());
                        deleteInventory.setReferenceField4(deleteInventory.getInventoryQuantity());         //Allocated Qty is always 0 for BinClassId 3
//                        deleteInventory.setAllocatedQuantity(0D);
                        deleteInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                        InventoryV2 createInventory = inventoryV2Repository.save(deleteInventory);
                        log.info("Delete Inventory Inserted: " + createInventory);
                        isDeleted = true;
                    }
                    log.info("deleteInventory deleted.." + isDeleted);

                    if (isDeleted) {
//                        for (IInventoryImpl updateStorageBin : updateStorageBinList) {
                        StorageBinV2 dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 1L, updateStorageBin.getStorageBin());
                        log.info("dbStorageBin: " + dbstorageBin);

                        if (dbstorageBin != null) {
                            StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                    dbPutAwayLine.getPreInboundNo(), dbPutAwayLine.getItemCode(), dbPutAwayLine.getManufacturerName(), dbPutAwayLine.getLineNo());

                            if (dbStagingLineEntity != null) {
//                                    Double rec_accept_qty = 0D;
//                                    Double rec_damage_qty = 0D;
//
//                                    if (updateStorageBin.getInventoryQuantity() != null) {
//                                        invQty = updateStorageBin.getInventoryQuantity();
//                                    }
//                                    if (dbStagingLineEntity.getRec_accept_qty() != null && dbPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
//                                        rec_accept_qty = dbStagingLineEntity.getRec_accept_qty() - invQty;
//                                        dbStagingLineEntity.setRec_accept_qty(rec_accept_qty);
//                                    }
//                                    if (dbStagingLineEntity.getRec_damage_qty() != null && dbPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
//                                        rec_damage_qty = dbStagingLineEntity.getRec_damage_qty() - invQty;
//                                        dbStagingLineEntity.setRec_damage_qty(rec_damage_qty);
//                                    }
//                                    log.info("invQty, rec_accept_qty, rec_damage_qty: " + invQty + ", " + rec_accept_qty + "," + rec_damage_qty);
                                dbStagingLineEntity.setStatusId(13L);
                                statusDescription = stagingLineV2Repository.getStatusDescription(13L, languageId);
                                dbStagingLineEntity.setStatusDescription(statusDescription);
                                stagingLineV2Repository.save(dbStagingLineEntity);
                                log.info("invQty, rec_accept_qty, rec_damage_qty updated successfully: ");
                            }

                            storageBinCapacityCheck = dbstorageBin.isCapacityCheck();
                            log.info("storageBinCapacityCheck: " + storageBinCapacityCheck);

                            if (capacityCheck && storageBinCapacityCheck) {
                                if (updateStorageBin.getInventoryQuantity() != null) {
                                    invQty = updateStorageBin.getInventoryQuantity();
                                }
                                if (itemCodeCapacityCheck.getLength() != null) {
                                    itemLength = itemCodeCapacityCheck.getLength();
                                }
                                if (itemCodeCapacityCheck.getWidth() != null) {
                                    itemWidth = itemCodeCapacityCheck.getWidth();
                                }
                                if (itemCodeCapacityCheck.getHeight() != null) {
                                    itemHeight = itemCodeCapacityCheck.getHeight();
                                }

                                itemVolume = itemLength * itemWidth * itemHeight;
                                reversalVolume = itemVolume * invQty;

                                log.info("item Length, Width, Height: " + itemLength + ", " + itemWidth + "," + itemHeight);
                                log.info("item volume, invQty, reversalVolume: " + itemVolume + ", " + invQty + "," + reversalVolume);

                                if (dbstorageBin.getRemainingVolume() != null) {
                                    remainingVolume = Double.valueOf(dbstorageBin.getRemainingVolume());
                                    log.info("remainingVolume: " + dbstorageBin.getRemainingVolume());
                                }
                                if (dbstorageBin.getOccupiedVolume() != null) {
                                    occupiedVolume = Double.valueOf(dbstorageBin.getOccupiedVolume());
                                }
                                if (dbstorageBin.getTotalVolume() != null) {
                                    totalVolume = Double.valueOf(dbstorageBin.getTotalVolume());
                                }

                                log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                                remainingVolume = remainingVolume + reversalVolume;
                                occupiedVolume = occupiedVolume - reversalVolume;

                                log.info("after reversal remainingVolume, occupiedVolume, totalVolume: " + remainingVolume + ", " + occupiedVolume + ", " + totalVolume);

                                if (remainingVolume.equals(totalVolume) && (occupiedVolume == 0D || occupiedVolume == 0 || occupiedVolume == 0.0)) {
                                    dbstorageBin.setStatusId(0L);
                                    log.info("status Id: 0 [Storage Bin Emptied]");
                                }
                                dbstorageBin.setRemainingVolume(String.valueOf(remainingVolume));
                                dbstorageBin.setOccupiedVolume(String.valueOf(occupiedVolume));
                                dbstorageBin.setCapacityCheck(true);

                                StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(dbstorageBin.getStorageBin(),
                                        dbstorageBin,
                                        companyCode,
                                        plantId,
                                        languageId,
                                        warehouseId,
                                        loginUserID,
                                        authTokenForMastersService.getAccess_token());

                                if (updateStorageBinV2 != null) {
                                    log.info("Storage Bin Volume Updated successfully ");
                                }
                            }
                        }
//                        }
                        //End - CBM StorageBin Update

//                        dbPutAwayLine.setStatusId(22L);
//                        statusDescription = stagingLineV2Repository.getStatusDescription(22L, languageId);
//                        dbPutAwayLine.setStatusDescription(statusDescription);

                        //delete code
                        dbPutAwayLine.setDeletionIndicator(1L);

                        dbPutAwayLine.setConfirmedBy(loginUserID);
                        dbPutAwayLine.setUpdatedBy(loginUserID);
                        dbPutAwayLine.setConfirmedOn(new Date());
                        dbPutAwayLine.setUpdatedOn(new Date());
                        dbPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                        log.info("dbPutAwayLine updated: " + dbPutAwayLine);
                    }

                    /*
                     * Pass WH_ID/REF_DOC_NO/IB_LINE_NO/ ITM_CODE values in Inboundline table and update
                     * If QTY_TYPE = A, update ACCEPT_QTY as (ACCEPT_QTY-PA_CNF_QTY)
                     * if QTY_TYPE= D, update DAMAGE_QTY as (DAMAGE_QTY-PA_CNF_QTY)
                     */
                    InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(
                            dbPutAwayHeader.getCompanyCodeId(), dbPutAwayHeader.getPlantId(),
                            dbPutAwayHeader.getLanguageId(), dbPutAwayHeader.getWarehouseId(),
                            refDocNumber, dbPutAwayHeader.getPreInboundNo(),
                            dbPutAwayLine.getLineNo(), dbPutAwayLine.getItemCode());
                    if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                        Double acceptedQty = inboundLine.getAcceptedQty() - dbPutAwayLine.getPutawayConfirmedQty();
                        log.info("Accepted Qty: " + acceptedQty);
                        inboundLine.setAcceptedQty(acceptedQty);
                        Double VAR_QTY = 0D;
                        if(inboundLine.getVarianceQty() != null) {
                            VAR_QTY = inboundLine.getVarianceQty() - acceptedQty;
                        }
                        inboundLine.setVarianceQty(VAR_QTY);
                    }

                    if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                        Double damageQty = inboundLine.getDamageQty() - dbPutAwayLine.getPutawayConfirmedQty();
                        log.info("Damage Qty: " + damageQty);
                        inboundLine.setDamageQty(damageQty);
                        Double VAR_QTY = 0D;
                        if(inboundLine.getVarianceQty() != null) {
                            VAR_QTY = inboundLine.getVarianceQty() - damageQty;
                        }
                        inboundLine.setVarianceQty(VAR_QTY);
                    }

                    if (isDeleted) {
                        // Updating InboundLine only if Inventory got deleted
                        InboundLineV2 updatedInboundLine = inboundLineV2Repository.save(inboundLine);
                        log.info("updatedInboundLine : " + updatedInboundLine);
                    }
                }
            }

            /*
             * 3. For STATUS_ID=19 and 20 , below tables to be updated
             * Pass the selected REF_DOC_NO/PACK_BARCODE values  and PUTAWAYHEADER tables and update Status ID as 22 and
             * PA_UTD_BY = USR_ID and PA_UTD_ON=Server time and fetch CASE_CODE
             */
            if (dbPutAwayHeader.getStatusId() == 19L) {

//                List<InventoryV2> updateStorageBinList = inventoryService.getInventoryForDeleteV2(companyCode, plantId, languageId, warehouseId, packBarcodes, itemCode, manufactureName);
                InventoryV2 updateStorageBin = inventoryService.getInventoryForReversalV2(companyCode, plantId, languageId, warehouseId, "99999", itemCode, manufactureName);
                log.info("Inventory for Delete: " + updateStorageBin);
                createInventoryMovement = inventoryService.getInventoryForInvMmt(companyCode, plantId, languageId, warehouseId, itemCode, manufactureName, 1L);

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(companyCode);
                imBasicData.setPlantId(plantId);
                imBasicData.setLanguageId(languageId);
                imBasicData.setWarehouseId(warehouseId);
                imBasicData.setItemCode(itemCode);
                imBasicData.setManufacturerName(manufactureName);
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + itemCodeCapacityCheck);
                if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                    capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                    log.info("capacity Check: " + capacityCheck);
                }

                log.info("---#---deleteInventory: " + warehouseId + "," + packBarcodes + "," + itemCode);
//                boolean isDeleted = inventoryService.deleteInventoryV2(companyCode, plantId, languageId, warehouseId, packBarcodes, itemCode, manufactureName);
                boolean isDeleted = false;
//                for (InventoryV2 dbInventory : updateStorageBinList) {
                if(updateStorageBin != null){
                    InventoryV2 deleteInventory = new InventoryV2();
                    BeanUtils.copyProperties(updateStorageBin, deleteInventory, CommonUtils.getNullPropertyNames(updateStorageBin));
                    deleteInventory.setInventoryQuantity(updateStorageBin.getInventoryQuantity() - dbPutAwayHeader.getPutAwayQuantity());
                    deleteInventory.setReferenceField4(deleteInventory.getInventoryQuantity());         //Allocated Qty is always 0 for BinClassId 3
//                    deleteInventory.setAllocatedQuantity(0D);
                    deleteInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                    InventoryV2 createInventory = inventoryV2Repository.save(deleteInventory);
                    log.info("Delete Inventory Inserted: " + createInventory);
                    isDeleted = true;
                }

                log.info("---#---deleteInventory deleted.." + isDeleted);

                if (isDeleted) {
//                    for (InventoryV2 updateStorageBin : updateStorageBinList) {
                    StorageBinV2 dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 1L, updateStorageBin.getStorageBin());
                    log.info("dbStorageBin: " + dbstorageBin);

                    if (dbstorageBin == null) {
                        dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 1L, storageBin);
                    }

                    if (dbstorageBin != null) {
                        Long lineNo = dbPutAwayHeader.getReferenceField9() != null ? Long.valueOf(dbPutAwayHeader.getReferenceField9()) : 0;

                        StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                dbPutAwayHeader.getPreInboundNo(), updateStorageBin.getItemCode(), updateStorageBin.getManufacturerName(),
                                dbPutAwayHeader.getCaseCode(), dbPutAwayHeader.getPalletCode(), lineNo);

                        if (dbStagingLineEntity != null) {
//                                Double rec_accept_qty = 0D;
//                                Double rec_damage_qty = 0D;
//                                if (updateStorageBin.getInventoryQuantity() != null) {
//                                    invQty = updateStorageBin.getInventoryQuantity();
//                                }
//                                if (dbStagingLineEntity.getRec_accept_qty() != null && dbPutAwayHeader.getQuantityType().equalsIgnoreCase("A")) {
//                                    rec_accept_qty = dbStagingLineEntity.getRec_accept_qty() - invQty;
//                                    dbStagingLineEntity.setRec_accept_qty(rec_accept_qty);
//                                }
//                                if (dbStagingLineEntity.getRec_damage_qty() != null && dbPutAwayHeader.getQuantityType().equalsIgnoreCase("D")) {
//                                    rec_damage_qty = dbStagingLineEntity.getRec_damage_qty() - invQty;
//                                    dbStagingLineEntity.setRec_damage_qty(rec_damage_qty);
//                                }
//                                log.info("invQty, rec_accept_qty, rec_damage_qty: " + invQty + ", " + rec_accept_qty + "," + rec_damage_qty);
                            dbStagingLineEntity.setStatusId(13L);
                            statusDescription = stagingLineV2Repository.getStatusDescription(13L, languageId);
                            dbStagingLineEntity.setStatusDescription(statusDescription);
                            stagingLineV2Repository.save(dbStagingLineEntity);
                            log.info("invQty, rec_accept_qty, rec_damage_qty updated successfully: ");
                        }

                        storageBinCapacityCheck = dbstorageBin.isCapacityCheck();
                        log.info("storageBinCapacityCheck: " + storageBinCapacityCheck);

                        if (capacityCheck && storageBinCapacityCheck) {
                            if (updateStorageBin.getInventoryQuantity() != null) {
                                invQty = updateStorageBin.getInventoryQuantity();
                            }
                            if (itemCodeCapacityCheck.getLength() != null) {
                                itemLength = itemCodeCapacityCheck.getLength();
                            }
                            if (itemCodeCapacityCheck.getWidth() != null) {
                                itemWidth = itemCodeCapacityCheck.getWidth();
                            }
                            if (itemCodeCapacityCheck.getHeight() != null) {
                                itemHeight = itemCodeCapacityCheck.getHeight();
                            }

                            itemVolume = itemLength * itemWidth * itemHeight;
                            reversalVolume = itemVolume * invQty;

                            log.info("item Length, Width, Height: " + itemLength + ", " + itemWidth + "," + itemHeight);
                            log.info("item volume, invQty, reversalVolume: " + itemVolume + ", " + invQty + "," + reversalVolume);

                            if (dbstorageBin.getRemainingVolume() != null) {
                                remainingVolume = Double.valueOf(dbstorageBin.getRemainingVolume());
                                log.info("remainingVolume: " + dbstorageBin.getRemainingVolume());
                            }
                            if (dbstorageBin.getOccupiedVolume() != null) {
                                occupiedVolume = Double.valueOf(dbstorageBin.getOccupiedVolume());
                            }
                            if (dbstorageBin.getTotalVolume() != null) {
                                totalVolume = Double.valueOf(dbstorageBin.getTotalVolume());
                            }

                            log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            remainingVolume = remainingVolume + reversalVolume;
                            occupiedVolume = occupiedVolume - reversalVolume;

                            log.info("after reversal remainingVolume, occupiedVolume, totalVolume: " + remainingVolume + ", " + occupiedVolume + ", " + totalVolume);

                            if (remainingVolume.equals(totalVolume) && (occupiedVolume == 0D || occupiedVolume == 0 || occupiedVolume == 0.0)) {
                                dbstorageBin.setStatusId(0L);
                                log.info("status Id: 0 [Storage Bin Emptied]");
                            }
                            dbstorageBin.setRemainingVolume(String.valueOf(remainingVolume));
                            dbstorageBin.setOccupiedVolume(String.valueOf(occupiedVolume));
                            dbstorageBin.setCapacityCheck(true);

                            StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(dbstorageBin.getStorageBin(),
                                    dbstorageBin,
                                    companyCode,
                                    plantId,
                                    languageId,
                                    warehouseId,
                                    loginUserID,
                                    authTokenForMastersService.getAccess_token());

                            if (updateStorageBinV2 != null) {
                                log.info("Storage Bin Volume Updated successfully ");
                            }
                        }
                    }
//                    }
                    //End - CBM StorageBin Update
//                    dbPutAwayHeader.setStatusId(22L);
//                    statusDescription = stagingLineV2Repository.getStatusDescription(22L, languageId);
//                    dbPutAwayHeader.setStatusDescription(statusDescription);
                }
            }
            //delete code
//            dbPutAwayHeader.setDeletionIndicator(1L);
            dbPutAwayHeader.setStatusId(22L);
            statusDescription = stagingLineV2Repository.getStatusDescription(22L, languageId);
            dbPutAwayHeader.setStatusDescription(statusDescription);
            dbPutAwayHeader.setUpdatedBy(loginUserID);
            dbPutAwayHeader.setUpdatedOn(new Date());
            PutAwayHeaderV2 updatedPutAwayHeader = putAwayHeaderV2Repository.save(dbPutAwayHeader);
            log.info("updatedPutAwayHeader : " + updatedPutAwayHeader);
        }

        // Insert a record into INVENTORYMOVEMENT table as below
//        if(grLineList != null && !grLineList.isEmpty()) {
//            for (GrLineV2 grLine : grLineList) {
//                createInventoryMovementV2(grLine, createInventoryMovement, caseCode, palletCode, storageBin);
//            }
//        }

        //update the statusId to complete reversal process
        reversalProcess(grLineList, preInboundNo, companyCode, plantId, languageId, warehouseId, refDocNumber, loginUserID);
        reversalProcessUpdateHeader(preInboundNo, companyCode, plantId, languageId, warehouseId, refDocNumber, loginUserID);

        return putAwayHeaderList;
    }

    /**
     * @param inputGrLineList
     * @param preInboundNo
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     */
    public void reversalProcess(List<GrLineV2> inputGrLineList, String preInboundNo,
                                String companyCode, String plantId, String languageId,
                                String warehouseId, String refDocNumber, String loginUserID) throws ParseException {
        // Update PREINBOUNDHEADER and PREINBOUNDLINE table with STATUS_ID = 05 and update the other fields from UI
        // PREINBOUNDLINE Update
        log.info("Line status revesal initiated ---> " );
        log.info("GrLine : " + inputGrLineList);
        if (inputGrLineList != null && !inputGrLineList.isEmpty()) {
            for (GrLineV2 grLine : inputGrLineList) {

                List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLineForInboundConfirmV2(
                        companyCode, plantId, languageId, warehouseId, refDocNumber,
                        grLine.getItemCode(), grLine.getManufacturerName(), grLine.getLineNo(), preInboundNo);
                log.info("PutawayLine List to check any partial Putaway done: " + putAwayLineList);

                if(putAwayLineList == null || putAwayLineList.isEmpty()) {
                    InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(companyCode,
                            plantId, languageId, warehouseId, refDocNumber,
                            grLine.getPreInboundNo(), grLine.getLineNo(), grLine.getItemCode());

                    inboundLine.setStatusId(14L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(14L, languageId);
                    inboundLine.setStatusDescription(statusDescription);
                    // warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine
                    InboundLineV2 updatedInboundLine = inboundLineV2Repository.save(inboundLine);
                    log.info("InboundLine status updated: " + updatedInboundLine);

                    //delete grline
                    grLine.setDeletionIndicator(1L);
                    grLineV2Repository.save(grLine);
                    log.info("grLine deleted successfully");
                }

                PreInboundLineEntityV2 preInboundLine = preInboundLineService.getPreInboundLineV2(
                        companyCode, plantId, languageId, grLine.getPreInboundNo(), warehouseId, refDocNumber, grLine.getLineNo(), grLine.getItemCode());

                preInboundLine.setStatusId(13L);
                statusDescription = stagingLineV2Repository.getStatusDescription(13L, languageId);
                preInboundLine.setStatusDescription(statusDescription);
                preInboundLine.setUpdatedBy(loginUserID);
                preInboundLine.setUpdatedOn(new Date());
                PreInboundLineEntityV2 updatedPreInboundLine = preInboundLineV2Repository.save(preInboundLine);
                log.info("preInboundLine status updated: " + updatedPreInboundLine);

//                StagingLineEntityV2 stagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, grLine.getItemCode(), grLine.getManufacturerName(), grLine.getLineNo());
//                if (stagingLineEntity != null) {
//                    Double rec_accept_qty = (stagingLineEntity.getRec_accept_qty() != null ? stagingLineEntity.getRec_accept_qty() : 0) - (grLine.getAcceptedQty() != null ? grLine.getAcceptedQty() : 0);
//                    Double rec_damage_qty = (stagingLineEntity.getRec_damage_qty() != null ? stagingLineEntity.getRec_damage_qty() : 0) - (grLine.getDamageQty() != null ? grLine.getDamageQty() : 0);
//
//                    stagingLineEntity.setRec_accept_qty(rec_accept_qty);
//                    stagingLineEntity.setRec_damage_qty(rec_damage_qty);
//                    stagingLineEntity.setStatusId(14L);
//                    statusDescription = stagingLineV2Repository.getStatusDescription(14L, grLine.getLanguageId());
//                    stagingLineEntity.setStatusDescription(statusDescription);
//                    stagingLineEntity = stagingLineV2Repository.save(stagingLineEntity);
//                    log.info("stagingLineEntity rec_accept_damage_qty and status updated: " + stagingLineEntity);
//                }

            }
        }

//        //Gr Header
//        GrHeaderV2 updateGrHeaderStatus = grHeaderService.getGrHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber);
//        log.info("GrHeader for Status Update: " + updateGrHeaderStatus);
//        if (updateGrHeaderStatus != null) {
//            updateGrHeaderStatus.setStatusId(16L);
//            statusDescription = stagingLineV2Repository.getStatusDescription(16L, languageId);
//            updateGrHeaderStatus.setStatusDescription(statusDescription);
//            grHeaderV2Repository.save(updateGrHeaderStatus);
//            log.info("GrHeader status updated successfully");
//        }
//
//        // PREINBOUNDHEADER Update
//        PreInboundHeaderEntityV2 preInboundHeader = preInboundHeaderService.
//                getPreInboundHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
//        log.info("preInboundHeader---found-------> : " + preInboundHeader);
//
//        preInboundHeader.setStatusId(5L);
//        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
//        preInboundHeader.setStatusDescription(statusDescription);
//        PreInboundHeaderEntity updatedPreInboundHeaderEntity = preInboundHeaderV2Repository.save(preInboundHeader);
//        log.info("PreInboundHeader status updated---@------> : " + updatedPreInboundHeaderEntity);
//
//
//        // Update INBOUNDHEADER
//
//        InboundHeaderV2 updateInboundHeader = inboundHeaderService.getInboundHeaderByEntityV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
//        updateInboundHeader.setStatusId(5L);
//        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
//        updateInboundHeader.setStatusDescription(statusDescription);
//        inboundHeaderV2Repository.saveAndFlush(updateInboundHeader);
//        log.info("optInboundHeader Status Updated: " + updateInboundHeader);
//
//
//        StagingHeaderV2 stagingHeader = stagingHeaderService.getStagingHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
//
//        // STATUS_ID
//        stagingHeader.setStatusId(14L);
//        statusDescription = stagingLineV2Repository.getStatusDescription(14L, preInboundHeader.getLanguageId());
//        stagingHeader.setStatusDescription(statusDescription);
//        stagingHeaderV2Repository.save(stagingHeader);
//        log.info("StagingHeader Status Updated: " + stagingHeader);
    }

    public void reversalProcessUpdateHeader(String preInboundNo, String companyCode, String plantId, String languageId,
                                            String warehouseId, String refDocNumber, String loginUserID) throws ParseException {
        // Update PREINBOUNDHEADER and PREINBOUNDLINE table with STATUS_ID = 05 and update the other fields from UI
        //Gr Header
        GrHeaderV2 updateGrHeaderStatus = grHeaderService.getGrHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
        log.info("GrHeader for Status Update: " + updateGrHeaderStatus);
        if (updateGrHeaderStatus != null) {
            updateGrHeaderStatus.setStatusId(16L);
            statusDescription = stagingLineV2Repository.getStatusDescription(16L, languageId);
            updateGrHeaderStatus.setStatusDescription(statusDescription);
            updateGrHeaderStatus.setUpdatedBy(loginUserID);
            grHeaderV2Repository.save(updateGrHeaderStatus);
            log.info("GrHeader status updated successfully");
        }

        // PREINBOUNDHEADER Update
        PreInboundHeaderEntityV2 preInboundHeader = preInboundHeaderService.
                getPreInboundHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
        log.info("preInboundHeader---found-------> : " + preInboundHeader);

        preInboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundHeader.setStatusDescription(statusDescription);
        preInboundHeader.setUpdatedBy(loginUserID);
        PreInboundHeaderEntity updatedPreInboundHeaderEntity = preInboundHeaderV2Repository.save(preInboundHeader);
        log.info("PreInboundHeader status updated---@------> : " + updatedPreInboundHeaderEntity);


        // Update INBOUNDHEADER

        InboundHeaderV2 updateInboundHeader = inboundHeaderService.getInboundHeaderByEntityV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
        updateInboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        updateInboundHeader.setStatusDescription(statusDescription);
        updateInboundHeader.setUpdatedBy(loginUserID);
        inboundHeaderV2Repository.saveAndFlush(updateInboundHeader);
        log.info("optInboundHeader Status Updated: " + updateInboundHeader);


        StagingHeaderV2 stagingHeader = stagingHeaderService.getStagingHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);

        // STATUS_ID
        stagingHeader.setStatusId(14L);
        statusDescription = stagingLineV2Repository.getStatusDescription(14L, preInboundHeader.getLanguageId());
        stagingHeader.setStatusDescription(statusDescription);
        stagingHeader.setUpdatedBy(loginUserID);
        stagingHeaderV2Repository.save(stagingHeader);
        log.info("StagingHeader Status Updated: " + stagingHeader);
    }

    /**
     * @param grLine
     * @param caseCode
     * @param palletCode
     * @param storageBin
     */
    private void createInventoryMovementV2(GrLineV2 grLine, List<IInventoryImpl> createInventoryMovement, String caseCode, String palletCode, String storageBin) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(grLine, inventoryMovement, CommonUtils.getNullPropertyNames(grLine));

        inventoryMovement.setCompanyCodeId(grLine.getCompanyCode());

        // CASE_CODE
        inventoryMovement.setCaseCode(caseCode);

        // PAL_CODE
        inventoryMovement.setPalletCode(palletCode);

        // MVT_TYP_ID
        inventoryMovement.setMovementType(1L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(3L);

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        inventoryMovement.setManufacturerName(grLine.getManufacturerName());

        inventoryMovement.setRefDocNumber(grLine.getRefDocNumber());
        inventoryMovement.setCompanyDescription(grLine.getCompanyDescription());
        inventoryMovement.setPlantDescription(grLine.getPlantDescription());
        inventoryMovement.setWarehouseDescription(grLine.getWarehouseDescription());
        inventoryMovement.setBarcodeId(grLine.getBarcodeId());
        inventoryMovement.setDescription(grLine.getItemDescription());

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(grLine.getRefDocNumber());

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY
        inventoryMovement.setMovementQty(grLine.getGoodReceiptQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue("N");

        // BAL_OH_QTY
        // PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
//        List<InventoryV2> inventoryList = inventoryService.getInventory(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(),
//                grLine.getWarehouseId(), grLine.getItemCode(), 1L);
        if (createInventoryMovement != null) {
            double sumOfInvQty = createInventoryMovement.stream().mapToDouble(a -> a.getInventoryQuantity()).sum();
            log.info("InvMmt - SumOfInvQty: " + sumOfInvQty);
            inventoryMovement.setBalanceOHQty(sumOfInvQty);
            Double openQty = sumOfInvQty + grLine.getGoodReceiptQty();
            inventoryMovement.setReferenceField2(String.valueOf(openQty));                      //Qty before inventory Movement occur
        }
        if(createInventoryMovement == null) {
            inventoryMovement.setBalanceOHQty(0D);
            inventoryMovement.setReferenceField2(String.valueOf(grLine.getGoodReceiptQty()));   //Qty before inventory Movement occur
        }

        // MVT_UOM
        inventoryMovement.setInventoryUom(grLine.getGrUom());

        // PACK_BARCODES
        inventoryMovement.setPackBarcodes(grLine.getPackBarcodes());

        // ITEM_CODE
        inventoryMovement.setItemCode(grLine.getItemCode());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(grLine.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(grLine.getCreatedOn());
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("inventoryMovement created: " + inventoryMovement);
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param putAwayNumber
     * @param proposedStorageBin
     * @param loginUserID
     */
    public void deletePutAwayHeaderV2(String companyCode, String plantId, String languageId,
                                      String warehouseId, String preInboundNo, String refDocNumber,
                                      String goodsReceiptNo, String palletCode, String caseCode,
                                      String packBarcodes, String putAwayNumber, String proposedStorageBin, String loginUserID) {
        PutAwayHeaderV2 putAwayHeader = getPutAwayHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, goodsReceiptNo, palletCode,
                caseCode, packBarcodes, putAwayNumber, proposedStorageBin);
        if (putAwayHeader != null) {
            putAwayHeader.setDeletionIndicator(1L);
            putAwayHeader.setUpdatedBy(loginUserID);
            putAwayHeaderV2Repository.save(putAwayHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + putAwayNumber);
        }
    }

    /**
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeadersV2() {
        List<PutAwayHeaderV2> putAwayHeaderList = putAwayHeaderV2Repository.findAll();
        putAwayHeaderList = putAwayHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return putAwayHeaderList;
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<PutAwayHeaderV2> putAwayHeaders = getPutAwayHeadersV2();
        putAwayHeaders.stream().forEach(p -> p.setReferenceField1(asnNumber));
        putAwayHeaderV2Repository.saveAll(putAwayHeaders);
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     */
    //Delete PutAwayHeader
    public List<PutAwayHeaderV2> deletePutAwayHeaderV2(String companyCodeId, String plantId, String languageId,
                                                       String warehouseId, String refDocNumber, String preInboundNo, String loginUserID) {

        List<PutAwayHeaderV2> putAwayHeaderV2List = new ArrayList<>();
        List<PutAwayHeaderV2> putAwayHeaderList = putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo,0L);
        log.info("PutAwayHeader - Cancellation : " + putAwayHeaderList);
        if (putAwayHeaderList != null && !putAwayHeaderList.isEmpty()) {
            for (PutAwayHeaderV2 putAwayHeaderV2 : putAwayHeaderList) {
                putAwayHeaderV2.setDeletionIndicator(1L);
                putAwayHeaderV2.setUpdatedBy(loginUserID);
                PutAwayHeaderV2 putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeaderV2);
                putAwayHeaderV2List.add(putAwayHeader);
            }
        }
        return putAwayHeaderV2List;
    }

    //===================================================walkaroo-v3========================================================
    public PutAwayHeaderV2 getPutAwayHeaderV3(String companyCodeId, String plantId, String languageId,
                                              String warehouseId, String packBarcodes, Date startDate, Date endDate) {
        try {
            return putAwayHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndCreatedOnBetweenAndDeletionIndicatorOrderByCreatedOnDesc(
                    companyCodeId, plantId, languageId, warehouseId, packBarcodes, startDate, endDate, 0L);
        } catch (Exception e) {
            throw new BadRequestException("Exception while getPutawayheader V3 : " + e.toString());
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param parentProductionOrderNo
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderV3(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, String parentProductionOrderNo) {
        try {
            return putAwayHeaderV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndParentProductionOrderNoAndDeletionIndicatorOrderByPackBarcodes(
                    companyCodeId, plantId, languageId, warehouseId, parentProductionOrderNo, 0L);
        } catch (Exception e) {
            throw new BadRequestException("Exception while getPutawayheader V3 : " + e.toString());
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param putAwayNumber
     * @param preInboundNo
     * @param barcodeId
     * @return
     */
    public PutAwayHeaderV2 getPutAwayHeaderV3(String companyCodeId, String plantId, String warehouseId,
                                              String languageId, String putAwayNumber, String preInboundNo, String barcodeId, String lineNumber) {
        if (lineNumber != null) {
            return putAwayHeaderV2Repository
                    .findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPutAwayNumberAndPreInboundNoAndBarcodeIdAndReferenceField9AndDeletionIndicator(
                            companyCodeId, plantId, warehouseId, languageId, putAwayNumber, preInboundNo, barcodeId, lineNumber, 0L);
        } else {
            return putAwayHeaderV2Repository
                    .findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPutAwayNumberAndPreInboundNoAndBarcodeIdAndDeletionIndicator(
                            companyCodeId, plantId, warehouseId, languageId, putAwayNumber, preInboundNo, barcodeId, 0L);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param parentProductionOrderNo
     * @return
     */
    public String getPutAwayNumberV3(String companyCodeId, String plantId, String languageId,
                                     String warehouseId, String parentProductionOrderNo) throws Exception {
        try {
            log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + parentProductionOrderNo);
            return putAwayHeaderV2Repository.getPutAwayNumberV3(companyCodeId, plantId, languageId, warehouseId, parentProductionOrderNo);
//            PutAwayHeaderV2 putAwayHeader = putAwayHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndParentProductionOrderNoAndDeletionIndicatorOrderByPackBarcodes(
//                    companyCodeId, plantId, languageId, warehouseId, parentProductionOrderNo, 0L);
//            if (putAwayHeader != null) {
//                return putAwayHeader.getPutAwayNumber();
//            }
//            return null;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param barcodeId
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderForReversalV3(String companyCode, String plantId, String languageId,
                                                               String warehouseId, String refDocNumber, String barcodeId) {
        return putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, refDocNumber, barcodeId, 0L);
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param barcodeId
     */
    public void updatePutAwayHeaderV3(String companyCode, String plantId, String languageId,
                                      String warehouseId, String itemCode, String barcodeId) {
        try {
            log.info("putawayHeader Status Update: " + companyCode + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + barcodeId);
//        statusDescription = getStatusDescription(20L, languageId);
//        putAwayHeaderV2Repository.updateHeaderStatus(
//                companyCode, plantId, languageId, warehouseId, 20L, statusDescription, itemCode, barcodeId);
            PutAwayHeaderV2 putAwayHeader = putAwayHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndReferenceField5AndBarcodeIdAndStatusIdAndDeletionIndicator(
                    companyCode, plantId, languageId, warehouseId, itemCode, barcodeId, 19L, 0L);
            log.info("PutawayHeader : " + putAwayHeader);
            if (putAwayHeader != null) {
                PutAwayLineV2 putAwayLine = new PutAwayLineV2();
                BeanUtils.copyProperties(putAwayHeader, putAwayLine);
                putAwayLine.setCompanyCode(companyCode);
                putAwayLine.setPutawayConfirmedQty(putAwayHeader.getPutAwayQuantity());
                putAwayLine.setConfirmedStorageBin(putAwayHeader.getProposedStorageBin());
                putAwayLine.setLineNo(Long.valueOf(putAwayHeader.getReferenceField9()));
                putAwayLine.setItemCode(putAwayHeader.getReferenceField5());
                putAwayLineService.putAwayLineConfirmNonCBMV3(putAwayLine, putAwayHeader, "STAGING_AREA");
            }
        } catch (Exception e) {
            throw new BadRequestException("Exception while creating ");
        }
    }

    /**
     *
     * @param inboundReversalInputList
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public List<PutAwayHeaderV2> batchPutAwayReversalV3(List<InboundReversalInput> inboundReversalInputList, String loginUserID) throws Exception {
        log.info("PutAway Reversal Input: " + inboundReversalInputList);
        if(inboundReversalInputList != null && !inboundReversalInputList.isEmpty()) {
            for (InboundReversalInput inboundReversalInput : inboundReversalInputList){
                inboundReversalNonCBMV3(
                        inboundReversalInput.getCompanyCodeId(), inboundReversalInput.getPlantId(), inboundReversalInput.getLanguageId(), inboundReversalInput.getWarehouseId(),
                        inboundReversalInput.getRefDocNumber(), inboundReversalInput.getPackBarcodes(), inboundReversalInput.getPutAwayNumber(), loginUserID);
            }
        }
        return null;
    }

    /**
     * throughUploadTemplate
     * @param reversal
     * @throws Exception
     */
    public void batchPutAwayReversalV3(ReversalV3 reversal) throws Exception {
        log.info("PutAway Reversal Input: " + reversal);
        if(reversal != null && reversal.getLines() != null && !reversal.getLines().isEmpty()) {
            for (ReversalLineV3 inboundReversalInput : reversal.getLines()){
                inboundReversalNonCBMV3(
                        reversal.getCompanyCodeId(), reversal.getPlantId(), reversal.getLanguageId(), reversal.getWarehouseId(),
                        inboundReversalInput.getOrderNumber(), inboundReversalInput.getHuSerialNo(), reversal.getLoginUserId());
            }
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param packBarcodes
     * @param putAwayNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public void inboundReversalNonCBMV3(String companyCode, String plantId, String languageId, String warehouseId,
                                        String refDocNumber, String packBarcodes, String putAwayNumber, String loginUserID) throws Exception {

        try {
            log.info("Inbound Reversal Initiated : order Number, putaway Number ----> " + refDocNumber + ", " + putAwayNumber);
            /*
             * Pass WH_ID/REF_DOC_NO/PACK_BARCODE values in PUTAWAYHEADER table and fetch STATUS_ID value and PA_NO
             * 1. If STATUS_ID=20, then
             */
            List<PutAwayHeaderV2> putAwayHeaderList = getPutAwayHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber, putAwayNumber);

            String preInboundNo = null;
            String barcodeId = null;
            String itemCode = null;
            String manufactureName = null;

            for (PutAwayHeaderV2 dbPutAwayHeader : putAwayHeaderList) {
                log.info("PutawayHeader - reversal ---> " + dbPutAwayHeader);
                preInboundNo = dbPutAwayHeader.getPreInboundNo();
                barcodeId = dbPutAwayHeader.getBarcodeId();
                itemCode = dbPutAwayHeader.getReferenceField5();
                manufactureName = dbPutAwayHeader.getManufacturerName();
                Long lineNumber = dbPutAwayHeader.getReferenceField9() != null ? Long.valueOf(dbPutAwayHeader.getReferenceField9()) : 0;

//                if (dbPutAwayHeader.getStatusId() == 24L) {
//                    throw new BadRequestException("confirmed Putaway cannot be reversed: " + putAwayNumber);
//                }

                if (dbPutAwayHeader.getStatusId() == 20L) {
                    /*
                     * Checking whether Line Items have updated with STATUS_ID = 22.
                     */
                    long STATUS_ID_22_COUNT = putAwayLineService.getPutAwayLineByStatusIdV2(companyCode, plantId, languageId, warehouseId, putAwayNumber, refDocNumber, 22L);
                    log.info("putAwayLine---STATUS_ID_22_COUNT---> : " + STATUS_ID_22_COUNT);
                    if (STATUS_ID_22_COUNT > 0) {
                        throw new BadRequestException("putAwayNumber : " + putAwayNumber + " is already reversed.");
                    }

                    /*
                     * Pass WH_ID/REF_DOC_NO/PA_NO values in PUTAWAYLINE table and fetch PA_CNF_QTY values and QTY_TYPE values and
                     * update Status ID as 22, PA_UTD_BY = USR_ID and PA_UTD_ON=Server time
                     */
                    List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLineV2ForReversal(companyCode, plantId, languageId, warehouseId, refDocNumber, putAwayNumber);
                    log.info("putAwayLineList : " + putAwayLineList);
                    if (putAwayLineList != null && !putAwayLineList.isEmpty()) {
                        for (PutAwayLineV2 dbPutAwayLine : putAwayLineList) {
                            log.info("dbPutAwayLine---------> : " + dbPutAwayLine);

                            //update Inventory for BinClassId 1 & 3
//                            inventoryService.binClassId3InventoryReduction(companyCode, plantId, languageId, warehouseId, barcodeId, itemCode, manufactureName, refDocNumber, dbPutAwayLine.getPutawayConfirmedQty(), loginUserID);
                            inventoryService.binClassId1InventoryReduction(companyCode, plantId, languageId, warehouseId, barcodeId, itemCode, manufactureName, dbPutAwayLine, loginUserID);

                            //updateStagingLine
                            inboundReversalStagingLineUpdateV3(companyCode, plantId, languageId, warehouseId, itemCode, manufactureName, lineNumber,
                                    refDocNumber, preInboundNo, dbPutAwayLine.getQuantityType(), dbPutAwayLine.getPutawayConfirmedQty());

                            //UpdateInboundLine
                            inboundReversalInboundLineUpdateV3(companyCode, plantId, languageId, warehouseId, itemCode, lineNumber,
                                    refDocNumber, preInboundNo, dbPutAwayLine.getQuantityType(), dbPutAwayLine.getPutawayConfirmedQty());

                            //delete code
                            dbPutAwayLine.setDeletionIndicator(1L);
                            dbPutAwayLine.setConfirmedBy(loginUserID);
                            dbPutAwayLine.setUpdatedBy(loginUserID);
                            dbPutAwayLine.setConfirmedOn(new Date());
                            dbPutAwayLine.setUpdatedOn(new Date());
                            dbPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                            log.info("dbPutAwayLine updated: " + dbPutAwayLine);
                        }
                    }
                }

                /*
                 * 3. For STATUS_ID=19 and 20 , below tables to be updated
                 * Pass the selected REF_DOC_NO/PACK_BARCODE values  and PUTAWAYHEADER tables and update Status ID as 22 and
                 * PA_UTD_BY = USR_ID and PA_UTD_ON=Server time and fetch CASE_CODE
                 */
                if (dbPutAwayHeader.getStatusId() == 19L) {

                    //update Inventory for BinClassId 1 & 3
                    inventoryService.binClassId3InventoryReduction(companyCode, plantId, languageId, warehouseId, barcodeId, itemCode, manufactureName, refDocNumber, dbPutAwayHeader.getPutAwayQuantity(), loginUserID);

                    //updateStagingLine
                    inboundReversalStagingLineUpdateV3(companyCode, plantId, languageId, warehouseId, itemCode, manufactureName, lineNumber,
                            refDocNumber, preInboundNo, dbPutAwayHeader.getQuantityType(), dbPutAwayHeader.getPutAwayQuantity());

                }
                //delete code
                dbPutAwayHeader.setStatusId(22L);
                statusDescription = getStatusDescription(22L, languageId);
                dbPutAwayHeader.setStatusDescription(statusDescription);
                dbPutAwayHeader.setUpdatedBy(loginUserID);
                dbPutAwayHeader.setUpdatedOn(new Date());
                PutAwayHeaderV2 updatedPutAwayHeader = putAwayHeaderV2Repository.save(dbPutAwayHeader);
                log.info("updatedPutAwayHeader : " + updatedPutAwayHeader);

                //update the statusId to complete reversal process
                inboundReversalLineStatusUpdateV3(companyCode, plantId, languageId, warehouseId, dbPutAwayHeader.getReferenceField5(),
                        dbPutAwayHeader.getManufacturerName(), lineNumber, refDocNumber, preInboundNo, loginUserID);
            }
            //Header statusUpdate
            inboundReversalHeaderStatusUpdateV3(preInboundNo, companyCode, plantId, languageId, warehouseId, refDocNumber, loginUserID);
        } catch (Exception e) {
            log.error("Exception while IB Reversal Process");
            throw e;
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param barcodeId
     * @param loginUserID
     * @throws Exception
     */
    public void inboundReversalNonCBMV3(String companyCode, String plantId, String languageId, String warehouseId,
                                        String refDocNumber, String barcodeId, String loginUserID) throws Exception {

        try {
            log.info("Inbound Reversal Initiated : order Number, HU serialNo ----> " + refDocNumber + "|" + barcodeId + "|" + companyCode + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + loginUserID);
            /*
             * Pass WH_ID/REF_DOC_NO/PACK_BARCODE values in PUTAWAYHEADER table and fetch STATUS_ID value and PA_NO
             * 1. If STATUS_ID=20, then
             */
            List<PutAwayHeaderV2> putAwayHeaderList = getPutAwayHeaderForReversalV3(companyCode, plantId, languageId, warehouseId, refDocNumber, barcodeId);
            Long STATUS_ID = 22L;
            String preInboundNo = null;
            String itemCode = null;
            String manufactureName = null;
            String putAwayNumber = null;

            for (PutAwayHeaderV2 dbPutAwayHeader : putAwayHeaderList) {
                log.info("PutawayHeader - reversal ---> " + dbPutAwayHeader);
                preInboundNo = dbPutAwayHeader.getPreInboundNo();
                itemCode = dbPutAwayHeader.getReferenceField5();
                manufactureName = dbPutAwayHeader.getManufacturerName();
                putAwayNumber = dbPutAwayHeader.getPutAwayNumber();
                Long lineNumber = dbPutAwayHeader.getReferenceField9() != null ? Long.valueOf(dbPutAwayHeader.getReferenceField9()) : 0;

                if (dbPutAwayHeader.getStatusId() == 20L || dbPutAwayHeader.getStatusId() == 24L) {
                    /*
                     * Checking whether Line Items have updated with STATUS_ID = 22.
                     */
                    boolean STATUS_ID_22_COUNT = putAwayLineService.getPutAwayLineByStatusIdV3(companyCode, plantId, languageId, warehouseId, refDocNumber, barcodeId, putAwayNumber,  22L);
                    log.info("putAwayLine---STATUS_ID_22_COUNT---> : " + STATUS_ID_22_COUNT);
                    if (STATUS_ID_22_COUNT) {
                        throw new BadRequestException("putAwayNumber,barcodeId : " + putAwayNumber + "|" + barcodeId + " is already reversed.");
                    }

                    /*
                     * Pass WH_ID/REF_DOC_NO/PA_NO values in PUTAWAYLINE table and fetch PA_CNF_QTY values and QTY_TYPE values and
                     * update Status ID as 22, PA_UTD_BY = USR_ID and PA_UTD_ON=Server time
                     */
                    List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLineForReversalV3(companyCode, plantId, languageId, warehouseId, refDocNumber, barcodeId);
                    log.info("putAwayLineList : " + putAwayLineList);
                    if (putAwayLineList != null && !putAwayLineList.isEmpty()) {
                        for (PutAwayLineV2 dbPutAwayLine : putAwayLineList) {
                            log.info("dbPutAwayLine---------> : " + dbPutAwayLine);

                            //update Inventory for BinClassId 1 & 3
//                            inventoryService.binClassId3InventoryReduction(companyCode, plantId, languageId, warehouseId, barcodeId, itemCode, manufactureName, refDocNumber, dbPutAwayLine.getPutawayConfirmedQty(), loginUserID);
                            inventoryService.binClassId1InventoryReduction(companyCode, plantId, languageId, warehouseId, barcodeId, itemCode, manufactureName, dbPutAwayLine, loginUserID);

                            //updateStagingLine
                            inboundReversalStagingLineUpdateV3(companyCode, plantId, languageId, warehouseId, itemCode, manufactureName, lineNumber,
                                    refDocNumber, preInboundNo, dbPutAwayLine.getQuantityType(), dbPutAwayLine.getPutawayConfirmedQty());

                            //UpdateInboundLine
                            inboundReversalInboundLineUpdateV3(companyCode, plantId, languageId, warehouseId, itemCode, lineNumber,
                                    refDocNumber, preInboundNo, dbPutAwayLine.getQuantityType(), dbPutAwayLine.getPutawayConfirmedQty());

                        }
                    }
                }

                /*
                 * 3. For STATUS_ID=19 and 20 , below tables to be updated
                 * Pass the selected REF_DOC_NO/PACK_BARCODE values  and PUTAWAYHEADER tables and update Status ID as 22 and
                 * PA_UTD_BY = USR_ID and PA_UTD_ON=Server time and fetch CASE_CODE
                 */
                if (dbPutAwayHeader.getStatusId() == 19L) {
                    //update Inventory for BinClassId 1 & 3
                    inventoryService.binClassId3InventoryReduction(companyCode, plantId, languageId, warehouseId, barcodeId, itemCode, manufactureName,
                            refDocNumber, dbPutAwayHeader.getPutAwayQuantity(), loginUserID);

                    //updateStagingLine
                    inboundReversalStagingLineUpdateV3(companyCode, plantId, languageId, warehouseId, itemCode, manufactureName, lineNumber,
                            refDocNumber, preInboundNo, dbPutAwayHeader.getQuantityType(), dbPutAwayHeader.getPutAwayQuantity());

                }
            }
            //statusUpdate
            statusDescription = getStatusDescription(STATUS_ID, languageId);
            putAwayHeaderV2Repository.inboundReversalStatusUpdate(companyCode, plantId, languageId, warehouseId, refDocNumber, barcodeId, STATUS_ID, statusDescription, loginUserID, new Date());

            //Header statusCheck
            Long headerStatusUpdateCheck = putAwayHeaderV2Repository.checkReversalHeaderStatusUpdate(companyCode, plantId, languageId, warehouseId, refDocNumber, STATUS_ID);
            log.info("headerStatusUpdateCheck : " + headerStatusUpdateCheck);
            if(headerStatusUpdateCheck == 1) {
                putAwayHeaderV2Repository.inboundReversalHeaderStatusUpdate(companyCode, plantId, languageId, warehouseId, refDocNumber, STATUS_ID, statusDescription, loginUserID, new Date());
            }

        } catch (Exception e) {
            log.error("Exception while IB Reversal Process");
            throw e;
        }
    }

    /**
     * @param preInboundNo
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @throws Exception
     */
    public void inboundReversalHeaderStatusUpdateV3(String preInboundNo, String companyCode, String plantId, String languageId,
                                                    String warehouseId, String refDocNumber, String loginUserID) throws Exception {
        try {
            Long statusId1 = 13L;
            Long statusId2 = 14L;
            Long statusId3 = 16L;
            statusDescription = getStatusDescription(statusId1, languageId);
            String statusDescription2 = getStatusDescription(statusId2, languageId);
            // Update Header Status
            putAwayHeaderV2Repository.reversalHeaderStatusUpdateProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                    statusId1, statusId2, statusId3, statusDescription, statusDescription2, loginUserID, new Date());
            log.info("Header Status Updated through Stored Procedure");
        } catch (Exception e) {
            log.error("Reversal Header Update Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     * @throws Exception
     */
    public void inboundReversalLineStatusUpdateV3(String companyCode, String plantId, String languageId, String warehouseId,
                                                  String itemCode, String manufacturerName, Long lineNumber,
                                                  String refDocNumber, String preInboundNo, String loginUserID) throws Exception {

        try {
            log.info("revesal Line status Update initiated ---> ");
            boolean partialPutAwayExists = putAwayLineService.getPutAwayLineForReversalV3(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, itemCode, manufacturerName, lineNumber, preInboundNo);
            log.info("PutawayLine List to check any partial Putaway done: " + partialPutAwayExists);
            long grLineDeleteStatus = partialPutAwayExists ? 1 : 0;

            statusDescription = getStatusDescription(13L, languageId);
            putAwayHeaderV2Repository.reversalLineStatusUpdateProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, itemCode, manufacturerName,
                    lineNumber, grLineDeleteStatus, 13L, statusDescription, loginUserID, new Date());
        } catch (Exception e) {
            log.error("Reversal Line Update Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufactureName
     * @param lineNumber
     * @param refDocNumber
     * @param preInboundNo
     * @param quantityType
     * @param putAwayQty
     */
    private void inboundReversalStagingLineUpdateV3(String companyCode, String plantId, String languageId, String warehouseId,
                                                    String itemCode, String manufactureName, Long lineNumber,
                                                    String refDocNumber, String preInboundNo, String quantityType, Double putAwayQty) throws Exception {
        try {
            StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForReversalV2(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, itemCode, manufactureName, lineNumber);
            if (dbStagingLineEntity != null) {
                Double rec_accept_qty = 0D;
                Double rec_damage_qty = 0D;
                double PA_CNF_QTY = getQuantity(putAwayQty);
                if (quantityType.equalsIgnoreCase("A")) {
                    rec_accept_qty = getQuantity(dbStagingLineEntity.getRec_accept_qty()) - PA_CNF_QTY;
                }
                if (quantityType.equalsIgnoreCase("D")) {
                    rec_damage_qty = getQuantity(dbStagingLineEntity.getRec_damage_qty()) - PA_CNF_QTY;
                }

                dbStagingLineEntity.setRec_accept_qty(rec_accept_qty);
                dbStagingLineEntity.setRec_damage_qty(rec_damage_qty);
                dbStagingLineEntity.setStatusId(14L);
                statusDescription = getStatusDescription(14L, languageId);
                dbStagingLineEntity.setStatusDescription(statusDescription);
                stagingLineV2Repository.save(dbStagingLineEntity);
                log.info("stagingLineEntity rec_accept_damage_qty and status updated: " + dbStagingLineEntity);
            }
        } catch (Exception e) {
            log.error("Exception while Reversal stagingLine Update");
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param lineNumber
     * @param refDocNumber
     * @param preInboundNo
     * @param quantityType
     * @param putAwayQty
     */
    private void inboundReversalInboundLineUpdateV3(String companyCode, String plantId, String languageId, String warehouseId,
                                                    String itemCode, Long lineNumber, String refDocNumber, String preInboundNo,
                                                    String quantityType, Double putAwayQty) throws Exception {
        try {
            /*
             * Pass WH_ID/REF_DOC_NO/IB_LINE_NO/ ITM_CODE values in Inboundline table and update
             * If QTY_TYPE = A, update ACCEPT_QTY as (ACCEPT_QTY-PA_CNF_QTY)
             * if QTY_TYPE= D, update DAMAGE_QTY as (DAMAGE_QTY-PA_CNF_QTY)
             */
            InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNumber, itemCode);

            double PA_CNF_QTY = getQuantity(putAwayQty);
            Double IBL_VAR_QTY = inboundLine.getVarianceQty();

            if (quantityType.equalsIgnoreCase("A")) {
                Double acceptedQty = getQuantity(inboundLine.getAcceptedQty()) - PA_CNF_QTY;
                log.info("Accepted Qty: " + acceptedQty);
                inboundLine.setAcceptedQty(acceptedQty);
                Double VAR_QTY = IBL_VAR_QTY != null ? (IBL_VAR_QTY - acceptedQty) : 0D;
                inboundLine.setVarianceQty(VAR_QTY);
            }

            if (quantityType.equalsIgnoreCase("D")) {
                Double damageQty = getQuantity(inboundLine.getDamageQty()) - PA_CNF_QTY;
                log.info("Damage Qty: " + damageQty);
                inboundLine.setDamageQty(damageQty);
                Double VAR_QTY = IBL_VAR_QTY != null ? (IBL_VAR_QTY - damageQty) : 0D;
                inboundLine.setVarianceQty(VAR_QTY);
            }

            // Updating InboundLine only if Inventory got deleted
            InboundLineV2 updatedInboundLine = inboundLineV2Repository.save(inboundLine);
            log.info("updatedInboundLine : " + updatedInboundLine);
        } catch (Exception e) {
            log.error("Exception while Reversal InboundLine Update");
            throw e;
        }
    }

    /**
     * createPutawayHeaderv3
     * @param putawayHeaders
     * @return
     */
    public List<PutAwayHeaderV2> createPutawayHeaderv3(List<PutawayHeaderInt> putawayHeaders) {
        log.info("PutAwayHeader from SAP started....");
        log.info("PutAwayHeader -------> {}", putawayHeaders);

        List<PutAwayHeaderV2> putAwayHeaderV2List = new ArrayList<>();
        putawayHeaders.forEach(putAway -> {
            String huSerial = putAway.getHuSerialNo();
            List<StagingLineEntityV2> stagingLineEntityV2List = stagingLineV2Repository
                    .findByRefDocNumberAndBarcodeIdAndDeletionIndicator(putAway.getRefDocNumber(), huSerial, 0L);

//			log.info("stagingLineEntityV2List -----------> {}", stagingLineEntityV2List);
            List<GrLineV2> createdGRLines = new ArrayList<>();
            String idMasterAuthToken = getIDMasterAuthToken();
            long NUM_RAN_CODE_PA_NO = 7;

            stagingLineEntityV2List.forEach(stagingLine -> {
                try {
                    log.info("stagingLine ------> {}", stagingLine);
//					log.info("stagingLine Remarks -------> {}", stagingLine.getRemarks());
                    if (stagingLine.getRemarks() == null) {
                        GrHeaderV2 grHeaderV2 = grHeaderV2Repository
                                .findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                                        stagingLine.getCompanyCode(), stagingLine.getPlantId(),
                                        stagingLine.getLanguageId(), stagingLine.getWarehouseId(),
                                        stagingLine.getRefDocNumber(), stagingLine.getPreInboundNo(), 0L);

//						if (putAway.getSapDocumentNo() != null && !putAway.getType().equals("E")) {
                        if (putAway.getSapDocumentNo() != null) {
                            GrLineV2 newGrLine = new GrLineV2();
                            BeanUtils.copyProperties(stagingLine, newGrLine,
                                    CommonUtils.getNullPropertyNames(stagingLine));

                            String packBarCode = getNextRangeNumber(6L, stagingLine.getCompanyCode(),
                                    stagingLine.getPlantId(), stagingLine.getLanguageId(), stagingLine.getWarehouseId(),
                                    idMasterAuthToken);

                            newGrLine.setGoodsReceiptNo(grHeaderV2.getGoodsReceiptNo());
                            newGrLine.setPackBarcodes(packBarCode);
                            newGrLine.setQuantityType("A");
                            newGrLine.setCompanyCode(stagingLine.getCompanyCode());
                            newGrLine.setGoodReceiptQty(stagingLine.getOrderQty());
                            newGrLine.setAcceptedQty(stagingLine.getOrderQty());
                            newGrLine.setOrderUom(stagingLine.getOrderUom());
                            newGrLine.setGrUom(stagingLine.getOrderUom());
                            newGrLine.setDamageQty(0D);

                            if (stagingLine.getBarcodeId() != null) {
                                newGrLine.setBarcodeId(stagingLine.getBarcodeId());
                            } else {
                                newGrLine.setBarcodeId(stagingLine.getManufacturerName() + stagingLine.getItemCode());
                            }

                            // newGrLine.setAssignedUserId(loginUserId);
                            newGrLine.setConfirmedQty(stagingLine.getOrderQty());
                            newGrLine.setBranchCode(stagingLine.getBranchCode());
                            newGrLine.setTransferOrderNo(stagingLine.getTransferOrderNo());
                            newGrLine.setStatusId(17L);
                            statusDescription = stagingLineV2Repository.getStatusDescription(17L,
                                    stagingLine.getLanguageId());
                            newGrLine.setStatusDescription(statusDescription);
                            newGrLine.setPalletId(stagingLine.getPalletId());
                            newGrLine.setDeletionIndicator(0L);
                            newGrLine.setCreatedBy("ADMIN");
                            newGrLine.setUpdatedBy("ADMIN");
                            newGrLine.setConfirmedBy("ADMIN");
                            newGrLine.setCreatedOn(new Date());
                            newGrLine.setUpdatedOn(new Date());
                            newGrLine.setConfirmedOn(new Date());

                            // Lead Time
                            GrLineImpl implForLeadTime = grLineV2Repository.getLeadTime(stagingLine.getLanguageId(),
                                    stagingLine.getCompanyCode(), stagingLine.getPlantId(),
                                    stagingLine.getWarehouseId(), grHeaderV2.getGoodsReceiptNo(), new Date());
                            if (implForLeadTime != null) {
                                if (!implForLeadTime.getDiffDays().equals("00")) {
                                    String leadTime = implForLeadTime.getDiffDays() + "Days: "
                                            + implForLeadTime.getDiffHours() + "Hours: "
                                            + implForLeadTime.getDiffMinutes() + "Minutes: "
                                            + implForLeadTime.getDiffSeconds() + "Seconds";
                                    newGrLine.setReferenceField10(leadTime);
                                } else if (!implForLeadTime.getDiffHours().equals("00")) {
                                    String leadTime = implForLeadTime.getDiffHours() + "Hours: "
                                            + implForLeadTime.getDiffMinutes() + "Minutes: "
                                            + implForLeadTime.getDiffSeconds() + "Seconds";
                                    newGrLine.setReferenceField10(leadTime);
                                } else if (!implForLeadTime.getDiffMinutes().equals("00")) {
                                    String leadTime = implForLeadTime.getDiffMinutes() + "Minutes: "
                                            + implForLeadTime.getDiffSeconds() + "Seconds";
                                    newGrLine.setReferenceField10(leadTime);
                                } else {
                                    String leadTime = implForLeadTime.getDiffSeconds() + "Seconds";
                                    newGrLine.setReferenceField10(leadTime);
                                }
                            }
                            Optional<GrLineV2> duplicateChecking = grLineV2Repository.findByBarcodeIdAndRefDocNumberAndDeletionIndicator(stagingLine.getBarcodeId(),
                                    stagingLine.getRefDocNumber(), 0L);
                            log.info("Duplicate Checking This RefDocNo {}, And BarcodeId {} ", stagingLine.getRefDocNumber(), stagingLine.getBarcodeId());
                            if(duplicateChecking.isEmpty()) {
                                createdGRLines.add(newGrLine);
                            }
                        }

                        log.info(" <---- GrLine Save Process Initiated ------> ");
                        grLineV2Repository.saveAll(createdGRLines);
                        log.info("GrLine Created Successfully: {}", createdGRLines.size());
                    } else {
                        createInventoryInBinclass8(stagingLine);
                        log.info("INVENTORY CREATED IN binclassID 8 -->");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            /*
             * PutAway Creation
             */
            Map<String, List<GrLineV2>> groupedByPalletId =
                    createdGRLines.stream()
                            .collect(Collectors.groupingBy(GrLineV2::getPalletId));

            for (Map.Entry<String, List<GrLineV2>> entry : groupedByPalletId.entrySet()) {
                String palletId = entry.getKey();
                List<GrLineV2> grLines = entry.getValue();

                // Getting PA_NUMBER per Pallet Id
                String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, grLines.get(0).getCompanyCode(),
                        grLines.get(0).getPlantId(), grLines.get(0).getLanguageId(), grLines.get(0).getWarehouseId(),
                        idMasterAuthToken);
                try {
                    log.info("-----nextPANumber:{} | PalId: {} ---->", nextPANumber, palletId);
                    createPutAwayHeaderv3(nextPANumber, grLines);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        putAwayHeaderIntRepository.saveAll(putawayHeaders);
        return putAwayHeaderV2List;
    }

    /**
     *
     * @param stagingLine
     */
    private void createInventoryInBinclass8 (StagingLineEntityV2 stagingLine) {
        try {
            InventoryV2 createdinventory = null;
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(stagingLine, inventory, CommonUtils.getNullPropertyNames(stagingLine));
            inventory.setCompanyCodeId(stagingLine.getCompanyCode());

            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);
            inventory.setVariantSubCode("1");
            inventory.setStorageMethod("1");

            inventory.setBatchSerialNumber("1");
            inventory.setPackBarcodes(PACK_BARCODE);

            inventory.setBinClassId(8L);
            inventory.setDeletionIndicator(0L);
            inventory.setBarcodeId(stagingLine.getBarcodeId());
//                inventory.setReferenceField8(stagingLine.getItemDescription());
//                inventory.setReferenceField9(stagingLine.getManufacturerName());
//                inventory.setManufacturerCode(stagingLine.getManufacturerName());
//                inventory.setDescription(stagingLine.getItemDescription());
//                inventory.setReferenceDocumentNo(stagingLine.getRefDocNumber());
//                inventory.setReferenceOrderNo(stagingLine.getRefDocNumber());

            log.info("StorageBin get and set from Master -------------BinClassId --8--> ");
//			 ST_BIN ---Pass WH_ID/BIN_CL_ID = 8 in STORAGEBIN table and fetch ST_BIN value and update
            StorageBinV2 storageBin =
                    storageBinService.getStorageBinByBinClassIdV2(stagingLine.getWarehouseId(), 8L, stagingLine.getCompanyCode(),
                            stagingLine.getPlantId(), stagingLine.getLanguageId());
            log.info("storageBin: {}", storageBin);

            if (storageBin != null) {
                inventory.setStorageBin(storageBin.getStorageBin());
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setStorageSectionId(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
            }

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(stagingLine.getCompanyCode(), stagingLine.getPlantId(),
                    stagingLine.getLanguageId(), stagingLine.getWarehouseId(), 1L);
            inventory.setStockTypeDescription(stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
//                inventory.setPalletId(stagingLine.getPalletId());

            // INV_QTY
            double INV_QTY = round(stagingLine.getOrderQty());
            inventory.setInventoryQuantity(INV_QTY);
            inventory.setReferenceField4(INV_QTY); // Allocated Qty is zero for bin Class Id 3
            log.info("New - Inventory - INV_QTY,TOT_QTY: {}", INV_QTY);

            // INV_UOM
//                inventory.setInventoryUom(stagingLine.getOrderUom());
            inventory.setCreatedBy("ADMIN");
            inventory.setUpdatedBy("ADMIN");

//                if (inventory.getItemType() == null) {
//                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
//                    if (itemType != null) {
//                        inventory.setItemType(itemType.getItemType());
//                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
//                    }
//                }

            inventory.setCreatedOn(new Date());
            inventory.setUpdatedOn(new Date());
            try {
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("B3 created inventory : {}", createdinventory);
            } catch (Exception e1) {
                log.error("--ERROR--createInventoryInBinclass8 ----level1--inventory--error----> :" + e1.toString());
                e1.printStackTrace();

                // Inventory Error Handling
                InventoryTrans newInventoryTrans = new InventoryTrans();
                BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                newInventoryTrans.setReRun(0L);
                InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
            }
        } catch (Exception e) {
            // Exception Log
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *
     * @param createdGRLine
     * @return
     */
    private PutAwayHeaderV2 createPutAwayHeaderv3(GrLineV2 createdGRLine) {
        PutAwayHeaderV2 putAwayHeader = null;
        try {
            String itemCode = createdGRLine.getItemCode();
            String companyCode = createdGRLine.getCompanyCode();
            String plantId = createdGRLine.getPlantId();
            String languageId = createdGRLine.getLanguageId();
            String warehouseId = createdGRLine.getWarehouseId();
            String proposedStorageBin = createdGRLine.getInterimStorageBin();

            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(companyCode);
            storageBinPutAway.setPlantId(plantId);
            storageBinPutAway.setLanguageId(languageId);
            storageBinPutAway.setWarehouseId(warehouseId);

            Double cbm = 0D;

            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

            if (createdGRLine.getCbm() != null) {
                cbm = createdGRLine.getCbm();
                log.info("cbm, createdGrLine.getCbm: " + cbm + ", " + createdGRLine.getCbm());
            }
            outerloop:
//        while (true) {
            //  ASS_HE_NO
            if (createdGRLine != null) {
                // Insert record into PutAwayHeader
                putAwayHeader = new PutAwayHeaderV2();
                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                putAwayHeader.setCompanyCodeId(companyCode);
                putAwayHeader.setReferenceField5(itemCode);

                // PA_NO
                long NUM_RAN_CODE = 7;
                String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
                log.info("PutAwayNumber Generated: " + nextPANumber);

                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());

                //set bar code id for packbarcode
                putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());

                //set pack bar code for actual packbarcode
                putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());

                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                //-----------------PROP_ST_BIN---------------------------------------------

                //V2 Code
                Long binClassId = 0L;                   //actual code follows
                if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 || createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5) {
                    binClassId = 1L;
                }
                if (createdGRLine.getInboundOrderTypeId() == 2) {
                    binClassId = 7L;
                }
                log.info("BinClassId : " + binClassId);

                List<IInventoryImpl> stBinInventoryList = inventoryService.getInventoryForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                        itemCode, createdGRLine.getManufacturerName(), binClassId);
                log.info("stBinInventoryList -----------> : " + stBinInventoryList.size());

                List<String> inventoryStorageBinList = null;
                if (stBinInventoryList != null && !stBinInventoryList.isEmpty()) {
                    inventoryStorageBinList = stBinInventoryList.stream().map(IInventoryImpl::getStorageBin).collect(Collectors.toList());
                }
                log.info("Inventory StorageBin List: " + inventoryStorageBinList);

                if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
                    storageBinPutAway.setBinClassId(binClassId);
                    storageBinPutAway.setBin(proposedStorageBin);
                    StorageBinV2 storageBin = null;
                    try {
                        storageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                    } catch (Exception e) {
                        throw new BadRequestException("Invalid StorageBin");
                    }
                    log.info("InterimStorageBin: " + storageBin);
                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                    if (storageBin != null) {
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                        cbm = 0D;               //to break the loop
                    }
                    if (storageBin == null) {
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        cbm = 0D;               //to break the loop
                    }
                }
                //BinClassId - 7 - Return Order(Sale Return)
                if (createdGRLine.getInboundOrderTypeId() == 2) {

                    storageBinPutAway.setBinClassId(binClassId);
                    log.info("BinClassId : " + binClassId);

                    StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                    if (proposedBinClass7Bin != null) {
                        String proposedStBin = proposedBinClass7Bin.getStorageBin();
                        putAwayHeader.setProposedStorageBin(proposedStBin);
                        putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                        log.info("Return Order --> BinClassId7 Proposed Bin: " + proposedStBin);
                        cbm = 0D;   //break the loop
                    }
                    if (proposedBinClass7Bin == null) {
                        binClassId = 2L;
                        log.info("BinClassId : " + binClassId);
                        StorageBinV2 stBin = mastersService.getStorageBin(
                                companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                        log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                        cbm = 0D;   //break the loop
                    }
                }

                if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                    if (stBinInventoryList != null) {
                        log.info("BinClassId : " + binClassId);
                        if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                            if(createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                                storageBinPutAway.setBinClassId(binClassId);
                                storageBinPutAway.setStorageBin(inventoryStorageBinList);

                                StorageBinV2 proposedExistingBin = mastersService.getExistingStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                if (proposedExistingBin != null) {
                                    proposedStorageBin = proposedExistingBin.getStorageBin();
                                    log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);

                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
                                }
                                log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
                                cbm = 0D;   //break the loop
                            }
                            if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                                storageBinPutAway.setBinClassId(7L);
                                StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                if (proposedBinClass7Bin != null) {
                                    String proposedStBin = proposedBinClass7Bin.getStorageBin();
                                    putAwayHeader.setProposedStorageBin(proposedStBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                                    log.info("Damage Qty --> BinClassId7 Proposed Bin: " + proposedStBin);
                                    cbm = 0D;   //break the loop
                                }
                                if (proposedBinClass7Bin == null) {
                                    binClassId = 2L;
                                    log.info("BinClassId : " + binClassId);
                                    StorageBinV2 stBin = mastersService.getStorageBin(
                                            companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                    log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                                    cbm = 0D;   //break the loop
                                }
                            }
                        }
                    }
                }

                //Last Picked Bin as Proposed Bin If it is empty
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || stBinInventoryList.isEmpty())) {

//                    List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineForLastBinCheckV2(companyCode, plantId, languageId, warehouseId, itemCode, createdGRLine.getManufacturerName());
                    PickupLineV2 pickupLineList = pickupLineService.getPickupLineForLastBinCheck(companyCode, plantId, languageId, warehouseId, itemCode, createdGRLine.getManufacturerName());
                    log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
//                    String lastPickedStorageBinList = null;
                    if (pickupLineList != null) {
//                        lastPickedStorageBinList = pickupLineList.getPickedStorageBin();
//                    }
//                    log.info("LastPickedStorageBinList: " + lastPickedStorageBinList);

//                    if (lastPickedStorageBinList != null && !lastPickedStorageBinList.isEmpty()) {
//                        log.info("BinClassId : " + binClassId);

//                        storageBinPutAway.setStatusId(0L);
//                        storageBinPutAway.setBinClassId(1L);
//                        storageBinPutAway.setStorageBin(lastPickedStorageBinList);

//                        StorageBinV2 proposedNonCbmLastPickStorageBin = mastersService.getStorageBinNonCbmLastPicked(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                        log.info("proposedNonCbmLastPickStorageBin: " + proposedNonCbmLastPickStorageBin);
//                        if (proposedNonCbmLastPickStorageBin != null) {
                        putAwayHeader.setProposedStorageBin(pickupLineList.getPickedStorageBin());
                        putAwayHeader.setLevelId(pickupLineList.getLevelId());
                        log.info("LastPick NonCBM Bin: " + pickupLineList.getPickedStorageBin());
                        log.info("LastPick NonCBM PutawayQty: " + createdGRLine.getGoodReceiptQty());
                        cbm = 0D;   //break the loop
//                        }
                    }
                }

                //Propose Empty Bin if Last picked bin is unavailable
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || stBinInventoryList.isEmpty())) {
                    // If ST_BIN value is null
                    // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
                    // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
                    log.info("QuantityType : " + createdGRLine.getQuantityType());
                    log.info("BinClassId : " + binClassId);

                    storageBinPutAway.setStatusId(0L);
                    StorageBinV2 proposedNonCbmStorageBin = null;

                    if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                        storageBinPutAway.setBinClassId(binClassId);

                        //Checking confirmed bin in putAway line for that item
                        PutAwayLineV2 existingBinPutAwayLineItemCheck = putAwayLineService.getPutAwayLineExistingItemCheckV2(companyCode, plantId, languageId, warehouseId,
                                itemCode, createdGRLine.getManufacturerName());
                        log.info("existingBinPutAwayLineItemCheck: " + existingBinPutAwayLineItemCheck);
                        if (existingBinPutAwayLineItemCheck != null) {
                            proposedStorageBin = existingBinPutAwayLineItemCheck.getConfirmedStorageBin();
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            if (existingBinPutAwayLineItemCheck.getLevelId() != null) {
                                putAwayHeader.setLevelId(String.valueOf(existingBinPutAwayLineItemCheck.getLevelId()));
                            } else {
                                storageBinPutAway.setBin(proposedStorageBin);
                                StorageBinV2 getLevelIdForProposedBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                if (getLevelIdForProposedBin != null) {
                                    putAwayHeader.setLevelId(String.valueOf(getLevelIdForProposedBin.getFloorId()));
                                }
                            }
                            log.info("Existing PutAwayCreate ProposedStorageBin from putAway line-->A : " + proposedStorageBin);
                        }
                        List<PutAwayHeaderV2> existingBinItemCheck = putAwayHeaderService.getPutawayHeaderExistingBinItemCheckV2(companyCode, plantId, languageId, warehouseId,
                                itemCode, createdGRLine.getManufacturerName());
                        log.info("existingBinItemCheck: " + existingBinItemCheck);
                        if (existingBinItemCheck != null && !existingBinItemCheck.isEmpty()) {
                            proposedStorageBin = existingBinItemCheck.get(0).getProposedStorageBin();
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(existingBinItemCheck.get(0).getLevelId()));
                            log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                            cbm = 0D;   //break the loop
                        }
                        List<String> existingBinCheck = putAwayHeaderService.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
                        log.info("existingBinCheck: " + existingBinCheck);
                        if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck != null && !existingBinCheck.isEmpty())) {
                            storageBinPutAway.setStorageBin(existingBinCheck);
                            proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (proposedNonCbmStorageBin != null) {
                                proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
                                log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                                cbm = 0D;   //break the loop
                            }
                        }
                        if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck == null || existingBinCheck.isEmpty() || existingBinCheck.size() == 0)) {
                            List<String> existingProposedPutawayStorageBin = putAwayHeaderService.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
                            log.info("existingProposedPutawayStorageBin: " + existingProposedPutawayStorageBin);
                            log.info("BinClassId: " + binClassId);
                            storageBinPutAway.setStorageBin(existingProposedPutawayStorageBin);
                            proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin);
                            if (proposedNonCbmStorageBin != null) {
                                proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));

                                cbm = 0D;   //break the loop
                            }
                            if (proposedNonCbmStorageBin == null) {
                                binClassId = 2L;
                                log.info("BinClassId : " + binClassId);
                                StorageBinV2 stBin = mastersService.getStorageBin(
                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                                cbm = 0D;   //break the loop
                            }
                        }
                    }

                    /*
                     * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
                     * fetch ST_BIN values for STATUS_ID=EMPTY.
                     */
                    if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                        binClassId = 7L;
                        storageBinPutAway.setBinClassId(binClassId);
                        log.info("BinClassId : " + binClassId);
                        StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        if (proposedBinClass7Bin != null) {
                            proposedStorageBin = proposedBinClass7Bin.getStorageBin();
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                            log.info("D --> BinClassId7 Proposed Bin: " + proposedStorageBin);
                            cbm = 0D;   //break the loop
                        }
                        if (proposedBinClass7Bin == null) {
                            binClassId = 2L;
                            log.info("BinClassId : " + binClassId);
                            StorageBinV2 stBin = mastersService.getStorageBin(
                                    companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                            log.info("D --> reserveBin: " + stBin.getStorageBin());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                            cbm = 0D;   //break the loop
                        }
                    }
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////
                log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                //PROP_HE_NO	<- PAWAY_HE_NO
                if (createdGRLine.getReferenceDocumentType() != null) {
                    putAwayHeader.setReferenceDocumentType(createdGRLine.getReferenceDocumentType());
                } else {
                    putAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(companyCode, plantId, languageId, warehouseId, createdGRLine.getInboundOrderTypeId()));
                }
                putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                putAwayHeader.setCbmQuantity(createdGRLine.getCbmQuantity());

                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                        languageId,
                        plantId,
                        warehouseId);

                putAwayHeader.setCompanyDescription(description.getCompanyDesc());
                putAwayHeader.setPlantDescription(description.getPlantDesc());
                putAwayHeader.setWarehouseDescription(description.getWarehouseDesc());

                PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                        createdGRLine.getPreInboundNo(), createdGRLine.getRefDocNumber());

                putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
                putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
                putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
                putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
                putAwayHeader.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());

                putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
                putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
                putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
                putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
                putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
                putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());

                putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
                putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
                putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                putAwayHeader.setStatusId(19L);
                statusDescription = stagingLineV2Repository.getStatusDescription(19L, createdGRLine.getLanguageId());
                putAwayHeader.setStatusDescription(statusDescription);

                putAwayHeader.setDeletionIndicator(0L);
                putAwayHeader.setCreatedBy("ADMIN");
                putAwayHeader.setUpdatedBy("ADMIN");
                putAwayHeader.setCreatedOn(new Date());
                putAwayHeader.setUpdatedOn(new Date());
                putAwayHeader.setConfirmedOn(new Date());
                putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                log.info("putAwayHeader : " + putAwayHeader);

                /*----------------Inventory tables Create---------------------------------------------*/
//                InventoryV2 createdinventory = createInventoryNonCBMV2(createdGRLine);

                /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
//                createInventoryMovementV2(createdGRLine, createdinventory.getStorageBin());
            }
//            if (cbm == 0D) {
//                break outerloop;
//            }
//        }
        } catch (Exception e) {
            log.error("Exception while PutAwayHeader create : " + e.toString());
            throw e;
        }
        return putAwayHeader;
    }

    /**
     *
     * @param paNo
     * @param newGrLineList
     * @return
     * @throws Exception
     */
    private List<PutAwayHeaderV2> createPutAwayHeaderv3(String paNo, List<GrLineV2> newGrLineList) throws Exception {
        log.info("Inbound Process PutAwayHeader Initiated ------> {}", new Date());

        Set<String> groupByRefDocNo = newGrLineList.stream().map(GrLineV2::getRefDocNumber)
                .collect(Collectors.toSet());

        List<PutAwayHeaderV2> savedPutAwayHeaders = new ArrayList<>();
        newGrLineList.forEach(createdGRLine -> {
            try {
                String companyCode = createdGRLine.getCompanyCode();
                String plantId = createdGRLine.getPlantId();
                String languageId = createdGRLine.getLanguageId();
                String warehouseId = createdGRLine.getWarehouseId();

                // Create and Copy Properties
                PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                putAwayHeader.setPalletId(createdGRLine.getPalletId());
                putAwayHeader.setCompanyCodeId(companyCode);
                putAwayHeader.setGoodsReceiptNo(createdGRLine.getGoodsReceiptNo());
                putAwayHeader.setQuantityType(createdGRLine.getQuantityType() != null ? createdGRLine.getQuantityType() : "A");

                String priceSegment = (createdGRLine.getPriceSegment() != null
                        && createdGRLine.getPriceSegment().length() > 1)
                        ? createdGRLine.getPriceSegment().substring(0, 2)
                        : createdGRLine.getPriceSegment();

                StringBuilder concatField = new StringBuilder().append(createdGRLine.getArticleNo()).append("_")
                        .append(createdGRLine.getGender()).append("_").append(createdGRLine.getColor()).append("_")
                        .append(createdGRLine.getSize()).append("_").append(priceSegment);
                putAwayHeader.setReferenceField3(concatField.toString());

                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
                putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());
                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                String proposedBin = null;

                // Step 1: Check MTO
                if (createdGRLine.getMtoNumber() != null) {
                    proposedBin = storageBinV2Repository.getPutAwayStrategyStorageBinV3(companyCode, plantId,
                            languageId, warehouseId, createdGRLine.getReferenceField5(), createdGRLine.getGender(),
                            createdGRLine.getArticleNo());
//					log.info(companyCode+","+ plantId+","+
//							languageId+","+ warehouseId+","+ createdGRLine.getReferenceField5()+","+ createdGRLine.getGender()+","+
//							createdGRLine.getArticleNo());
                    log.info("MasterPutAwayHeaderStrategyBin: {}", proposedBin);

                    if (proposedBin != null) {
                        putAwayHeader.setProposedStorageBin(proposedBin);
                    } else {
                        putAwayHeader.setProposedStorageBin("B09_00");
                    }
                } else {
                    String storageBin = getStorageBin(companyCode, plantId, languageId, warehouseId,
                            createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), createdGRLine.getArticleNo(),
                            createdGRLine.getGender(), createdGRLine.getReferenceField5());
                    log.info("-----proposedBin---------assigned: {}", storageBin);
                    if (storageBin != null) {
                        putAwayHeader.setProposedStorageBin(storageBin);
                    } else {
                        putAwayHeader.setProposedStorageBin("B09_00");
                    }
                }
                putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
                putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
                putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
                putAwayHeader.setStatusId(19L);
                putAwayHeader.setStatusDescription(statusDescription);
                putAwayHeader.setDeletionIndicator(0L);
                putAwayHeader.setCreatedBy(createdGRLine.getCreatedBy());
                putAwayHeader.setUpdatedBy(createdGRLine.getCreatedBy());
                putAwayHeader.setCreatedOn(new Date());
                putAwayHeader.setUpdatedOn(new Date());
                putAwayHeader.setConfirmedOn(new Date());
                putAwayHeader.setPutAwayNumber(paNo);
                log.info("putAwayHeader create---->: {}", putAwayHeader);
                log.info("-------PalletId---> {}" , putAwayHeader.getPalletId());

                /*
                 * createInventory
                 */
                createInventoryNonCBMV3(companyCode, plantId, languageId, warehouseId, createdGRLine, createdGRLine.getCreatedBy());

                // Save the entity and add it to the list
                PutAwayHeaderV2 savedPutAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                savedPutAwayHeaders.add(savedPutAwayHeader);
            } catch (Exception e) {
                log.error("Exception while PutAwayHeader create : " + e);
                throw new BadRequestException(e.getLocalizedMessage());
            }
        });

        for(String refDocNo : groupByRefDocNo) {
            String orderText = "PutAwayHeader Created";
            int updateCount =  inboundOrderV2Repository.updatePutawayHeader(refDocNo, orderText);
            log.info("Update PutAwayHeader Update Successfully ---> RefDocNo is {} And Affected Row {}", refDocNo, updateCount);
        }

        return savedPutAwayHeaders;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param newGrLineList
     * @param loginUserID
     * @return
     * @throws Exception
     */
//    private PutAwayHeaderV2 createPutAwayHeaderv3(String companyCode, String plantId, String languageId, String warehouseId,
//			String paNo, List<GrLineV2> newGrLineList, String loginUserID) throws Exception {
//		log.info("Inbound Process PutAwayHeader Initiated ------> {}", new Date());
//
//		List<PutAwayHeaderV2> savedPutAwayHeaders = new ArrayList<>();
//		newGrLineList.forEach(createdGRLine -> {
//			try {
//				InboundHeaderV2 dbPreInboundHeader = inboundHeaderService.getInboundHeaderByEntityV2(companyCode,
//						plantId, languageId, warehouseId, createdGRLine.getRefDocNumber(),
//						createdGRLine.getPreInboundNo());
//
//				// Create and Copy Properties
//				PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
//				BeanUtils.copyProperties(dbPreInboundHeader, putAwayHeader,
//						CommonUtils.getNullPropertyNames(dbPreInboundHeader));
//				BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
//
//				putAwayHeader.setPalletId(createdGRLine.getPalletId());
//
//				// Fetch related GrHeaderV2
//				GrHeaderV2 grHeaderV2 = grHeaderV2Repository
//						.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
//								companyCode, plantId, languageId, warehouseId, createdGRLine.getRefDocNumber(),
//								createdGRLine.getPreInboundNo(), 0L);
//
//				putAwayHeader.setCompanyCodeId(companyCode);
//				putAwayHeader.setGoodsReceiptNo(grHeaderV2.getGoodsReceiptNo());
//				putAwayHeader.setQuantityType(
//						createdGRLine.getQuantityType() != null ? createdGRLine.getQuantityType() : "A");
//
//				String priceSegment = (createdGRLine.getPriceSegment() != null
//						&& createdGRLine.getPriceSegment().length() > 1)
//								? createdGRLine.getPriceSegment().substring(0, 2)
//								: createdGRLine.getPriceSegment();
//
//				StringBuilder concatField = new StringBuilder().append(createdGRLine.getArticleNo()).append("_")
//						.append(createdGRLine.getGender()).append("_").append(createdGRLine.getColor()).append("_")
//						.append(createdGRLine.getSize()).append("_").append(priceSegment);
//				putAwayHeader.setReferenceField3(concatField.toString());
//
//				putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
//				putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());
//				putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
//
//				String proposedBin = null;
//
//				// Step 1: Check MTO
//				if (createdGRLine.getMtoNumber() != null) {
//					proposedBin = storageBinV2Repository.getPutAwayStrategyStorageBinV3(companyCode, plantId,
//							languageId, warehouseId, createdGRLine.getReferenceField5(), createdGRLine.getGender(),
//							createdGRLine.getArticleNo());
//					log.info(companyCode+","+ plantId+","+
//							languageId+","+ warehouseId+","+ createdGRLine.getReferenceField5()+","+ createdGRLine.getGender()+","+
//							createdGRLine.getArticleNo());
//					log.info("MasterPutAwayHeaderStrategyBin: {}", proposedBin);
//					if (proposedBin != null) {
//						putAwayHeader.setProposedStorageBin(proposedBin);
//					} else {
//						putAwayHeader.setProposedStorageBin("B09_00");
//					}
//				} else {
////					// Step 2: Check order line status
////					List<OrderManagementLineV2> orderLine = orderManagementLineV2Repository
////							.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
////									createdGRLine.getCompanyCode(), createdGRLine.getPlantId(),
////									createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(),
////									createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), 0L);
////
////					if (orderLine != null) {
////						for (OrderManagementLineV2 ol : orderLine) {
////							String statusId = String.valueOf(ol.getStatusId());
////							if ("42".equals(statusId) || "43".equals(statusId) || "47".equals(statusId)) {
////								proposedBin = "A00_00";
////								putAwayHeader.setProposedStorageBin(proposedBin);
////								break;
////							}
////						}
////					}
////
////					boolean binAssigned = false;
////
////					if (proposedBin == null) {
////						String currentBin = storageBinV2Repository.getStorageBin(companyCode, plantId, languageId,
////								warehouseId, createdGRLine.getGender(), createdGRLine.getArticleNo());
////
////						boolean binFound = false;
////						while (currentBin != null && !binFound) {
////							String maxCapacityStr = storageBinV2Repository.checkMaxAndSuperMaxCapacity(companyCode,
////									plantId, languageId, warehouseId, createdGRLine.getGender(),
////									createdGRLine.getArticleNo(), currentBin);
////
////							Long count = inventoryV2Repository.countByStorageBin(currentBin);
////
////							log.info("Inventory Count for PropBin --> {} {}", currentBin, count);
////							log.info("Capacity check --> {}", maxCapacityStr);
////							double maxCapacity = 0;
////
////							if (maxCapacityStr != null) {
////								maxCapacity = Double.parseDouble(maxCapacityStr);
////							}
////
////							if (count <= maxCapacity) {
////								log.info("Found suitable bin: {}", currentBin);
////								putAwayHeader.setProposedStorageBin(currentBin);
////								binFound = true;
////							} else {
////								log.info("Max capacity exceeded for bin: {}", currentBin);
////								currentBin = getNextStorageBin(currentBin);
////								log.info("Checking next bin: {}", currentBin);
////							}
////						}
////
////						if (binFound) {
////							binAssigned = true;
////						} else {
////							log.warn("No suitable bin found for Article: {}, Gender: {}", createdGRLine.getArticleNo(),
////									createdGRLine.getGender());
////						}
////					}
////
////					// Step 5: Fallback � assign fixed bin if no bin assigned
////					if (!binAssigned) {
////						String fallbackBin = getStorageBin(companyCode, plantId, languageId, warehouseId,
////								createdGRLine.getItemCode(), createdGRLine.getManufacturerName(),
////								createdGRLine.getArticleNo(), createdGRLine.getGender(),
////								createdGRLine.getReferenceField5());
////						putAwayHeader.setProposedStorageBin(fallbackBin);
////						log.info("Fallback bin assigned: {}", fallbackBin);
////					}
//
//					String storageBin = getStorageBin(companyCode, plantId, languageId, warehouseId,
//	                        createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), createdGRLine.getArticleNo(),
//	                        createdGRLine.getGender(), createdGRLine.getReferenceField5());
//					log.info("-----proposedBin---------assigned: {}", storageBin);
//					if (storageBin != null) {
//						putAwayHeader.setProposedStorageBin(storageBin);
//					} else {
//						putAwayHeader.setProposedStorageBin("B09_00");
//					}
//				}
//				putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
//				putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
//				putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
//				putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
//				putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
//				putAwayHeader.setStatusId(19L);
//				putAwayHeader.setStatusDescription(statusDescription);
//
//				putAwayHeader.setDeletionIndicator(0L);
//				putAwayHeader.setCreatedBy(loginUserID);
//				putAwayHeader.setUpdatedBy(loginUserID);
//				putAwayHeader.setCreatedOn(new Date());
//				putAwayHeader.setUpdatedOn(new Date());
//				putAwayHeader.setConfirmedOn(new Date());
//				putAwayHeader.setPutAwayNumber(paNo);
//				log.info("putAwayHeader create---->: {}", putAwayHeader);
//
//				/*
//				 *
//				 */
//				createInventoryNonCBMV3(companyCode, plantId, languageId, warehouseId, createdGRLine, loginUserID);
//
//				String orderText = "PutAwayHeader Created";
//				inboundOrderV2Repository.updatePutawayHeader(putAwayHeader.getInboundOrderTypeId(),
//						putAwayHeader.getRefDocNumber(), orderText);
//				log.info("Update PutAwayHeader Update Successfully");
//
//				// Save the entity and add it to the list
//				PutAwayHeaderV2 savedPutAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
//				savedPutAwayHeaders.add(savedPutAwayHeader);
//			} catch (Exception e) {
//				log.error("Exception while PutAwayHeader create : " + e);
//				throw new BadRequestException(e.getLocalizedMessage());
//			}
//		});
//
//		String idMasterAuthToken = getIDMasterAuthToken();
//		savedPutAwayHeaders.forEach(pa -> {
//			long NUM_RAN_CODE_PA_NO = 7;
//			String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, pa.getCompanyCodeId(), pa.getPlantId(),
//					pa.getLanguageId(), pa.getWarehouseId(), idMasterAuthToken);
//			putAwayHeaderV2Repository.getPalId(pa.getPalletId(), nextPANumber);
//			pa.setPutAwayNumber(nextPANumber);
//		});
//		log.info("Update putaway number Successfully");
//
//		// Return the last saved PutAwayHeaderV2, or null if nothing was saved
//		return savedPutAwayHeaders.isEmpty() ? null : savedPutAwayHeaders.get(savedPutAwayHeaders.size() - 1);
//	}

    /**
     *
     * @param bin
     * @return
     */
    private String getNextStorageBin(String bin) {
        if (bin == null || !bin.contains("_")) {
            throw new IllegalArgumentException("Invalid bin format: " + bin);
        }

        String[] parts = bin.split("_");
        String prefix = parts[0]; // e.g., B09
        String suffix = parts[1]; // e.g., 00

        // Extract letter and number from prefix
        char letter = prefix.charAt(0);            // B
        int number = Integer.parseInt(prefix.substring(1)); // 09

        // Increment the number
        number += 1;

        // Format the number to maintain leading zeros (2 digits)
        String newPrefix = String.format("%c%02d", letter, number);

        // Return the new bin with same suffix
        return newPrefix + "_" + suffix;
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param createdGRLine
     * @param loginUserId
     */
    @org.springframework.transaction.annotation.Transactional
    public void createInventoryNonCBMV3(String companyCode, String plantId, String languageId, String warehouseId, GrLineV2 createdGRLine, String loginUserId) {
        try {
            InventoryV2 createdinventory = null;
            InventoryV2 dbInventory = getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, createdGRLine.getItemCode(),
                    createdGRLine.getManufacturerName(), createdGRLine.getBarcodeId());
            if (dbInventory != null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));

                Double INV_QTY = round(dbInventory.getInventoryQuantity());
                Double ALLOC_QTY = round(dbInventory.getAllocatedQuantity());
                Double GR_QTY = round(createdGRLine.getGoodReceiptQty());

                log.info("Before - Inventory - INV_QTY,ALLOC_QTY,TOT_QTY,GrQty: {}, {}, {}, {}", INV_QTY, ALLOC_QTY, dbInventory.getReferenceField4(), GR_QTY);
                INV_QTY = INV_QTY + GR_QTY;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;

                inventory.setInventoryQuantity(INV_QTY);
                inventory.setAllocatedQuantity(ALLOC_QTY);
                inventory.setReferenceField4(TOT_QTY);

                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, createdGRLine.getItemCode());
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setExpiryDate(createdGRLine.getExpiryDate());
                inventory.setBusinessPartnerCode(createdGRLine.getBusinessPartnerCode());
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                inventory.setUpdatedBy(loginUserId);
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory[Existing] : {}", createdinventory);

                    // Calling BinClass 8 Inventory update
                    updateInventoryBinClassID8 (createdinventory.getBarcodeId());
                } catch (Exception e1) {
                    log.error("--ERROR--createInventoryNonCBMV3 ----level1--inventory--error----> :" + e1.toString());
                    e1.printStackTrace();

                    // Inventory Error Handling
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }

            if (dbInventory == null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));
                inventory.setCompanyCodeId(companyCode);

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");
                inventory.setBatchSerialNumber("1");
                inventory.setPackBarcodes(PACK_BARCODE);
                inventory.setBinClassId(3L);
                inventory.setDeletionIndicator(0L);
                inventory.setReferenceField8(createdGRLine.getItemDescription());
                inventory.setReferenceField9(createdGRLine.getManufacturerName());
                inventory.setManufacturerCode(createdGRLine.getManufacturerName());
                inventory.setDescription(createdGRLine.getItemDescription());
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());

                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                StorageBinV2 storageBin = storageBinService.getStorageBinByBinClassIdV2(warehouseId, 3L, companyCode, plantId, languageId);
                log.info("storageBin: {}", storageBin);

                if (storageBin != null) {
                    inventory.setStorageBin(storageBin.getStorageBin());
                    inventory.setReferenceField10(storageBin.getStorageSectionId());
                    inventory.setStorageSectionId(storageBin.getStorageSectionId());
                    inventory.setReferenceField5(storageBin.getAisleNumber());
                    inventory.setReferenceField6(storageBin.getShelfId());
                    inventory.setReferenceField7(storageBin.getRowId());
                    inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
                inventory.setStockTypeDescription(stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

                // INV_QTY
                double INV_QTY = round(createdGRLine.getGoodReceiptQty());
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setReferenceField4(INV_QTY);      //Allocated Qty is zero for bin Class Id 3
                log.info("New - Inventory - INV_QTY,TOT_QTY: {}", INV_QTY);

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(loginUserId);

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getItemCode());
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }

                inventory.setCreatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory : {}", createdinventory);

                    // Calling BinClass 8 Inventory update
                    updateInventoryBinClassID8 (createdinventory.getBarcodeId());
                } catch (Exception e1) {
                    log.error("--ERROR--createInventoryNonCBMV3 ----level2--inventory--error----> :" + e1.toString());
                    e1.printStackTrace();

                    // Inventory Error Handling
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }
        } catch (Exception e) {
            // Exception Log
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Query Inventory BINCLASSID = 8 by passing barcodeId, make INV_QTY, ALLOC_QTY, REF_4 as 0
     * @param barcodeId
     */
    private void updateInventoryBinClassID8 (String barcodeId) {
        InventoryV2 inventory = inventoryService.getInventoryV3 (barcodeId, 8L);
        if (inventory != null) {
            inventoryV2Repository.updateInventoryV3(inventory.getBarcodeId());
            log.info("----updateInventoryBinClassID8-----update----");
        }
    }

    /**
     *
     */
   // @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT2M")
    private void updateErroredOutInventory () {
        List<InventoryTrans> inventoryTransList = inventoryTransRepository.findByReRun(0L);
        inventoryTransList.stream().forEach( it -> {
            log.info("----updateErroredOutInventory-->: " + it);
            inventoryV2Repository.updateInventory(it.getWarehouseId(),
                    it.getPackBarcodes(), it.getItemCode(),
                    it.getStorageBin(), it.getInventoryQuantity(), it.getAllocatedQuantity());
            it.setReRun(1L);
            inventoryTransRepository.save(it);
            log.info("----updateInventoryTrans-is-done-->: " + it);
        });

        /*
         * SAP DATA Update in Staging Line
         */
        List<PutawayHeaderInt> putawayHeaderIntList = putAwayHeaderIntRepository.findByReRun(0L);
        putawayHeaderIntList.stream().forEach(putAway -> {
            stagingLineV2Repository.updateStagingLine_SAP(putAway.getRefDocNumber(), putAway.getSapDocumentNo(),
                    putAway.getMessage(), putAway.getType(), putAway.getMatDocDate());
            log.info("-----StagingLine----updatedStagingLine_SAP-->");

            grHeaderV2Repository.updateGRHeader_SAP (putAway.getRefDocNumber(), putAway.getType(), putAway.getSapDocumentNo());
            log.info("----grHeader-----updated_SAP-->");

            putAway.setReRun(1L);
            putAwayHeaderIntRepository.save(putAway);
        });
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param articleNo
     * @param gender
     * @param location
     * @return
     */
    private String getStorageBin(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String articleNo, String gender, String location) {
        try {
            return grLineService.getProposedStorageBin(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, articleNo, gender, location);
        } catch (Exception e) {
            throw new BadRequestException("Invalid StorageBin");
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barCodeId
     * @return
     */
    public InventoryV2 getInventoryBinClassId3V4(String companyCode, String plantId, String languageId, String warehouseId,
                                                 String itemCode, String manufacturerName, String barCodeId) {
        InventoryV2 dbInventoryV2 = new InventoryV2();
        IInventoryImpl inventory = inventoryV2Repository.getInboundInventoryV3(companyCode, plantId, languageId, warehouseId, barCodeId, null,
                itemCode, manufacturerName, PACK_BARCODE, null, 3L, 1L);
        if (inventory != null) {
            BeanUtils.copyProperties(inventory, dbInventoryV2, CommonUtils.getNullPropertyNames(inventory));
            return dbInventoryV2;
        }
        return null;
    }


    /**
     *
     * @param searchPutAwayHeader putawayInput
     * @return
     * @throws Exception
     */
    public List<PutAwayPalletGroupResponse> findPutAwayMobApp(SearchPutAwayHeaderV2 searchPutAwayHeader) throws Exception {

        log.info("PutawayHeader Find Mobile App Input's {} ", searchPutAwayHeader);
        return putAwayHeaderV2Repository.findPutAwayHeaderValues(searchPutAwayHeader.getPalletId(), 19L, searchPutAwayHeader.getCompanyCodeId(),
                searchPutAwayHeader.getPlantId(), searchPutAwayHeader.getWarehouseId());
        }


}
