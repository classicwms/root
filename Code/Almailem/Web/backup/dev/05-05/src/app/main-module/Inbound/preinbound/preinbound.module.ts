import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PreinboundRoutingModule } from './preinbound-routing.module';
import { PreinboundMainComponent } from './preinbound-main/preinbound-main.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { PreinboundNewComponent } from './preinbound-new/preinbound-new.component';
import { GoodreceiptCreateComponent } from './goodreceipt-create/goodreceipt-create.component';
import { VariantComponent } from './goodreceipt-create/variant/variant.component';
import { ReceiptUomComponent } from './goodreceipt-create/receipt-uom/receipt-uom.component';
import { PalletComponent } from './goodreceipt-create/pallet/pallet.component';
import { CrossDockComponent } from './goodreceipt-create/cross-dock/cross-dock.component';
import { BatchComponent } from './goodreceipt-create/batch/batch.component';
import { Pallet1Component } from './goodreceipt-create/pallet1/pallet1.component';
import { AssignInvoiceComponent } from './preinbound-new/assign-invoice/assign-invoice.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { ResizeColumnDirective } from './preinbound-main/resize-column.directive';
import { PreinboundUploadComponent } from './preinbound-upload/preinbound-upload.component';
import { InboundModule } from '../inbound.module';


@NgModule({
  declarations: [
    PreinboundMainComponent,
    PreinboundNewComponent,
    GoodreceiptCreateComponent,
    VariantComponent,
    ReceiptUomComponent,
    PalletComponent,
    CrossDockComponent,
    BatchComponent,
    Pallet1Component,
    AssignInvoiceComponent,
    ResizeColumnDirective,
    PreinboundUploadComponent
  ],
  imports: [
    CommonModule,
    PreinboundRoutingModule,
    SharedModule,
    InboundModule,
    CommonFieldModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],
  exports: [
    PreinboundMainComponent,
    PreinboundNewComponent,
    GoodreceiptCreateComponent,
    VariantComponent,
    ReceiptUomComponent,
    PalletComponent,
    CrossDockComponent,
    BatchComponent,
    ResizeColumnDirective
  ],
})
export class PreinboundModule { }
