package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.hhtuser.*;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.OutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.HhtUserSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HhtUserService {
    @Autowired
    private OrderTypeIdRepository orderTypeIdRepository;
    @Autowired
    private LevelIdRepository levelIdRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private PlantIdRepository plantIdRepository;

    @Autowired
    private OrderTypeIdService orderTypeIdService;

    @Autowired
    private CompanyIdRepository companyIdRepository;

    @Autowired
    private OutboundOrderTypeIdService outboundOrderTypeIdService;

    @Autowired
    private HhtUserRepository hhtUserRepository;

    /**
     * @return
     */
    public List<HhtUserOutput> getHhtUsers() {
        List<HhtUser> hhtUserList = hhtUserRepository.findAll();
        hhtUserList = hhtUserList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());

        List<HhtUserOutput> newHhtUser = new ArrayList<>();

        for (HhtUser dbHhtUser : hhtUserList) {

            HhtUserOutput dbHhtUserOutput = new HhtUserOutput();

//			if (dbHhtUser.getCompanyIdAndDescription() != null && dbHhtUser.getPlantIdAndDescription() != null &&
//					dbHhtUser.getWarehouseIdAndDescription() != null) {

            IKeyValuePair iKeyValuePair =
                    companyIdRepository.getCompanyIdAndDescription(dbHhtUser.getCompanyCodeId(), dbHhtUser.getLanguageId());

            IKeyValuePair iKeyValuePair1 =
                    plantIdRepository.getPlantIdAndDescription(dbHhtUser.getPlantId(),
                            dbHhtUser.getLanguageId(), dbHhtUser.getCompanyCodeId());

            IKeyValuePair iKeyValuePair2 =
                    warehouseRepository.getWarehouseIdAndDescription(dbHhtUser.getWarehouseId(),
                            dbHhtUser.getLanguageId(), dbHhtUser.getCompanyCodeId(), dbHhtUser.getPlantId());

            IKeyValuePair iKeyValuePair3 =
                    levelIdRepository.getLevelIdAndDescription(dbHhtUser.getLevelId(),
                            dbHhtUser.getLanguageId(), dbHhtUser.getCompanyCodeId(), dbHhtUser.getPlantId(),
                            dbHhtUser.getWarehouseId());

            if (iKeyValuePair != null) {
                dbHhtUser.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
            }
            if (iKeyValuePair1 != null) {
                dbHhtUser.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
            }
            if (iKeyValuePair2 != null) {
                dbHhtUser.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
            }
            if (iKeyValuePair3 != null) {
                dbHhtUser.setLevelIdAndDescription(iKeyValuePair3.getLevelId() + "-" + iKeyValuePair3.getDescription());
            }
//			}
            BeanUtils.copyProperties(dbHhtUser, dbHhtUserOutput, CommonUtils.getNullPropertyNames(dbHhtUser));
            if (dbHhtUser.getOrderTypeIds() != null) {
                List<String> orderTypeId = new ArrayList<>();
                for (OrderTypeId dbOrderTypeId : dbHhtUser.getOrderTypeIds()) {
                    orderTypeId.add(dbOrderTypeId.getOrderTypeId());
                }
                dbHhtUserOutput.setOrderType(orderTypeId);
            }

            newHhtUser.add(dbHhtUserOutput);
        }
        return newHhtUser;
    }

    /**
     * @param userId
     * @param warehouseId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public HhtUserOutput getHhtUser(String userId, String warehouseId, String companyCodeId, String plantId, String languageId) {
        Optional<HhtUser> hhtUser = hhtUserRepository.findByUserIdAndWarehouseIdAndCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                userId,
                warehouseId,
                companyCodeId,
                plantId,
                languageId,
                0L);
        if (hhtUser.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "companyCodeId - " + companyCodeId +
                    "userId - " + userId +
                    " Plant - " + plantId +
                    " doesn't exist.");
        }

        HhtUserOutput dbHhtUserOutput = new HhtUserOutput();
        BeanUtils.copyProperties(hhtUser.get(), dbHhtUserOutput, CommonUtils.getNullPropertyNames(hhtUser.get()));
        if (hhtUser.get().getOrderTypeIds() != null) {
            List<String> orderTypeId = new ArrayList<>();
            for (OrderTypeId dbOrderTypeId : hhtUser.get().getOrderTypeIds()) {
                orderTypeId.add(dbOrderTypeId.getOrderTypeId());
            }
            dbHhtUserOutput.setOrderType(orderTypeId);
        }
        return dbHhtUserOutput;
    }

//    /**
//     * @param userId
//     * @param warehouseId
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @return
//     */
//    public HhtUserOutput getHhtUser(String userId, String warehouseId, String companyCodeId, Long levelId,
//                                    String plantId, String languageId) {
//        Optional<HhtUser> hhtUser = hhtUserRepository.findByUserIdAndWarehouseIdAndCompanyCodeIdAndPlantIdAndLanguageIdAndLevelIdAndDeletionIndicator(
//                userId,
//                warehouseId,
//                companyCodeId,
//                plantId,
//                languageId,
//                levelId,
//                0L);
//        if (hhtUser.isEmpty()) {
//            throw new BadRequestException("The given values : " +
//                    "warehouseId - " + warehouseId +
//                    "companyCodeId - " + companyCodeId +
//                    "userId - " + userId +
//                    "LevelId - " + levelId +
//                    " Plant - " + plantId +
//                    " doesn't exist.");
//        }
//
//        HhtUserOutput dbHhtUserOutput = new HhtUserOutput();
//        BeanUtils.copyProperties(hhtUser.get(), dbHhtUserOutput, CommonUtils.getNullPropertyNames(hhtUser.get()));
//        if (hhtUser.get().getOrderTypeIds() != null) {
//            List<String> orderTypeId = new ArrayList<>();
//            for (OrderTypeId dbOrderTypeId : hhtUser.get().getOrderTypeIds()) {
//                orderTypeId.add(dbOrderTypeId.getOrderTypeId());
//            }
//            dbHhtUserOutput.setOrderType(orderTypeId);
//        }
//        return dbHhtUserOutput;
//    }

    /**
     * @param warehouseId
     * @return
     */
    public List<HhtUserOutput> getHhtUser(String warehouseId) {
        List<HhtUser> hhtUser = hhtUserRepository.findByWarehouseIdAndDeletionIndicator(warehouseId, 0L);
        if (hhtUser != null) {
            List<HhtUserOutput> hhtUserOutputList = new ArrayList<>();
            for (HhtUser dbHhtUser : hhtUser) {
                HhtUserOutput dbHhtUserOutput = new HhtUserOutput();
                BeanUtils.copyProperties(dbHhtUser, dbHhtUserOutput, CommonUtils.getNullPropertyNames(dbHhtUser));
                if (dbHhtUser.getOrderTypeIds() != null) {
                    List<String> orderTypeId = new ArrayList<>();
                    for (OrderTypeId dbOrderTypeId : dbHhtUser.getOrderTypeIds()) {
                        orderTypeId.add(dbOrderTypeId.getOrderTypeId());
                    }
                    dbHhtUserOutput.setOrderType(orderTypeId);
                }
                hhtUserOutputList.add(dbHhtUserOutput);
            }

            return hhtUserOutputList;

        } else {
            throw new BadRequestException("The given warehouseId ID : " + warehouseId + " doesn't exist.");
        }
    }

    /**
     * @param newHhtUser
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public HhtUser createHhtUser(AddHhtUser newHhtUser, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        Optional<HhtUser> duplicateHhtUser = hhtUserRepository.findByUserIdAndWarehouseIdAndCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                newHhtUser.getUserId(),
                newHhtUser.getWarehouseId(),
                newHhtUser.getCompanyCodeId(),
                newHhtUser.getPlantId(),
                newHhtUser.getLanguageId(),
                0L
        );
        if (!duplicateHhtUser.isEmpty()) {
            throw new IllegalAccessException("User is getting Duplicated");
        }
        IKeyValuePair iKeyValuePair =
                companyIdRepository.getCompanyIdAndDescription(newHhtUser.getCompanyCodeId(), newHhtUser.getLanguageId());

        IKeyValuePair iKeyValuePair1 =
                plantIdRepository.getPlantIdAndDescription(newHhtUser.getPlantId(),
                        newHhtUser.getLanguageId(), newHhtUser.getCompanyCodeId());

        IKeyValuePair iKeyValuePair2 =
                warehouseRepository.getWarehouseIdAndDescription(newHhtUser.getWarehouseId(), newHhtUser.getLanguageId(),
                        newHhtUser.getCompanyCodeId(), newHhtUser.getPlantId());

        HhtUser dbHhtUser = new HhtUser();
        BeanUtils.copyProperties(newHhtUser, dbHhtUser, CommonUtils.getNullPropertyNames(newHhtUser));
        if (iKeyValuePair != null && iKeyValuePair1 != null && iKeyValuePair2 != null) {
            dbHhtUser.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
            dbHhtUser.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
            dbHhtUser.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());

        } else {
            throw new BadRequestException("The given values of Company Id "
                    + newHhtUser.getCompanyCodeId() + " Plant Id "
                    + newHhtUser.getPlantId() + " User Id "
                    + newHhtUser.getUserId() + " Warehouse Id "
                    + newHhtUser.getWarehouseId() +
                    " doesn't exist");
        }
        if(newHhtUser.getLevelId() != null) {
            IKeyValuePair iKeyValuePair3 =
                    levelIdRepository.getLevelIdAndDescription(newHhtUser.getLevelId(), newHhtUser.getLanguageId(),
                            newHhtUser.getCompanyCodeId(), newHhtUser.getPlantId(), newHhtUser.getWarehouseId());
            if (iKeyValuePair3 != null) {
                dbHhtUser.setLevelIdAndDescription(iKeyValuePair3.getLevelId() + "-" + iKeyValuePair3.getDescription());
            }
        }
        dbHhtUser.setDeletionIndicator(0L);
        dbHhtUser.setUserPresent("1");
        dbHhtUser.setUserId(newHhtUser.getUserId().toUpperCase());
        dbHhtUser.setCreatedBy(loginUserID);
        dbHhtUser.setUpdatedBy(loginUserID);
        dbHhtUser.setCreatedOn(new Date());
        dbHhtUser.setUpdatedOn(new Date());
        HhtUser savedHhtUser = hhtUserRepository.save(dbHhtUser);

        savedHhtUser.setOrderTypeIds(new HashSet<>());
        if (newHhtUser.getOrderType() != null) {
            for (String newOrderTypeId : newHhtUser.getOrderType()) {
                OutboundOrderTypeId dbOutboundOrderTypeId =
                        outboundOrderTypeIdService.getOutboundOrderTypeId(dbHhtUser.getWarehouseId(),
                                newOrderTypeId, dbHhtUser.getCompanyCodeId(), dbHhtUser.getLanguageId(), dbHhtUser.getPlantId());

                OrderTypeId dbOrderTypeId = new OrderTypeId();
                dbOrderTypeId.setOrderTypeId(newOrderTypeId);
                dbOrderTypeId.setId(savedHhtUser.getId());
                dbOrderTypeId.setCompanyCodeId(dbHhtUser.getCompanyCodeId());
                dbOrderTypeId.setPlantId(dbHhtUser.getPlantId());
                dbOrderTypeId.setLanguageId(dbHhtUser.getLanguageId());
                dbOrderTypeId.setWarehouseId(dbHhtUser.getWarehouseId());
                dbOrderTypeId.setDeletionIndicator(0L);
                dbOrderTypeId.setCreatedBy(loginUserID);
                dbOrderTypeId.setUpdatedBy(loginUserID);
                dbOrderTypeId.setCreatedOn(new Date());
                dbOrderTypeId.setUpdatedOn(new Date());
                dbOrderTypeId.setUserId(savedHhtUser.getUserId());
                OrderTypeId savedOrderTypeId = orderTypeIdRepository.save(dbOrderTypeId);
                savedHhtUser.getOrderTypeIds().add(savedOrderTypeId);
            }
        }
        return savedHhtUser;
    }


    /**
     * @param warehouseId
     * @param userId
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param updateHhtUser
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public HhtUser updateHhtUser(String warehouseId, String userId, String companyCodeId, String languageId,
                                 String plantId, UpdateHhtUser updateHhtUser, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Optional<HhtUser> dbHhtUser = hhtUserRepository.findByUserIdAndWarehouseIdAndCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                userId, warehouseId, companyCodeId, plantId, languageId, 0L);
        BeanUtils.copyProperties(updateHhtUser, dbHhtUser.get(), CommonUtils.getNullPropertyNames(updateHhtUser));

        if (updateHhtUser.getStartDate() == null) {
            dbHhtUser.get().setStartDate(null);
        }
        if (updateHhtUser.getEndDate() == null) {
            dbHhtUser.get().setEndDate(null);
        }
        if (updateHhtUser.getNoOfDaysLeave() == null) {
            dbHhtUser.get().setNoOfDaysLeave(null);
        }

        dbHhtUser.get().setUserId(updateHhtUser.getUserId().toUpperCase());
        dbHhtUser.get().setUpdatedBy(loginUserID);
        dbHhtUser.get().setUpdatedOn(new Date());
        HhtUser savedHhtUser = hhtUserRepository.save(dbHhtUser.get());

        if (updateHhtUser.getOrderType() != null ) {
            if (orderTypeIdService.getOrderTypeId(companyCodeId, plantId, languageId, warehouseId, userId) != null) {
                orderTypeIdService.deleteOrderTypeIdV2(companyCodeId, plantId, languageId, warehouseId, userId, loginUserID);
            }
            for (String newOrderTypeId : updateHhtUser.getOrderType()) {
                OrderTypeId dbOrderTypeId = new OrderTypeId();

                OutboundOrderTypeId outboundOrderTypeId =
                        outboundOrderTypeIdService.getOutboundOrderTypeId(dbHhtUser.get().getWarehouseId(),
                                newOrderTypeId,
                                dbHhtUser.get().getCompanyCodeId(),
                                dbHhtUser.get().getLanguageId(),
                                dbHhtUser.get().getPlantId());
                if (outboundOrderTypeId != null) {
                    dbOrderTypeId.setOrderTypeId(newOrderTypeId);
                    dbOrderTypeId.setId(dbHhtUser.get().getId());
                    dbOrderTypeId.setCompanyCodeId(companyCodeId);
                    dbOrderTypeId.setPlantId(plantId);
                    dbOrderTypeId.setLanguageId(languageId);
                    dbOrderTypeId.setWarehouseId(warehouseId);
                    dbOrderTypeId.setDeletionIndicator(0L);
                    dbOrderTypeId.setCreatedBy(loginUserID);
                    dbOrderTypeId.setUpdatedBy(loginUserID);
                    dbOrderTypeId.setCreatedOn(new Date());
                    dbOrderTypeId.setUpdatedOn(new Date());
                    dbOrderTypeId.setUserId(savedHhtUser.getUserId());
                    OrderTypeId savedOrderTypeId = orderTypeIdRepository.save(dbOrderTypeId);
                    savedHhtUser.getOrderTypeIds().add(savedOrderTypeId);
                }
            }
        }
        return savedHhtUser;
    }

    /**
     * @param warehouseId
     * @param userId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param loginUserID
     */
    public void deleteHhtUser(String warehouseId, String userId, String companyCodeId,
                              String plantId, String languageId, String loginUserID) throws ParseException {

        Optional<HhtUser> hhtUser = hhtUserRepository.findByUserIdAndWarehouseIdAndCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                userId,
                warehouseId,
                companyCodeId,
                plantId,
                languageId,
                0L);

        if (hhtUser.get() != null) {
            hhtUser.get().setDeletionIndicator(1L);
            hhtUser.get().setUpdatedBy(loginUserID);
            hhtUserRepository.save(hhtUser.get());
            if (orderTypeIdService.getOrderTypeId(userId) != null) {
                orderTypeIdService.deleteOrderTypeId(userId, loginUserID);
            }
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + userId);
        }
    }

    /**
     * @param findHhtUser
     * @return
     * @throws ParseException
     */
    //Find HhtUser
    public List<HhtUserOutput> findHhtUser(FindHhtUser findHhtUser) throws ParseException {

//        HhtUserSpecification spec = new HhtUserSpecification(findHhtUser);
//		List<HhtUser> results = hhtUserRepository.findAll(spec);

        if (findHhtUser.getCompanyCodeId() == null || findHhtUser.getCompanyCodeId().isEmpty()) {
            findHhtUser.setCompanyCodeId(null);
        }
        if (findHhtUser.getPlantId() == null || findHhtUser.getPlantId().isEmpty()) {
            findHhtUser.setPlantId(null);
        }
        if (findHhtUser.getLanguageId() == null || findHhtUser.getLanguageId().isEmpty()) {
            findHhtUser.setLanguageId(null);
        }
        if (findHhtUser.getWarehouseId() == null || findHhtUser.getWarehouseId().isEmpty()) {
            findHhtUser.setWarehouseId(null);
        }
        if (findHhtUser.getUserPresent() == null || findHhtUser.getUserPresent().isEmpty()) {
            findHhtUser.setUserPresent(null);
        }
        if (findHhtUser.getUserId() == null || findHhtUser.getUserId().isEmpty()) {
            findHhtUser.setUserId(null);
        }
        if (findHhtUser.getLevelId() == null || findHhtUser.getLevelId().isEmpty()) {
            findHhtUser.setLevelId(null);
        }
        if (findHhtUser.getNoOfDaysLeave() == null || findHhtUser.getNoOfDaysLeave().isEmpty()) {
            findHhtUser.setNoOfDaysLeave(null);
        }

        List<HhtUser> results = hhtUserRepository.getHhtUser(
                findHhtUser.getCompanyCodeId(),
                findHhtUser.getLanguageId(),
                findHhtUser.getPlantId(),
                findHhtUser.getWarehouseId(),
                findHhtUser.getUserId(),
                findHhtUser.getLevelId(),
                findHhtUser.getUserPresent(),
                findHhtUser.getNoOfDaysLeave());

        List<HhtUserOutput> newHhtUser = new ArrayList<>();

        for (HhtUser dbHhtUser : results) {

            HhtUserOutput dbHhtUserOutput = new HhtUserOutput();

            //V2 Code
            IKeyValuePair description = companyIdRepository.getDescription(dbHhtUser.getCompanyCodeId(),
                    dbHhtUser.getLanguageId(), dbHhtUser.getPlantId(), dbHhtUser.getWarehouseId());

            IKeyValuePair iKeyValuePair3 =
                    levelIdRepository.getLevelIdAndDescription(dbHhtUser.getLevelId(),
                            dbHhtUser.getLanguageId(), dbHhtUser.getCompanyCodeId(), dbHhtUser.getPlantId(),
                            dbHhtUser.getWarehouseId());

            if (iKeyValuePair3 != null) {
                dbHhtUser.setLevelIdAndDescription(iKeyValuePair3.getLevelId() + "-" + iKeyValuePair3.getDescription());
            }

            if (description != null) {
                dbHhtUser.setCompanyIdAndDescription(description.getCompanyDesc());
                dbHhtUser.setPlantIdAndDescription(description.getPlantDesc());
                dbHhtUser.setWarehouseIdAndDescription(description.getWarehouseDesc());
            }

            BeanUtils.copyProperties(dbHhtUser, dbHhtUserOutput, CommonUtils.getNullPropertyNames(dbHhtUser));
            if (dbHhtUser.getOrderTypeIds() != null) {
                List<String> orderTypeId = new ArrayList<>();
                for (OrderTypeId dbOrderTypeId : dbHhtUser.getOrderTypeIds()) {
                    orderTypeId.add(dbOrderTypeId.getOrderTypeId());
                }
                dbHhtUserOutput.setOrderType(orderTypeId);
            }
            if(dbHhtUser != null) {
                if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                    List<HhtUser> userPresent = hhtUserRepository.getHhtUserAttendance(
                            dbHhtUser.getCompanyCodeId(),
                            dbHhtUser.getLanguageId(),
                            dbHhtUser.getPlantId(),
                            dbHhtUser.getWarehouseId(),
                            dbHhtUser.getUserId(),
                            dbHhtUser.getStartDate(),
                            dbHhtUser.getEndDate());
                    log.info("HHt User Absent: " + userPresent);
                    if (userPresent != null && !userPresent.isEmpty()) {
                        dbHhtUserOutput.setUserPresent("0");
                    } else {
                        dbHhtUserOutput.setUserPresent("1");
                    }
                } else {
                    dbHhtUserOutput.setUserPresent("1");
                }
            }

            newHhtUser.add(dbHhtUserOutput);
        }
        return newHhtUser;
    }
}
