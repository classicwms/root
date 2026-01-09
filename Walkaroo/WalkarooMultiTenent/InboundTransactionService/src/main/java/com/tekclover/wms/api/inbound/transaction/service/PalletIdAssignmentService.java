package com.tekclover.wms.api.inbound.transaction.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.FindPalletIdAssignment;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PalletIdAssignment;
import com.tekclover.wms.api.inbound.transaction.repository.PalletIdAssignmentRepository;
import com.tekclover.wms.api.inbound.transaction.repository.specification.PalletIdAssignmentSpecification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class PalletIdAssignmentService extends BaseService {

    @Autowired
    PalletIdAssignmentRepository palletIdAssignmentRepository;

    @Autowired
    PutAwayLineService putAwayLineService;

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param pId
     * @return
     */
    public PalletIdAssignment getPalletIdAssignment(String companyCodeId, String plantId, String languageId, String warehouseId, Long pId) {
        Optional<PalletIdAssignment> dbPalletIdAssignment =
                palletIdAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPaIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId, plantId, warehouseId, pId, languageId, 0L);
        if (dbPalletIdAssignment.isEmpty()) {
            throw new BadRequestException("The given values : " + "warehouseId - " + warehouseId + "pId - " + pId + " doesn't exist.");
        }
        return dbPalletIdAssignment.get();
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param palletId
     * @return
     */
    public PalletIdAssignment getPalletIdAssignmentV3(String companyCodeId, String plantId, String languageId, String warehouseId, String palletId) {
        Optional<PalletIdAssignment> dbPalletIdAssignment = palletIdAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPalletIdAndDeletionIndicator(
                companyCodeId, plantId, warehouseId, languageId, palletId, 0L);
        if (dbPalletIdAssignment.isEmpty()) {
            throw new BadRequestException("The given values : " + "warehouseId - " + warehouseId + "palletId - " + palletId + " doesn't exist.");
        }
        return dbPalletIdAssignment.get();
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param palletId
     * @return
     */
    public boolean getPalletIdAssignmentV3(String companyCodeId, String plantId, String languageId, String warehouseId, String palletId, Long status) {
        return palletIdAssignmentRepository.existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPalletIdAndStatusIdAndDeletionIndicator(
                companyCodeId, plantId, warehouseId, languageId, palletId, status, 0L);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param newPalletIdAssignments
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public List<PalletIdAssignment> createPalletIdAssignment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             List<PalletIdAssignment> newPalletIdAssignments, String loginUserID) throws Exception {
        try {
            log.info("newPalletIdAssignments: " + newPalletIdAssignments);
            List<PalletIdAssignment> createPalletIdAssignment = new ArrayList<>();
            if (newPalletIdAssignments != null && !newPalletIdAssignments.isEmpty()) {
                for (PalletIdAssignment newPalletIdAssignment : newPalletIdAssignments) {
                    boolean isPalletAssigned = getPalletIdAssignmentV3(companyCodeId, plantId, languageId, warehouseId, newPalletIdAssignment.getPalletId(), 0L);
                    log.info("isPalletAssigned: " + isPalletAssigned);

                    if(isPalletAssigned) {
                        throw new BadRequestException("PalletId Already assigned: " + newPalletIdAssignment.getPalletId());
                    }

                    if (!isPalletAssigned) {
                        PalletIdAssignment dbPalletIdAssignment = new PalletIdAssignment();
                        log.info("newPalletIdAssignment : " + newPalletIdAssignment);
                        BeanUtils.copyProperties(newPalletIdAssignment, dbPalletIdAssignment, CommonUtils.getNullPropertyNames(newPalletIdAssignment));

                        if (dbPalletIdAssignment.getCompanyDescription() == null) {
                            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                            if (description != null) {
                                dbPalletIdAssignment.setCompanyDescription(description.getCompanyDesc());
                                dbPalletIdAssignment.setPlantDescription(description.getPlantDesc());
                                dbPalletIdAssignment.setWarehouseDescription(description.getWarehouseDesc());
                            }
                        }

                        String palletId = newPalletIdAssignment.getPalletId();
                        if (palletId != null) {
                            palletId = palletId.trim().replaceAll("\\s+", "");
                        }
                        dbPalletIdAssignment.setPalletId(palletId);
                        dbPalletIdAssignment.setStatusId(1L);
                        dbPalletIdAssignment.setStatusDescription(IN_ACTIVE);
                        dbPalletIdAssignment.setDeletionIndicator(0L);
                        dbPalletIdAssignment.setCreatedBy(loginUserID);
                        dbPalletIdAssignment.setAssignedBy(loginUserID);
                        dbPalletIdAssignment.setCreatedOn(new Date());
                        dbPalletIdAssignment.setAssignedOn(new Date());
                        PalletIdAssignment palletIdAssignment = palletIdAssignmentRepository.save(dbPalletIdAssignment);
                        createPalletIdAssignment.add(palletIdAssignment);
                    }
                }
            }
            return createPalletIdAssignment;
        } catch (Exception e) {
            log.error("Exception while creating PalletIdAssignemnt: " + e.getLocalizedMessage());
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param updatePalletIdAssignments
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public List<PalletIdAssignment> updatePalletIdAssignment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             List<PalletIdAssignment> updatePalletIdAssignments, String loginUserID) throws Exception {
        try {
            log.info("updatePalletIdAssignments:" + updatePalletIdAssignments);
            List<PalletIdAssignment> updatedPalletIdAssignmentList = new ArrayList<>();
            if (updatePalletIdAssignments != null && !updatePalletIdAssignments.isEmpty()) {
                for (PalletIdAssignment updatePalletIdAssignment : updatePalletIdAssignments) {
                    PalletIdAssignment dbPalletIdAssignment = getPalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, updatePalletIdAssignment.getPaId());

                    boolean isPalletAssigned = getPalletIdAssignmentV3(companyCodeId, plantId, languageId, warehouseId, updatePalletIdAssignment.getPalletId(), 0L);
                    log.info("isPalletAssigned: " + isPalletAssigned);
                    if(isPalletAssigned) {
                        throw new BadRequestException("PalletId Already assigned: " + updatePalletIdAssignment.getPalletId());
                    }

                    if (!isPalletAssigned) {
                        BeanUtils.copyProperties(updatePalletIdAssignment, dbPalletIdAssignment, CommonUtils.getNullPropertyNames(updatePalletIdAssignment));
                        dbPalletIdAssignment.setAssignedBy(loginUserID);
                        dbPalletIdAssignment.setAssignedOn(new Date());
                        dbPalletIdAssignment.setUpdatedBy(loginUserID);
                        dbPalletIdAssignment.setUpdatedOn(new Date());
                        PalletIdAssignment updatedPalletId = palletIdAssignmentRepository.save(dbPalletIdAssignment);
                        updatedPalletIdAssignmentList.add(updatedPalletId);
                    }
                }
            }
            return updatedPalletIdAssignmentList;
        } catch (Exception e) {
            log.error("Exception while Updating PalletIdAssignemnt : " + e.getLocalizedMessage());
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param updatePalletIdAssignments
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public List<PalletIdAssignment> updatePalletIdStatus(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         List<PalletIdAssignment> updatePalletIdAssignments, String loginUserID) throws Exception {
        List<PalletIdAssignment> updatedPalletIdAssignmentList = new ArrayList<>();
        if (updatePalletIdAssignments != null && !updatePalletIdAssignments.isEmpty()) {
            for (PalletIdAssignment updatePalletIdAssignment : updatePalletIdAssignments) {
                PalletIdAssignment dbPalletIdAssignment = getPalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, updatePalletIdAssignment.getPaId());
                BeanUtils.copyProperties(updatePalletIdAssignment, dbPalletIdAssignment, CommonUtils.getNullPropertyNames(updatePalletIdAssignment));
                dbPalletIdAssignment.setStatusDescription(getStatus(updatePalletIdAssignment.getStatusId()));
                if(dbPalletIdAssignment.getStatusDescription().equalsIgnoreCase(IN_ACTIVE)) {
                    dbPalletIdAssignment.setConfirmedBy(loginUserID);
                    dbPalletIdAssignment.setConfirmedOn(new Date());

                }
                dbPalletIdAssignment.setAssignedBy(loginUserID);
                dbPalletIdAssignment.setAssignedOn(new Date());
                dbPalletIdAssignment.setUpdatedBy(loginUserID);
                dbPalletIdAssignment.setUpdatedOn(new Date());
                PalletIdAssignment updatedPalletId = palletIdAssignmentRepository.save(dbPalletIdAssignment);
                updatedPalletIdAssignmentList.add(updatedPalletId);
            }
        }
        return updatedPalletIdAssignmentList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param pId
     * @param loginUserID
     */
    public void deletePalletIdAssignment(String companyCodeId, String plantId, String languageId, String warehouseId, Long pId, String loginUserID) {
        PalletIdAssignment dbPalletIdAssignment = getPalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, pId);
        if (dbPalletIdAssignment != null) {
            dbPalletIdAssignment.setDeletionIndicator(1L);
            dbPalletIdAssignment.setUpdatedBy(loginUserID);
            palletIdAssignmentRepository.save(dbPalletIdAssignment);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + pId);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param palletId
     * @param loginUserId
     */
    public void updatePalletIdAssignment(String companyCodeId, String plantId, String languageId, String warehouseId, String palletId, String loginUserId) {
        palletIdAssignmentRepository.updatePalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, palletId, loginUserId, new Date(), 1L, IN_ACTIVE);
    }

    /**
     * @param findPalletIdAssignment
     * @return
     * @throws Exception
     */
    public Stream<PalletIdAssignment> findPalletIdAssignment(FindPalletIdAssignment findPalletIdAssignment) throws Exception {
        log.info("findPalletIdAssignment: " + findPalletIdAssignment);
        if(findPalletIdAssignment.getPalletId() != null && !findPalletIdAssignment.getPalletId().isEmpty()) {
            List<String> trimmedList = findPalletIdAssignment.getPalletId().stream().filter(n -> n != null && !n.isBlank()).map(String::trim).distinct().collect(Collectors.toList());
            findPalletIdAssignment.setPalletId(trimmedList);
        }
        log.info("trimmed findPalletIdAssignment: " + findPalletIdAssignment);
        PalletIdAssignmentSpecification spec = new PalletIdAssignmentSpecification(findPalletIdAssignment);
        return palletIdAssignmentRepository.stream(spec, PalletIdAssignment.class);
    }


}
