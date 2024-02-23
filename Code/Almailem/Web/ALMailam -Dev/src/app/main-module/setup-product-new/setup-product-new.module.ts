import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SetupProductNewRoutingModule } from './setup-product-new-routing.module';
import { SetupProductNewComponent } from './setup-product-new/setup-product-new.component';
import { ItemtypeComponent } from './itemtype/itemtype.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { ItemtypeNewComponent } from './itemtype/itemtype-new/itemtype-new.component';
import { ItemgroupComponent } from './itemgroup/itemgroup.component';
import { ItemgroupNewComponent } from './itemgroup/itemgroup-new/itemgroup-new.component';
import { BatchserialComponent } from './batchserial/batchserial.component';
import { BatchserialNewComponent } from './batchserial/batchserial-new/batchserial-new.component';
import { VariantComponent } from './variant/variant.component';
import { VariantNewComponent } from './variant/variant-new/variant-new.component';
import { BatchserialtableComponent } from './batchserial/batchserialtable/batchserialtable.component';
import { VarinatableComponent } from './variant/varinatable/varinatable.component';
import { ItemGroupAddLinesComponent } from './itemgroup/itemgroup-new/item-group-add-lines/item-group-add-lines.component';


@NgModule({
  declarations: [
    SetupProductNewComponent,
    ItemtypeComponent,
    ItemtypeNewComponent,
    ItemgroupComponent,
    ItemgroupNewComponent,
    BatchserialComponent,
    BatchserialNewComponent,
    VariantComponent,
    VariantNewComponent,
    BatchserialtableComponent,
    VarinatableComponent,
    ItemGroupAddLinesComponent
  ],
  imports: [
    CommonModule,
    SetupProductNewRoutingModule,
    SharedModule,
  ]
})
export class SetupProductNewModule { }
