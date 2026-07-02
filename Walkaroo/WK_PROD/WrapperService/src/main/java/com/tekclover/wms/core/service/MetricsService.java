package com.tekclover.wms.core.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // ================= INBOUND =================
    private final AtomicInteger inboundOrderCount = new AtomicInteger(0);
    private final AtomicInteger inboundLineCount = new AtomicInteger(0);
    private final AtomicInteger putawayLineCount = new AtomicInteger(0);
    private final AtomicInteger inboundConfirmedCount = new AtomicInteger(0);

    // ================= OUTBOUND =================
    private final AtomicInteger outboundOrderCount = new AtomicInteger(0);
    private final AtomicInteger outboundLineCount = new AtomicInteger(0);
    private final AtomicInteger obPutawayLineCount = new AtomicInteger(0);
    private final AtomicInteger outboundConfirmedCount = new AtomicInteger(0);

    // ================= QUEUE / FAILURE =================
    private final AtomicInteger queuedOrdersCount = new AtomicInteger(0);
    private final AtomicInteger failedOrdersCount = new AtomicInteger(0);

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void registerMetrics() {

        // ================= INBOUND =================
        Gauge.builder("wms_inbound_order_count", inboundOrderCount, AtomicInteger::get)
                .description("Current inbound order count")
                .register(meterRegistry);

        Gauge.builder("wms_inbound_line_count", inboundLineCount, AtomicInteger::get)
                .description("Current inbound line count")
                .register(meterRegistry);

        Gauge.builder("wms_putaway_line_count", putawayLineCount, AtomicInteger::get)
                .description("Current putaway line count")
                .register(meterRegistry);

        Gauge.builder("wms_inbound_confirmed_count", inboundConfirmedCount, AtomicInteger::get)
                .description("Current confirmed inbound count")
                .register(meterRegistry);

        // ================= OUTBOUND =================
        Gauge.builder("wms_outbound_order_count", outboundOrderCount, AtomicInteger::get)
                .description("Current outbound order count")
                .register(meterRegistry);

        Gauge.builder("wms_outbound_line_count", outboundLineCount, AtomicInteger::get)
                .description("Current outbound line count")
                .register(meterRegistry);

        Gauge.builder("wms_obputaway_line_count", obPutawayLineCount, AtomicInteger::get)
                .description("Current outbound putaway line count")
                .register(meterRegistry);

        Gauge.builder("wms_outbound_confirmed_count", outboundConfirmedCount, AtomicInteger::get)
                .description("Current outbound confirmed count")
                .register(meterRegistry);

        // ================= QUEUE / FAILURE =================
        Gauge.builder("wms_queued_orders_count", queuedOrdersCount, AtomicInteger::get)
                .description("Current queued orders count")
                .register(meterRegistry);

        Gauge.builder("wms_failed_orders_count", failedOrdersCount, AtomicInteger::get)
                .description("Current failed orders count")
                .register(meterRegistry);
    }

    // ================= UPDATE METHODS =================

    public void updateInboundOrderCount(int count) {
        inboundOrderCount.set(count);
    }

    public void updateInboundLineCount(int count) {
        inboundLineCount.set(count);
    }

    public void updatePutawayLineCount(int count) {
        putawayLineCount.set(count);
    }

    public void updateInboundConfirmedCount(int count) {
        inboundConfirmedCount.set(count);
    }

    public void updateOutboundOrderCount(int count) {
        outboundOrderCount.set(count);
    }

    public void updateOutboundLineCount(int count) {
        outboundLineCount.set(count);
    }

    public void updateObPutawayLineCount(int count) {
        obPutawayLineCount.set(count);
    }

    public void updateOutboundConfirmedCount(int count) {
        outboundConfirmedCount.set(count);
    }

    public void updateQueuedOrdersCount(int count) {
        queuedOrdersCount.set(count);
    }

    public void updateFailedOrdersCount(int count) {
        failedOrdersCount.set(count);
    }
}