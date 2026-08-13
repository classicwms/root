package com.tekclover.wms.api.inbound.transaction.service;


import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.dashboard.BinOccupancyDashboardResponse;
import com.tekclover.wms.api.inbound.transaction.model.dashboard.BinOccupancyProjection;
import com.tekclover.wms.api.inbound.transaction.model.dashboard.WarehouseOccupancyResponse;
import com.tekclover.wms.api.inbound.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.inbound.transaction.repository.OutboundLineV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardReportService {


    @Autowired
    private OutboundLineRepository outboundLineRepository;

    @Autowired
    private ScheduleAsyncService scheduleAsyncService;

    public BinOccupancyDashboardResponse capacityDashBoard () {

        log.info("Capacity Dashboard Process Started ------------> {} ", new Date());

        List<CompletableFuture<List<BinOccupancyProjection>>> futures =
                companies.stream().map(comp -> scheduleAsyncService.getBinOccupancy(comp)).collect(Collectors.toList());

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        List<BinOccupancyProjection> result =
                futures.stream()
                        .flatMap(f -> f.join().stream())
                        .collect(Collectors.toList());

        return buildDashboard(result);
    }

    private BinOccupancyDashboardResponse buildDashboard(List<BinOccupancyProjection> list) {

        BinOccupancyDashboardResponse response =
                new BinOccupancyDashboardResponse();

        response.setTotalWarehouses(companies.size());

        response.setTotalCapacity(list.stream().mapToDouble(BinOccupancyProjection::getOpeningQty).sum());

        response.setTotalUsed(
                list.stream()
                        .mapToDouble(BinOccupancyProjection::getOccupaidQty)
                        .sum());

        response.setTotalAvailable(
                list.stream()
                        .mapToDouble(BinOccupancyProjection::getBalanceQty)
                        .sum());

        List<WarehouseOccupancyResponse> warehouses = list.stream()
                .map(this::convert)
                .collect(Collectors.toList());

        response.setWarehouses(warehouses);

        return response;
    }

    private WarehouseOccupancyResponse convert(
            BinOccupancyProjection p) {

        WarehouseOccupancyResponse r =
                new WarehouseOccupancyResponse();

        r.setWarehouseId(p.getWarehouseId());
        r.setCompanyCodeId(p.getCompanyCodeId());
        r.setPlantId(p.getPlantId());
        r.setCapacity(p.getOpeningQty());
        r.setUsed(p.getOccupaidQty());
        r.setAvailable(p.getBalanceQty());
        r.setPercent(p.getOccupaidPercentage());
        r.setPlantText(p.getPlantText());
        r.setWarehouseText(p.getWarehouseText());

        return r;
    }


    List<String> companies = Arrays.asList(
            "MDU",
            "CMP",
            "CHN",
            "VGA",
            "CCL",
            "HYD",
            "AHM",
            "MUB",
            "NGP1",
            "NGP2",
            "MYS",
            "KNP"
    );
}
