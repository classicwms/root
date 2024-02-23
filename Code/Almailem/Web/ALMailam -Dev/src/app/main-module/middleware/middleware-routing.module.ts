import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiddlewareTabComponent } from './middleware-tab/middleware-tab.component';
import { SalesInvoiceMidllewareComponent } from './sales-invoice-midlleware/sales-invoice-midlleware.component';
import { SalesreturnheaderComponent } from './salesreturnheader/salesreturnheader.component';
import { SalesreturnheaderLinesComponent } from './salesreturnheader/salesreturnheader-lines/salesreturnheader-lines.component';
import { ShipmentorderComponent } from './shipmentorder/shipmentorder.component';
import { ShipemntorderLinesComponent } from './shipmentorder/shipemntorder-lines/shipemntorder-lines.component';
import { PeriodicHeaderComponent } from './periodic-header/periodic-header.component';
import { StockadjustmentComponent } from './stockadjustment/stockadjustment.component';
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
import { PeriodicLinesComponent } from './periodic-header/periodic-lines/periodic-lines.component';
import { PerpetualHeaderComponent } from './perpetual-header/perpetual-header.component';
import { PerpetualLinesComponent } from './perpetual-header/perpetual-lines/perpetual-lines.component';
import { PicklistHeaderComponent } from './picklist-header/picklist-header.component';
import { PicklistLinesComponent } from './picklist-header/picklist-lines/picklist-lines.component';
import { PurchasereturnComponent } from './purchasereturn/purchasereturn.component';
import { PurchasereturnlinesComponent } from './purchasereturn/purchasereturnlines/purchasereturnlines.component';
import { StockreceiptheaderComponent } from './stockreceiptheader/stockreceiptheader.component';

const routes: Routes = [
  
  {
  path: 'salesInvoice',
  component: SalesInvoiceMidllewareComponent
},
{
  path: 'salesReturn',
  component: SalesreturnheaderComponent
},
{
  path: 'salesReturnlines/:code',
  component: SalesreturnheaderLinesComponent
},
{
  path: 'shippingorder',
  component: ShipmentorderComponent
},
{
  path: 'shippingorderlines/:code',
  component: ShipemntorderLinesComponent
},
{
  path: 'stockadjustment',
  component: StockadjustmentComponent
},
{
  path: 'supplierinvoice',
  component: SupplierinvoiceComponent
},
{
  path: 'supplierinvoicelines/:code',
  component: SupplierinvoiceLinesComponent
},
{
  path: 'B2BTransferOrder',
  component: B2btransferorderComponent
},
{
  path: 'B2BTransferOrderlines/:code',
  component: B2btransferorderLinesComponent
},
{
  path: 'shippingorder',
  component: ShipmentorderComponent
},
{
  path: 'shippingorderlines/:code',
  component: ShipemntorderLinesComponent
},
{
  path: 'periodicHeader',
  component: PeriodicHeaderComponent
},
{
  path: 'perpetualHeader',
  component: PerpetualHeaderComponent
},
{
  path: 'periodicHeaderlines/:code',
  component: PeriodicLinesComponent
},
{
  path: 'perpetualHeaderlines/:code',
  component: PerpetualLinesComponent
},
{
  path: 'customer',
  component: CustomermasterComponent
},
{
  path: 'interwarehousein',
  component: InterwarehouseinComponent
},
{
  path: 'interwarehouseinlines/:code',
  component: InterwarehouseinLinesComponent
},
{
  path: 'interwarehouseout',
  component: InterwarehouseoutComponent
},
{
  path: 'interwarehouseoutlines/:code',
  component: InterwarehouseoutLinesComponent
},
{
  path: 'itemaster',
  component: ItemmasterComponent
},
{
  path: 'picklistheader',
  component: PicklistHeaderComponent
},
{
  path: 'picklistlines/:code',
  component: PicklistLinesComponent
},
{
  path: 'purchasereturn',
  component: PurchasereturnComponent
},
{
  path: 'purchasereturnlines/:code',
  component: PurchasereturnlinesComponent
},
{
  path: 'stockreceiptheader',
  component: StockreceiptheaderComponent
},
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MiddlewareRoutingModule { }
