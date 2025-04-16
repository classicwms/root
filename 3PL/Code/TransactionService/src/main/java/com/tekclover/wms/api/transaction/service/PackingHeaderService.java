package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.outbound.packing.AddPackingHeader;
import com.tekclover.wms.api.transaction.model.outbound.packing.PackingHeader;
import com.tekclover.wms.api.transaction.model.outbound.packing.SearchPackingHeader;
import com.tekclover.wms.api.transaction.model.outbound.packing.UpdatePackingHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;
import com.tekclover.wms.api.transaction.repository.PackingHeaderRepository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.specification.PackingHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PackingHeaderService extends BaseService {

    @Autowired
    private PackingHeaderRepository packingHeaderRepository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    String statusDescription = null;

    @Autowired
    protected AuthTokenService authTokenService;

    /**
     * getPackingHeaders
     *
     * @return
     */
    public List<PackingHeader> getPackingHeaders() {
        List<PackingHeader> packingHeaderList = packingHeaderRepository.findAll();
        packingHeaderList = packingHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return packingHeaderList;
    }

    /**
     * getPackingHeader
     *
     * @param packingNo2
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
        PackingHeader packingHeader = packingHeaderRepository.findByPackingNo(packingNo).orElse(null);
        if (packingHeader != null && packingHeader.getDeletionIndicator() == 0) {
            return packingHeader;
        } else {
            throw new BadRequestException("The given PackingHeader ID : " + packingNo + " doesn't exist.");
        }
    }


    // Number Range
    public String getPackingHeaderNumberRange(String languageId, String companyCodeId, String plantId, String warehouseId) {
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            String nextRangeNumber = getNextRangeNumber(16L, companyCodeId, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }

	public List<PackingHeader> findPackingHeader(SearchPackingHeader searchPackingHeader)
			throws Exception {
		PackingHeaderSpecification spec = new PackingHeaderSpecification(searchPackingHeader);
		List<PackingHeader> results = packingHeaderRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}

    /**
     * createPackingHeader
     *
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
        BeanUtils.copyProperties(newPackingHeader, dbPackingHeader);
        IKeyValuePair description = stagingLineV2Repository.getDescription(dbPackingHeader.getCompanyCodeId(),
                dbPackingHeader.getLanguageId(),
                dbPackingHeader.getPlantId(),
                dbPackingHeader.getWarehouseId());

        dbPackingHeader.setCompanyDescription(description.getCompanyDesc());
        dbPackingHeader.setPlantDescription(description.getPlantDesc());
        dbPackingHeader.setWarehouseDescription(description.getWarehouseDesc());
        if (dbPackingHeader.getStatusId() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(dbPackingHeader.getStatusId(), dbPackingHeader.getLanguageId());
            dbPackingHeader.setStatusDescription(statusDescription);
        }

        dbPackingHeader.setDeletionIndicator(0L);
        return packingHeaderRepository.save(dbPackingHeader);
    }

    /**
     * updatePackingHeader
     *
     * @param loginUserId
     * @param packingNo
     * @param updatePackingHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PackingHeader updatePackingHeader(String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String qualityInspectionNo, String packingNo,
                                             String loginUserID, UpdatePackingHeader updatePackingHeader)
            throws IllegalAccessException, InvocationTargetException {
        PackingHeader dbPackingHeader = getPackingHeader(packingNo);
        BeanUtils.copyProperties(updatePackingHeader, dbPackingHeader, CommonUtils.getNullPropertyNames(updatePackingHeader));
        return packingHeaderRepository.save(dbPackingHeader);
    }

    private PackingHeader getPackingHeader(String packingNo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * deletePackingHeader
     *
     * @param loginUserID
     * @param packingNo
     */
    public void deletePackingHeader(String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String qualityInspectionNo, String packingNo, String loginUserID) {
        PackingHeader packingHeader = getPackingHeader(packingNo);
        if (packingHeader != null) {
            packingHeader.setDeletionIndicator(1L);
            packingHeaderRepository.save(packingHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + packingNo);
        }
    }

    public PackingHeader createPackingQualityHeader(QualityLine qualityLine)
            throws IllegalAccessException, InvocationTargetException {
//		Optional<PackingHeader> packingheader =
//				packingHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndQualityInspectionNoAndPackingNoAndDeletionIndicator(
//						newPackingHeader.getLanguageId(),
//						newPackingHeader.getCompanyCodeId(),
//						newPackingHeader.getPlantId(),
//						newPackingHeader.getWarehouseId(),
//						newPackingHeader.getPreOutboundNo(),
//						newPackingHeader.getRefDocNumber(),
//						newPackingHeader.getPartnerCode(),
//						newPackingHeader.getQualityInspectionNo(),
//						newPackingHeader.getPackingNo(),
//						0L);
//		if (!packingheader.isEmpty()) {
//			throw new BadRequestException("Record is getting duplicated with the given values");
//		}
        PackingHeader dbPackingHeader = new PackingHeader();
        BeanUtils.copyProperties(qualityLine, dbPackingHeader);
        String num = getPackingHeaderNumberRange(dbPackingHeader.getWarehouseId(), dbPackingHeader.getCompanyCodeId(), dbPackingHeader.getPlantId(), dbPackingHeader.getWarehouseId());
        dbPackingHeader.setPackingNo(num);
        dbPackingHeader.setDeletionIndicator(0L);
        return packingHeaderRepository.save(dbPackingHeader);
    }
}
