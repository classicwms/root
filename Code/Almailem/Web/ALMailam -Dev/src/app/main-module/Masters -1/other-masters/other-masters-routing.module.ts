import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BomComponent } from './bom/bom.component';
import { BusinessPartnerComponent } from './business-partner/business-partner.component';
//import { CompanyComponent } from './company/company.component';
import { HandlingEquipmentComponent } from './handling-equipment/handling-equipment.component';
import { HandlingUnitComponent } from './handling-unit/handling-unit.component';
import { PackingMaterialComponent } from './packing-material/packing-material.component';
import { DockComponent } from './dock/dock.component';
import { DockNewComponent } from './dock/dock-new/dock-new.component';
import { WorkcenterComponent } from './workcenter/workcenter.component';
import { WorkcenterNewComponent } from './workcenter/workcenter-new/workcenter-new.component';
import { NumberrangeitemNewComponent } from './numberrangeitem/numberrangeitem-new/numberrangeitem-new.component';
import { NumberrangeitemComponent } from './numberrangeitem/numberrangeitem.component';
import { CyclecountschedularComponent } from './cyclecountschedular/cyclecountschedular.component';
import { CyclecountschedularNewComponent } from './cyclecountschedular/cyclecountschedular-new/cyclecountschedular-new.component';
import { NumberrangestoragebinComponent } from './numberrangestoragebin/numberrangestoragebin.component';
import { NumberrangestoragebinNewComponent } from './numberrangestoragebin/numberrangestoragebin-new/numberrangestoragebin-new.component';
import { BomTableComponent } from './bom/bom-table/bom-table.component';

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
  }, {
    path: 'packing-material',
    component: PackingMaterialComponent
  },
  // {
  //   path: 'company',
  //   component: CompanyComponent
  // },
  {
    path: '',
    redirectTo: 'handling-equipment'
  },
  {
    path: 'dock',
    component:DockComponent
  },
  {
    path: 'dockNew/:code',
    component: DockNewComponent
  },
  {
    path: 'workcenter',
    component:WorkcenterComponent
  },
  {
    path: 'workcenterNew/:code',
    component: WorkcenterNewComponent
  },
  {
    path: 'numberrange',
    component:NumberrangeitemComponent
  },
  {
    path: 'numberrangeNew/:code',
    component: NumberrangeitemNewComponent
  },
  {
    path: 'cyclecountschedular',
    component:CyclecountschedularComponent
  },
  {
    path: 'cyclecountschedularNew/:code',
    component: CyclecountschedularNewComponent
  },
  {
    path: 'numberrangestoragebin',
    component: NumberrangestoragebinComponent
  },
  {
    path: 'numberrangestoragebinNew/:code',
    component: NumberrangestoragebinNewComponent
  },
  {
    path: 'bom-table/:code',
    component: BomTableComponent
  },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OtherMastersRoutingModule { }
