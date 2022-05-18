import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SetupRoutingModule } from './setup-routing.module';
import { CompanyComponent } from './company/company.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { PlantComponent } from './plant/plant.component';
import { WarehouseComponent } from './warehouse/warehouse.component';
import { WarehousePage2Component } from './warehouse/warehouse-page2/warehouse-page2.component';
import { WarehousePage3Component } from './warehouse/warehouse-page3/warehouse-page3.component';
import { FloorComponent } from './floor/floor.component';
import { StorageComponent } from './storage/storage.component';
import { SetupSelectionComponent } from './setup-selection/setup-selection.component';
import { SetupPopupComponent } from './setup-selection/setup-popup/setup-popup.component';


@NgModule({
  declarations: [CompanyComponent,
     PlantComponent,
      WarehouseComponent,
       WarehousePage2Component, 
       WarehousePage3Component, 
       FloorComponent,
        StorageComponent,
        SetupSelectionComponent,
        SetupPopupComponent,
  ],
  imports: [
    CommonModule,
    SetupRoutingModule,
    SharedModule,
    CommonFieldModule
  ],
  exports: [CompanyComponent,
     PlantComponent,
      WarehouseComponent,
      WarehousePage2Component, 
      WarehousePage3Component,
      FloorComponent,
      StorageComponent,
  ],
})
export class SetupModule { }
