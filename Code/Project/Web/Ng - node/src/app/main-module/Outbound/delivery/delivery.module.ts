import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DeliveryRoutingModule } from './delivery-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrderManagementModule } from '../order-management/order-management.module';
import { DeliveryMainComponent } from './delivery-main/delivery-main.component';
import { DeliveryConfirmComponent } from './delivery-confirm/delivery-confirm.component';


@NgModule({
  declarations: [
    DeliveryMainComponent,
    DeliveryConfirmComponent
  ],
  imports: [
    CommonModule,
    DeliveryRoutingModule,
    SharedModule,
    CommonFieldModule,
    OrderManagementModule
  ],
  exports:[
    DeliveryMainComponent
  ]
})
export class DeliveryModule { }
