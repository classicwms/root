import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PreoutboundRoutingModule } from './preoutbound-routing.module';
import { PreoutboundMainComponent } from './preoutbound-main/preoutbound-main.component';
import { PreoutboundCreateComponent } from './preoutbound-create/preoutbound-create.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { InventoryQtyComponent } from './preoutbound-create/inventory-qty/inventory-qty.component';
import { OrderManagementModule } from '../order-management/order-management.module';


@NgModule({
  declarations: [
    PreoutboundMainComponent,
    PreoutboundCreateComponent,
    InventoryQtyComponent
  ],
  imports: [
    CommonModule,
    PreoutboundRoutingModule,
    SharedModule,
    CommonFieldModule,
    OrderManagementModule
  ],
  exports: [
    PreoutboundMainComponent,
    PreoutboundCreateComponent,
    InventoryQtyComponent
  ]
})
export class PreoutboundModule { }
