import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FailedOrdersComponent } from './failed-orders/failed-orders.component';
import { OutboundfailedordersComponent } from './outboundfailedorders/outboundfailedorders.component';
import { OutboundsucessordersComponent } from './outboundsucessorders/outboundsucessorders.component';
import { SuccessfulOrdersComponent } from './successful-orders/successful-orders.component';

const routes: Routes = [
  {
    path: 'successfulOrder',
    component: SuccessfulOrdersComponent
  },
  {
    path: 'failedOrders',
    component: FailedOrdersComponent
  },
  {
    path: 'outboundsucessorders',
    component: OutboundsucessordersComponent
  },
  {
    path: 'outboundfailedsucessorders',
    component: OutboundfailedordersComponent
  },
  {
    path: '',
    component: SuccessfulOrdersComponent
  },
  
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DailyOrderStatusRoutingModule { }
