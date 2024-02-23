import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { BinEmptyComponent } from "./dashboard/a-sample dashboard/binstatus/bin-empty/bin-empty.component";
import { BinFullComponent } from "./dashboard/a-sample dashboard/binstatus/bin-full/bin-full.component";
import { SkuListComponent } from "./dashboard/a-sample dashboard/binstatus/sku inventory/sku-list/sku-list.component";
import { DeliveryComponent } from "./dashboard/a-sample dashboard/delivery/delivery/delivery.component";
import { AsnComponent } from "./dashboard/a-sample dashboard/receipts/asn/asn.component";
import { ReceivedComponent } from "./dashboard/a-sample dashboard/receipts/received/received.component";
import { ShippingComponent } from "./dashboard/a-sample dashboard/shipping/shipping/shipping.component";
import { SpecialOrdersComponent } from "./dashboard/a-sample dashboard/shipping/special-orders/special-orders.component";
import { DashboardComponent } from "./dashboard/dashboard.component";
import { FastSlowListComponent } from "./dashboard/list/fast-slow-list/fast-slow-list.component";
import { LandingPageComponent } from "./landing-page/landing-page.component";

const routes: Routes = [{
  path: '',
  component: DashboardComponent,
  pathMatch: 'full'

},


{ path: 'bin-full',component: BinFullComponent},
{path: 'bin-empty', component: BinEmptyComponent},

{path: 'asn', component: AsnComponent},
{path: 'received', component: ReceivedComponent},

{path: 'delivery', component: DeliveryComponent},

{path: 'shipping', component: ShippingComponent},

{path: 'special-order', component: SpecialOrdersComponent},

{path: 'sku', component: SkuListComponent},
{path: 'fastslowList/:code', component: FastSlowListComponent},


{path: 'landingPage', component: LandingPageComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
