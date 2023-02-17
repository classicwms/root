package com.iweb2b.core.controller;

import com.iweb2b.core.config.PropertiesConfig;
import com.iweb2b.core.exception.BadRequestException;
import com.iweb2b.core.model.auth.AuthToken;
import com.iweb2b.core.model.auth.AuthTokenRequest;
import com.iweb2b.core.model.play.*;
import com.iweb2b.core.service.AuthTokenService;
import com.iweb2b.core.service.PlayService;
import com.iweb2b.core.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Play Service" }, value = "Play Operations related to PlayController")
@SwaggerDefinition(tags = { @Tag(name = "Play", description = "Operations related to Play") })
@RequestMapping("/b2b-play-service")
@RestController
public class PlayController {

	@Autowired
	PlayService playService;

	@Autowired
	AuthTokenService authTokenService;

	@Autowired
	PropertiesConfig propertiesConfig;

	//-----------------------------------auth token------------------------------------------------------------

	@ApiOperation(response = Optional.class, value = "OAuth Token") // label for swagger
	@PostMapping("/auth-token")
	public ResponseEntity<?> authToken(@Valid @RequestBody AuthTokenRequest authTokenRequest) {
		AuthToken authToken = authTokenService.getAuthToken(authTokenRequest);
		return new ResponseEntity<>(authToken, HttpStatus.OK);
	}

	//-----------------------------------User------------------------------------------------------------
	@ApiOperation(response = User.class, value = "Get all User details") // label for swagger
	@GetMapping("/login/users")
	public ResponseEntity<?> getAllUser(@RequestParam String authToken) {
		User[] userList = playService.getAllUser(authToken);
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}

	@ApiOperation(response = User.class, value = "Get a User") // label for swagger
	@GetMapping("/login/user/{id}")
	public ResponseEntity<?> getUser(@PathVariable String id, @RequestParam String authToken) {
		User dbUser = playService.getUser(id, authToken);
		return new ResponseEntity<>(dbUser, HttpStatus.OK);
	}

	@ApiOperation(response = User.class, value = "Create User") // label for swagger
	@PostMapping("/login/user")
	public ResponseEntity<?> postUser(@Valid @RequestBody AddUser newUser,
									  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		User createdUser = playService.createUser(newUser, loginUserID, authToken);
		return new ResponseEntity<>(createdUser, HttpStatus.OK);
	}

