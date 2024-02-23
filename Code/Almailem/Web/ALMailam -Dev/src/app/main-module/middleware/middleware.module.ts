import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MiddlewareRoutingModule } from './middleware-routing.module';
import { MiddlewareTabComponent } from './middleware-tab/middleware-tab.component';
import { SalesInvoiceMidllewareComponent } from './sales-invoice-midlleware/sales-invoice-midlleware.component';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { SalesreturnheaderComponent } from './salesreturnheader/salesreturnheader.component';
import { SalesreturnheaderLinesComponent } from './salesreturnheader/salesreturnheader-lines/salesreturnheader-lines.component';
import { ShipmentorderComponent } from './shipmentorder/shipmentorder.component';
import { ShipemntorderLinesComponent } from './shipmentorder/shipemntorder-lines/shipemntorder-lines.component';
import { StockadjustmentComponent } from './stockadjustment/stockadjustment.component';
import { PeriodicHeaderComponent } from './periodic-header/periodic-header.component';
import { PeriodicLinesComponent } from './periodic-header/periodic-lines/periodic-lines.component';
import { SupplierinvoiceComponent } from './supplierinvoice/supplierinvoice.component';
import { SupplierinvoiceLinesComponent } from './supplierinvoice/supplierinvoice-lines/supplierinvoice-lines.component';
import { B2btransferorderComponent } from './b2btransferorder/b2btransferorder.component';
import { B2btransferorderLinesComponent } from './b2btransferorder/b2btransferorder-lines/b2btransferorder-lines.component';
import { CustomermasterComponent } from './customermaster/customermaster.component';
import { InterwarehouseinComponent } from './interwarehousein/interwarehousein.component';
import { InterwarehouseinLinesComponent } from './interwarehousein/interwarehousein-lines/interwarehousein-lines.component';
import { InterwarehouseoutComponent } from './interwarehouseout/interwarehouseout.component';
import { InterwarehouseoutLinesComponent } from './interwarehouseout/interwarehouseout-lines/interwarehouseout-lines.component';
import { ItemmasterComponent } from './itemmaster/itemmaster.component';
import { PerpetualHeaderComponent } from './perpetual-header/perpetual-header.component';
import { PerpetualLinesComponent } from './perpetual-header/perpetual-lines/perpetual-lines.component';
import { PicklistHeaderComponent } from './picklist-header/picklist-header.component';
import { PicklistLinesComponent } from './picklist-header/picklist-lines/picklist-lines.component';
import { PurchasereturnComponent } from './purchasereturn/purchasereturn.component';
import { PurchasereturnlinesComponent } from './purchasereturn/purchasereturnlines/purchasereturnlines.component';
import { StockreceiptheaderComponent } from './stockreceiptheader/stockreceiptheader.component';


@NgModule({
  declarations: [
    MiddlewareTabComponent,
    SalesInvoiceMidllewareComponent,
    SalesreturnheaderComponent,
    SalesreturnheaderLinesComponent,
    ShipmentorderComponent,
    ShipemntorderLinesComponent,
    StockadjustmentComponent,
    PeriodicHeaderComponent,
    PeriodicLinesComponent,
    SupplierinvoiceComponent,
    SupplierinvoiceLinesComponent,
    B2btransferorderComponent,
    B2btransferorderLinesComponent,
    CustomermasterComponent,
    InterwarehouseinComponent,
    InterwarehouseinLinesComponent,
    InterwarehouseoutComponent,
    InterwarehouseoutLinesComponent,
    ItemmasterComponent,
    PerpetualHeaderComponent,
    PerpetualLinesComponent,
    PicklistHeaderComponent,
    PicklistLinesComponent,
    PurchasereturnComponent,
    PurchasereturnlinesComponent,
    StockreceiptheaderComponent
  ],
  imports: [
    CommonModule,
    MiddlewareRoutingModule,
    SharedModule,
    CommonFieldModule,
    AngularMultiSelectModule
  ]
})
export class MiddlewareModule { }
