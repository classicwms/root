import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrderDetailsComponent } from './order-details/order-details.component';
import { OrderDetailsNewComponent } from './order-details/order-details-new/order-details-new.component';
import { CuttingMainComponent } from './cutting/cutting-main/cutting-main.component';
import { MoldingMainComponent } from './molding/molding-main/molding-main.component';
import { FabricationMainComponent } from './fabrication/fabrication-main/fabrication-main.component';
import { AssemblyMainComponent } from './assembly/assembly-main/assembly-main.component';
import { DeliveryMainComponent } from './delivery/delivery-main/delivery-main.component';

const routes: Routes = [
  {path: 'orderDetails', component: OrderDetailsComponent},
{path: 'orderNew',component: OrderDetailsNewComponent },
{path: 'cutting', component: CuttingMainComponent},
{path: 'modling', component: MoldingMainComponent},
{path: 'fabrication', component: FabricationMainComponent},
{path: 'assembly', component: AssemblyMainComponent},
{path: 'delivery', component: DeliveryMainComponent},
// {
//   path: 'companyNew/:code',
//   component: CompanyNewComponent
// },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManufacturingRoutingModule { }
