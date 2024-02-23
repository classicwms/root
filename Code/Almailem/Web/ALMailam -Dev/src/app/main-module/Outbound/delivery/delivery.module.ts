import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DeliveryRoutingModule } from './delivery-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrderManagementModule } from '../order-management/order-management.module';
import { DeliveryMainComponent } from './delivery-main/delivery-main.component';
import { DeliveryConfirmComponent } from './delivery-confirm/delivery-confirm.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { DeliveryTab2Component } from './delivery-tab2/delivery-tab2.component';
import { DeliveryUpdateComponent } from './delivery-main/delivery-update/delivery-update.component';


@NgModule({
  declarations: [
    DeliveryMainComponent,
    DeliveryConfirmComponent,
    
    DeliveryTab2Component,
          DeliveryUpdateComponent
  ],
  imports: [
    CommonModule,
    DeliveryRoutingModule,
    SharedModule,
    CommonFieldModule,
    OrderManagementModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],
  exports:[
    DeliveryMainComponent,
    DeliveryTab2Component
  ]
})
export class DeliveryModule { }
