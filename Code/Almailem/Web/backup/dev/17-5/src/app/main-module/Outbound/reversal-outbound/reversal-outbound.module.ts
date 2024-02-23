import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReversalOutboundRoutingModule } from './reversal-outbound-routing.module';
import { ReversaloutboundMainComponent } from './reversaloutbound-main/reversaloutbound-main.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { ReversalOutboundPopupComponent } from './reversal-outbound-popup/reversal-outbound-popup.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { OrderManagementModule } from '../order-management/order-management.module';


@NgModule({
  declarations: [
    ReversaloutboundMainComponent,
    ReversalOutboundPopupComponent
  ],
  imports: [
    CommonModule,
    ReversalOutboundRoutingModule,
    SharedModule,
    CommonFieldModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
    OrderManagementModule
  ],
  exports:[
    ReversaloutboundMainComponent
  ]
})
export class ReversalOutboundModule { }
