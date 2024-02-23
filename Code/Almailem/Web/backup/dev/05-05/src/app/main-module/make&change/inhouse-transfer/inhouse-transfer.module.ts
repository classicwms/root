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
import { SkuPopupComponent } from './inhouse-new/sku-popup/sku-popup.component';
import { StockPopupComponent } from './inhouse-new/stock-popup/stock-popup.component';
import { BinPopupComponent } from './inhouse-new/bin-popup/bin-popup.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { InhouseLinesComponent } from './inhouse-main/inhouse-lines/inhouse-lines.component';
import { MakeandchangeModule } from '../makeandchange.module';
import { MakeandchangeTabComponent } from './inhouse-main/makeandchange-tab/makeandchange-tab.component';


@NgModule({
  declarations: [
    InhouseMainComponent,
    BintobinMainComponent,
    SkuSkuComponent,
    StockDetailsComponent,
    ReceiptDetailsComponent,
    InhouseNewComponent,
    SkuPopupComponent,
    StockPopupComponent,
    BinPopupComponent,
    InhouseLinesComponent,
    MakeandchangeTabComponent
  ],
  imports: [
    CommonModule,
    InhouseTransferRoutingModule,
    SharedModule,
    CommonFieldModule,
    MakeandchangeModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],
  exports: [
    InhouseMainComponent,
    BintobinMainComponent,
    SkuSkuComponent,
    MakeandchangeTabComponent
  ]
})
export class InhouseTransferModule { }
