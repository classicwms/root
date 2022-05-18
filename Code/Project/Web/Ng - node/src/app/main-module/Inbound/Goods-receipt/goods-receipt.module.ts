import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GoodsReceiptRoutingModule } from './goods-receipt-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { InboundModule } from '../inbound.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { GoodsreceiptMainComponent } from './goodsreceipt-main/goodsreceipt-main.component';
import { AcceptedQtyComponent } from './putawaycreation-create/accepted-qty/accepted-qty.component';
import { BarcodesComponent } from './putawaycreation-create/barcodes/barcodes.component';
import { DamageQtyComponent } from './putawaycreation-create/damage-qty/damage-qty.component';
import { PacksComponent } from './putawaycreation-create/packs/packs.component';
import { PutawayDetailsComponent } from './putawaycreation-create/putaway-details/putaway-details.component';
import { PutawaycreationCreateComponent } from './putawaycreation-create/putawaycreation-create.component';
import { InboundTabbarComponent } from '../inbound-tabbar/inbound-tabbar.component';


@NgModule({
  declarations: [
    GoodsreceiptMainComponent,
    PutawaycreationCreateComponent,
    AcceptedQtyComponent,
    BarcodesComponent,
    DamageQtyComponent,
    PacksComponent,
    PutawayDetailsComponent,
    

  ],
  imports: [
    CommonModule,
    GoodsReceiptRoutingModule,
    SharedModule,
    CommonFieldModule,
    InboundModule,
  ], 
  exports:
  [
    GoodsreceiptMainComponent,
    PutawaycreationCreateComponent,
    AcceptedQtyComponent,
    BarcodesComponent,
    DamageQtyComponent,
    PacksComponent,
    PutawayDetailsComponent
  ]
})
export class GoodsReceiptModule { }
