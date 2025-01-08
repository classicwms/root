package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.dto.ImPartner;
import com.tekclover.wms.api.transaction.repository.ImPartnerRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImPartnerService {

    @Autowired
    private ImPartnerRepository impartnerRepository;

    public List<ImPartner> getImpartnerList(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String partnerItemBarcode, String itemCode, String manufacturerName) {

        List<ImPartner> imPartnerList =
                impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, partnerItemBarcode, itemCode, manufacturerName, 0L);
//		if(imPartnerList != null && !imPartnerList.isEmpty()) {
        log.info("Impartner List: " + imPartnerList);
        return imPartnerList;
//		}

//		throw new BadRequestException("ImPartner Not Found: " + partnerItemBarcode + ", " + itemCode + ", " + manufacturerName);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param partnerItemBarcode
     * @return
     */
    public List<ImPartner> getImpartnerBarcodeList(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String partnerItemBarcode) {

        List<ImPartner> imPartnerList =
                impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, partnerItemBarcode, 0L);
//		if(imPartnerList != null && !imPartnerList.isEmpty()) {
        log.info("Impartner List: " + imPartnerList);
        return imPartnerList;
//		}
//		throw new BadRequestException("ImPartner Barcode Not Found: " + partnerItemBarcode );
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
    public List<ImPartner> getImpartnerItemCodeList(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, String itemCode, String manufacturerName) {

        List<ImPartner> imPartnerList =
                impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L);
//		if(imPartnerList != null && !imPartnerList.isEmpty()) {
        log.info("Impartner List: " + imPartnerList);
        return imPartnerList;
//		}
//		throw new BadRequestException("ImPartner Not Found: " + itemCode + ", " + manufacturerName);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param loginUserID
     */
    public ImPartner updateImPartner(String companyCodeId, String plantId, String languageId, String warehouseId,
                                     String itemCode, String manufacturerName, String barcodeId, String loginUserID) {
        try {
            //Duplicate Barcode Validation
            List<ImPartner> duplicateBarcodeCheck = impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, barcodeId, 0L);
            log.info("Duplicate BarcodeId : " + duplicateBarcodeCheck);
            if (duplicateBarcodeCheck != null && !duplicateBarcodeCheck.isEmpty()) {
                for (ImPartner dbImPartner : duplicateBarcodeCheck) {
                    String dbItemCode = dbImPartner.getItemCode();
                    String dbManufacturerName = dbImPartner.getManufacturerName();
                    String dbItmMfrName = dbItemCode + dbManufacturerName;
                    String newItmMfrName = itemCode + manufacturerName;
                    log.info("dbItmMfrName, newItmMfrName : " + dbItmMfrName + ", " + newItmMfrName);
                    if (!dbItmMfrName.equalsIgnoreCase(newItmMfrName)) {
                        throw new BadRequestException("BarcodeId already exist for different ItemCode MfrName: "
                                                              + barcodeId + ", " + dbItemCode + ", " + dbManufacturerName);
                    }
                }
            }

            List<ImPartner> existingBarcodeId = impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, 0L);
            log.info("Existing BarcodeId : " + existingBarcodeId);

            if (existingBarcodeId != null && !existingBarcodeId.isEmpty()) {
                return existingBarcodeId.get(0);
            }

            ImPartner imPartner = new ImPartner();
            imPartner.setBusinessPartnerCode(manufacturerName);
            imPartner.setCompanyCodeId(companyCodeId);
            imPartner.setPlantId(plantId);
            imPartner.setLanguageId(languageId);
            imPartner.setWarehouseId(warehouseId);
            imPartner.setItemCode(itemCode);
            imPartner.setManufacturerName(manufacturerName);
            imPartner.setManufacturerCode(manufacturerName);
            imPartner.setBusinessPartnerType("1");
            imPartner.setPartnerItemBarcode(barcodeId);
            imPartner.setDeletionIndicator(0L);
            imPartner.setCreatedBy(loginUserID);
            imPartner.setCreatedOn(new Date());
            log.info("BarCode Created: " + imPartner);
            return impartnerRepository.save(imPartner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     *
     * @param imPartnerList
     * @throws Exception
     */
    public void createImPartner(List<ImPartner> imPartnerList) throws Exception {
        log.info("Impartner Input: " + imPartnerList.size());
        try {
            List<ImPartner> newImPartnerList = imPartnerList.stream().map(addImpartner -> {
                ImPartner imPartner = new ImPartner();
                BeanUtils.copyProperties(addImpartner, imPartner, CommonUtils.getNullPropertyNames(addImpartner));
                return imPartner;
            }).collect(Collectors.toList());
            impartnerRepository.saveAll(newImPartnerList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}