import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboundConfirmRoutingModule } from './inbound-confirm-routing.module';
import { PutawayDetailsComponent } from './inbound-create/putaway-details/putaway-details.component';
import { PalletdetailsComponent } from './inbound-create/palletdetails/palletdetails.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { ReplaceASNComponent } from './replace-asn/replace-asn.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { ReadyToConfirmComponent } from './ready-to-confirm/ready-to-confirm.component';
import { InboundModule } from '../inbound.module';


@NgModule({
  declarations: [
    PutawayDetailsComponent,
    PalletdetailsComponent,
    ReplaceASNComponent,
    ReadyToConfirmComponent
  ],
  imports: [
    CommonModule,
    InboundConfirmRoutingModule,
    SharedModule,
    InboundModule,
    CommonFieldModule,  NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule
  ],
  exports:[
    PutawayDetailsComponent,
    PalletdetailsComponent
  ]
})
export class InboundConfirmModule { }
