import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonFieldModule } from "src/app/common-field/common-field.module";
import { SharedModule } from "src/app/shared/shared.module";
import { AltUomComponent } from "./alt-uom/alt-uom.component";
import { BasicData1Component } from "./basic-data1/basic-data1.component";
import { BasicData2Component } from "./basic-data2/basic-data2.component";
import { BatchPopupComponent } from "./batch-serial/batch-popup/batch-popup.component";
import { BatchSerialComponent } from "./batch-serial/batch-serial.component";
import { MastersRoutingModule } from "./masters-routing.module";
import { PackingComponent } from "./packing/packing.component";
import { PartnerComponent } from "./partner/partner.component";
import { StorageUnitComponent } from "./storage-unit/storage-unit.component";
import { StrategiesComponent } from "./strategies/strategies.component";
import { VariantComponent } from "./variant/variant.component";
import { SelectionComponent } from './selection/selection.component';
import { SelectionPopupComponent } from './selection/selection-popup/selection-popup.component';




@NgModule({
  declarations: [
    BasicData1Component,
    AltUomComponent,
    VariantComponent,
    StrategiesComponent,
    PartnerComponent,
    StorageUnitComponent,
    PackingComponent,
    BatchSerialComponent,
    BasicData2Component,
    BatchPopupComponent,
    SelectionComponent,
    SelectionPopupComponent
  ],
  imports: [
    CommonModule,
    MastersRoutingModule,
    SharedModule,
    CommonFieldModule,
  ],
  exports:[
    BasicData1Component,
    AltUomComponent,
    VariantComponent,
    StrategiesComponent,
    PartnerComponent,
    StorageUnitComponent,
    PackingComponent,
    BatchSerialComponent
  ]
})
export class MastersModule { }
