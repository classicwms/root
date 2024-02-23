import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ThreeplRoutingModule } from './threepl-routing.module';
import { ThreeplComponent } from './threepl/threepl.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { PricelistComponent } from './pricelist/pricelist.component';
import { PricelistNewComponent } from './pricelist/pricelist-new/pricelist-new.component';
import { BillingComponent } from './billing/billing.component';
import { BillingNewComponent } from './billing/billing-new/billing-new.component';
import { PricelistassignmentComponent } from './pricelistassignment/pricelistassignment.component';
import { PricelistassignmentNewComponent } from './pricelistassignment/pricelistassignment-new/pricelistassignment-new.component';
import { CbminboundComponent } from './cbminbound/cbminbound.component';
import { CbminboundNewComponent } from './cbminbound/cbminbound-new/cbminbound-new.component';
import { ProformainvoiceheaderComponent } from './proformainvoiceheader/proformainvoiceheader.component';
import { ProformainvoiceheaderNewComponent } from './proformainvoiceheader/proformainvoiceheader-new/proformainvoiceheader-new.component';
import { ProformainvoicelineComponent } from './proformainvoiceline/proformainvoiceline.component';
import { ProformainvoicelineNewComponent } from './proformainvoiceline/proformainvoiceline-new/proformainvoiceline-new.component';
import { InvoiceheaderComponent } from './invoiceheader/invoiceheader.component';
import { InvoiceheaderNewComponent } from './invoiceheader/invoiceheader-new/invoiceheader-new.component';
import { InvoicelinesComponent } from './invoicelines/invoicelines.component';
import { InvoicelinesNewComponent } from './invoicelines/invoicelines-new/invoicelines-new.component';
import { InquiryComponent } from './inquiry/inquiry.component';
import { InquiryNewComponent } from './inquiry/inquiry-new/inquiry-new.component';
import { QuotationheaderComponent } from './quotationheader/quotationheader.component';
import { QuotationheaderNewComponent } from './quotationheader/quotationheader-new/quotationheader-new.component';
import { QuotationlineComponent } from './quotationline/quotationline.component';
import { QuotationlineNewComponent } from './quotationline/quotationline-new/quotationline-new.component';
import { AgreementComponent } from './agreement/agreement.component';
import { AgreementNewComponent } from './agreement/agreement-new/agreement-new.component';


@NgModule({
  declarations: [
    ThreeplComponent,
    PricelistComponent,
    PricelistNewComponent,
    BillingComponent,
    BillingNewComponent,
    PricelistassignmentComponent,
    PricelistassignmentNewComponent,
    CbminboundComponent,
    CbminboundNewComponent,
    ProformainvoiceheaderComponent,
    ProformainvoiceheaderNewComponent,
    ProformainvoicelineComponent,
    ProformainvoicelineNewComponent,
    InvoiceheaderComponent,
    InvoiceheaderNewComponent,
    InvoicelinesComponent,
    InvoicelinesNewComponent,
    InquiryComponent,
    InquiryNewComponent,
    QuotationheaderComponent,
    QuotationheaderNewComponent,
    QuotationlineComponent,
    QuotationlineNewComponent,
    AgreementComponent,
    AgreementNewComponent
  ],
  imports: [
    CommonModule,
    ThreeplRoutingModule,
    SharedModule,
  ]
})
export class ThreeplModule { }
