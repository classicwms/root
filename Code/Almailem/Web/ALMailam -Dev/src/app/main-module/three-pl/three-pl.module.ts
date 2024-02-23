import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ThreePlRoutingModule } from './three-pl-routing.module';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { ProformaComponent } from './proforma/proforma.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { ProformaNewComponent } from './proforma/proforma-new/proforma-new.component';
import { InvoiceComponent } from './invoice/invoice.component';
import { InvoiceNewComponent } from './invoice/invoice-new/invoice-new.component';


@NgModule({
  declarations: [
    ProformaComponent,
    ProformaNewComponent,
    InvoiceComponent,
    InvoiceNewComponent
  ],
  imports: [
    CommonModule,
    ThreePlRoutingModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
    SharedModule,
    CommonFieldModule,
  ],
  exports: [
    ProformaComponent
  ]
})
export class ThreePlModule { }
