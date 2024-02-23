import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GoodsReceiptRoutingModule } from './goods-receipt-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { InboundModule } from '../inbound.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { GoodsreceiptMainComponent } from './goodsreceipt-main/goodsreceipt-main.component';
import { PutawayDetailsComponent } from './putawaycreation-create/putaway-details/putaway-details.component';
import { PutawaycreationCreateComponent } from './putawaycreation-create/putawaycreation-create.component';
import { InboundTabbarComponent } from '../inbound-tabbar/inbound-tabbar.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';


@NgModule({
  declarations: [
    GoodsreceiptMainComponent,
    PutawaycreationCreateComponent,

    PutawayDetailsComponent,


  ],
  imports: [
    CommonModule,
    GoodsReceiptRoutingModule,
    SharedModule,
    CommonFieldModule,
    InboundModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],
  exports:
    [
      GoodsreceiptMainComponent,
      PutawaycreationCreateComponent,

      PutawayDetailsComponent
    ]
})
export class GoodsReceiptModule { }
