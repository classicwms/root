import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BasicDataComponent } from './basic-data/basic-data.component';
import { InventoryComponent } from './inventory/inventory.component';
import { StorageDataComponent } from './storage-data/storage-data.component';
import { StorageSelectionComponent } from './storage-selection/storage-selection.component';

const routes: Routes = [
  {
    path: 'basic-data',
    component: BasicDataComponent
  },
  {
    path: 'Inventory',
    component: InventoryComponent
  },
  {
    path: 'Storage-data',
    component: StorageDataComponent
  },
  {
    path: 'selection',
    component: StorageSelectionComponent
  },
  
  
  {
    path: '',
    redirectTo: 'basic-data'
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasterStorageRoutingModule { }
