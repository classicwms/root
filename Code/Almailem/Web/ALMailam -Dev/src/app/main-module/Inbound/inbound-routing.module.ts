import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ContainerMainComponent } from './Container-receipt/container-main/container-main.component';
import { ContainerreceiptCreateComponent } from './Container-receipt/containerreceipt-create/containerreceipt-create.component';
import { GoodsreceiptMainComponent } from './Goods-receipt/goodsreceipt-main/goodsreceipt-main.component';
import { PutawaycreationCreateComponent } from './Goods-receipt/putawaycreation-create/putawaycreation-create.component';
import { InboundCreateComponent } from './inbound-confirmation/inbound-create/inbound-create.component';
import { InboundconfirmMainComponent } from './inbound-confirmation/inboundconfirm-main/inboundconfirm-main.component';
import { ReadyToConfirmComponent } from './inbound-confirmation/ready-to-confirm/ready-to-confirm.component';
import { ItemCreateComponent } from './Item receipt/item-create/item-create.component';
import { ItemMainComponent } from './Item receipt/item-main/item-main.component';
import { GoodreceiptCreateComponent } from './preinbound/goodreceipt-create/goodreceipt-create.component';
import { PreinboundMainComponent } from './preinbound/preinbound-main/preinbound-main.component';
import { PreinboundNewComponent } from './preinbound/preinbound-new/preinbound-new.component';
import { PutawayMainComponent } from './putaway/putaway-main/putaway-main.component';
import { ReversalComponent } from './reversal/reversal.component';
import { PreinboundUploadComponent } from './preinbound/preinbound-upload/preinbound-upload.component';
import { FailedorderComponent } from './preinbound/failedorder/failedorder.component';


const routes: Routes = [



  {
    path: 'goods-receipt',
    component: GoodsreceiptMainComponent,
  },
  {
    path: 'putaway-create',
    component: PutawaycreationCreateComponent,
  },
  {
    path: 'container-receipt',
    component: ContainerMainComponent,
  },
  {
    path: 'container-create/:code',
    component: ContainerreceiptCreateComponent,
  },
  {
    path: 'preinbound',
    component: PreinboundMainComponent,
  },
  {
    path: 'failedorder',
    component: FailedorderComponent,
  },
  {
    path: 'preinbound-create/:code',
    component: PreinboundNewComponent,
  },
  {
    path: 'goods-create/:code',
    component: GoodreceiptCreateComponent,
  },
  {
    path: 'putaway',
    component: PutawayMainComponent,
  },
  {
    path: 'putaway-create/:code',
    component: PutawaycreationCreateComponent,
  },

  {
    path: 'inbound-confirm',
    component: InboundconfirmMainComponent,
  },
  {
    path: 'readytoConfirm',
    component: ReadyToConfirmComponent,
  },
  {
    path: 'inbound-create/:code',
    component: InboundCreateComponent,
  },
  {
    path: 'item-create/:code',
    component: ItemCreateComponent,
  },
  {
    path: 'item-main',
    component: ItemMainComponent,
  },
  {
    path: 'reversal-main',
    component: ReversalComponent,
  },
  {
    path: '',
    redirectTo: 'container-receipt'
  },
  {
    path: 'preInboundCreate',
    component: PreinboundUploadComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InboundRoutingModule { }
