package com.tekclover.wms.api.masters.controller;


import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.route.AddRoute;
import com.tekclover.wms.api.masters.model.route.Route;
import com.tekclover.wms.api.masters.model.route.SearchRoute;
import com.tekclover.wms.api.masters.model.route.UpdateRoute;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.repository.RouteRepository;
import com.tekclover.wms.api.masters.service.BaseService;
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
@SwaggerDefinition(tags = {@Tag(name = "Route ", description = "Operations related to Route ")})
@RequestMapping("/route")
@RestController
public class RouteController {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = Route.class, value = "Get all Route details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Route> routeList = routeService.getAllRoute();
        return new ResponseEntity<>(routeList, HttpStatus.OK);
    }

    @ApiOperation(response = Route.class, value = "Get a Route") // label for swagger
    @GetMapping("/{routeId}")
    public ResponseEntity<?> getRoute(@PathVariable Long routeId, @RequestParam String companyCodeId,
                                      @RequestParam String languageId, @RequestParam String plantId,
                                      @RequestParam String warehouseId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Route dbRoute = routeService.getRoute(routeId, companyCodeId, plantId, languageId, warehouseId);
            log.info("dbRoute : " + dbRoute);
            return new ResponseEntity<>(dbRoute, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = Route.class, value = "Create Route") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postRoute(@Valid @RequestBody AddRoute newRoute, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newRoute.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Route createdRoute = routeService.createRoute(newRoute, loginUserID);
            return new ResponseEntity<>(createdRoute, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Route.class, value = "Update Route") // label for swagger
    @PatchMapping("/{routeId}")
    public ResponseEntity<?> patchRoute(@PathVariable Long routeId, @RequestParam String companyCodeId,
                                        @RequestParam String languageId, @RequestParam String plantId,
                                        @RequestParam String warehouseId, @Valid @RequestBody UpdateRoute updateRoute,
                                        @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Route createRoute =
                    routeService.updateRoute(companyCodeId, plantId, warehouseId, languageId, routeId, updateRoute, loginUserID);
            return new ResponseEntity<>(createRoute, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Route.class, value = "Delete Route") // label for swagger
    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long routeId, @RequestParam String companyCodeId,
                                         @RequestParam String languageId, @RequestParam String plantId,
                                         @RequestParam String warehouseId, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            routeService.deleteRoute(companyCodeId, languageId, plantId, warehouseId, routeId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Route.class, value = "Find Route") // label for swagger
    @PostMapping("/findRoute")
    public ResponseEntity<?> findRoute(@Valid @RequestBody SearchRoute searchRoute) throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchRoute.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<Route> createRoute = routeService.findRoute(searchRoute);
            return new ResponseEntity<>(createRoute, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
