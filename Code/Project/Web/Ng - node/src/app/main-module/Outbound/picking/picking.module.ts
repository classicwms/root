import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PickingRoutingModule } from './picking-routing.module';
import { PickingMainComponent } from './picking-main/picking-main.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrderManagementModule } from '../order-management/order-management.module';
import { PickingConfirmComponent } from './picking-confirm/picking-confirm.component';


@NgModule({
  declarations: [
    PickingMainComponent,
    PickingConfirmComponent
  ],
  imports: [
    CommonModule,
    PickingRoutingModule,
    SharedModule,
    CommonFieldModule,
    OrderManagementModule
  ],
  exports: [
    PickingMainComponent
  ]
})
export class PickingModule { }
