import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OtherMastersRoutingModule } from './other-masters-routing.module';
import { HandlingEquipmentComponent } from './handling-equipment/handling-equipment.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { EquipmentNewComponent } from './handling-equipment/equipment-new/equipment-new.component';
import { HandlingUnitComponent } from './handling-unit/handling-unit.component';
import { UnitNewComponent } from './handling-unit/unit-new/unit-new.component';
import { BomComponent } from './bom/bom.component';
import { BomNewComponent } from './bom/bom-new/bom-new.component';
import { BusinessPartnerComponent } from './business-partner/business-partner.component';
import { BusinessNewComponent } from './business-partner/business-new/business-new.component';
import { PackingMaterialComponent } from './packing-material/packing-material.component';
import { PackingNewComponent } from './packing-material/packing-new/packing-new.component';


@NgModule({
  declarations: [
    HandlingEquipmentComponent,
    EquipmentNewComponent,
    HandlingUnitComponent,
    UnitNewComponent,
    BomComponent,
    BomNewComponent,
    BusinessPartnerComponent,
    BusinessNewComponent,
    PackingMaterialComponent,
    PackingNewComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    OtherMastersRoutingModule,
    CommonFieldModule,
  ],

exports:[
  HandlingEquipmentComponent,
  EquipmentNewComponent,
    HandlingUnitComponent,
    UnitNewComponent,
    BomComponent,
    BomNewComponent,
    BusinessPartnerComponent,
    BusinessNewComponent,
    PackingMaterialComponent,
    PackingNewComponent
]
})
export class OtherMastersModule { }
