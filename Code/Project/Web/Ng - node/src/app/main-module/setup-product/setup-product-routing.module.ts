import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BarcodeComponent } from './barcode/barcode.component';
import { BatchSerialComponent } from './batch-serial/batch-serial.component';
import { ItemGroupComponent } from './item-group/item-group.component';
import { ItemTypeComponent } from './item-type/item-type.component';
import { ProductSelectionComponent } from './product-selection/product-selection.component';
import { VariantComponent } from './variant/variant.component';

const routes: Routes = [
  {
    path: 'itemtype',
    component: ItemTypeComponent
  },
  {
    path: 'itemgroup',
    component: ItemGroupComponent
  },
  {
    path: 'batchserial',
    component: BatchSerialComponent
  },
  {
    path: 'variant',
    component: VariantComponent
  },
  {
    path: 'barcode',
    component: BarcodeComponent
  },
  {
    path: 'selection',
    component: ProductSelectionComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SetupProductRoutingModule { }