	@ApiOperation(response = User.class, value = "Update User") // label for swagger
	@PatchMapping("/login/user/{id}")
	public ResponseEntity<?> patchUser(@PathVariable String id,
									   @RequestParam String loginUserID,
									   @Valid @RequestBody UpdateUser updateUser,
									   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		User updatedUser = playService.updateUser(id, loginUserID,
				updateUser, authToken);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	@ApiOperation(response = User.class, value = "Delete User") // label for swagger
	@DeleteMapping("/login/user/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable String id, @RequestParam String loginUserID,
										@RequestParam String authToken) {
		playService.deleteUser(id, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Validate
	@ApiOperation(response = Optional.class, value = "Validate Login User") // label for swagger
	@RequestMapping(value = "/login/validate", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> loginUser(@RequestParam String userID, @RequestParam String password,
									   @RequestParam String authToken) {
		try {
			User loggedInUser = playService.validateUser(userID, password, authToken);
			log.info("LoginUser::: " + loggedInUser);
			return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
		} catch (BadRequestException e) {
			e.printStackTrace();
			CommonUtils commonUtils = new CommonUtils();
			Map<String, String> mapError = commonUtils.prepareErrorResponse(e.getLocalizedMessage());
			return new ResponseEntity<>(mapError, HttpStatus.UNAUTHORIZED);
		}
	}

	//-----------------------------------ArchiveLogs------------------------------------------------------------
	@ApiOperation(response = ArchiveLogs.class, value = "Get all ArchiveLogs details") // label for swagger
	@GetMapping("/archivelogs")
	public ResponseEntity<?> getAllArchiveLogs(@RequestParam String authToken) {
		ArchiveLogs[] archiveLogsList = playService.getAllArchiveLogs(authToken);
		return new ResponseEntity<>(archiveLogsList, HttpStatus.OK);
	}

	@ApiOperation(response = ArchiveLogs.class, value = "Get a ArchiveLogs") // label for swagger
	@GetMapping("/archivelogs/{archiveLogsId}")
	public ResponseEntity<?> getArchiveLogs(@PathVariable String archiveLogsId, @RequestParam String authToken) {
		ArchiveLogs dbArchiveLogs = playService.getArchiveLogs(archiveLogsId, authToken);
		return new ResponseEntity<>(dbArchiveLogs, HttpStatus.OK);
	}

	@ApiOperation(response = ArchiveLogs.class, value = "Create ArchiveLogs") // label for swagger
	@PostMapping("/archivelogs")
	public ResponseEntity<?> postArchiveLogs(@Valid @RequestBody AddArchiveLogs newArchiveLogs,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		ArchiveLogs createdArchiveLogs = playService.createArchiveLogs(newArchiveLogs, loginUserID, authToken);
		return new ResponseEntity<>(createdArchiveLogs, HttpStatus.OK);
	}

	@ApiOperation(response = ArchiveLogs.class, value = "Update ArchiveLogs") // label for swagger
	@PatchMapping("/archivelogs/{archiveLogsId}")
	public ResponseEntity<?> patchArchiveLogs(@PathVariable String archiveLogsId,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdateArchiveLogs updateArchiveLogs,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ArchiveLogs updatedArchiveLogs = playService.updateArchiveLogs(archiveLogsId, loginUserID,
				updateArchiveLogs, authToken);
		return new ResponseEntity<>(updatedArchiveLogs, HttpStatus.OK);
	}

	@ApiOperation(response = ArchiveLogs.class, value = "Delete ArchiveLogs") // label for swagger
	@DeleteMapping("/archivelogs/{archiveLogsId}")
	public ResponseEntity<?> deleteArchiveLogs(@PathVariable String archiveLogsId, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		playService.deleteArchiveLogs(archiveLogsId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------ClientAssignment------------------------------------------------------------
	@ApiOperation(response = ClientAssignment.class, value = "Get all ClientAssignment details") // label for swagger
	@GetMapping("/clientassignment")
	public ResponseEntity<?> getAllClientAssignment(@RequestParam String authToken) {
		ClientAssignment[] clientAssignmentList = playService.getAllClientAssignment(authToken);
		return new ResponseEntity<>(clientAssignmentList, HttpStatus.OK);
	}

	@ApiOperation(response = ClientAssignment.class, value = "Get a ClientAssignment") // label for swagger
	@GetMapping("/clientassignment/{clientAssignmentId}")
	public ResponseEntity<?> getClientAssignment(@PathVariable String clientAssignmentId, @RequestParam String authToken) {
		ClientAssignment dbClientAssignment = playService.getClientAssignment(clientAssignmentId, authToken);
		return new ResponseEntity<>(dbClientAssignment, HttpStatus.OK);
	}

	@ApiOperation(response = ClientAssignment.class, value = "Create ClientAssignment") // label for swagger
	@PostMapping("/clientassignment")
	public ResponseEntity<?> postClientAssignment(@Valid @RequestBody AddClientAssignment newClientAssignment,
												  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		ClientAssignment createdClientAssignment = playService.createClientAssignment(newClientAssignment, loginUserID, authToken);
		return new ResponseEntity<>(createdClientAssignment, HttpStatus.OK);
	}

	@ApiOperation(response = ClientAssignment.class, value = "Update ClientAssignment") // label for swagger
	@PatchMapping("/clientassignment/{clientAssignmentId}")
	public ResponseEntity<?> patchClientAssignment(@PathVariable String clientAssignmentId,
												   @RequestParam String loginUserID,
												   @Valid @RequestBody UpdateClientAssignment updateClientAssignment,
												   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ClientAssignment updatedClientAssignment = playService.updateClientAssignment(clientAssignmentId, loginUserID,
				updateClientAssignment, authToken);
		return new ResponseEntity<>(updatedClientAssignment, HttpStatus.OK);
	}

	@ApiOperation(response = ClientAssignment.class, value = "Delete ClientAssignment") // label for swagger
	@DeleteMapping("/clientassignment/{clientAssignmentId}")
	public ResponseEntity<?> deleteClientAssignment(@PathVariable String clientAssignmentId, @RequestParam String loginUserID,
													@RequestParam String authToken) {
		playService.deleteClientAssignment(clientAssignmentId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------CreateNewSystem------------------------------------------------------------
	@ApiOperation(response = CreateNewSystem.class, value = "Get all CreateNewSystem details") // label for swagger
	@GetMapping("/createnewsystem")
	public ResponseEntity<?> getAllCreateNewSystem(@RequestParam String authToken) {
		CreateNewSystem[] createNewSystemList = playService.getAllCreateNewSystem(authToken);
		return new ResponseEntity<>(createNewSystemList, HttpStatus.OK);
	}

	@ApiOperation(response = CreateNewSystem.class, value = "Get a CreateNewSystem") // label for swagger
	@GetMapping("/createnewsystem/{createNewSystemId}")
	public ResponseEntity<?> getCreateNewSystem(@PathVariable String createNewSystemId, @RequestParam String authToken) {
		CreateNewSystem dbCreateNewSystem = playService.getCreateNewSystem(createNewSystemId, authToken);
		return new ResponseEntity<>(dbCreateNewSystem, HttpStatus.OK);
	}

	@ApiOperation(response = CreateNewSystem.class, value = "Create CreateNewSystem") // label for swagger
	@PostMapping("/createnewsystem")
	public ResponseEntity<?> postCreateNewSystem(@Valid @RequestBody AddCreateNewSystem newCreateNewSystem,
												 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		CreateNewSystem createdCreateNewSystem = playService.createCreateNewSystem(newCreateNewSystem, loginUserID, authToken);
		return new ResponseEntity<>(createdCreateNewSystem, HttpStatus.OK);
	}

	@ApiOperation(response = CreateNewSystem.class, value = "Update CreateNewSystem") // label for swagger
	@PatchMapping("/createnewsystem/{createNewSystem}")
	public ResponseEntity<?> patchCreateNewSystem(@PathVariable String createNewSystem,
												  @RequestParam String loginUserID,
												  @Valid @RequestBody UpdateCreateNewSystem updateCreateNewSystem,
												  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		CreateNewSystem updatedCreateNewSystem = playService.updateCreateNewSystem(createNewSystem, loginUserID,
				updateCreateNewSystem, authToken);
		return new ResponseEntity<>(updatedCreateNewSystem, HttpStatus.OK);
	}

	@ApiOperation(response = CreateNewSystem.class, value = "Delete CreateNewSystem") // label for swagger
	@DeleteMapping("/createnewsystem/{createNewSystem}")
	public ResponseEntity<?> deleteCreateNewSystem(@PathVariable String createNewSystem, @RequestParam String loginUserID,
												   @RequestParam String authToken) {
		playService.deleteCreateNewSystem(createNewSystem, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------DataTrafficAnalytics------------------------------------------------------------
	@ApiOperation(response = DataTrafficAnalytics.class, value = "Get all DataTrafficAnalytics details") // label for swagger
	@GetMapping("/datatrafficanalytics")
	public ResponseEntity<?> getAllDataTrafficAnalytics(@RequestParam String authToken) {
		DataTrafficAnalytics[] dataTrafficAnalyticsList = playService.getAllDataTrafficAnalytics(authToken);
		return new ResponseEntity<>(dataTrafficAnalyticsList, HttpStatus.OK);
	}

	@ApiOperation(response = DataTrafficAnalytics.class, value = "Get a DataTrafficAnalytics") // label for swagger
	@GetMapping("/datatrafficanalytics/{dataTrafficAnalyticsId}")
	public ResponseEntity<?> getDataTrafficAnalytics(@PathVariable String dataTrafficAnalyticsId, @RequestParam String authToken) {
		DataTrafficAnalytics dbDataTrafficAnalytics = playService.getDataTrafficAnalytics(dataTrafficAnalyticsId, authToken);
		return new ResponseEntity<>(dbDataTrafficAnalytics, HttpStatus.OK);
	}

	@ApiOperation(response = DataTrafficAnalytics.class, value = "Create DataTrafficAnalytics") // label for swagger
	@PostMapping("/datatrafficanalytics")
	public ResponseEntity<?> postDataTrafficAnalytics(@Valid @RequestBody AddDataTrafficAnalytics newDataTrafficAnalytics,
													  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		DataTrafficAnalytics createdDataTrafficAnalytics = playService.createDataTrafficAnalytics(newDataTrafficAnalytics, loginUserID, authToken);
		return new ResponseEntity<>(createdDataTrafficAnalytics, HttpStatus.OK);
	}

	@ApiOperation(response = DataTrafficAnalytics.class, value = "Update DataTrafficAnalytics") // label for swagger
	@PatchMapping("/datatrafficanalytics/{dataTrafficAnalyticsId}")
	public ResponseEntity<?> patchDataTrafficAnalytics(@PathVariable String dataTrafficAnalyticsId,
													   @RequestParam String loginUserID,
													   @Valid @RequestBody UpdateDataTrafficAnalytics updateDataTrafficAnalytics,
													   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		DataTrafficAnalytics updatedDataTrafficAnalytics = playService.updateDataTrafficAnalytics(dataTrafficAnalyticsId, loginUserID,
				updateDataTrafficAnalytics, authToken);
		return new ResponseEntity<>(updatedDataTrafficAnalytics, HttpStatus.OK);
	}

	@ApiOperation(response = DataTrafficAnalytics.class, value = "Delete DataTrafficAnalytics") // label for swagger
	@DeleteMapping("/datatrafficanalytics/{dataTrafficAnalyticsId}")
	public ResponseEntity<?> deleteDataTrafficAnalytics(@PathVariable String dataTrafficAnalyticsId, @RequestParam String loginUserID,
														@RequestParam String authToken) {
		playService.deleteDataTrafficAnalytics(dataTrafficAnalyticsId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------HealthCheck------------------------------------------------------------
	@ApiOperation(response = HealthCheck.class, value = "Get all HealthCheck details") // label for swagger
	@GetMapping("/healthcheck")
	public ResponseEntity<?> getAllHealthCheck(@RequestParam String authToken) {
		HealthCheck[] healthCheckList = playService.getAllHealthCheck(authToken);
		return new ResponseEntity<>(healthCheckList, HttpStatus.OK);
	}

	@ApiOperation(response = HealthCheck.class, value = "Get a HealthCheck") // label for swagger
	@GetMapping("/healthcheck/{healthCheckId}")
	public ResponseEntity<?> getHealthCheck(@PathVariable String healthCheckId, @RequestParam String authToken) {
		HealthCheck dbHealthCheck = playService.getHealthCheck(healthCheckId, authToken);
		return new ResponseEntity<>(dbHealthCheck, HttpStatus.OK);
	}

	@ApiOperation(response = HealthCheck.class, value = "Create HealthCheck") // label for swagger
	@PostMapping("/healthcheck")
	public ResponseEntity<?> postHealthCheck(@Valid @RequestBody AddHealthCheck newHealthCheck,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		HealthCheck createdHealthCheck = playService.createHealthCheck(newHealthCheck, loginUserID, authToken);
		return new ResponseEntity<>(createdHealthCheck, HttpStatus.OK);
	}

	@ApiOperation(response = HealthCheck.class, value = "Update HealthCheck") // label for swagger
	@PatchMapping("/healthcheck/{healthCheckId}")
	public ResponseEntity<?> patchHealthCheck(@PathVariable String healthCheckId,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdateHealthCheck updateHealthCheck,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		HealthCheck updatedHealthCheck = playService.updateHealthCheck(healthCheckId, loginUserID,
				updateHealthCheck, authToken);
		return new ResponseEntity<>(updatedHealthCheck, HttpStatus.OK);
	}

	@ApiOperation(response = HealthCheck.class, value = "Delete HealthCheck") // label for swagger
	@DeleteMapping("/healthcheck/{healthCheckId}")
	public ResponseEntity<?> deleteHealthCheck(@PathVariable String healthCheckId, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		playService.deleteHealthCheck(healthCheckId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------MonitoringControl------------------------------------------------------------
	@ApiOperation(response = MonitoringControl.class, value = "Get all MonitoringControl details") // label for swagger
	@GetMapping("/monitoringcontrol")
	public ResponseEntity<?> getAllMonitoringControl(@RequestParam String authToken) {
		MonitoringControl[] monitoringControlList = playService.getAllMonitoringControl(authToken);
		return new ResponseEntity<>(monitoringControlList, HttpStatus.OK);
	}

	@ApiOperation(response = MonitoringControl.class, value = "Get a MonitoringControl") // label for swagger
	@GetMapping("/monitoringcontrol/{monitoringControlId}")
	public ResponseEntity<?> getMonitoringControl(@PathVariable String monitoringControlId, @RequestParam String authToken) {
		MonitoringControl dbMonitoringControl = playService.getMonitoringControl(monitoringControlId, authToken);
		return new ResponseEntity<>(dbMonitoringControl, HttpStatus.OK);
	}

	@ApiOperation(response = MonitoringControl.class, value = "Create MonitoringControl") // label for swagger
	@PostMapping("/monitoringcontrol")
	public ResponseEntity<?> postMonitoringControl(@Valid @RequestBody AddMonitoringControl newMonitoringControl,
												   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		MonitoringControl createdMonitoringControl = playService.createMonitoringControl(newMonitoringControl, loginUserID, authToken);
		return new ResponseEntity<>(createdMonitoringControl, HttpStatus.OK);
	}

	@ApiOperation(response = MonitoringControl.class, value = "Update MonitoringControl") // label for swagger
	@PatchMapping("/monitoringcontrol/{monitoringControlId}")
	public ResponseEntity<?> patchMonitoringControl(@PathVariable String monitoringControlId,
													@RequestParam String loginUserID,
													@Valid @RequestBody UpdateMonitoringControl updateMonitoringControl,
													@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		MonitoringControl updatedMonitoringControl = playService.updateMonitoringControl(monitoringControlId, loginUserID,
				updateMonitoringControl, authToken);
		return new ResponseEntity<>(updatedMonitoringControl, HttpStatus.OK);
	}

	@ApiOperation(response = MonitoringControl.class, value = "Delete MonitoringControl") // label for swagger
	@DeleteMapping("/monitoringcontrol/{monitoringControlId}")
	public ResponseEntity<?> deleteMonitoringControl(@PathVariable String monitoringControlId, @RequestParam String loginUserID,
													 @RequestParam String authToken) {
		playService.deleteMonitoringControl(monitoringControlId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------Notification------------------------------------------------------------
	@ApiOperation(response = Notification.class, value = "Get all Notification details") // label for swagger
	@GetMapping("/notification")
	public ResponseEntity<?> getAllNotification(@RequestParam String authToken) {
		Notification[] notificationList = playService.getAllNotification(authToken);
		return new ResponseEntity<>(notificationList, HttpStatus.OK);
	}

	@ApiOperation(response = Notification.class, value = "Get a Notification") // label for swagger
	@GetMapping("/notification/{notificationId}")
	public ResponseEntity<?> getNotification(@PathVariable String notificationId,@RequestParam String authToken) {
		Notification dbNotification = playService.getNotification(notificationId, authToken);
		return new ResponseEntity<>(dbNotification, HttpStatus.OK);
	}

	@ApiOperation(response = Notification.class, value = "Create Notification") // label for swagger
	@PostMapping("/notification")
	public ResponseEntity<?> postNotification(@Valid @RequestBody AddNotification newNotification,
											  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Notification createdNotification = playService.createNotification(newNotification, loginUserID, authToken);
		return new ResponseEntity<>(createdNotification, HttpStatus.OK);
	}

	@ApiOperation(response = Notification.class, value = "Update Notification") // label for swagger
	@PatchMapping("/notification/{notificationId}")
	public ResponseEntity<?> patchNotification(@PathVariable String notificationId,
											   @RequestParam String loginUserID,
											   @Valid @RequestBody UpdateNotification updateNotification,
											   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Notification updatedNotification = playService.updateNotification(notificationId, loginUserID,
				updateNotification, authToken);
		return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
	}

	@ApiOperation(response = Notification.class, value = "Delete Notification") // label for swagger
	@DeleteMapping("/notification/{notificationId}")
	public ResponseEntity<?> deleteNotification(@PathVariable String notificationId, @RequestParam String loginUserID,
												@RequestParam String authToken) {
		playService.deleteNotification(notificationId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------PartnerMaster------------------------------------------------------------
	@ApiOperation(response = PartnerMaster.class, value = "Get all PartnerMaster details") // label for swagger
	@GetMapping("/partnermaster")
	public ResponseEntity<?> getAllPartnerMaster(@RequestParam String authToken) {
		PartnerMaster[] partnerMasterList = playService.getAllPartnerMaster(authToken);
		return new ResponseEntity<>(partnerMasterList, HttpStatus.OK);
	}

	@ApiOperation(response = PartnerMaster.class, value = "Get a PartnerMaster") // label for swagger
	@GetMapping("/partnermaster/{partnerMasterId}")
	public ResponseEntity<?> getPartnerMaster(@PathVariable String partnerMasterId, @RequestParam String authToken) {
		PartnerMaster dbPartnerMaster = playService.getPartnerMaster(partnerMasterId, authToken);
		return new ResponseEntity<>(dbPartnerMaster, HttpStatus.OK);
	}

	@ApiOperation(response = PartnerMaster.class, value = "Create PartnerMaster") // label for swagger
	@PostMapping("/partnermaster")
	public ResponseEntity<?> postPartnerMaster(@Valid @RequestBody AddPartnerMaster newPartnerMaster,
											   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		PartnerMaster createdPartnerMaster = playService.createPartnerMaster(newPartnerMaster, loginUserID, authToken);
		return new ResponseEntity<>(createdPartnerMaster, HttpStatus.OK);
	}

	@ApiOperation(response = PartnerMaster.class, value = "Update PartnerMaster") // label for swagger
	@PatchMapping("/partnermaster/{partnerMasterId}")
	public ResponseEntity<?> patchPartnerMaster(@PathVariable String partnerMasterId,
												@RequestParam String loginUserID,
												@Valid @RequestBody UpdatePartnerMaster updatePartnerMaster,
												@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PartnerMaster updatedPartnerMaster = playService.updatePartnerMaster(partnerMasterId, loginUserID,
				updatePartnerMaster, authToken);
		return new ResponseEntity<>(updatedPartnerMaster, HttpStatus.OK);
	}

	@ApiOperation(response = PartnerMaster.class, value = "Delete PartnerMaster") // label for swagger
	@DeleteMapping("/partnermaster/{partnerMasterId}")
	public ResponseEntity<?> deletePartnerMaster(@PathVariable String partnerMasterId, @RequestParam String loginUserID,
												 @RequestParam String authToken) {
		playService.deletePartnerMaster(partnerMasterId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------PartnersActivityDeck------------------------------------------------------------
	@ApiOperation(response = PartnersActivityDeck.class, value = "Get all PartnersActivityDeck details") // label for swagger
	@GetMapping("/partnersactivitydeck")
	public ResponseEntity<?> getAllPartnersActivityDeck(@RequestParam String authToken) {
		PartnersActivityDeck[] partnersActivityDeckList = playService.getAllPartnersActivityDeck(authToken);
		return new ResponseEntity<>(partnersActivityDeckList, HttpStatus.OK);
	}

	@ApiOperation(response = PartnersActivityDeck.class, value = "Get a PartnersActivityDeck") // label for swagger
	@GetMapping("/partnersactivitydeck/{partnersActivityDeckId}")
	public ResponseEntity<?> getPartnersActivityDeck(@PathVariable String partnersActivityDeckId, @RequestParam String authToken) {
		PartnersActivityDeck dbPartnersActivityDeck = playService.getPartnersActivityDeck(partnersActivityDeckId, authToken);
		return new ResponseEntity<>(dbPartnersActivityDeck, HttpStatus.OK);
	}

	@ApiOperation(response = PartnersActivityDeck.class, value = "Create PartnersActivityDeck") // label for swagger
	@PostMapping("/partnersactivitydeck")
	public ResponseEntity<?> postPartnersActivityDeck(@Valid @RequestBody AddPartnersActivityDeck newPartnersActivityDeck,
													  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		PartnersActivityDeck createdPartnersActivityDeck = playService.createPartnersActivityDeck(newPartnersActivityDeck, loginUserID, authToken);
		return new ResponseEntity<>(createdPartnersActivityDeck, HttpStatus.OK);
	}

	@ApiOperation(response = PartnersActivityDeck.class, value = "Update PartnersActivityDeck") // label for swagger
	@PatchMapping("/partnersactivitydeck/{partnersActivityDeckId}")
	public ResponseEntity<?> patchPartnersActivityDeck(@PathVariable String partnersActivityDeckId,
													   @RequestParam String loginUserID,
													   @Valid @RequestBody UpdatePartnersActivityDeck updatePartnersActivityDeck,
													   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PartnersActivityDeck updatedPartnersActivityDeck = playService.updatePartnersActivityDeck(partnersActivityDeckId, loginUserID,
				updatePartnersActivityDeck, authToken);
		return new ResponseEntity<>(updatedPartnersActivityDeck, HttpStatus.OK);
	}

	@ApiOperation(response = PartnersActivityDeck.class, value = "Delete PartnersActivityDeck") // label for swagger
	@DeleteMapping("/partnersactivitydeck/{partnersActivityDeckId}")
	public ResponseEntity<?> deletePartnersActivityDeck(@PathVariable String partnersActivityDeckId, @RequestParam String loginUserID,
														@RequestParam String authToken) {
		playService.deletePartnersActivityDeck(partnersActivityDeckId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------SetupConfiguration------------------------------------------------------------
	@ApiOperation(response = SetupConfiguration.class, value = "Get all SetupConfiguration details") // label for swagger
	@GetMapping("/setupconfiguration")
	public ResponseEntity<?> getAllSetupConfiguration(@RequestParam String authToken) {
		SetupConfiguration[] setupConfigurationList = playService.getAllSetupConfiguration(authToken);
		return new ResponseEntity<>(setupConfigurationList, HttpStatus.OK);
	}

	@ApiOperation(response = SetupConfiguration.class, value = "Get a SetupConfiguration") // label for swagger
	@GetMapping("/setupconfiguration/{setupConfigurationId}")
	public ResponseEntity<?> getSetupConfiguration(@PathVariable String setupConfigurationId, @RequestParam String authToken) {
		SetupConfiguration dbSetupConfiguration = playService.getSetupConfiguration(setupConfigurationId, authToken);
		return new ResponseEntity<>(dbSetupConfiguration, HttpStatus.OK);
	}

	@ApiOperation(response = SetupConfiguration.class, value = "Create SetupConfiguration") // label for swagger
	@PostMapping("/setupconfiguration")
	public ResponseEntity<?> postSetupConfiguration(@Valid @RequestBody AddSetupConfiguration newSetupConfiguration,
													@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		SetupConfiguration createdSetupConfiguration = playService.createSetupConfiguration(newSetupConfiguration, loginUserID, authToken);
		return new ResponseEntity<>(createdSetupConfiguration, HttpStatus.OK);
	}

	@ApiOperation(response = SetupConfiguration.class, value = "Update SetupConfiguration") // label for swagger
	@PatchMapping("/setupconfiguration/{setupConfigurationId}")
	public ResponseEntity<?> patchSetupConfiguration(@PathVariable String setupConfigurationId,
													 @RequestParam String loginUserID,
													 @Valid @RequestBody UpdateSetupConfiguration updateSetupConfiguration,
													 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		SetupConfiguration updatedSetupConfiguration = playService.updateSetupConfiguration(setupConfigurationId, loginUserID,
				updateSetupConfiguration, authToken);
		return new ResponseEntity<>(updatedSetupConfiguration, HttpStatus.OK);
	}

	@ApiOperation(response = SetupConfiguration.class, value = "Delete SetupConfiguration") // label for swagger
	@DeleteMapping("/setupconfiguration/{setupConfigurationId}")
	public ResponseEntity<?> deleteSetupConfiguration(@PathVariable String setupConfigurationId, @RequestParam String loginUserID,
													  @RequestParam String authToken) {
		playService.deleteSetupConfiguration(setupConfigurationId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------SubscriptionManagement------------------------------------------------------------
	@ApiOperation(response = SubscriptionManagement.class, value = "Get all SubscriptionManagement details") // label for swagger
	@GetMapping("/subscriptionmanagement")
	public ResponseEntity<?> getAllSubscriptionManagement(@RequestParam String authToken) {
		SubscriptionManagement[] subscriptionManagementList = playService.getAllSubscriptionManagement(authToken);
		return new ResponseEntity<>(subscriptionManagementList, HttpStatus.OK);
	}

	@ApiOperation(response = SubscriptionManagement.class, value = "Get a SubscriptionManagement") // label for swagger
	@GetMapping("/subscriptionmanagement/{subscriptionManagementId}")
	public ResponseEntity<?> getSubscriptionManagement(@PathVariable String subscriptionManagementId, @RequestParam String authToken) {
		SubscriptionManagement dbSubscriptionManagement = playService.getSubscriptionManagement(subscriptionManagementId, authToken);
		return new ResponseEntity<>(dbSubscriptionManagement, HttpStatus.OK);
	}

	@ApiOperation(response = SubscriptionManagement.class, value = "Create SubscriptionManagement") // label for swagger
	@PostMapping("/subscriptionmanagement")
	public ResponseEntity<?> postSubscriptionManagement(@Valid @RequestBody AddSubscriptionManagement newSubscriptionManagement,
														@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		SubscriptionManagement createdSubscriptionManagement = playService.createSubscriptionManagement(newSubscriptionManagement, loginUserID, authToken);
		return new ResponseEntity<>(createdSubscriptionManagement, HttpStatus.OK);
	}

	@ApiOperation(response = SubscriptionManagement.class, value = "Update SubscriptionManagement") // label for swagger
	@PatchMapping("/subscriptionmanagement/{subscriptionManagementId}")
	public ResponseEntity<?> patchSubscriptionManagement(@PathVariable String subscriptionManagementId,
														 @RequestParam String loginUserID,
														 @Valid @RequestBody UpdateSubscriptionManagement updateSubscriptionManagement,
														 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		SubscriptionManagement updatedSubscriptionManagement = playService.updateSubscriptionManagement(subscriptionManagementId, loginUserID,
				updateSubscriptionManagement, authToken);
		return new ResponseEntity<>(updatedSubscriptionManagement, HttpStatus.OK);
	}

	@ApiOperation(response = SubscriptionManagement.class, value = "Delete SubscriptionManagement") // label for swagger
	@DeleteMapping("/subscriptionmanagement/{subscriptionManagementId}")
	public ResponseEntity<?> deleteSubscriptionManagement(@PathVariable String subscriptionManagementId, @RequestParam String loginUserID,
														  @RequestParam String authToken) {
		playService.deleteSubscriptionManagement(subscriptionManagementId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------SystemMonitoringDeck------------------------------------------------------------
	@ApiOperation(response = SystemMonitoringDeck.class, value = "Get all SystemMonitoringDeck details") // label for swagger
	@GetMapping("/systemmonitoringdeck")
	public ResponseEntity<?> getAllSystemMonitoringDeck(@RequestParam String authToken) {
		SystemMonitoringDeck[] systemMonitoringDeckList = playService.getAllSystemMonitoringDeck(authToken);
		return new ResponseEntity<>(systemMonitoringDeckList, HttpStatus.OK);
	}

	@ApiOperation(response = SystemMonitoringDeck.class, value = "Get a SystemMonitoringDeck") // label for swagger
	@GetMapping("/systemmonitoringdeck/{systemMonitoringDeckId}")
	public ResponseEntity<?> getSystemMonitoringDeck(@PathVariable String systemMonitoringDeckId, @RequestParam String authToken) {
		SystemMonitoringDeck dbSystemMonitoringDeck = playService.getSystemMonitoringDeck(systemMonitoringDeckId, authToken);
		return new ResponseEntity<>(dbSystemMonitoringDeck, HttpStatus.OK);
	}

	@ApiOperation(response = SystemMonitoringDeck.class, value = "Create SystemMonitoringDeck") // label for swagger
	@PostMapping("/systemmonitoringdeck")
	public ResponseEntity<?> postSystemMonitoringDeck(@Valid @RequestBody AddSystemMonitoringDeck newSystemMonitoringDeck,
													  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		SystemMonitoringDeck createdSystemMonitoringDeck = playService.createSystemMonitoringDeck(newSystemMonitoringDeck, loginUserID, authToken);
		return new ResponseEntity<>(createdSystemMonitoringDeck, HttpStatus.OK);
	}

	@ApiOperation(response = SystemMonitoringDeck.class, value = "Update SystemMonitoringDeck") // label for swagger
	@PatchMapping("/systemmonitoringdeck/{systemMonitoringDeckId}")
	public ResponseEntity<?> patchSystemMonitoringDeck(@PathVariable String systemMonitoringDeckId,
													   @RequestParam String loginUserID,
													   @Valid @RequestBody UpdateSystemMonitoringDeck updateSystemMonitoringDeck,
													   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		SystemMonitoringDeck updatedSystemMonitoringDeck = playService.updateSystemMonitoringDeck(systemMonitoringDeckId, loginUserID,
				updateSystemMonitoringDeck, authToken);
		return new ResponseEntity<>(updatedSystemMonitoringDeck, HttpStatus.OK);
	}

	@ApiOperation(response = SystemMonitoringDeck.class, value = "Delete SystemMonitoringDeck") // label for swagger
	@DeleteMapping("/systemmonitoringdeck/{systemMonitoringDeckId}")
	public ResponseEntity<?> deleteSystemMonitoringDeck(@PathVariable String systemMonitoringDeckId, @RequestParam String loginUserID,
														@RequestParam String authToken) {
		playService.deleteSystemMonitoringDeck(systemMonitoringDeckId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------UserCreation------------------------------------------------------------
	@ApiOperation(response = UserCreation.class, value = "Get all UserCreation details") // label for swagger
	@GetMapping("/usercreation")
	public ResponseEntity<?> getAllUserCreation(@RequestParam String authToken) {
		UserCreation[] userCreationList = playService.getAllUserCreation(authToken);
		return new ResponseEntity<>(userCreationList, HttpStatus.OK);
	}

	@ApiOperation(response = UserCreation.class, value = "Get a UserCreation") // label for swagger
	@GetMapping("/usercreation/{userCreationId}")
	public ResponseEntity<?> getUserCreation(@PathVariable String userCreationId, @RequestParam String authToken) {
		UserCreation dbUserCreation = playService.getUserCreation(userCreationId, authToken);
		return new ResponseEntity<>(dbUserCreation, HttpStatus.OK);
	}

	@ApiOperation(response = UserCreation.class, value = "Create UserCreation") // label for swagger
	@PostMapping("/usercreation")
	public ResponseEntity<?> postUserCreation(@Valid @RequestBody AddUserCreation newUserCreation,
											  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		UserCreation createdUserCreation = playService.createUserCreation(newUserCreation, loginUserID, authToken);
		return new ResponseEntity<>(createdUserCreation, HttpStatus.OK);
	}

	@ApiOperation(response = UserCreation.class, value = "Update UserCreation") // label for swagger
	@PatchMapping("/usercreation/{userCreationId}")
	public ResponseEntity<?> patchUserCreation(@PathVariable String userCreationId,
											   @RequestParam String loginUserID,
											   @Valid @RequestBody UpdateUserCreation updateUserCreation,
											   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		UserCreation updatedUserCreation = playService.updateUserCreation(userCreationId, loginUserID,
				updateUserCreation, authToken);
		return new ResponseEntity<>(updatedUserCreation, HttpStatus.OK);
	}

	@ApiOperation(response = UserCreation.class, value = "Delete UserCreation") // label for swagger
	@DeleteMapping("/usercreation/{userCreationId}")
	public ResponseEntity<?> deleteUserCreation(@PathVariable String userCreationId, @RequestParam String loginUserID,
												@RequestParam String authToken) {
		playService.deleteUserCreation(userCreationId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------UserManagement------------------------------------------------------------
	@ApiOperation(response = UserManagement.class, value = "Get all UserManagement details") // label for swagger
	@GetMapping("/usermanagement")
	public ResponseEntity<?> getAllUserManagement(@RequestParam String authToken) {
		UserManagement[] userManagementList = playService.getAllUserManagement(authToken);
		return new ResponseEntity<>(userManagementList, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Get a UserManagement") // label for swagger
	@GetMapping("/usermanagement/{userManagementId}")
	public ResponseEntity<?> getUserManagement(@PathVariable String userManagementId, @RequestParam String authToken) {
		UserManagement dbUserManagement = playService.getUserManagement(userManagementId, authToken);
		return new ResponseEntity<>(dbUserManagement, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Create UserManagement") // label for swagger
	@PostMapping("/usermanagement")
	public ResponseEntity<?> postUserManagement(@Valid @RequestBody AddUserManagement newUserManagement,
												@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		UserManagement createdUserManagement = playService.createUserManagement(newUserManagement, loginUserID, authToken);
		return new ResponseEntity<>(createdUserManagement, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Update UserManagement") // label for swagger
	@PatchMapping("/usermanagement/{userManagementId}")
	public ResponseEntity<?> patchUserManagement(@PathVariable String userManagementId,
												 @RequestParam String loginUserID,
												 @Valid @RequestBody UpdateUserManagement updateUserManagement,
												 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		UserManagement updatedUserManagement = playService.updateUserManagement(userManagementId, loginUserID,
				updateUserManagement, authToken);
		return new ResponseEntity<>(updatedUserManagement, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Delete UserManagement") // label for swagger
	@DeleteMapping("/usermanagement/{userManagementId}")
	public ResponseEntity<?> deleteUserManagement(@PathVariable String userManagementId, @RequestParam String loginUserID,
												  @RequestParam String authToken) {
		playService.deleteUserManagement(userManagementId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------UserProfiles------------------------------------------------------------
	@ApiOperation(response = UserProfiles.class, value = "Get all UserProfiles details") // label for swagger
	@GetMapping("/userprofiles")
	public ResponseEntity<?> getAllUserProfiles(@RequestParam String authToken) {
		UserProfiles[] userProfilesList = playService.getAllUserProfiles(authToken);
		return new ResponseEntity<>(userProfilesList, HttpStatus.OK);
	}

	@ApiOperation(response = UserProfiles.class, value = "Get a UserProfiles") // label for swagger
	@GetMapping("/userprofiles/{userProfilesId}")
	public ResponseEntity<?> getUserProfiles(@PathVariable String userProfilesId, @RequestParam String authToken) {
		UserProfiles dbUserProfiles = playService.getUserProfiles(userProfilesId, authToken);
		return new ResponseEntity<>(dbUserProfiles, HttpStatus.OK);
	}

	@ApiOperation(response = UserProfiles.class, value = "Create UserProfiles") // label for swagger
	@PostMapping("/userprofiles")
	public ResponseEntity<?> postUserProfiles(@Valid @RequestBody AddUserProfiles newUserProfiles,
											  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		UserProfiles createdUserProfiles = playService.createUserProfiles(newUserProfiles, loginUserID, authToken);
		return new ResponseEntity<>(createdUserProfiles, HttpStatus.OK);
	}

	@ApiOperation(response = UserProfiles.class, value = "Update UserProfiles") // label for swagger
	@PatchMapping("/userprofiles/{userProfilesId}")
	public ResponseEntity<?> patchUserProfiles(@PathVariable String userProfilesId,
											   @RequestParam String loginUserID,
											   @Valid @RequestBody UpdateUserProfiles updateUserProfiles,
											   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		UserProfiles updatedUserProfiles = playService.updateUserProfiles(userProfilesId, loginUserID,
				updateUserProfiles, authToken);
		return new ResponseEntity<>(updatedUserProfiles, HttpStatus.OK);
	}

	@ApiOperation(response = UserProfiles.class, value = "Delete UserProfiles") // label for swagger
	@DeleteMapping("/userprofiles/{userProfilesId}")
	public ResponseEntity<?> deleteUserProfiles(@PathVariable String userProfilesId, @RequestParam String loginUserID,
												@RequestParam String authToken) {
		playService.deleteUserProfiles(userProfilesId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
