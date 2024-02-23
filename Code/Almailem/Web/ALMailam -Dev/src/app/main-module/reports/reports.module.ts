import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { ContainerReportComponent } from './container-report/container-report.component';
import { InventoryListComponent } from './inventory-report/inventory-list/inventory-list.component';
import { InventoryReportComponent } from './inventory-report/inventory-report.component';
import { NewStockMovementComponent } from './new-stock-movement/new-stock-movement.component';
import { OrderStatusComponent } from './order-status/order-status.component';
import { OrderstatusListComponent } from './order-status/orderstatus-list/orderstatus-list.component';
import { ReceiptConfirmationComponent } from './receipt-confirmation/receipt-confirmation.component';
import { ReceiptconfirmationListComponent } from './receipt-confirmation/receiptconfirmation-list/receiptconfirmation-list.component';
import { ReportsListComponent } from './reports-list/reports-list.component';
import { ReportsRoutingModule } from './reports-routing.module';
import { ShipmentDispatchComponent } from './shipment-dispatch/shipment-dispatch.component';
import { ShipmentdispatchListComponent } from './shipment-dispatch/shipmentdispatch-list/shipmentdispatch-list.component';
import { ShipmentSummaryComponent } from './shipment-summary/shipment-summary.component';
import { ShipmentsummaryListComponent } from './shipment-summary/shipmentsummary-list/shipmentsummary-list.component';
import { ShipmentListComponent } from './shipment/shipment-list/shipment-list.component';
import { ShipmentComponent } from './shipment/shipment.component';
import { StockMovenmentComponent } from './stock-movenment/stock-movenment.component';
import { StockmovenmentListComponent } from './stock-movenment/stockmovenment-list/stockmovenment-list.component';
import { StockListComponent } from './stock-report/stock-list/stock-list.component';
import { StockReportComponent } from './stock-report/stock-report.component';
import { StocksamplereportComponent } from './stocksamplereport/stocksamplereport.component';
import { PutawayDeatilsComponent } from './new-stock-movement/putaway-deatils/putaway-deatils.component';
import { PickingDetailsComponent } from './new-stock-movement/picking-details/picking-details.component';
import { HhtPickingReportComponent } from './hht-picking-report/hht-picking-report.component';
import { InventorymovementComponent } from './inventorymovement/inventorymovement.component';
import { TransferReportComponent } from './transfer-report/transfer-report.component';
import { BinnerComponent } from './binner/binner.component';
import { OrderSummaryComponent } from './order-summary/order-summary.component';
import { InboundStatusComponent } from './inbound-status/inbound-status.component';
import { InboundLinesComponent } from './inbound-status/inbound-lines/inbound-lines.component';
import { LeadtimeComponent } from './leadtime/leadtime.component';
import { ResourceResultsComponent } from './leadtime/resource-results/resource-results.component';
import { ScheduleReportComponent } from './schedule-report/schedule-report.component';
import { BarcodeScanComponent } from './barcode-scan/barcode-scan.component';
import { ShipmentTotalSummaryComponent } from './shipment-total-summary/shipment-total-summary.component';
import { OwlDateTimeModule, OwlNativeDateTimeModule } from 'ng-pick-datetime';
import { PreinboundComponent } from './preinbound/preinbound.component';
import { PreoutboundComponent } from './preoutbound/preoutbound.component';
import { PickingReportUsersComponent } from './picking-report-users/picking-report-users.component';
import { TransactionHistoryComponent } from './transaction-history/transaction-history.component';
import { PerpetualreportComponent } from './perpetualreport/perpetualreport.component';
import { PeriodicreportComponent } from './periodicreport/periodicreport.component';
import { HhtPickupLinesComponent } from './hht-picking-report/hht-pickup-lines/hht-pickup-lines.component';
import { TransactionhistrylinesComponent } from './transaction-history/transactionhistrylines/transactionhistrylines.component';
import { PickingproductivityComponent } from './pickingproductivity/pickingproductivity.component';
import { PickingproductivitylinesComponent } from './pickingproductivity/pickingproductivitylines/pickingproductivitylines.component';
import { ShippingreportComponent } from './shippingreport/shippingreport.component';
import { DetailspopupComponent } from './detailspopup/detailspopup.component';
import { GrlineComponent } from './grline/grline.component';
import { OrdermanagementComponent } from './ordermanagement/ordermanagement.component';


@NgModule({
  declarations: [
    StockReportComponent,
    InventoryReportComponent,
    StockListComponent,
    InventoryListComponent,
    StockMovenmentComponent,
    StockmovenmentListComponent,
    OrderStatusComponent,
    OrderstatusListComponent,
    ShipmentComponent,
    ShipmentListComponent,
    ShipmentSummaryComponent,
    ShipmentsummaryListComponent,
    ShipmentDispatchComponent,
    ShipmentdispatchListComponent,
    ReceiptConfirmationComponent,
    ReceiptconfirmationListComponent,
    ReportsListComponent,
    StocksamplereportComponent,
    ContainerReportComponent,
    NewStockMovementComponent,
    PutawayDeatilsComponent,
    PickingDetailsComponent,
    HhtPickingReportComponent,
    InventorymovementComponent,
    TransferReportComponent,
    BinnerComponent,
    OrderSummaryComponent,
    InboundStatusComponent,
    InboundLinesComponent,
    LeadtimeComponent,
    ResourceResultsComponent,
    ScheduleReportComponent,
    BarcodeScanComponent,
    ShipmentTotalSummaryComponent,
    PreinboundComponent,
    PreoutboundComponent,
    PickingReportUsersComponent,
    TransactionHistoryComponent,
    PerpetualreportComponent,
    PeriodicreportComponent,
    HhtPickupLinesComponent,
    TransactionhistrylinesComponent,
    PickingproductivityComponent,
    PickingproductivitylinesComponent,
    ShippingreportComponent,
    DetailspopupComponent,
    GrlineComponent,
    OrdermanagementComponent,
  ],
  imports: [
    CommonModule,
    ReportsRoutingModule,
    CommonModule,
    SharedModule,
    CommonFieldModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
    OwlDateTimeModule, 
    OwlNativeDateTimeModule,
  ],
  exports:[
    StockReportComponent,
    InventoryReportComponent,
  ]
})
export class ReportsModule { }
