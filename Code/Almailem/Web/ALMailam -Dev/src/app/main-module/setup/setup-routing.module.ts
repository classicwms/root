import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CompanyComponent } from './company/company.component';
import { FloorComponent } from './floor/floor.component';
import { PlantComponent } from './plant/plant.component';
import { SetupSelectionComponent } from './setup-selection/setup-selection.component';
import { StorageComponent } from './storage/storage.component';
import { WarehouseComponent } from './warehouse/warehouse.component';

const routes: Routes = [{
  path: 'company',
  component: CompanyComponent
},
{
  path: 'company/:companyId',
  component: CompanyComponent
},
{
  path: 'plant',
  component: PlantComponent
},
{
  path: 'plant/:companyId',
  component: PlantComponent
},
{
  path: 'warehouse',
  component: WarehouseComponent
},
{
  path: 'warehouse/:companyId/:plantId',
  component: WarehouseComponent
},
{
  path: 'floor',
  component: FloorComponent
},
{
  path: 'floor/:companyId/:plantId/:warehouseId',
  component: FloorComponent
},
{
  path: 'storage',
  component: StorageComponent
},
{
  path: 'storage/:companyId/:plantId/:warehouseId/:floorId',
  component: StorageComponent
},
{
  path: 'selection',
  component: SetupSelectionComponent
},

{
  path: '',
  redirectTo: 'company'
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SetupRoutingModule { }
