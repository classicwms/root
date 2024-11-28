package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.packing.AddPackingLine;
import com.tekclover.wms.api.transaction.model.packing.PackingLine;
import com.tekclover.wms.api.transaction.model.packing.UpdatePackingLine;
import com.tekclover.wms.api.transaction.repository.PackingLineRepository;
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
public class PackingLineService {

    @Autowired
    private PackingLineRepository packingLinesRepository;

    /**
     * getPackingLines
     * @return
     */
    public List<PackingLine> getPackingLines() {
        List<PackingLine> packingLinesList = packingLinesRepository.findAll();
        packingLinesList = packingLinesList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return packingLinesList;
    }


//	public List<PackingLine> findPackingLine(SearchPackingLine searchPackingLine)
//			throws ParseException {
//		PackingLineSpecification spec = new PackingLineSpecification(searchPackingLine);
//		List<PackingLine> results = packingLinesRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

    /**
     * createPackingLine
     * @param newPackingLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PackingLine createPackingLine(AddPackingLine newPackingLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
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
        log.info("newPackingLine : " + newPackingLine);
        BeanUtils.copyProperties(newPackingLine, dbPackingLine);
        dbPackingLine.setDeletionIndicator(0L);
        return packingLinesRepository.save(dbPackingLine);
    }

    /**
     * updatePackingLine
     * @param loginUserId
     * @param itemCode
     * @param updatePackingLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PackingLine updatePackingLine(String languageId, Long companyCodeId, String plantId, String warehouseId,
                                         String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                         String packingNo, String itemCode, String loginUserID, UpdatePackingLine updatePackingLine)
            throws IllegalAccessException, InvocationTargetException {
        PackingLine dbPackingLine = getPackingLine(languageId, companyCodeId, plantId, warehouseId,
                                                   preOutboundNo, refDocNumber, partnerCode, lineNumber, packingNo, itemCode);
        BeanUtils.copyProperties(updatePackingLine, dbPackingLine, CommonUtils.getNullPropertyNames(updatePackingLine));
        dbPackingLine.setPackUpdatedBy(loginUserID);
        dbPackingLine.setPackUpdatedOn(new Date());
        return packingLinesRepository.save(dbPackingLine);
    }

    /**
     * DeletePackingLine
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param packingNo
     * @param itemCode
     * @param loginUserID
     */
    public void deletePackingLine(String languageId, Long companyCodeId, String plantId, String warehouseId,
                                  String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                  String packingNo, String itemCode, String loginUserID) {
        PackingLine packingLines = getPackingLine(languageId, companyCodeId, plantId, warehouseId,
                                                  preOutboundNo, refDocNumber, partnerCode, lineNumber, packingNo, itemCode);
        if (packingLines != null) {
            packingLines.setPackUpdatedOn(new Date());
            packingLines.setPackUpdatedBy(loginUserID);
            packingLines.setDeletionIndicator(1L);
            packingLinesRepository.save(packingLines);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + itemCode);
        }
    }

    /**
     * GetPackingLine
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param packingNo
     * @param itemCode
     * @return
     */
    public PackingLine getPackingLine(String languageId, Long companyCodeId, String plantId, String warehouseId,
                                      String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String packingNo,
                                      String itemCode) {

        Optional<PackingLine> dbPackingLine = packingLinesRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPackingNoAndItemCodeAndDeletionIndicator(
                languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, packingNo, itemCode, 0L);

        if (dbPackingLine.isEmpty()) {
            throw new BadRequestException("Given Values Doesn't exits");
        }
        return dbPackingLine.get();
    }
}