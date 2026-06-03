package com.tekclover.wms.core.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    private final AtomicInteger inboundOrderCount = new AtomicInteger(0);
    private final AtomicInteger inboundLineCount = new AtomicInteger(0);
    private final AtomicInteger putawayLineCount = new AtomicInteger(0);
    private final AtomicInteger inboundConfirmedCount = new AtomicInteger(0);
    private final AtomicInteger outboundOrderCount = new AtomicInteger(0);
    private final AtomicInteger outboundLineCount = new AtomicInteger(0);
    private final AtomicInteger obPutawayLineCount = new AtomicInteger(0);
    private final AtomicInteger outboundConfirmedCount = new AtomicInteger(0);

    private final AtomicInteger queuedOrdersCount = new AtomicInteger(0);
    private final AtomicInteger failedOrdersCount = new AtomicInteger(0);

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        meterRegistry.gauge("wms_inbound_order_count", inboundOrderCount);
        meterRegistry.gauge("wms_inbound_line_count", inboundLineCount);
        meterRegistry.gauge("wms_putaway_line_count", putawayLineCount);
        meterRegistry.gauge("wms_inbound_confirmed_count", inboundConfirmedCount);
        meterRegistry.gauge("wms_outbound_order_count", outboundOrderCount);
        meterRegistry.gauge("wms_outbound_line_count", outboundLineCount);
        meterRegistry.gauge("wms_obputaway_line_count", obPutawayLineCount);
        meterRegistry.gauge("wms_obbound_confirmed_count", outboundConfirmedCount);
        meterRegistry.gauge("wms_queued_orders_count", queuedOrdersCount);

        meterRegistry.gauge("wms_failed_orders_count", failedOrdersCount);
    }

    public void updateInboundOrderCount(int count) {
        inboundOrderCount.set(count);
    }

    public void updateInboundLineCount(int count) {
        inboundLineCount.set(count);
    }

    public void updatePutawayLineCount(int count) {
        putawayLineCount.set(count);
    }

    public void updateConfirmedCount(int count) { inboundConfirmedCount.set(count); }

    public void updateOutboundOrderCount(int count) {
        outboundOrderCount.set(count);
    }

    public void updateOutboundLineCount(int count) {
        outboundLineCount.set(count);
    }

    public void updateObPutawayLineCount(int count) {
        obPutawayLineCount.set(count);
    }
    public void updateQueuedOrdersCount(int count) {
        queuedOrdersCount.set(count);
    }

    public void updateFailedOrdersCount(int count) {
        failedOrdersCount.set(count);
    }

    public void updateObConfirmedCount(int count) { outboundConfirmedCount.set(count); }
}