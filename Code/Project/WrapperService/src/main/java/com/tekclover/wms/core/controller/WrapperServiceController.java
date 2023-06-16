package com.tekclover.wms.core.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVWriter;
import com.tekclover.wms.core.batch.config.singleton.AccountService;
import com.tekclover.wms.core.batch.config.singleton.AppConfig;
import com.tekclover.wms.core.batch.scheduler.BatchJobScheduler;
import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.auth.AuthTokenRequest;
import com.tekclover.wms.core.model.user.NewUser;
import com.tekclover.wms.core.model.user.RegisteredUser;
import com.tekclover.wms.core.service.CommonService;
import com.tekclover.wms.core.service.FileStorageService;
import com.tekclover.wms.core.service.RegisterService;
import com.tekclover.wms.core.service.ReportService;
import com.tekclover.wms.core.util.DateUtils;
import com.tekclover.wms.core.util.User1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@Api(tags = {"Wrapper Service"}, value = "Wrapper Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User",description = "Operations related to User")})
public class WrapperServiceController { 
	
	@Autowired
	RegisterService registerService;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
    FileStorageService fileStorageService;
	
	@Autowired
	BatchJobScheduler batchJobScheduler;
	
	@Autowired
	ReportService reportService;
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	
	@ApiOperation(response = Optional.class, value = "TestAXAPI") // label for swagger
	@PostMapping("/testAXAPI")
	public ResponseEntity<?> testAXAPI () {
		commonService.generateAXOAuthToken();
    	return new ResponseEntity<>(HttpStatus.OK);
	}
	 
	 
	/**
	 * This endpoint is for registering thrd party clients to get the Client ID and Secret Key
	 * @param clientName
	 * @return
	 */
    @ApiOperation(response = Optional.class, value = "Register Client") // label for swagger
	@PostMapping("/register-app-client")
	public ResponseEntity<?> registerClient (String clientName) {
		// Generate Unique ID for each client
    	// Store Client Unique ID and send Client Secret ID along with RegistrationID
    	NewUser registeredUser = registerService.registerNewUser(clientName);
    	return new ResponseEntity<>(registeredUser, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get Client Secret Key") // label for swagger
    @RequestMapping(value = "/client-secret-key", method = RequestMethod.POST, produces = "application/json")
 	public ResponseEntity<?> getClientSecretKey (@Valid @RequestBody RegisteredUser registeredUser) {
    	try {
			String secretKey = registerService.validateRegisteredUser(registeredUser);
			Map<String, String> responseMap = Collections.singletonMap("client-secret-key", secretKey);
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error on getting Client Secret key: " + e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}
 	}
    
    @ApiOperation(response = Optional.class, value = "OAuth Token") // label for swagger
	@PostMapping("/auth-token")
	public ResponseEntity<?> authToken (@Valid @RequestBody AuthTokenRequest authTokenRequest) {
		AuthToken authToken = registerService.getAuthToken(authTokenRequest);
    	return new ResponseEntity<>(authToken, HttpStatus.OK);
	}
    
    
    //----------------------------------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Batch Fetch") // label for swagger
    @GetMapping("/batch-fetch/jobInventoryMovementQuery")
    public ResponseEntity<?> jobInventoryQuery2() throws Exception {
//    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
//    	 
//        batchJobScheduler.runJobInventoryMovement();
//       
//		AccountService service2 = context.getBean("accountService", AccountService.class);
//		log.info("inventoryMovement size: " + service2.getInventoryHolder().size());
//		context.close();
    	
    	List<User1> list = new ArrayList<User1>();
    	String strDate = "2023-04-12";
    	Date d = DateUtils.convertStringToYYYYMMDD(strDate);
    	
		User1 emp1 = new User1("3", "Reenta", d);
		User1 emp2 = new User1("4", "DEffea", d);
		list.add(emp1);
		list.add(emp2);
		
    	try (
				Writer writer = Files.newBufferedWriter(Paths.get("data.csv"));
				CSVWriter csvWriter = new CSVWriter(writer, 
					CSVWriter.DEFAULT_SEPARATOR, 
					CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);
			) {
			String[] headerRecord = { "id", "name", "dob"};
			csvWriter.writeNext(headerRecord);
			
			List<String[]> listArr = new ArrayList<>();
			for (User1 user : list) {
				String[] sarr = toArray(user);
				listArr.add(sarr);
			}
			csvWriter.writeAll(listArr);
		}

		batchJobScheduler.runJobInventoryMovement();
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    private String[] toArray(User1 user) {
		String[] strarr = new String[] {
				user.getId(),
				user.getName(),
				String.valueOf(user.getDob())
		};
		return strarr;
	}
    
    @ApiOperation(response = Optional.class, value = "Batch Fetch") // label for swagger
    @GetMapping("/batch-upload/jobInventoryQuery")
    public ResponseEntity<?> jobInventoryQuery() throws Exception {
    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    	 
        batchJobScheduler.runJobdbToCsvJob();
       
		AccountService service2 = context.getBean("accountService", AccountService.class);
		log.info("inventory size: " + service2.getInventoryHolder().size());
		context.close();
        return new ResponseEntity<>(service2.getInventoryHolder(), HttpStatus.OK);
    }
    
    @ApiOperation(response = Optional.class, value = "jobPeriodicRun") // label for swagger
    @GetMapping("/batch-upload/jobPeriodicRun")
    public ResponseEntity<?> jobPeriodicRun() throws Exception {
        batchJobScheduler.runJobPeriodic();
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /*-----------------------------BATCH-UPLOAD----------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "BomHeader") // label for swagger
    @PostMapping("/batch-upload/bomheader")
    public ResponseEntity<?> bomHeaderUpload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobBomHeader();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------BomLine-------------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "BomLine") // label for swagger
    @PostMapping("/batch-upload/bomline")
    public ResponseEntity<?> bomLineUpload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobBomLine();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------BinLocation--------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "BinLocation") // label for swagger
    @PostMapping("/batch-upload/binlocation")
    public ResponseEntity<?> binLocationUpload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobBinLocation();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------BusinessPartner--------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "BusinessPartner") // label for swagger
    @PostMapping("/batch-upload/businesspartner")
    public ResponseEntity<?> businesspartnerUpload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobBusinessPartner();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------HandlingEquipment--------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "HandlingEquipment") // label for swagger
    @PostMapping("/batch-upload/handlingequipment")
    public ResponseEntity<?> handlingequipmentUpload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobHandlingEquipment();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------imbasicdata1_110--------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "Imbasicdata1 WH_ID 110") // label for swagger
    @PostMapping("/batch-upload/imbasicdata1-110")
    public ResponseEntity<?> imbasicdata1110Upload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobImBasicData1();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------imbasicdata1_111--------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "Imbasicdata1 WH_ID 111") // label for swagger
    @PostMapping("/batch-upload/imbasicdata1-111")
    public ResponseEntity<?> imbasicdata1111Upload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobImBasicData1WhId111();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------Impartner_110--------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "Impartner_110") // label for swagger
    @PostMapping("/batch-upload/impartner-110")
    public ResponseEntity<?> impartner110Upload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobIMPartner();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------Impartner_111--------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "Impartner_111") // label for swagger
    @PostMapping("/batch-upload/impartner-111")
    public ResponseEntity<?> impartner111Upload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobIMPartnerWhId111();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------Inventory--------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "Inventory") // label for swagger
    @PostMapping("/batch-upload/inventory")
    public ResponseEntity<?> inventoryUpload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobInventory();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------Inventory-Stock---------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "Inventory") // label for swagger
    @PostMapping("/batch-upload/inventoryStock")
    public ResponseEntity<?> inventoryStockUpload (@RequestParam("file") MultipartFile file) 
    		throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        batchJobScheduler.runJobInventoryStock();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /*--------------------------------------------------------------------------------------------------*/
    
    @ApiOperation(response = Optional.class, value = "Generate BOM Report") // label for swagger
   	@GetMapping("/report/{format}/bomheader")
   	public ResponseEntity<?> bomReport (@PathVariable String format) throws FileNotFoundException, JRException {
   		String response = reportService.exportBom(format);
       	return new ResponseEntity<>(response, HttpStatus.OK);
   	}
    
    @ApiOperation(response = Optional.class, value = "Get Placed Order Report") // label for swagger
   	@GetMapping("/report/orders")
   	public ResponseEntity<?> getOrders (@RequestParam String warehouseID, @RequestParam Long statusId, 
   			@RequestParam(required = false) String date) 
   					throws FileNotFoundException, JRException, java.text.ParseException {
   		Map<String, Object> response = reportService.getOrderDetails(warehouseID, statusId, date);
       	return new ResponseEntity<>(response, HttpStatus.OK);
   	}
    
    /*------------------------------------------------------------------------------------------------------*/
    @ApiOperation(response = Optional.class, value = "Document Storage Download") // label for swagger
   	@GetMapping("/report/inventory/download")
   	public ResponseEntity<?> docStorageDownload() throws Exception {
//    	String filePath = "/home/ubuntu/classicwms/root/Code/Project/TransactionService/inventory.xlsx";
    	String filePath = propertiesConfig.getInventoryCsvDownloadPath() + 
    			"/TransactionService/inventory.xlsx";
    	
    	File file = new File (filePath);
    	Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
   	}
    
    @ApiOperation(response = Optional.class, value = "Document Storage Download") // label for swagger
   	@GetMapping("/report/inventory/online")
   	public ResponseEntity<?> inventoryOnlineReport() throws Exception {
    	batchJobScheduler.runJobdbToCsvJob();
//    	String filePath = "/home/ubuntu/classicwms/root/Code/Project/WrapperService/inventory.csv";
    	String filePath = propertiesConfig.getInventoryCsvDownloadPath() + "/WrapperService/inventory.csv";
    	File file = new File (filePath);
    	Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
   	}
}