package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.route.AddRoute;
import com.tekclover.wms.api.masters.model.route.Route;
import com.tekclover.wms.api.masters.model.route.SearchRoute;
import com.tekclover.wms.api.masters.model.route.UpdateRoute;
import com.tekclover.wms.api.masters.repository.RouteRepository;
import com.tekclover.wms.api.masters.repository.specification.RouteSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    /**
     * getAllRoute
     *
     * @return
     */
    public List<Route> getAllRoute() {
        try {
            List<Route> routeList = routeRepository.findAll();
            log.info("routeList : " + routeList);
            routeList = routeList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return routeList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param routeId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public Route getRoute(Long routeId, String companyCodeId, String plantId,
                          String languageId, String warehouseId) {
        try {
            Optional<Route> dbRoute = routeRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRouteIdAndDeletionIndicator(
                    companyCodeId,
                    languageId,
                    plantId,
                    warehouseId,
                    routeId,
                    0L
            );
            if (dbRoute.isEmpty()) {
                throw new BadRequestException("The given Values : " +
                        " routeId " + routeId +
                        " companyCodeId " + companyCodeId +
                        " plantId " + plantId +
                        " languageId " + languageId +
                        " warehouseId " + warehouseId + " doesn't exist.");
            }
            return dbRoute.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * @param searchRoute
     * @return
     */
    public List<Route> findRoute(SearchRoute searchRoute) {
        try {
            RouteSpecification spec = new RouteSpecification(searchRoute);
            List<Route> results = routeRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createRoute
     *
     * @param newRoute
     * @return
     */
    public Route createRoute(AddRoute newRoute, String loginUserID) {
        try {
            Route dbRoute = new Route();
            Optional<Route> duplicateRoute = routeRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRouteIdAndDeletionIndicator(
                    newRoute.getCompanyCodeId(),
                    newRoute.getLanguageId(),
                    newRoute.getPlantId(),
                    newRoute.getWarehouseId(),
                    newRoute.getRouteId(),
                    0L);
            if (!duplicateRoute.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newRoute, dbRoute, CommonUtils.getNullPropertyNames(newRoute));
                dbRoute.setDeletionIndicator(0L);
                dbRoute.setCreatedBy(loginUserID);
                dbRoute.setUpdatedBy(loginUserID);
                dbRoute.setCreatedOn(new Date());
                dbRoute.setUpdatedOn(new Date());
                return routeRepository.save(dbRoute);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param routeId
     * @param updateRoute
     * @param loginUserID
     * @return
     */
    public Route updateRoute(String companyCodeId, String plantId, String warehouseId, String languageId,
                             Long routeId, UpdateRoute updateRoute, String loginUserID) {
        try {
            Route dbRoute = getRoute(routeId, companyCodeId, plantId, languageId, warehouseId);
            BeanUtils.copyProperties(updateRoute, dbRoute, CommonUtils.getNullPropertyNames(updateRoute));
            dbRoute.setUpdatedBy(loginUserID);
            dbRoute.setUpdatedOn(new Date());
            return routeRepository.save(dbRoute);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param routeId
     * @param loginUserID
     */
    public void deleteRoute(String companyCodeId, String languageId, String plantId, String warehouseId,
                            Long routeId, String loginUserID) {
        try {
            Route dbRoute = getRoute(routeId, companyCodeId, plantId, languageId, warehouseId);
            if (dbRoute != null) {
                dbRoute.setDeletionIndicator(1L);
                dbRoute.setUpdatedBy(loginUserID);
                dbRoute.setUpdatedOn(new Date());
                routeRepository.save(dbRoute);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + routeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}