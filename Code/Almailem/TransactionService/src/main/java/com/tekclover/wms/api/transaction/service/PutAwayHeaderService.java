package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.GrLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundHeaderEntity;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.*;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.InboundReversalInput;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.SearchPutAwayHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.PutAwayHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PutAwayHeaderV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PutAwayHeaderService extends BaseService {
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
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
    private PutAwayLineService putAwayLineService;

    @Autowired
    private InboundLineService inboundLineService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private GrLineService grLineService;

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

    String statusDescription = null;
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
    public long getPutawayHeaderForInboundConfirmV2(String companyId, String plantId, String warehouseId,
                                                    String preInboundNo, String refDocNumber) {
        long putAwayHeaderStatusIdCount = putAwayHeaderRepository.getPutawayHeaderForInboundConfirm(companyId, plantId, warehouseId, preInboundNo, refDocNumber);
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
    public PutAwayHeaderV2 getPutAwayHeaderV2ForPutAwayLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                            String preInboundNo, String refDocNumber, String lineNumber,
                                                            String itemCode, String manufacturerName) {
        PutAwayHeaderV2 putAwayHeader =
                putAwayHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndReferenceField5AndManufacturerNameAndReferenceField9AndDeletionIndicatorOrderByCreatedBy(
                        companyCodeId,
                        plantId,
                        languageId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        itemCode,
                        manufacturerName,
                        lineNumber,
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
    public List<PutAwayHeaderV2> findPutAwayHeaderV2(SearchPutAwayHeaderV2 searchPutAwayHeader)
            throws Exception {
        if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayHeader.getStartCreatedOn(),
                    searchPutAwayHeader.getEndCreatedOn());
            searchPutAwayHeader.setStartCreatedOn(dates[0]);
            searchPutAwayHeader.setEndCreatedOn(dates[1]);
        }
        log.info("searchPutAwayHeader V2: " + searchPutAwayHeader);
        PutAwayHeaderV2Specification spec = new PutAwayHeaderV2Specification(searchPutAwayHeader);
        List<PutAwayHeaderV2> results = putAwayHeaderV2Repository.findAll(spec);
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

        dbPutAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(newPutAwayHeader.getInboundOrderTypeId()));

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
        List<GrLineV2> grLineList = grLineService.getGrLineV2ForReversal(companyCode, languageId, plantId, warehouseId, refDocNumber, packBarcodes);
//        List<IInventoryImpl> createInventoryMovement = null;

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

        // Fetching Item Code
        String itemCode = null;
        String manufactureName = null;
        if (grLineList != null && !grLineList.isEmpty()) {
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

                    ImBasicData imBasicData = new ImBasicData();
                    imBasicData.setCompanyCodeId(companyCode);
                    imBasicData.setPlantId(plantId);
                    imBasicData.setLanguageId(languageId);
                    imBasicData.setWarehouseId(warehouseId);
                    imBasicData.setItemCode(itemCode);
                    imBasicData.setManufacturerName(dbPutAwayLine.getManufacturerName());
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
                        deleteInventory.setInventoryId(System.currentTimeMillis());
                        deleteInventory.setInventoryQuantity(updateStorageBin.getInventoryQuantity() - dbPutAwayHeader.getPutAwayQuantity());
                        deleteInventory.setReferenceField4(deleteInventory.getInventoryQuantity());         //Allocated Qty is always 0 for BinClassId 3
                        InventoryV2 createInventory = inventoryV2Repository.save(deleteInventory);
                        log.info("Delete Inventory Inserted: " + createInventory);
                        isDeleted = true;
                    }
                    log.info("deleteInventory deleted.." + isDeleted);

                    if (isDeleted) {
                            StorageBinV2 dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 1L, updateStorageBin.getStorageBin());
                            log.info("dbStorageBin: " + dbstorageBin);

                            if (dbstorageBin != null) {
                                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                        dbPutAwayLine.getPreInboundNo(), dbPutAwayLine.getItemCode(), dbPutAwayLine.getManufacturerName(), dbPutAwayLine.getLineNo());

                                if (dbStagingLineEntity != null) {
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
                    deleteInventory.setInventoryId(System.currentTimeMillis());
                    deleteInventory.setInventoryQuantity(updateStorageBin.getInventoryQuantity() - dbPutAwayHeader.getPutAwayQuantity());
                    deleteInventory.setReferenceField4(deleteInventory.getInventoryQuantity());         //Allocated Qty is always 0 for BinClassId 3
                    InventoryV2 createInventory = inventoryV2Repository.save(deleteInventory);
                    log.info("Delete Inventory Inserted: " + createInventory);
                    isDeleted = true;
                }

                log.info("---#---deleteInventory deleted.." + isDeleted);

                if (isDeleted) {
                        StorageBinV2 dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 1L, updateStorageBin.getStorageBin());
                        log.info("dbStorageBin: " + dbstorageBin);

                        if (dbstorageBin == null) {
                            dbstorageBin = storageBinRepository.getStorageBinByBinClassId(companyCode, plantId, languageId, warehouseId, 1L, storageBin);
                        }

                        if (dbstorageBin != null) {
                            StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                    dbPutAwayHeader.getPreInboundNo(), updateStorageBin.getItemCode(), updateStorageBin.getManufacturerName(), dbPutAwayHeader.getCaseCode(), dbPutAwayHeader.getPalletCode());

                            if (dbStagingLineEntity != null) {
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
        }

        // Insert a record into INVENTORYMOVEMENT table as below
//        if(grLineList != null && !grLineList.isEmpty()) {
//            for (GrLineV2 grLine : grLineList) {
//                createInventoryMovementV2(grLine, createInventoryMovement, caseCode, palletCode, storageBin);
//            }
//        }

        //update the statusId to complete reversal process
        reversalProcess(grLineList, preInboundNo, companyCode, plantId, languageId, warehouseId, refDocNumber, loginUserID);

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
                        deleteInventory.setInventoryId(System.currentTimeMillis());
                        deleteInventory.setInventoryQuantity(updateStorageBin.getInventoryQuantity() - dbPutAwayHeader.getPutAwayQuantity());
                        deleteInventory.setReferenceField4(deleteInventory.getInventoryQuantity());         //Allocated Qty is always 0 for BinClassId 3
//                        deleteInventory.setAllocatedQuantity(0D);
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
                    deleteInventory.setInventoryId(System.currentTimeMillis());
                    deleteInventory.setInventoryQuantity(updateStorageBin.getInventoryQuantity() - dbPutAwayHeader.getPutAwayQuantity());
                    deleteInventory.setReferenceField4(deleteInventory.getInventoryQuantity());         //Allocated Qty is always 0 for BinClassId 3
//                    deleteInventory.setAllocatedQuantity(0D);
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
                            StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                    dbPutAwayHeader.getPreInboundNo(), updateStorageBin.getItemCode(), updateStorageBin.getManufacturerName(), dbPutAwayHeader.getCaseCode(), dbPutAwayHeader.getPalletCode());

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
        if (inputGrLineList != null && !inputGrLineList.isEmpty()) {
        for (GrLineV2 grLine : inputGrLineList) {
            InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(companyCode,
                    plantId, languageId, warehouseId, refDocNumber,
                    grLine.getPreInboundNo(), grLine.getLineNo(), grLine.getItemCode());

            inboundLine.setStatusId(13L);
            statusDescription = stagingLineV2Repository.getStatusDescription(13L, languageId);
            inboundLine.setStatusDescription(statusDescription);
            // warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine
            InboundLineV2 updatedInboundLine = inboundLineV2Repository.save(inboundLine);
            log.info("InboundLine status updated: " + updatedInboundLine);

            PreInboundLineEntityV2 preInboundLine = preInboundLineService.getPreInboundLineV2(
                    companyCode, plantId, languageId, grLine.getPreInboundNo(), warehouseId, refDocNumber, grLine.getLineNo(), grLine.getItemCode());

            preInboundLine.setStatusId(13L);
            statusDescription = stagingLineV2Repository.getStatusDescription(13L, languageId);
            preInboundLine.setStatusDescription(statusDescription);
            preInboundLine.setUpdatedBy(loginUserID);
            preInboundLine.setUpdatedOn(new Date());
            PreInboundLineEntityV2 updatedPreInboundLine = preInboundLineV2Repository.save(preInboundLine);
            log.info("preInboundLine status updated: " + updatedPreInboundLine);

            StagingLineEntityV2 stagingLineEntity = stagingLineService.getStagingLineForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, grLine.getItemCode(), grLine.getManufacturerName(), grLine.getLineNo());
            if (stagingLineEntity != null) {
                Double rec_accept_qty = stagingLineEntity.getRec_accept_qty() - grLine.getAcceptedQty();
                Double rec_damage_qty = stagingLineEntity.getRec_damage_qty() - grLine.getDamageQty();

                stagingLineEntity.setRec_accept_qty(rec_accept_qty);
                stagingLineEntity.setRec_damage_qty(rec_damage_qty);
                stagingLineEntity.setStatusId(13L);
                statusDescription = stagingLineV2Repository.getStatusDescription(13L, grLine.getLanguageId());
                stagingLineEntity.setStatusDescription(statusDescription);
                stagingLineEntity = stagingLineV2Repository.save(stagingLineEntity);
                log.info("stagingLineEntity rec_accept_damage_qty and status updated: " + stagingLineEntity);
            }

            //delete grline
            grLine.setDeletionIndicator(1L);
            grLineV2Repository.save(grLine);
            log.info("grLine deleted successfully");
        }
        }

        //Gr Header
        GrHeaderV2 updateGrHeaderStatus = grHeaderService.getGrHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, refDocNumber);
        log.info("GrHeader for Status Update: " + updateGrHeaderStatus);
        if (updateGrHeaderStatus != null) {
            updateGrHeaderStatus.setStatusId(16L);
            statusDescription = stagingLineV2Repository.getStatusDescription(16L, languageId);
            updateGrHeaderStatus.setStatusDescription(statusDescription);
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
        PreInboundHeaderEntity updatedPreInboundHeaderEntity = preInboundHeaderV2Repository.save(preInboundHeader);
        log.info("PreInboundHeader status updated---@------> : " + updatedPreInboundHeaderEntity);


        // Update INBOUNDHEADER

        InboundHeaderV2 updateInboundHeader = inboundHeaderService.getInboundHeaderByEntityV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
        updateInboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        updateInboundHeader.setStatusDescription(statusDescription);
        inboundHeaderV2Repository.saveAndFlush(updateInboundHeader);
        log.info("optInboundHeader Status Updated: " + updateInboundHeader);


        StagingHeaderV2 stagingHeader = stagingHeaderService.getStagingHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);

        // STATUS_ID
        stagingHeader.setStatusId(12L);
        statusDescription = stagingLineV2Repository.getStatusDescription(12L, preInboundHeader.getLanguageId());
        stagingHeader.setStatusDescription(statusDescription);
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
                                                       String warehouseId, String refDocNumber, String loginUserID) {

        List<PutAwayHeaderV2> putAwayHeaderV2List = new ArrayList<>();
        List<PutAwayHeaderV2> putAwayHeaderList = putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
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
}
