import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonFieldModule } from "src/app/common-field/common-field.module";
import { SharedModule } from "src/app/shared/shared.module";
import { BasicDataComponent } from "./basic-data/basic-data.component";
import { InventoryComponent } from "./inventory/inventory.component";
import { MasterStorageRoutingModule } from "./master-storage-routing.module";
import { StorageDataComponent } from "./storage-data/storage-data.component";
import { StorageSelectionComponent } from './storage-selection/storage-selection.component';
import { StoragePopupComponent } from './storage-selection/storage-popup/storage-popup.component';
import { NgMultiSelectDropDownModule } from "ng-multiselect-dropdown";
import { AngularMultiSelectModule } from "angular2-multiselect-dropdown";



@NgModule({
  declarations: [
    BasicDataComponent,
    StorageDataComponent,
    InventoryComponent,
    StorageSelectionComponent,
    StoragePopupComponent
  ],
  imports: [
    CommonModule,
    MasterStorageRoutingModule,
    SharedModule,
    CommonFieldModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],
  exports:[
    BasicDataComponent,
    StorageDataComponent,
    InventoryComponent
  ]
})
export class MasterStorageModule { }
