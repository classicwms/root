import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonFieldModule } from "src/app/common-field/common-field.module";
import { SharedModule } from "src/app/shared/shared.module";
import { ItemTypeComponent } from "./item-type/item-type.component";
import { SetupProductRoutingModule } from "./setup-product-routing.module";
import { ItemGroupComponent } from './item-group/item-group.component';
import { BatchSerialComponent } from './batch-serial/batch-serial.component';
import { VariantComponent } from './variant/variant.component';
import { BarcodeComponent } from './barcode/barcode.component';
import { ProductSelectionComponent } from './product-selection/product-selection.component';
import { NgMultiSelectDropDownModule } from "ng-multiselect-dropdown";



@NgModule({
  declarations: [
    ItemTypeComponent,
    ItemGroupComponent,
    BatchSerialComponent,
    VariantComponent,
    BarcodeComponent,
    ProductSelectionComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    SetupProductRoutingModule,
    CommonFieldModule,
    NgMultiSelectDropDownModule.forRoot(),
  ],
  exports: [
    ItemTypeComponent,
    ItemGroupComponent,
    BatchSerialComponent,
    VariantComponent,
    BarcodeComponent,
  ]
})
export class SetupProductModule { }
