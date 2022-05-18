import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboundConfirmRoutingModule } from './inbound-confirm-routing.module';
import { PutawayDetailsComponent } from './inbound-create/putaway-details/putaway-details.component';
import { PalletdetailsComponent } from './inbound-create/palletdetails/palletdetails.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';


@NgModule({
  declarations: [
    PutawayDetailsComponent,
    PalletdetailsComponent
  ],
  imports: [
    CommonModule,
    InboundConfirmRoutingModule,
    SharedModule,
    CommonFieldModule,
  ],
  exports:[
    PutawayDetailsComponent,
    PalletdetailsComponent
  ]
})
export class InboundConfirmModule { }
