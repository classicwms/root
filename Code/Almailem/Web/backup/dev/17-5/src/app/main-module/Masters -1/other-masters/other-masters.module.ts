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
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { ChilditemcodeComponent } from './bom/bom-new/childitemcode/childitemcode.component';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { OthermasterTabComponent } from './othermaster-tab/othermaster-tab.component';
// import { CompanyComponent } from './company/company.component';
// import { CompanyNewComponent } from './company/company-new/company-new.component';


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
    PackingNewComponent,
    ChilditemcodeComponent,
    OthermasterTabComponent,
    // CompanyComponent,
    // CompanyNewComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    OtherMastersRoutingModule,
    CommonFieldModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],

  exports: [
    HandlingEquipmentComponent,
    EquipmentNewComponent,
    HandlingUnitComponent,
    UnitNewComponent,
    BomComponent,
    BomNewComponent,
    BusinessPartnerComponent,
    BusinessNewComponent,
    PackingMaterialComponent,
    PackingNewComponent,
    OthermasterTabComponent,
    //  CompanyComponent
  ]
})
export class OtherMastersModule { }
