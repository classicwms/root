import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CompanyNewComponent } from './company/company-new/company-new.component';
import { CompanyComponent } from './company/company.component';
import { FloorNewComponent } from './floor/floor-new/floor-new.component';
import { FloorComponent } from './floor/floor.component';
import { PlantNewComponent } from './plant/plant-new/plant-new.component';
import { PlantComponent } from './plant/plant.component';
import { StorageSectionNewComponent } from './storage-section/storage-section-new/storage-section-new.component';
import { StorageSectionComponent } from './storage-section/storage-section.component';
import { WarehouseNewComponent } from './warehouse/warehouse-new/warehouse-new.component';
import { WarehouseComponent } from './warehouse/warehouse.component';

const routes: Routes = [
  {path: 'company',
    component: CompanyComponent
  },
  {
    path: 'companyNew/:code',
    component: CompanyNewComponent
  },
  {path: 'plant',
  component: PlantComponent
},
{
  path: 'plantNew/:code',
  component: PlantNewComponent
},
{path: 'warehouse',
  component: WarehouseComponent
},
{
  path: 'warehouseNew/:code',
  component: WarehouseNewComponent
},
{path: 'floor',
  component: FloorComponent
},
{
  path: 'floorNew/:code',
  component: FloorNewComponent
},
{path: 'storage',
  component: StorageSectionComponent
},
{
  path: 'storageNew/:code',
  component: StorageSectionNewComponent
},
  {
    path: '',
    component: CompanyComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SetupOrganisationRoutingModule { }
