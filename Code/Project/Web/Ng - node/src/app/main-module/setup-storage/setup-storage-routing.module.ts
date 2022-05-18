import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StorageClassComponent } from './storage-class/storage-class.component';
import { StorageTypeComponent } from './storage-type/storage-type.component';
import { StoragebinTypeComponent } from './storagebin-type/storagebin-type.component';
import { StrategiesComponent } from './strategies/strategies.component';

const routes: Routes = [
  {
    path: 'strategies',
    component: StrategiesComponent
  },
  {
    path: 'storage-bin',
    component: StoragebinTypeComponent
  },
  {
    path: 'storage-class',
    component: StorageClassComponent
  },
  {
    path: 'storage-type',
    component: StorageTypeComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SetupStorageRoutingModule { }
