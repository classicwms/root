package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.moduleid.FindModuleId;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import com.tekclover.wms.api.idmaster.model.user.Login;
import com.tekclover.wms.api.idmaster.model.user.UserLoginInput;
import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import com.tekclover.wms.api.idmaster.repository.UserManagementRepository;
import com.tekclover.wms.api.idmaster.util.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserLoginService {

	@Autowired
	private UserManagementRepository userManagementRepository;

	@Autowired
	private RoleAccessService roleAccessService;

	@Autowired
	private ModuleIdService moduleIdService;

	private PasswordEncoder passwordEncoder = new PasswordEncoder();

	public Login validateUser (UserLoginInput userLoginInput) throws ParseException {

		String userId = userLoginInput.getUserId();
		String loginPassword = userLoginInput.getLoginPassword();
		String companyCodeId = userLoginInput.getCompanyCodeId();
		String plantId = userLoginInput.getPlantId();
		String languageId = userLoginInput.getLanguageId();
		String warehouseId = userLoginInput.getWarehouseId();

		Login login = new Login();

		if(userId == null && loginPassword == null) {
			throw new BadRequestException("UserId & password is must!");
		}
		if(userId == null) {
			throw new BadRequestException("UserId is must!");
		}
		if(loginPassword == null) {
			throw new BadRequestException("Password is must!");
		}

		List<UserManagement> userManagementList = userManagementRepository.getUserId(userId, companyCodeId, plantId, languageId, warehouseId);
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

				if(roleAccessList != null && !roleAccessList.isEmpty()) {
					login.setUserRole(roleAccessList);
				}
				if(moduleIdList != null && !moduleIdList.isEmpty()) {
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

}