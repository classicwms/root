package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.enterprise.WarehouseEnterprise;
import com.tekclover.wms.api.idmaster.model.moduleid.FindModuleId;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import com.tekclover.wms.api.idmaster.model.user.*;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.UserManagementSpecification;
import com.tekclover.wms.api.idmaster.repository.enterprise.WarehouseEnterpriseRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserManagementService {
    @Autowired
    private PlantIdRepository plantIdRepository;
    @Autowired
    private CompanyIdRepository companyIdRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    private UserManagementRepository userManagementRepository;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private RoleAccessService roleAccessService;

    @Autowired
    private ModuleIdService moduleIdService;

    @Autowired
    private WarehouseRepository warehouseRepository1;
    @Autowired
    private WarehouseEnterpriseRepository warehouseRepository;

    @Autowired
    private UserTypeIdRepository userTypeIdRepository;

    @Autowired
    private RoleAccessRepository roleAccessRepository;

    private PasswordEncoder passwordEncoder = new PasswordEncoder();

    /**
     * @return
     */
    public List<UserManagement> getUserManagements() {
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        userManagementList = userManagementList.stream().filter(a -> a.getDeletionIndicator() != null &&
                a.getDeletionIndicator().longValue() == 0).collect(Collectors.toList());

        List<UserManagement> newUserManagement = new ArrayList<>();
        for (UserManagement dbUserManagement : userManagementList) {
            if (dbUserManagement.getCompanyIdAndDescription() != null && dbUserManagement.getPlantIdAndDescription() != null && dbUserManagement.getWarehouseIdAndDescription() != null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbUserManagement.getCompanyCode(), dbUserManagement.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbUserManagement.getPlantId(), dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode());
                IKeyValuePair iKeyValuePair2 = warehouseRepository1.getWarehouseIdAndDescription(dbUserManagement.getWarehouseId(), dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId());
                IKeyValuePair iKeyValuePair3 = userTypeIdRepository.getUserTypeIdandDescription(dbUserManagement.getWarehouseId(), dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId(), dbUserManagement.getUserTypeId());
                IKeyValuePair iKeyValuePair4 = roleAccessRepository.getRoleIdIdandDescription(dbUserManagement.getWarehouseId(), dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId(), dbUserManagement.getUserRoleId());

                if (iKeyValuePair != null) {
                    dbUserManagement.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbUserManagement.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbUserManagement.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                if (iKeyValuePair3 != null) {
                    dbUserManagement.setUserTypeIdAndDescription(iKeyValuePair3.getUserTypeId() + "-" + iKeyValuePair3.getUserTypeDescription());
                }
                if (iKeyValuePair4 != null) {
                    dbUserManagement.setUserRoleIdAndDescription(iKeyValuePair4.getRoleId() + "-" + iKeyValuePair4.getRoleDescription());
                }
            }
            newUserManagement.add(dbUserManagement);
        }
        return newUserManagement;

    }

    /**
     * @param userId
     * @return
     */
    public UserManagement getUserManagement(String warehouseId, String userId) {
        UserManagement userManagement = userManagementRepository.findByWarehouseIdAndUserIdAndDeletionIndicator(warehouseId, userId, 0L);
        if (userManagement == null) {
            throw new BadRequestException("Invalid Username : " + userId);
        }
        return userManagement;
    }

    /**
     * @param userId
     * @return
     */
    public UserManagement getUserManagement(String languageId, String companyCode, String plantId, String warehouseId,
                                            String userId, Long userRoleId) {
        UserManagement userManagement = userManagementRepository.
                findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndUserIdAndUserRoleIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        userId,
                        userRoleId,
                        0L);
        if (userManagement == null) {
            throw new BadRequestException("Invalid Username : " + userId);
        }
        UserManagement dbUserManagement = new UserManagement();
        BeanUtils.copyProperties(userManagement, dbUserManagement, CommonUtils.getNullPropertyNames(userManagement));
        IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(companyCode, languageId);
        IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(plantId, languageId, companyCode);
        IKeyValuePair iKeyValuePair2 = warehouseRepository1.getWarehouseIdAndDescription(warehouseId, languageId, companyCode, plantId);
        IKeyValuePair iKeyValuePair3 = userTypeIdRepository.getUserTypeIdandDescription(dbUserManagement.getWarehouseId(),
                dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId(), dbUserManagement.getUserTypeId());

        IKeyValuePair iKeyValuePair4 = roleAccessRepository.getRoleIdIdandDescription(dbUserManagement.getWarehouseId(), dbUserManagement.getLanguageId(),
                dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId(), dbUserManagement.getUserRoleId());
        if (iKeyValuePair != null) {
            dbUserManagement.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if (iKeyValuePair1 != null) {
            dbUserManagement.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if (iKeyValuePair2 != null) {
            dbUserManagement.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
        }
        if (iKeyValuePair3 != null) {
            dbUserManagement.setUserTypeIdAndDescription(iKeyValuePair3.getUserTypeId() + "-" + iKeyValuePair3.getUserTypeDescription());
        }
        if (iKeyValuePair4 != null) {
            dbUserManagement.setUserRoleIdAndDescription(iKeyValuePair4.getRoleId() + "-" + iKeyValuePair4.getRoleDescription());
        }
        return dbUserManagement;
    }

    /**
     * @param userId
     * @param
     * @return
     */
    public List<UserManagement> getUserManagement(String userId) {
        List<UserManagement> userManagement =
                userManagementRepository.findByUserIdAndDeletionIndicator(userId, 0L);
        if (userManagement == null) {
            throw new BadRequestException("Invalid Username : " + userId);
        }
        return userManagement;
    }

    /**
     * @param userLoginInput
     * @return
     * @throws ParseException
     */
    public Login validateUser(UserLoginInput userLoginInput) throws ParseException {
        String userId = userLoginInput.getUserId();
        String loginPassword = userLoginInput.getLoginPassword();
        String companyCodeId = userLoginInput.getCompanyCodeId();
        String plantId = userLoginInput.getPlantId();
        String languageId = userLoginInput.getLanguageId();
        String warehouseId = userLoginInput.getWarehouseId();

        DataBaseContextHolder.setCurrentDb("MT");
        UserManagement user = userManagementRepository.getUserDetails(userLoginInput.getUserId());
        log.info("user -----> {}", user);

        String routingDb = dbConfigRepository.getDbName(user.getCompanyCode(), user.getPlantId(), user.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);

        Login login = new Login();

        if (userId == null && loginPassword == null) {
            throw new BadRequestException("UserId & password is must!");
        }
        if (userId == null) {
            throw new BadRequestException("UserId is must!");
        }
        if (loginPassword == null) {
            throw new BadRequestException("Password is must!");
        }

//		DataBaseContextHolder.setCurrentDb("MT");
//		String routingDb = dbConfigRepository.getDbName(companyCodeId,plantId,warehouseId);
//		log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}",routingDb);
////
////		log.info("datasource 1 --> {}",DataBaseContextHolder.getCurrentDb());
//		DataBaseContextHolder.clear();
//		DataBaseContextHolder.setCurrentDb(routingDb);

        List<UserManagement> userManagementList = userManagementRepository.getUserId(userId, user.getCompanyCode(), user.getPlantId(), user.getLanguageId(), user.getWarehouseId());
        log.info("userManagementList -----> {}", userManagementList);
        if (userManagementList == null || userManagementList.isEmpty()) {
            throw new BadRequestException("Invalid Username : " + userId);
        }

        if (userManagementList.size() > 1) {
            log.info("UserManagement List Size : " + userManagementList.size());
            login.setUsers(userManagementList);
            return login;
        }

        boolean isSuccess = false;
        for (UserManagement userManagement : userManagementList) {
            isSuccess = passwordEncoder.matches(loginPassword, userManagement.getPassword());
            if (isSuccess) {
                DataBaseContextHolder.setCurrentDb("MT");
                routingDb = dbConfigRepository.getDbName(userManagement.getCompanyCode(), userManagement.getPlantId(), userManagement.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//
//		log.info("datasource 1 --> {}",DataBaseContextHolder.getCurrentDb());
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
                //get respective role access
                List<RoleAccess> roleAccessList = roleAccessService.getRoleAccess(
                        userManagement.getWarehouseId(), userManagement.getUserRoleId(),
                        userManagement.getCompanyCode(), userManagement.getLanguageId(), userManagement.getPlantId());

                //get respective module
                FindModuleId findModuleId = new FindModuleId();
                findModuleId.setCompanyCodeId(userManagement.getCompanyCode());
                findModuleId.setPlantId(userManagement.getPlantId());
                findModuleId.setLanguageId(Collections.singletonList(userManagement.getLanguageId()));
                findModuleId.setWarehouseId(userManagement.getWarehouseId());
                List<ModuleId> moduleIdList = moduleIdService.findModuleId(findModuleId);

                login.setUsers(userManagementList);

                if (roleAccessList != null && !roleAccessList.isEmpty()) {
                    login.setUserRole(roleAccessList);
                }
                if (moduleIdList != null && !moduleIdList.isEmpty()) {
                    login.setUserModule(moduleIdList);
                }
                log.info("UserId, RoleAccess Size, User Module Size: " + userManagement + "; " + roleAccessList.size() + "; " + moduleIdList.size());
                return login;
            }
        }

        if (!isSuccess) {
            throw new BadRequestException("Password is wrong. Please enter correct password.");
        }
        return null;
    }

    /**
     * For Fahaheel
     *
     * @param userLoginInput
     * @return
     * @throws ParseException
     */
    public Login validateUserV8(UserLoginInput userLoginInput) throws ParseException {

        String userId = userLoginInput.getUserId();
        String loginPassword = userLoginInput.getLoginPassword();

        DataBaseContextHolder.setCurrentDb("MT");
        UserManagement user = userManagementRepository.getUserDetails(userLoginInput.getUserId());

        log.info("user -----> {}", user);

        String routingDb = dbConfigRepository.getDbName(user.getCompanyCode(), user.getPlantId(), user.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);

        Login login = new Login();

        if (userId == null && loginPassword == null) {
            throw new BadRequestException("UserId & password is must!");
        }
        if (userId == null) {
            throw new BadRequestException("UserId is must!");
        }
        if (loginPassword == null) {
            throw new BadRequestException("Password is must!");
        }

        List<UserManagement> userManagementList = null;

        if (userLoginInput.getPlantId() != null) {
            userManagementList = userManagementRepository.getUserIdWithPlant(userId, userLoginInput.getPlantId(), user.getWarehouseId());
        } else if (userLoginInput.getWarehouseId() != null) {
            userManagementList = userManagementRepository.getUserIdV8(userId, userLoginInput.getWarehouseId());
        } else {
            userManagementList = userManagementRepository.getUserIdV6(userLoginInput.getUserId());
        }

        log.info("userManagementList -----> {}", userManagementList);
        if (userManagementList == null || userManagementList.isEmpty()) {
            throw new BadRequestException("Invalid Username : " + userId);
        }

        if (userManagementList.size() > 1) {
            log.info("UserManagement List Size : " + userManagementList.size());
            login.setUsers(userManagementList);
            return login;
        }

        boolean isSuccess = false;
        for (UserManagement userManagement : userManagementList) {
            isSuccess = passwordEncoder.matches(loginPassword, userManagement.getPassword());
            if (isSuccess) {
                DataBaseContextHolder.setCurrentDb("MT");
                routingDb = dbConfigRepository.getDbName(userManagement.getCompanyCode(), userManagement.getPlantId(), userManagement.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//
//		log.info("datasource 1 --> {}",DataBaseContextHolder.getCurrentDb());
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
                //get respective role access
                List<RoleAccess> roleAccessList = roleAccessService.getRoleAccess(
                        userManagement.getWarehouseId(), userManagement.getUserRoleId(),
                        userManagement.getCompanyCode(), userManagement.getLanguageId(), userManagement.getPlantId());

                //get respective module
                FindModuleId findModuleId = new FindModuleId();
                findModuleId.setCompanyCodeId(userManagement.getCompanyCode());
                findModuleId.setPlantId(userManagement.getPlantId());
                findModuleId.setLanguageId(Collections.singletonList(userManagement.getLanguageId()));
                findModuleId.setWarehouseId(userManagement.getWarehouseId());
                List<ModuleId> moduleIdList = moduleIdService.findModuleId(findModuleId);

                login.setUsers(userManagementList);

                if (roleAccessList != null && !roleAccessList.isEmpty()) {
                    login.setUserRole(roleAccessList);
                }
                if (moduleIdList != null && !moduleIdList.isEmpty()) {
                    login.setUserModule(moduleIdList);
                }
                log.info("UserId, RoleAccess Size, User Module Size: " + userManagement + "; " + roleAccessList.size() + "; " + moduleIdList.size());
                return login;
            }
        }

        if (!isSuccess) {
            throw new BadRequestException("Password is wrong. Please enter correct password.");
        }
        return null;
    }

    /**
     * @param userId
     * @param loginPassword
     * @param version
     * @return
     */
    public UserManagement validateUser(String userId, String loginPassword, String version) {

        DataBaseContextHolder.setCurrentDb("MT");
        UserManagement user = userManagementRepository.getUserDetails(userId);

        log.info("user ----> {}", user);

        String routingDb = dbConfigRepository.getDbName(user.getCompanyCode(), user.getPlantId(), user.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);

        List<UserManagement> userManagementList =
                userManagementRepository.findByUserIdAndDeletionIndicator(userId, 0L);

        log.info("userManagementList -----> {}", userManagementList);
        if (userManagementList.isEmpty()) {
            throw new BadRequestException("Invalid Username : " + userId);
        }

        boolean isSuccess = false;
        for (UserManagement userManagement : userManagementList) {
            isSuccess = passwordEncoder.matches(loginPassword, userManagement.getPassword());
            log.info("version :" + version);
            if (isSuccess) {
                if (version != null && version.length() > 0) { // If the value is passed from Mobile
//					List<WarehouseEnterprise> optWarehouse = warehouseRepository.findByZone(version);
                    List<WarehouseEnterprise> optWarehouse = warehouseRepository.findByCompanyIdAndPlantIdAndLanguageIdAndWarehouseIdAndZoneAndDeletionIndicator(
                            userManagement.getCompanyCode(), userManagement.getPlantId(), userManagement.getLanguageId(), userManagement.getWarehouseId(), version, 0L);
                    log.info("optWarehouse :" + optWarehouse);
                    if (optWarehouse.isEmpty()) {
                        throw new BadRequestException("You are not using the current version. Please install updated latest version.");
                    }
                }
                return userManagement;
            }
        }

        if (!isSuccess) {
            throw new BadRequestException("Password is wrong. Please enter correct password.");
        }
        return null;
    }

    /**
     * createUserManagement
     *
     * @param newUserManagement
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public UserManagement createUserManagement(AddUserManagement newUserManagement, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        UserManagement dbUserManagement = new UserManagement();
        UserManagement duplicateCheck = userManagementRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndUserIdAndUserRoleIdAndDeletionIndicator(newUserManagement.getLanguageId(),
                newUserManagement.getCompanyCode(),
                newUserManagement.getPlantId(),
                newUserManagement.getWarehouseId(),
                newUserManagement.getUserId(),
                newUserManagement.getUserRoleId(),
                0L);

        if (duplicateCheck != null) {
            throw new IllegalAccessException("User is getting Duplicated");
        } else {
            Warehouse dbWarehouse = warehouseService.getWarehouse(newUserManagement.getWarehouseId(), newUserManagement.getCompanyCode(), newUserManagement.getPlantId(), newUserManagement.getLanguageId());

            Long currentUserCount = userManagementRepository.getUserCountForWarehouse(newUserManagement.getCompanyCode(), newUserManagement.getPlantId(),
                    newUserManagement.getWarehouseId(), newUserManagement.getLanguageId());

            Long warehouseUserLimit = dbWarehouse.getNoOfUsers() != null ? dbWarehouse.getNoOfUsers() : 0L;

            if (currentUserCount >= warehouseUserLimit) {
                throw new IllegalAccessException(String.format(
                        "Cannot create user warehouse has reached its maximum allowed users.",
                        newUserManagement.getWarehouseId(), warehouseUserLimit, currentUserCount));
            }

            BeanUtils.copyProperties(newUserManagement, dbUserManagement, CommonUtils.getNullPropertyNames(newUserManagement));
            dbUserManagement.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
            dbUserManagement.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
            dbUserManagement.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId() + "-" + dbWarehouse.getWarehouseDesc());


            IKeyValuePair iKeyValuePair3 = userTypeIdRepository.getUserTypeIdandDescription(dbUserManagement.getWarehouseId(), dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId(), dbUserManagement.getUserTypeId());

            if (iKeyValuePair3 != null) {
                dbUserManagement.setUserTypeIdAndDescription(iKeyValuePair3.getUserTypeId() + "-" + iKeyValuePair3.getUserTypeDescription());
            }

            IKeyValuePair iKeyValuePair4 = roleAccessRepository.getRoleIdIdandDescription(dbUserManagement.getWarehouseId(), dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId(), dbUserManagement.getUserRoleId());
            if (iKeyValuePair4 != null) {
                dbUserManagement.setUserRoleIdAndDescription(iKeyValuePair4.getRoleId() + "-" + iKeyValuePair4.getRoleDescription());
            }

            // Password encryption
            try {
                String encodedPwd = passwordEncoder.encodePassword(newUserManagement.getPassword());
                dbUserManagement.setPassword(encodedPwd);
                dbUserManagement.setCreatedBy(loginUserID);
                dbUserManagement.setCreatedOn(new Date());
                dbUserManagement.setUpdatedBy(loginUserID);
                dbUserManagement.setUpdatedOn(new Date());
                dbUserManagement.setDeletionIndicator(0L);
                UserManagement response = userManagementRepository.save(dbUserManagement);
                // Saving to Common DB
                try {
                    DataBaseContextHolder.clear();
                    DataBaseContextHolder.setCurrentDb("MT");
                    log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
                    UserManagement saveImf = userManagementRepository.save(dbUserManagement);
                    log.info("Hhtuser saved in common DB : savedHhtUserImf ");
                } catch (Exception e) {
                    log.info("HhtUser save in common DB failed....");
                }

                return response;
            } catch (Exception e) {
                log.error("Error : " + e);
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * @param userId
     * @param updateUserManagement
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public UserManagement updateUserManagement(String userId, String warehouseId, String companyCode, String languageId, String plantId, Long userRoleId, UpdateUserManagement updateUserManagement, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        UserManagement dbUserManagement = userManagementRepository.
                findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndUserIdAndUserRoleIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        userId,
                        userRoleId,
                        0L);
        BeanUtils.copyProperties(updateUserManagement, dbUserManagement, CommonUtils.getNullPropertyNames(updateUserManagement));

        if (updateUserManagement.getPassword() != null) {
            // Password encryption
            String encodedPwd = passwordEncoder.encodePassword(updateUserManagement.getPassword());
            dbUserManagement.setPassword(encodedPwd);
        }
        dbUserManagement.setUpdatedBy(loginUserID);
        dbUserManagement.setUpdatedOn(new Date());
        return userManagementRepository.save(dbUserManagement);
    }

    /**
     * deleteUserManagement
     *
     * @param warehouseId
     */
    public void deleteUserManagement(String userId, String warehouseId,
                                     String languageId,
                                     String companyCode,
                                     String plantId,
                                     Long userRoleId,
                                     String loginUserID) throws ParseException {
        UserManagement dbUserManagement = getUserManagement(languageId, companyCode, plantId, warehouseId, userId, userRoleId);
        if (dbUserManagement != null) {
            dbUserManagement.setUpdatedBy(loginUserID);
            dbUserManagement.setUpdatedOn(new Date());
            dbUserManagement.setDeletionIndicator(1L);
            userManagementRepository.save(dbUserManagement);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + userId);
        }
    }

    //Find UserManagement
    public List<UserManagement> findUserManagement(FindUserManagement findUserManagement) throws ParseException {

        UserManagementSpecification spec = new UserManagementSpecification(findUserManagement);
        List<UserManagement> results = userManagementRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());

        List<UserManagement> newUserManagement = new ArrayList<>();
        for (UserManagement dbUserManagement : results) {

            IKeyValuePair iKeyValuePair =
                    companyIdRepository.getCompanyIdAndDescription(dbUserManagement.getCompanyCode(), dbUserManagement.getLanguageId());

            IKeyValuePair iKeyValuePair1 =
                    plantIdRepository.getPlantIdAndDescription(dbUserManagement.getPlantId(),
                            dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode());

            IKeyValuePair iKeyValuePair2 =
                    warehouseRepository1.getWarehouseIdAndDescription(dbUserManagement.getWarehouseId(),
                            dbUserManagement.getLanguageId(), dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId());

            IKeyValuePair iKeyValuePair3 = userTypeIdRepository.getUserTypeIdandDescription(dbUserManagement.getWarehouseId(), dbUserManagement.getLanguageId(),
                    dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId(), dbUserManagement.getUserTypeId());

            IKeyValuePair iKeyValuePair4 = roleAccessRepository.getRoleIdIdandDescription(dbUserManagement.getWarehouseId(), dbUserManagement.getLanguageId(),
                    dbUserManagement.getCompanyCode(), dbUserManagement.getPlantId(), dbUserManagement.getUserRoleId());

            if (iKeyValuePair3 != null) {
                dbUserManagement.setUserTypeIdAndDescription(iKeyValuePair3.getUserTypeId() + "-" + iKeyValuePair3.getUserTypeDescription());
            }

            if (iKeyValuePair4 != null) {
                dbUserManagement.setUserRoleIdAndDescription(iKeyValuePair4.getRoleId() + "-" + iKeyValuePair4.getRoleDescription());
            }
            if (iKeyValuePair != null) {
                dbUserManagement.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
            }
            if (iKeyValuePair1 != null) {
                dbUserManagement.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
            }
            if (iKeyValuePair2 != null) {
                dbUserManagement.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
            }
            newUserManagement.add(dbUserManagement);
        }

        log.info("results: " + newUserManagement);
        return newUserManagement;



    }

}
