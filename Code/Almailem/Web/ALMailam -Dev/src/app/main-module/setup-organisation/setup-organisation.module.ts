import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SetupOrganisationRoutingModule } from './setup-organisation-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { CompanyComponent } from './company/company.component';
import { OrganisationTabComponent } from './organisation-tab/organisation-tab.component';
import { CompanyNewComponent } from './company/company-new/company-new.component';
import { PlantComponent } from './plant/plant.component';
import { PlantNewComponent } from './plant/plant-new/plant-new.component';
import { WarehouseComponent } from './warehouse/warehouse.component';
import { WarehouseNewComponent } from './warehouse/warehouse-new/warehouse-new.component';
import { FloorComponent } from './floor/floor.component';
import { FloorNewComponent } from './floor/floor-new/floor-new.component';
import { StorageSectionComponent } from './storage-section/storage-section.component';
import { StorageSectionNewComponent } from './storage-section/storage-section-new/storage-section-new.component';


@NgModule({
  declarations: [
    CompanyComponent,
    OrganisationTabComponent,
    CompanyNewComponent,
    PlantComponent,
    PlantNewComponent,
    WarehouseComponent,
    WarehouseNewComponent,
    FloorComponent,
    FloorNewComponent,
    StorageSectionComponent,
    StorageSectionNewComponent
  ],
  imports: [
    CommonModule,
    SetupOrganisationRoutingModule,
    CommonModule,
    SharedModule,
    CommonFieldModule,
  ]
})
export class SetupOrganisationModule { }
