import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonFieldModule } from "src/app/common-field/common-field.module";
import { SharedModule } from "src/app/shared/shared.module";
import { StorageClassComponent } from './storage-class/storage-class.component';
import { StorageTypeComponent } from './storage-type/storage-type.component';
import { StoragebinTypeComponent } from './storagebin-type/storagebin-type.component';
import { StrategiesComponent } from './strategies/strategies.component';
import { StoragebinTableComponent } from './storagebin-type/storagebin-table/storagebin-table.component';
import { SetupStorageRoutingModule } from "./setup-storage-routing.module";
import { StorageSelectionComponent } from './storage-selection/storage-selection.component';
import { StrategyPopupComponent } from './strategies/strategy-popup/strategy-popup.component';
import { StrategiesNewComponent } from './strategies/strategies-new/strategies-new.component';



@NgModule({
  declarations: [
    StorageClassComponent,
    StorageTypeComponent,
    StoragebinTypeComponent,
    StrategiesComponent,
    StoragebinTableComponent,
    StorageSelectionComponent,
    StrategyPopupComponent,
    StrategiesNewComponent,
  ],
  imports:  [
    CommonModule,
    SetupStorageRoutingModule,
    SharedModule,
    CommonFieldModule
  ],
  exports: [
    StorageClassComponent,
    StorageTypeComponent,
    StoragebinTypeComponent,
    StrategiesComponent,
  ],
})
export class SetupStorageModule { }
