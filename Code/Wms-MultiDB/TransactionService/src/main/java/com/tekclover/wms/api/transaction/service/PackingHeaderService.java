package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.packing.AddPackingHeader;
import com.tekclover.wms.api.transaction.model.packing.PackingHeader;
import com.tekclover.wms.api.transaction.model.packing.UpdatePackingHeader;
import com.tekclover.wms.api.transaction.repository.PackingHeaderRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PackingHeaderService {

    @Autowired
    private PackingHeaderRepository packingHeaderRepository;

    /**
     * getPackingHeaders
     * @return
     */
    public List<PackingHeader> getPackingHeaders() {
        List<PackingHeader> packingHeaderList = packingHeaderRepository.findAll();
        packingHeaderList = packingHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return packingHeaderList;
    }

    /**
     * getPackingHeader
     * @param languageId
     * @param qualityInspectionNo
     * @param partnerCode
     * @param refDocNumber
     * @param preOutboundNo
     * @param warehouseId
     * @param plantId
     * @param companyCodeId
     * @return
     */
    public PackingHeader getPackingHeader(String languageId, String companyCodeId, String plantId, String warehouseId,
                                          String preOutboundNo, String refDocNumber, String partnerCode, String qualityInspectionNo,
                                          String packingNo) {
        Optional<PackingHeader> packingHeader =
                packingHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndQualityInspectionNoAndPackingNoAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, qualityInspectionNo, packingNo, 0L);
        if (packingHeader.isEmpty()) {
            return packingHeader.get();
        }
        return packingHeader.get();

    }

//	public List<PackingHeader> findPackingHeader(SearchPackingHeader searchPackingHeader)
//			throws ParseException {
//		PackingHeaderSpecification spec = new PackingHeaderSpecification(searchPackingHeader);
//		List<PackingHeader> results = packingHeaderRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

    /**
     * createPackingHeader
     * @param newPackingHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PackingHeader createPackingHeader(AddPackingHeader newPackingHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Optional<PackingHeader> packingheader =
                packingHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndQualityInspectionNoAndPackingNoAndDeletionIndicator(
                        newPackingHeader.getLanguageId(),
                        newPackingHeader.getCompanyCodeId(),
                        newPackingHeader.getPlantId(),
                        newPackingHeader.getWarehouseId(),
                        newPackingHeader.getPreOutboundNo(),
                        newPackingHeader.getRefDocNumber(),
                        newPackingHeader.getPartnerCode(),
                        newPackingHeader.getQualityInspectionNo(),
                        newPackingHeader.getPackingNo(),
                        0L);
        if (!packingheader.isEmpty()) {
            throw new BadRequestException("Record is getting duplicated with the given values");
        }
        PackingHeader dbPackingHeader = new PackingHeader();
        log.info("newPackingHeader : " + newPackingHeader);
        BeanUtils.copyProperties(newPackingHeader, dbPackingHeader, CommonUtils.getNullPropertyNames(newPackingHeader));
        dbPackingHeader.setPackConfirmedBy(loginUserID);
        dbPackingHeader.setPackUpdatedBy(loginUserID);
        dbPackingHeader.setPackConfirmedOn(new Date());
        dbPackingHeader.setPackUpdatedOn(new Date());

        dbPackingHeader.setDeletionIndicator(0L);
        return packingHeaderRepository.save(dbPackingHeader);
    }

    /***
     * UpdatePackingHeader
     *
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param qualityInspectionNo
     * @param packingNo
     * @param loginUserID
     * @param updatePackingHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PackingHeader updatePackingHeader(String languageId, String companyCodeId, String plantId, String warehouseId,
                                             String preOutboundNo, String refDocNumber, String partnerCode,
                                             String qualityInspectionNo, String packingNo,
                                             String loginUserID, UpdatePackingHeader updatePackingHeader)
            throws IllegalAccessException, InvocationTargetException {
        PackingHeader dbPackingHeader = getPackingHeader(languageId, companyCodeId, plantId, warehouseId, preOutboundNo,
                                                         refDocNumber, partnerCode, qualityInspectionNo, packingNo);
        BeanUtils.copyProperties(updatePackingHeader, dbPackingHeader, CommonUtils.getNullPropertyNames(updatePackingHeader));
        dbPackingHeader.setPackUpdatedBy(loginUserID);
        dbPackingHeader.setPackConfirmedOn(new Date());
        return packingHeaderRepository.save(dbPackingHeader);
    }


    /**
     * DeletePackingHeader
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param qualityInspectionNo
     * @param packingNo
     * @param loginUserID
     */
    public void deletePackingHeader(String languageId, String companyCodeId, String plantId,
                                    String warehouseId, String preOutboundNo, String refDocNumber,
                                    String partnerCode, String qualityInspectionNo, String packingNo, String loginUserID) {
        PackingHeader packingHeader = getPackingHeader(languageId, companyCodeId, plantId, warehouseId,
                                                       preOutboundNo, refDocNumber, partnerCode, qualityInspectionNo, packingNo);
        if (packingHeader != null) {
            packingHeader.setPackUpdatedBy(loginUserID);
            packingHeader.setPackUpdatedOn(new Date());
            packingHeader.setDeletionIndicator(1L);
            packingHeaderRepository.save(packingHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + packingNo);
        }
    }
}