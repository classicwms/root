import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonFieldModule } from "src/app/common-field/common-field.module";
import { SharedModule } from "src/app/shared/shared.module";
import { PhysicalAssignComponent } from "./physical-assign/physical-assign.component";
import { PhysicalCreateComponent } from "./physical-create/physical-create.component";
import { PhysicalInventoryRoutingModule } from "./physical-inventory-routing.module";
import { PhysicalMainComponent } from "./physical-main/physical-main.component";
import { AnnualCreateComponent } from './physical-main/annual-create/annual-create.component';
import { AssignPhysicalComponent } from './physical-create/assign-physical/assign-physical.component';
import { VariantAnnualComponent } from './variant-annual/variant-annual.component';
import { VarianteditAnnualComponent } from './variant-annual/variantedit-annual/variantedit-annual.component';
import { NgMultiSelectDropDownModule } from "ng-multiselect-dropdown";
import { AngularMultiSelectModule } from "angular2-multiselect-dropdown";
import { PhysicalTabComponent } from './physical-tab/physical-tab.component';



@NgModule({
  declarations: [
    PhysicalMainComponent,
    PhysicalCreateComponent,
    PhysicalAssignComponent,
    AnnualCreateComponent,
    AssignPhysicalComponent,
    VariantAnnualComponent,
    VarianteditAnnualComponent,
    PhysicalTabComponent
  ],
  imports: [
    CommonModule,
    PhysicalInventoryRoutingModule,
    SharedModule,
    CommonFieldModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule
  ],
  exports:[
    PhysicalMainComponent,
    PhysicalCreateComponent,
    PhysicalAssignComponent,
  ]
})
export class PhysicalInventoryModule { }
