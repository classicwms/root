package com.tekclover.wms.api.masters.repository;


import com.tekclover.wms.api.masters.model.route.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface RouteRepository extends JpaRepository<Route, Long>, JpaSpecificationExecutor<Route> {

    Optional<Route> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRouteIdAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, Long routeId, Long deletionIndicator);
}