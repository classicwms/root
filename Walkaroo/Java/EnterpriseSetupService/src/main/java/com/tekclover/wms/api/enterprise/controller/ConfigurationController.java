package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.model.configuration.Configuration;
import com.tekclover.wms.api.enterprise.model.configuration.SearchConfiguration;
import com.tekclover.wms.api.enterprise.service.ConfigurationService;
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
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"Configuration"}, value = "Configuration Operations related to ConfigurationController")   // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Configuration ", description = "Operations related to Configuration")})
@RequestMapping("/configuration")
@RestController
public class ConfigurationController {

    @Autowired
    ConfigurationService configurationService;

    @ApiOperation(response = Configuration.class, value = "Get a Configuration")
    @GetMapping("/{configurationId}")
    public ResponseEntity<?> getConfiguration(@PathVariable Long configurationId) {
        Configuration configuration = configurationService.getConfiguration(configurationId);
        return new ResponseEntity<>(configuration, HttpStatus.OK);
    }

    @ApiOperation(response = Configuration.class, value = "Get a Configuration")
    @GetMapping("")
    public ResponseEntity<?> getConfiguration(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId) {
        Configuration configuration = configurationService.getConfiguration(companyCodeId, plantId, languageId, warehouseId);
        return new ResponseEntity<>(configuration, HttpStatus.OK);
    }

    @ApiOperation(response = Configuration.class, value = "Search Configuration") // label for swagger
    @PostMapping("/findConfiguration")
    public Stream<Configuration> findConfiguration(@RequestBody SearchConfiguration searchConfiguration) throws Exception {
        return configurationService.findConfiguration(searchConfiguration);
    }

    @ApiOperation(response = Configuration.class, value = "Create Configuration") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postConfiguration(@Valid @RequestBody List<Configuration> newConfiguration, @RequestParam String loginUserID) throws Exception {
        List<Configuration> createdConfiguration = configurationService.createConfiguration(newConfiguration, loginUserID);
        return new ResponseEntity<>(createdConfiguration, HttpStatus.OK);
    }

    @ApiOperation(response = Configuration.class, value = "Update Configuration") // label for swagger
    @PatchMapping("/{configurationId}")
    public ResponseEntity<?> patchConfiguration(@Valid @RequestBody List<Configuration> updateConfiguration, @RequestParam String loginUserID) throws Exception {
        List<Configuration> createdConfiguration = configurationService.updateConfiguration(updateConfiguration, loginUserID);
        return new ResponseEntity<>(createdConfiguration, HttpStatus.OK);
    }

    @ApiOperation(response = Configuration.class, value = "Delete Configuration") // label for swagger
    @DeleteMapping("/{configurationId}")
    public ResponseEntity<?> deleteConfiguration(@PathVariable Long configurationId, @RequestParam String loginUserID) throws Exception {
        configurationService.deleteConfiguration(configurationId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}