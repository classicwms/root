import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DailyOrderStatusRoutingModule } from './daily-order-status-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrderstatusTabComponent } from './orderstatus-tab/orderstatus-tab.component';
import { SuccessfulOrdersComponent } from './successful-orders/successful-orders.component';
import { FailedOrdersComponent } from './failed-orders/failed-orders.component';
import { OutboundsucessordersComponent } from './outboundsucessorders/outboundsucessorders.component';
import { SuccessfulOrdersNewComponent } from './successful-orders/successful-orders-new/successful-orders-new.component';
import { OutboundstatusTabComponent } from './outboundstatus-tab/outboundstatus-tab.component';
import { OutboundfailedordersComponent } from './outboundfailedorders/outboundfailedorders.component';
import { OutboundsucessordersNewComponent } from './outboundsucessorders/outboundsucessorders-new/outboundsucessorders-new.component';


@NgModule({
  declarations: [
    OrderstatusTabComponent,
    SuccessfulOrdersComponent,
    FailedOrdersComponent,
    OutboundsucessordersComponent,
    SuccessfulOrdersNewComponent,
    OutboundstatusTabComponent,
    OutboundfailedordersComponent,
    OutboundsucessordersNewComponent
  ],
  imports: [
    CommonModule,
    DailyOrderStatusRoutingModule,
    SharedModule,
    CommonFieldModule,
  ]
})
export class DailyOrderStatusModule { }
