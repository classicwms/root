import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BintobinMainComponent } from './inhouse-transfer/bintobin-main/bintobin-main.component';
import { InhouseMainComponent } from './inhouse-transfer/inhouse-main/inhouse-main.component';
import { InhouseNewComponent } from './inhouse-transfer/inhouse-new/inhouse-new.component';
import { SkuSkuComponent } from './inhouse-transfer/sku-sku/sku-sku.component';
import { WarehouseMainComponent } from './warehouse-transfer/warehouse-main/warehouse-main.component';

const routes: Routes = [
  {
    path: 'inhouse-transfer',
    component: InhouseMainComponent
  },
  {
    path: 'warehouse-transfer',
    component: WarehouseMainComponent
  },
  {
    path: 'bintobin',
    component: BintobinMainComponent
  },
  {
    path: 'skutosku',
    component: SkuSkuComponent
  },
  {
    path: 'inhouse-new',
    component: InhouseNewComponent
  },
  {
    path: '',
    redirectTo: 'inhouse-transfer'
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MakeandchangeRoutingModule { }
