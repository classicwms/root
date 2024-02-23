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
import { Packdetails1Component } from './Item receipt/item-create/packdetails1/packdetails1.component';
import { AssignHEComponent } from './Item receipt/item-create/assign-he/assign-he.component';
import { ReversalComponent } from './reversal/reversal.component';
import { ReversalPopupComponent } from './reversal/reversal-popup/reversal-popup.component';
import { CasenoPopupComponent } from './Item receipt/item-main/caseno-popup/caseno-popup.component';
import { ContainerreceiptCreateComponent } from './Container-receipt/containerreceipt-create/containerreceipt-create.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { InventoryDetailsComponent } from './Item receipt/item-create/inventory-details/inventory-details.component';
import { ReleasedQtyComponent } from './Item receipt/item-create/released-qty/released-qty.component';
import { CreateBarcodeComponent } from './Item receipt/item-create/create-barcode/create-barcode.component';
import { LabelGenerateComponent } from './Item receipt/item-create/label-generate/label-generate.component';
import { EditpopupComponent } from './Item receipt/item-create/editpopup/editpopup.component';
import { ItemMainEditComponent } from './Item receipt/item-main/item-main-edit/item-main-edit.component';
import { PutawayheaderComponent } from './putaway/putawayheader/putawayheader.component';
import { ReversalEditComponent } from './reversal/reversal-edit/reversal-edit.component';
@NgModule({
  declarations: [
    InboundTabbarComponent,
    PutawayMainComponent,
    InboundconfirmMainComponent,
    InboundCreateComponent,
    ItemMainComponent,
    ItemCreateComponent,
    Packdetails1Component,
    AssignHEComponent,
    ReversalComponent,
    ReversalPopupComponent,
    CasenoPopupComponent, ContainerreceiptCreateComponent, InventoryDetailsComponent, ReleasedQtyComponent, CreateBarcodeComponent, LabelGenerateComponent, EditpopupComponent, ItemMainEditComponent, PutawayheaderComponent, ReversalEditComponent
  ],
  
  imports: [
    CommonModule,
    InboundRoutingModule,
    SharedModule,
    CommonFieldModule,
    AngularMultiSelectModule,
    NgMultiSelectDropDownModule.forRoot(),
  ],
  exports: [ 
    InboundTabbarComponent
  ]
})
export class InboundModule { }
