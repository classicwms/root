import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboundRoutingModule } from './inbound-routing.module';
import { InboundTabbarComponent } from './inbound-tabbar/inbound-tabbar.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { PutawayMainComponent } from './putaway/putaway-main/putaway-main.component';
import { InboundconfirmMainComponent } from './inbound-confirmation/inboundconfirm-main/inboundconfirm-main.component';
import { InboundCreateComponent } from './inbound-confirmation/inbound-create/inbound-create.component';
import { ItemMainComponent } from './Item receipt/item-main/item-main.component';
import { ItemCreateComponent } from './Item receipt/item-create/item-create.component';
import { PackDetailsComponent } from './Item receipt/item-create/pack-details/pack-details.component';
import { Packdetails1Component } from './Item receipt/item-create/packdetails1/packdetails1.component';
import { AssignHEComponent } from './Item receipt/item-create/assign-he/assign-he.component';
import { ReversalComponent } from './reversal/reversal.component';
import { ReversalMainComponent } from './reversal/reversal-main/reversal-main.component';
import { ReversalPopupComponent } from './reversal/reversal-popup/reversal-popup.component';
import { CasenoPopupComponent } from './Item receipt/item-main/caseno-popup/caseno-popup.component';


@NgModule({
  declarations: [
    InboundTabbarComponent,
    PutawayMainComponent,
    InboundconfirmMainComponent,
    InboundCreateComponent,
    ItemMainComponent,
    ItemCreateComponent,
    PackDetailsComponent,
    Packdetails1Component,
    AssignHEComponent,
    ReversalComponent,
    ReversalMainComponent,
    ReversalPopupComponent,
    CasenoPopupComponent,
  ],
  imports: [
    CommonModule,
    InboundRoutingModule,
    SharedModule,
    CommonFieldModule,
  ],
  exports:[ 
    InboundTabbarComponent
    
  ]
})
export class InboundModule { }
