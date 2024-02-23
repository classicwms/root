import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { BinnerComponent } from "./binner/binner.component";
import { ContainerReportComponent } from "./container-report/container-report.component";
import { InboundStatusComponent } from "./inbound-status/inbound-status.component";
import { InventoryReportComponent } from "./inventory-report/inventory-report.component";
import { InventorymovementComponent } from "./inventorymovement/inventorymovement.component";
import { LeadtimeComponent } from "./leadtime/leadtime.component";
import { NewStockMovementComponent } from "./new-stock-movement/new-stock-movement.component";
import { OrderStatusComponent } from "./order-status/order-status.component";
import { OrderSummaryComponent } from "./order-summary/order-summary.component";
import { PickingReportComponent } from "./picking-report/picking-report.component";
import { ReceiptConfirmationComponent } from "./receipt-confirmation/receipt-confirmation.component";
import { ReportsListComponent } from "./reports-list/reports-list.component";
import { ReportstabComponent } from "./reportstab/reportstab.component";
import { ShipmentDispatchComponent } from "./shipment-dispatch/shipment-dispatch.component";
import { ShipmentSummaryComponent } from "./shipment-summary/shipment-summary.component";
import { ShipmentComponent } from "./shipment/shipment.component";
import { StockMovenmentComponent } from "./stock-movenment/stock-movenment.component";
import { StockReportComponent } from "./stock-report/stock-report.component";
import { StocksamplereportComponent } from "./stocksamplereport/stocksamplereport.component";
import { TransferReportComponent } from "./transfer-report/transfer-report.component";





const routes: Routes = [
  {
    path: '',
    component: ReportstabComponent,
    children: [

      // { path: 'stock', component: StockReportComponent,  },
      // { path: 'inventory', component: InventoryReportComponent,  },
      // { path: 'stockmovement', component: StockMovenmentComponent,  },
      // { path: 'orderstatus', component: OrderStatusComponent,  },
      // { path: 'shipment', component: ShipmentComponent,  },
      // { path: 'shipment-summary', component: ShipmentSummaryComponent,  },
      // { path: 'shipment-dispatch', component: ShipmentDispatchComponent,  },
      // { path: 'receipt-confirmation', component: ReceiptConfirmationComponent,  },
    ]
  },
  { path: 'report-list', component: ReportsListComponent, },
  { path: 'stock', component: StockReportComponent, },
  { path: 'inventory', component: InventoryReportComponent, },
  { path: 'stockmovement', component: StockMovenmentComponent, },
  { path: 'orderstatus', component: OrderStatusComponent, },
  { path: 'shipment', component: ShipmentComponent, },
  { path: 'shipment-summary', component: ShipmentSummaryComponent, },
  { path: 'shipment-dispatch', component: ShipmentDispatchComponent, },
  { path: 'receipt-confirmation', component: ReceiptConfirmationComponent, },
  { path: 'samplestock', component: StocksamplereportComponent, },
  { path: 'container', component: ContainerReportComponent, },
  { path: 'new-stock-movement', component: NewStockMovementComponent, },
  
  { path: 'picking-report', component: PickingReportComponent, },

  { path: 'inventorymovement', component: InventorymovementComponent, },
  { path: 'transfer', component: TransferReportComponent, },
  { path: 'binner', component: BinnerComponent, },
  { path: 'ordersummary', component: OrderSummaryComponent, },
  { path: 'inbound-status', component: InboundStatusComponent , },
  { path: 'leadtime', component: LeadtimeComponent , }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportsRoutingModule { }
