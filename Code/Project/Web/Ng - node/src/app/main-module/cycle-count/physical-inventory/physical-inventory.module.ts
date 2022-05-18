import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonFieldModule } from "src/app/common-field/common-field.module";
import { SharedModule } from "src/app/shared/shared.module";
import { PhysicalAssignComponent } from "./physical-assign/physical-assign.component";
import { PhysicalCountingComponent } from "./physical-counting/physical-counting.component";
import { PhysicalCreateComponent } from "./physical-create/physical-create.component";
import { PhysicalInventoryRoutingModule } from "./physical-inventory-routing.module";
import { PhysicalMainComponent } from "./physical-main/physical-main.component";
import { AnnualCreateComponent } from './physical-main/annual-create/annual-create.component';
import { AssignPhysicalComponent } from './physical-create/assign-physical/assign-physical.component';



@NgModule({
  declarations: [
    PhysicalMainComponent,
    PhysicalCreateComponent,
    PhysicalAssignComponent,
    PhysicalCountingComponent,
    AnnualCreateComponent,
    AssignPhysicalComponent
  ],
  imports: [
    CommonModule,
    PhysicalInventoryRoutingModule,
    SharedModule,
    CommonFieldModule
  ],
  exports:[
    PhysicalMainComponent,
    PhysicalCreateComponent,
    PhysicalAssignComponent,
    PhysicalCountingComponent
  ]
})
export class PhysicalInventoryModule { }
