import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConsignmentsComponent } from './consignments/consignments.component';
import { PickupComponent } from './pickup/pickup.component';
import { DeliveryComponent } from './delivery/delivery.component';
import { ConsignmentsEditComponent } from './consignments/consignments-edit/consignments-edit.component';
import { ConsignmentsOpenComponent } from './consignments/consignments-open/consignments-open.component';
import { DeliveryOpenComponent } from './delivery/delivery-open/delivery-open.component';
import { ManifestComponent } from './manifest/manifest/manifest.component';
import { ManifestEditComponent } from './manifest/manifest/manifest-edit/manifest-edit.component';
import { PickupEditComponent } from './pickup/pickup-edit/pickup-edit.component';
import { DeliveredCompletedComponent } from './delivered-completed/delivered-completed.component';
import { CompletedLineComponent } from './delivered-completed/completed-line/completed-line.component';
import { ReDeliveryComponent } from './re-delivery/re-delivery.component';
import { ReDeliveryLineComponent } from './re-delivery/re-delivery-line/re-delivery-line.component';
import { ReturnedLinesComponent } from './returned/returned-lines/returned-lines.component';
import { ReturnedComponent } from './returned/returned.component';



const routes: Routes = [
  {
    path: 'consignment',
    component: ConsignmentsComponent
  },
  {
    path: 'pickup',
    component: PickupComponent
  },
  {
    path: 'pickupEdit/:code',
    component: PickupEditComponent
  },
  {
    path: 'delivery',
    component: DeliveryComponent
  },
  {
    path: 'delivered',
    component: DeliveredCompletedComponent
  }, 
  {
    path: 'deliveryCompoleted/:code',
    component: CompletedLineComponent
  },
  {
    path: 'consignmentEdit/:code',
    component: ConsignmentsEditComponent
  },
  {
    path: 'consignment-open',
    component: ConsignmentsOpenComponent
  }, 

    {
    path: 'redeliveryLine/:code',
    component: ReDeliveryLineComponent
  },
  {
    path: 'returned',
    component: ReturnedComponent
  },

  {
    path: 'returnedLine/:code',
    component: ReturnedLinesComponent
  },
  {
    path: 'redelivery',
    component: ReDeliveryComponent
  },

  {
    path: 'delivery-open/:code',
    component: DeliveryOpenComponent
  },
  
  {
    path: 'manifestEdit/:code',
    component: ManifestEditComponent
  },
  {
    path: 'manifest',
    component: ManifestComponent
  },


];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DeliveryManagementRoutingModule { }
