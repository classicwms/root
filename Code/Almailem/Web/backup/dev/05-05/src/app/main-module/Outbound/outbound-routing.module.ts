import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AssignmentMainComponent } from './assignment/assignment-main/assignment-main.component';
import { DeliveryConfirmComponent } from './delivery/delivery-confirm/delivery-confirm.component';
import { DeliveryMainComponent } from './delivery/delivery-main/delivery-main.component';
import { DeliveryTab2Component } from './delivery/delivery-tab2/delivery-tab2.component';
import { OrdermanagementMainComponent } from './order-management/ordermanagement-main/ordermanagement-main.component';
import { PickupCreationComponent } from './order-management/pickup-creation/pickup-creation.component';
import { ReallocationComponent } from './order-management/reallocation/reallocation.component';
import { PickingConfirmComponent } from './picking/picking-confirm/picking-confirm.component';
import { PickingMainComponent } from './picking/picking-main/picking-main.component';
import { PreoutboundCreateComponent } from './preoutbound/preoutbound-create/preoutbound-create.component';
import { PreoutboundMainComponent } from './preoutbound/preoutbound-main/preoutbound-main.component';
import { QualityConfirmComponent } from './quality/quality-confirm/quality-confirm.component';
import { QualityMainComponent } from './quality/quality-main/quality-main.component';
import { ReversaloutboundMainComponent } from './reversal-outbound/reversaloutbound-main/reversaloutbound-main.component';

const routes: Routes = [
  {
    path: 'order-management',
    component: OrdermanagementMainComponent
  },
  {
    path: 'preoutbound',
    component: PreoutboundMainComponent
  },
  {
    path: 'preoutbound-create/:code',
    component: PreoutboundCreateComponent
  },
  {
    path: 'assignment',
    component: AssignmentMainComponent
  },

  {
    path: 'pickup-main',
    component: PickingMainComponent
  },
  {
    path: 'pickup-confirm/:code',
    component: PickingConfirmComponent
  },
  {
    path: 'quality-main',
    component: QualityMainComponent
  },
  {
    path: 'quality-confirm/:code',
    component: QualityConfirmComponent
  },
  {
    path: 'delivery-main',
    component: DeliveryMainComponent
  },
  {
    path: 'delivery-main1',
    component: DeliveryTab2Component
  },
  {
    path: 'delivery-confirm/:code',
    component: DeliveryConfirmComponent
  },
  {
    path: 'reversal',
    component: ReversaloutboundMainComponent
  },
  {
    path: '',
    redirectTo: 'order-management'
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OutboundRoutingModule { }
