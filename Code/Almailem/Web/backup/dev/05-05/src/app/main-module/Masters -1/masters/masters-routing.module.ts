import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { AltUomComponent } from "./alt-uom/alt-uom.component";
import { BasicData1Component } from "./basic-data1/basic-data1.component";
import { BasicData2Component } from "./basic-data2/basic-data2.component";
import { BatchSerialComponent } from "./batch-serial/batch-serial.component";
import { PackingComponent } from "./packing/packing.component";
import { PartnerComponent } from "./partner/partner.component";
import { SelectionComponent } from "./selection/selection.component";
import { StorageUnitComponent } from "./storage-unit/storage-unit.component";
import { StrategiesComponent } from "./strategies/strategies.component";
import { VariantComponent } from "./variant/variant.component";



const routes: Routes = [{
  path: 'basic-data1',
  component: BasicData1Component
},
{
  path: 'basic-data1/:itemCode',
  component: BasicData1Component
},
// {
//   path: 'basic-data1/:itemCode',
//   component: BasicData1Component
// },
{
  path: 'basic-data2',
  component: BasicData2Component
},
{
  path: 'Batch / Serial',
  component: BatchSerialComponent
},
{
  path: 'Alternate-uom',
  component: AltUomComponent
},
{
  path: 'Alternate-uom/:itemCode',
  component: AltUomComponent
},
{
  path: 'Packing',
  component: PackingComponent
},
{
  path: 'Partner/:itemCode',
  component: PartnerComponent
},{
  path: 'Partner',
  component: PartnerComponent
},

{
  path: 'Palletization',
  component: StorageUnitComponent
},
{
  path: 'Strategies',
  component: StrategiesComponent
},
{
  path: 'Variant',
  component: VariantComponent
},
{
  path: 'selection',
  component: SelectionComponent
},
{
  path: '',
  redirectTo: 'Basic Data'
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MastersRoutingModule { }
