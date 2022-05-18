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



@NgModule({
  declarations: [
    StorageClassComponent,
    StorageTypeComponent,
    StoragebinTypeComponent,
    StrategiesComponent,
    StoragebinTableComponent,
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
