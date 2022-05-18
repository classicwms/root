import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AssignmentRoutingModule } from './assignment-routing.module';
import { AssignmentMainComponent } from './assignment-main/assignment-main.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrderManagementModule } from '../order-management/order-management.module';
import { AssignPickerComponent } from './assignment-main/assign-picker/assign-picker.component';


@NgModule({
  declarations: [
    AssignmentMainComponent,
    AssignPickerComponent
  ],
  imports: [
    CommonModule,
    AssignmentRoutingModule,
    SharedModule,
    CommonFieldModule,
    OrderManagementModule
  ],
  exports: [
    AssignmentMainComponent
  ]
})
export class AssignmentModule { }
