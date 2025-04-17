package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.outbound.packing.*;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.model.threepl.pricelist.PriceList;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.PackingLineSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PackingLineService extends BaseService {

    @Autowired
    private PackingLineRepository packingLinesRepository;

    @Autowired
    private PackingHeaderRepository packingHeaderRepository;

    @Autowired
    private OutboundHeaderService outboundHeaderService;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private PriceListRepository priceListRepository;

    @Autowired
    PriceListAssignmentRepository priceListAssignmentRepository;

    String statusDescription = null;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    /**
     * getPackingLines
     *
     * @return
     */
    public List<PackingLine> getPackingLines() {
        List<PackingLine> packingLinesList = packingLinesRepository.findAll();
        packingLinesList = packingLinesList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return packingLinesList;
    }

    /**
     * getPackingLine
     *
     * @return
     */
    public PackingLine getPackingLine(String itemCode) {
        PackingLine packingLines = packingLinesRepository.findByItemCode(itemCode).orElse(null);
        if (packingLines != null && packingLines.getDeletionIndicator() == 0) {
            return packingLines;
        } else {
            throw new BadRequestException("The given PackingLine ID : " + itemCode + " doesn't exist.");
        }
    }

    public List<PackingLine> findPackingLine(SearchPackingLine searchPackingLine)
            throws Exception {
        PackingLineSpecification spec = new PackingLineSpecification(searchPackingLine);
        List<PackingLine> results = packingLinesRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * createPackingLine
     *
     * @param newPackingLineList
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PackingLine> createPackingLine(List<AddPackingLine> newPackingLineList, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<PackingLine> packingLineList = new ArrayList<>();

        AddPackingLine number = newPackingLineList.get(0);
        String PACK_NO = getNextRangeNumber(17L, number.getCompanyCodeId(), number.getPlantId(), number.getLanguageId(), number.getWarehouseId());
        for (AddPackingLine newPackingLine : newPackingLineList) {
            Optional<PackingLine> packingline =
                    packingLinesRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPackingNoAndItemCodeAndDeletionIndicator(
                            newPackingLine.getLanguageId(),
                            newPackingLine.getCompanyCodeId(),
                            newPackingLine.getPlantId(),
                            newPackingLine.getWarehouseId(),
                            newPackingLine.getPreOutboundNo(),
                            newPackingLine.getRefDocNumber(),
                            newPackingLine.getPartnerCode(),
                            newPackingLine.getLineNumber(),
                            newPackingLine.getPackingNo(),
                            newPackingLine.getItemCode(),
                            0L);
            if (!packingline.isEmpty()) {
                throw new BadRequestException("Record is getting duplicated with the given values");
            }
            PackingLine dbPackingLine = new PackingLine();
            BeanUtils.copyProperties(newPackingLine, dbPackingLine, CommonUtils.getNullPropertyNames(newPackingLine));
            InventoryV2 inventory = inventoryService.getInventoryForPacking(newPackingLine.getCompanyCodeId(),
                    newPackingLine.getPlantId(), newPackingLine.getLanguageId(), newPackingLine.getWarehouseId(), newPackingLine.getItemCode());

            if (inventory.getThreePLCbmPerQty() != null) {
                if (newPackingLine.getPickConfirmQty() != null) {
                    Double pickCbm = inventory.getThreePLCbmPerQty() * newPackingLine.getPickConfirmQty();
                    dbPackingLine.setThreePLCbm(pickCbm);
                    dbPackingLine.setThreePLUom(inventory.getThreePLUom());
                }
            }
            // ThreePL
            log.info("ThreePL Logic Started --------------------------------> ");
            {
                Optional.ofNullable(priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
                                newPackingLine.getCompanyCodeId(), newPackingLine.getPlantId(), newPackingLine.getWarehouseId(), newPackingLine.getPartnerCode(), 0L))
                        .ifPresentOrElse(priceListAssignment -> {
                            List<PriceList> priceLists = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndDeletionIndicator(
                                    priceListAssignment.getCompanyCodeId(), priceListAssignment.getPlantId(), priceListAssignment.getWarehouseId(), priceListAssignment.getPriceListId(), 5L, 0L);
                            log.info("PriceList Values {}", priceLists);

                            Double threePLCbm = dbPackingLine.getThreePLCbm(); // Given total CBM
                            String currency = new String();
                            double chargeUnit = 0.0;

                            // Sort the price list by chargeFrom so that lower ranges are processed first
                            priceLists.sort(Comparator.comparingDouble(PriceList::getChargeRangeFrom));

                            for (PriceList dbPrice : priceLists) {
                                double chargeFrom = dbPrice.getChargeRangeFrom() != null ? dbPrice.getChargeRangeFrom() : 0.0;
                                double chargeTo = dbPrice.getChargeRangeTo() != null ? dbPrice.getChargeRangeTo() : 0.0;
                                double chargePerUnit = dbPrice.getPricePerChargeUnit() != null ? dbPrice.getPricePerChargeUnit() : 0.0;
//                                currency.append(dbPrice.getReferenceField4());
                                currency = dbPrice.getReferenceField4();
                                log.info("ThreePL CBM Value {}, ChargeFrom Value {}, ChargeTo Value {}, ChargeUnit Value{} ", threePLCbm, chargeFrom, chargeTo, chargeUnit);
                                if (threePLCbm >= chargeFrom && threePLCbm >= chargeTo) {
                                    if (chargeUnit != 0.0) {
                                        chargeUnit = chargePerUnit;
                                    } else {
                                        chargeUnit = chargePerUnit;
                                    }
                                    threePLCbm -= chargeTo;
                                } else if (threePLCbm <= chargeTo) {
                                    if (chargeUnit != 0.0) {
                                        chargeUnit = chargePerUnit;
                                    } else {
                                        chargeUnit = chargePerUnit;
                                    }
                                    threePLCbm -= chargeTo;
                                }
                                if (threePLCbm <= 0) {
                                    break;
                                }
                            }
                            log.info("Finally ThreePLUom Value is {}", chargeUnit);
                            dbPackingLine.setRate(chargeUnit);
                            dbPackingLine.setCurrency(String.valueOf(currency));

                        }, () -> log.warn("PriceListAssignment not found for CompanyCode {}, PlantId {}, WarehouseId {}, PartnerCode {}",
                                dbPackingLine.getCompanyCodeId(), dbPackingLine.getPlantId(), dbPackingLine.getWarehouseId(), dbPackingLine.getPartnerCode()));
            }

            log.info("ThreePL Logic completed -----------------------------------------> ");
            log.info("newPackingLine : " + newPackingLine);

            IKeyValuePair description = stagingLineV2Repository.getDescription(dbPackingLine.getCompanyCodeId(),
                    dbPackingLine.getLanguageId(),
                    dbPackingLine.getPlantId(),
                    dbPackingLine.getWarehouseId());

            dbPackingLine.setPackingNo(Long.valueOf(PACK_NO));
            dbPackingLine.setCompanyDescription(description.getCompanyDesc());
            dbPackingLine.setPlantDescription(description.getPlantDesc());
            dbPackingLine.setWarehouseDescription(description.getWarehouseDesc());
            dbPackingLine.setDeletionIndicator(0L);
            dbPackingLine.setStatusId(67L);
            statusDescription = stagingLineV2Repository.getStatusDescription(67L, dbPackingLine.getLanguageId());
            dbPackingLine.setStatusDescription(statusDescription);
            // Outbound_Header_Update

            try {
                outboundHeaderService.updateOutboundHeaderV2(dbPackingLine.getCompanyCodeId(), dbPackingLine.getPlantId(), dbPackingLine.getLanguageId(),
                        dbPackingLine.getWarehouseId(), dbPackingLine.getPreOutboundNo(), dbPackingLine.getRefDocNumber(), dbPackingLine.getPartnerCode(), loginUserID);

            } catch (Exception e) {
                log.info("Exception throw OutboundHeader Status Update 59 ----------------------------------> ");
            }
            // Update_Packing_Header
            packingHeaderRepository.updatePackingHeader(67L, statusDescription, dbPackingLine.getRefDocNumber());
            log.info("Packing Header Updated Successfully");
            packingLineList.add(packingLinesRepository.save(dbPackingLine));
        }
        return packingLineList;
    }

    /**
     * updatePackingLine
     *
     * @param itemCode
     * @param updatePackingLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PackingLine updatePackingLine(String languageId, String companyCodeId, String plantId, String
            warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String
                                                 packingNo, String itemCode,
                                         String loginUserID, UpdatePackingLine updatePackingLine)
            throws IllegalAccessException, InvocationTargetException {
        PackingLine dbPackingLine = getPackingLine(itemCode);
        BeanUtils.copyProperties(updatePackingLine, dbPackingLine, CommonUtils.getNullPropertyNames(updatePackingLine));
        return packingLinesRepository.save(dbPackingLine);
    }

    /**
     * deletePackingLine
     *
     * @param loginUserID
     * @param itemCode
     */
    public void deletePackingLine(String languageId, String companyCodeId, String plantId, String
            warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String
                                          packingNo, String itemCode, String loginUserID) {
        PackingLine packingLines = getPackingLine(itemCode);
        if (packingLines != null) {
            packingLines.setDeletionIndicator(1L);
            packingLinesRepository.save(packingLines);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + itemCode);
        }
    }

    public PackingLine getPackingLine(String languageId, Long companyCodeId, String plantId, String warehouseId,
                                      String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String packingNo,
                                      String itemCode) {
        // TODO Auto-generated method stub
        return null;
    }
//
//	public PackingLine create3PLPackingLine (AddPackingLine newPackingLine, String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//		Optional<PackingHeader> packingHeader =
//				packingHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndQualityInspectionNoAndPackingNoAndDeletionIndicator(
//						newPackingLine.getLanguageId(),
//						newPackingLine.getCompanyCodeId(),
//						newPackingLine.getPlantId(),
//						newPackingLine.getWarehouseId(),
//						newPackingLine.getPreOutboundNo(),
//						newPackingLine.getRefDocNumber(),
//						newPackingLine.getPartnerCode(),
//						newPackingLine.getLineNumber(),
//						newPackingLine.getPackingNo(),
//						newPackingLine.getItemCode(),
//						0L);
//		if (!packingline.isEmpty()) {
//			throw new BadRequestException("Record is getting duplicated with the given values");
//		}
//		PackingLine dbPackingLine = new PackingLine();
//		log.info("newPackingLine : " + newPackingLine);
//		BeanUtils.copyProperties(newPackingLine, dbPackingLine);
//		dbPackingLine.setDeletionIndicator(0L);
//		return packingLinesRepository.save(dbPackingLine);
//	}
}
