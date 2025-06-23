package com.tekclover.wms.api.masters.controller;


import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.dock.AddDock;
import com.tekclover.wms.api.masters.model.dock.Dock;
import com.tekclover.wms.api.masters.model.dock.SearchDock;
import com.tekclover.wms.api.masters.model.dock.UpdateDock;
import com.tekclover.wms.api.masters.model.route.AddRoute;
import com.tekclover.wms.api.masters.model.route.Route;
import com.tekclover.wms.api.masters.model.route.SearchRoute;
import com.tekclover.wms.api.masters.model.route.UpdateRoute;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.repository.RouteRepository;
import com.tekclover.wms.api.masters.service.DockService;
import com.tekclover.wms.api.masters.service.RouteService;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"Route"}, value = "Route  Operations related to RouteController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Route ",description = "Operations related to Route ")})
@RequestMapping("/route")
@RestController
public class RouteController {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    private RouteService routeService;

    @ApiOperation(response = Route.class, value = "Get all Route details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Route> routeList = routeService.getAllRoute();
        return new ResponseEntity<>(routeList, HttpStatus.OK);
    }

    @ApiOperation(response = Route.class, value = "Get a Route") // label for swagger
    @GetMapping("/{routeId}")
    public ResponseEntity<?> getRoute(@PathVariable Long routeId, @RequestParam String companyCodeId,
                                      @RequestParam String languageId,@RequestParam String plantId,
                                      @RequestParam String warehouseId) {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        Route dbRoute = routeService.getRoute(routeId,companyCodeId,plantId,languageId,warehouseId);
        log.info("dbRoute : " + dbRoute);
        return new ResponseEntity<>(dbRoute, HttpStatus.OK);
    }
finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = Route.class, value = "Create Route") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postRoute(@Valid @RequestBody AddRoute newRoute,@RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(newRoute.getCompanyCodeId(), newRoute.getPlantId(), newRoute.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        Route createdRoute= routeService.createRoute(newRoute, loginUserID);
        return new ResponseEntity<>(createdRoute , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = Route.class, value = "Update Route") // label for swagger
    @PatchMapping("/{routeId}")
    public ResponseEntity<?> patchRoute(@PathVariable Long routeId, @RequestParam String companyCodeId,
                                        @RequestParam String languageId, @RequestParam String plantId,
                                        @RequestParam String warehouseId,@Valid @RequestBody UpdateRoute updateRoute,
                                        @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        Route createRoute =
                routeService.updateRoute(companyCodeId,plantId,warehouseId,languageId,routeId,updateRoute,loginUserID);
        return new ResponseEntity<>(createRoute , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = Route.class, value = "Delete Route") // label for swagger
    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long routeId, @RequestParam String companyCodeId,
                                         @RequestParam String languageId, @RequestParam String plantId,
                                         @RequestParam String warehouseId,@RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        routeService.deleteRoute(companyCodeId,languageId,plantId,warehouseId,routeId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = Route.class, value = "Find Route") // label for swagger
    @PostMapping("/findRoute")
    public ResponseEntity<?> findRoute(@Valid @RequestBody SearchRoute searchRoute) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbNameList(searchRoute.getCompanyCodeId(), searchRoute.getPlantId(), searchRoute.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<Route> createRoute = routeService.findRoute(searchRoute);
        return new ResponseEntity<>(createRoute, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
