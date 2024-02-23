import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { HighchartsChartModule } from 'highcharts-angular';
import { PiechartComponent } from './dashboard/piechart/piechart.component';
import { LineChartComponent } from './dashboard/line-chart/line-chart.component';
import { StackChartComponent } from './dashboard/stack-chart/stack-chart.component';
import { StackBarComponent } from './dashboard/stack-bar/stack-bar.component';
import { BarChartComponent } from './dashboard/bar-chart/bar-chart.component';
import { BinningComponent } from './dashboard/binning/binning.component';
import { BinFullComponent } from './dashboard/a-sample dashboard/binstatus/bin-full/bin-full.component';
import { BinEmptyComponent } from './dashboard/a-sample dashboard/binstatus/bin-empty/bin-empty.component';
import { SkuListComponent } from './dashboard/a-sample dashboard/binstatus/sku inventory/sku-list/sku-list.component';
import { AsnComponent } from './dashboard/a-sample dashboard/receipts/asn/asn.component';
import { ReceivedComponent } from './dashboard/a-sample dashboard/receipts/received/received.component';
import { DeliveryComponent } from './dashboard/a-sample dashboard/delivery/delivery/delivery.component';
import { ShippingComponent } from './dashboard/a-sample dashboard/shipping/shipping/shipping.component';
import { SpecialOrdersComponent } from './dashboard/a-sample dashboard/shipping/special-orders/special-orders.component';
import { SemiPieComponent } from './dashboard/charts-sample/semi-pie/semi-pie.component';
import { SkuchartComponent } from './dashboard/charts-sample/skuchart/skuchart.component';
import { ReceiptBarComponent } from './dashboard/charts-sample/receipt-bar/receipt-bar.component';
import { BinChartComponent } from './dashboard/charts-sample/bin-chart/bin-chart.component';
import { DeliverChartComponent } from './dashboard/charts-sample/deliver-chart/deliver-chart.component';
import { Floor1Component } from './dashboard/floor1/floor1.component';
import { Floor2Component } from './dashboard/floor2/floor2.component';
import { FastSlowListComponent } from './dashboard/list/fast-slow-list/fast-slow-list.component';
import { FloorChartComponent } from './dashboard/floor chart/floor-chart/floor-chart.component';
import { BoxdetailsComponent } from './dashboard/floor chart/floor-chart/boxdetails/boxdetails.component';
import { BoxDetails2Component } from './dashboard/floor chart/floor-chart/box-details2/box-details2.component';
import { TemperatureDetailsComponent } from './dashboard/floor chart/floor-chart/temperature-details/temperature-details.component';
import { StorageComponent } from './dashboard/temperature/storage/storage.component';
import { Zone1Component } from './dashboard/temperature/zone1/zone1.component';
import { Zone2Component } from './dashboard/temperature/zone2/zone2.component';
import { Storage2Component } from './dashboard/temperature/storage2/storage2.component';
import { FloorChart1Component } from './dashboard/floor chart/floor-chart/floor-chart1/floor-chart1.component';
import { LandingPageComponent } from './landing-page/landing-page.component';

@NgModule({
  declarations: [
    DashboardComponent,
    PiechartComponent,
    LineChartComponent,
    StackChartComponent,
    StackBarComponent,
    BarChartComponent,
    BinningComponent,
    BinFullComponent,
    BinEmptyComponent,
    SkuListComponent,
    AsnComponent,
    ReceivedComponent,
    DeliveryComponent,
    ShippingComponent,
    SpecialOrdersComponent,
    SemiPieComponent,
    SkuchartComponent,
    ReceiptBarComponent,
    BinChartComponent,
    DeliverChartComponent,
    Floor1Component,
    Floor2Component,
    FastSlowListComponent,
    FloorChartComponent,
    BoxdetailsComponent,
    BoxDetails2Component,
    TemperatureDetailsComponent,
    StorageComponent,
    Zone1Component,
    Zone2Component,
    Storage2Component,
    FloorChart1Component,
    LandingPageComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    DashboardRoutingModule,
    HighchartsChartModule,
  ],
  exports: []
})
export class DashboardModule { }
