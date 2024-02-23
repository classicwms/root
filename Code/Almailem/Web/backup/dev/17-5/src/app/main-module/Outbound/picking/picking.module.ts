import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PickingRoutingModule } from './picking-routing.module';
import { PickingMainComponent } from './picking-main/picking-main.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrderManagementModule } from '../order-management/order-management.module';
import { PickingConfirmComponent } from './picking-confirm/picking-confirm.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { UpdatePickerComponent } from './picking-main/update-picker/update-picker.component';


@NgModule({
  declarations: [
    PickingMainComponent,
    PickingConfirmComponent,
    UpdatePickerComponent
  ],
  imports: [
    CommonModule,
    PickingRoutingModule,
    SharedModule,
    CommonFieldModule,
    OrderManagementModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],
  exports: [
    PickingMainComponent
  ]
})
export class PickingModule { }
