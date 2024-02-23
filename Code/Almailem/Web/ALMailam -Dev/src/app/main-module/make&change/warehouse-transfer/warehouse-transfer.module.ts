import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { WarehouseTransferRoutingModule } from './warehouse-transfer-routing.module';
import { WarehouseMainComponent } from './warehouse-main/warehouse-main.component';
import { WarehouseCreateComponent } from './warehouse-create/warehouse-create.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';


@NgModule({
  declarations: [
    WarehouseMainComponent,
    WarehouseCreateComponent
  ],
  imports: [
    CommonModule,
    WarehouseTransferRoutingModule,
    SharedModule,
    CommonFieldModule
  ],
  exports: [
    WarehouseMainComponent,
    WarehouseCreateComponent
  ]
})
export class WarehouseTransferModule { }
