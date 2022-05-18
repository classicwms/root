import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MainModuleRoutingModule } from './main-module-routing.module';
import { HomeComponent } from './home/home.component';
import { SharedModule } from '../shared/shared.module';
import { CommonFieldModule } from '../common-field/common-field.module';
import { SetupModule } from './setup/setup.module';
import { SetupStorageModule } from './setup-storage/setup-storage.module';
import { SetupProductModule } from './setup-product/setup-product.module';
import { PreinboundModule } from './Inbound/preinbound/preinbound.module';
import { WarehouseTransferModule } from './make&change/warehouse-transfer/warehouse-transfer.module';
import { InhouseTransferModule } from './make&change/inhouse-transfer/inhouse-transfer.module';
import { OrderManagementModule } from './Outbound/order-management/order-management.module';
import { PreoutboundModule } from './Outbound/preoutbound/preoutbound.module';
import { PhysicalInventoryModule } from './cycle-count/physical-inventory/physical-inventory.module';
import { PrepetualCountModule } from './cycle-count/prepetual-count/prepetual-count.module';
import { GoodsreceiptMainComponent } from './Inbound/Goods-receipt/goodsreceipt-main/goodsreceipt-main.component';
import { PutawaycreationCreateComponent } from './Inbound/Goods-receipt/putawaycreation-create/putawaycreation-create.component';
import { PutawayDetailsComponent } from './Inbound/Goods-receipt/putawaycreation-create/putaway-details/putaway-details.component';
import { ContainerMainComponent } from './Inbound/Container-receipt/container-main/container-main.component';
import { InboundConfirmModule } from './Inbound/inbound-confirmation/inbound-confirm.module';
import { AssignmentModule } from './Outbound/assignment/assignment.module';
import { PickingModule } from './Outbound/picking/picking.module';
import { QualityModule } from './Outbound/quality/quality.module';
import { DeliveryModule } from './Outbound/delivery/delivery.module';
import { ReversalOutboundModule } from './Outbound/reversal-outbound/reversal-outbound.module';


@NgModule({
  declarations: [
    HomeComponent,
    GoodsreceiptMainComponent,
    PutawaycreationCreateComponent,
    PutawayDetailsComponent,
    ContainerMainComponent,
    
    
  ],
  imports: [
    CommonModule,
    SharedModule,
    MainModuleRoutingModule,
    CommonFieldModule,
    SetupModule,
    SetupStorageModule,
    SetupProductModule,
    PreinboundModule,
    WarehouseTransferModule,
    InhouseTransferModule,
    PreoutboundModule,
    OrderManagementModule,
    PhysicalInventoryModule,
    PrepetualCountModule,
    InboundConfirmModule,
    AssignmentModule,
    PickingModule,
    QualityModule,
    DeliveryModule,
    ReversalOutboundModule
    
  ]
})
export class MainModuleModule { }
