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
import { ContainerMainComponent } from './Inbound/Container-receipt/container-main/container-main.component';
import { InboundConfirmModule } from './Inbound/inbound-confirmation/inbound-confirm.module';

import { AssignmentModule } from './Outbound/assignment/assignment.module';
import { PickingModule } from './Outbound/picking/picking.module';
import { QualityModule } from './Outbound/quality/quality.module';
import { DeliveryModule } from './Outbound/delivery/delivery.module';
import { ReversalOutboundModule } from './Outbound/reversal-outbound/reversal-outbound.module';
import { ReportstabComponent } from './reports/reportstab/reportstab.component';
import { ReportsModule } from './reports/reports.module';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { BarcodePrintComponent } from './home/barcode-print/barcode-print.component';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { UpdateInventoryComponent } from './home/update-inventory/update-inventory.component';
import { UpdateOutboundLineComponent } from './home/update-outbound-line/update-outbound-line.component';
import { InboundModule } from './Inbound/inbound.module';
import { MenuItemComponent } from './home/menu-item/menu-item.component';
import { GoodsReceiptModule } from './Inbound/Goods-receipt/goods-receipt.module';
import { LoginUsersComponent } from './home/login-users/login-users.component';



@NgModule({
  declarations: [
    HomeComponent,
    ContainerMainComponent,
    ReportstabComponent,
    BarcodePrintComponent,
    UpdateInventoryComponent,
    UpdateOutboundLineComponent,
    MenuItemComponent,
    LoginUsersComponent,


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
    GoodsReceiptModule,
    AssignmentModule,
    PickingModule,
    QualityModule,
    DeliveryModule,
    ReversalOutboundModule,
    ReportsModule,
    InboundModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ]
})
export class MainModuleModule { }
