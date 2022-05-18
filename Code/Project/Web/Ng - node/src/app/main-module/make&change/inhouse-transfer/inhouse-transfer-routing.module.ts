import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BintobinMainComponent } from './bintobin-main/bintobin-main.component';
import { InhouseMainComponent } from './inhouse-main/inhouse-main.component';
import { SkuSkuComponent } from './sku-sku/sku-sku.component';

const routes: Routes = [];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InhouseTransferRoutingModule { }
