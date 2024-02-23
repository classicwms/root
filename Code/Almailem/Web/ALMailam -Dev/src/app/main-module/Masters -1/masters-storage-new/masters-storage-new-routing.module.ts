import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BinlocationComponent } from './binlocation/binlocation.component';
import { BinloactionNewComponent } from './binlocation/binloaction-new/binloaction-new.component';

const routes: Routes = [ {
  path: 'binLocation',
  component: BinlocationComponent
}, {
  path: 'binLocationNew/:code',
  component: BinloactionNewComponent
},];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MastersStorageNewRoutingModule { }
