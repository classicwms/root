import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InhouseTransferRoutingModule } from './inhouse-transfer-routing.module';
import { InhouseMainComponent } from './inhouse-main/inhouse-main.component';
import { BintobinMainComponent } from './bintobin-main/bintobin-main.component';
import { SkuSkuComponent } from './sku-sku/sku-sku.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { StockDetailsComponent } from './sku-sku/stock-details/stock-details.component';
import { ReceiptDetailsComponent } from './bintobin-main/receipt-details/receipt-details.component';
import { InhouseNewComponent } from './inhouse-new/inhouse-new.component';
import { StockTypeComponent } from './inhouse-new/stock-type/stock-type.component';
import { SkutoskuComponent } from './inhouse-new/skutosku/skutosku.component';
import { Bintobin2Component } from './inhouse-new/bintobin2/bintobin2.component';


@NgModule({
  declarations: [
    InhouseMainComponent,
    BintobinMainComponent,
    SkuSkuComponent,
    StockDetailsComponent,
    ReceiptDetailsComponent,
    InhouseNewComponent,
    StockTypeComponent,
    SkutoskuComponent,
    Bintobin2Component
  ],
  imports: [
    CommonModule,
    InhouseTransferRoutingModule,
    SharedModule,
    CommonFieldModule
  ],
  exports: [
    InhouseMainComponent,
    BintobinMainComponent,
    SkuSkuComponent
  ]
})
export class InhouseTransferModule { }
