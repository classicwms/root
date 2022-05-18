import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BomComponent } from './bom/bom.component';
import { BusinessPartnerComponent } from './business-partner/business-partner.component';
import { HandlingEquipmentComponent } from './handling-equipment/handling-equipment.component';
import { HandlingUnitComponent } from './handling-unit/handling-unit.component';
import { PackingMaterialComponent } from './packing-material/packing-material.component';

const routes: Routes = [
  {
    path: 'handling-equipment',
    component: HandlingEquipmentComponent
  },
  {
    path: 'handling-unit',
    component: HandlingUnitComponent
  },
  {
    path: 'bom',
    component: BomComponent
  },
  {
    path: 'business-partner',
    component: BusinessPartnerComponent
  },{
    path: 'packing-material',
    component: PackingMaterialComponent
  },
  
  {
    path: '',
    redirectTo: 'handling-equipment'
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OtherMastersRoutingModule { }
