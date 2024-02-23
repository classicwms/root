import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BatchserialNewComponent } from './batchserial/batchserial-new/batchserial-new.component';
import { BatchserialComponent } from './batchserial/batchserial.component';
import { ItemgroupNewComponent } from './itemgroup/itemgroup-new/itemgroup-new.component';
import { ItemgroupComponent } from './itemgroup/itemgroup.component';
import { ItemtypeNewComponent } from './itemtype/itemtype-new/itemtype-new.component';
import { ItemtypeComponent } from './itemtype/itemtype.component';
import { VariantNewComponent } from './variant/variant-new/variant-new.component';
import { VariantComponent } from './variant/variant.component';

const routes: Routes = [{
  path: 'itemtype',
  component: ItemtypeComponent
},
{
  path: 'itemtypeNew/:code',
  component: ItemtypeNewComponent
},
{
  path: 'itemgroup',
  component: ItemgroupComponent
},
{
  path: 'itemgroupNew/:code',
  component: ItemgroupNewComponent
},
{
  path: 'batch',
  component: BatchserialComponent
},
{
  path: 'batchNew/:code',
  component: BatchserialNewComponent
},
{
  path: 'variant',
  component: VariantComponent
},
{
  path: 'variantNew/:code',
  component: VariantNewComponent
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SetupProductNewRoutingModule { }
