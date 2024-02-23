import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CycleCountModule } from './cycle-count/cycle-count.module';
import { DailyOrderStatusModule } from './daily-order-status/daily-order-status.module';
import { DashboardModule } from './dashboard/dashboard.module';
import { HomeComponent } from './home/home.component';
import { InboundModule } from './Inbound/inbound.module';
import { MakeandchangeModule } from './make&change/makeandchange.module';
import { MasterStorageModule } from './Masters -1/master-storage/master-storage.module';
import { MastersModule } from './Masters -1/masters/masters.module';
import { OtherMastersModule } from './Masters -1/other-masters/other-masters.module';
import { OutboundModule } from './Outbound/outbound.module';
import { ReportsModule } from './reports/reports.module';
import { SetupOrganisationModule } from './setup-organisation/setup-organisation.module';
import { SetupProductNewModule } from './setup-product-new/setup-product-new.module';
import { SetupProductModule } from './setup-product/setup-product.module';
import { SetupStorageModule } from './setup-storage/setup-storage.module';
import { SetupModule } from './setup/setup.module';
import { SetupstoragenewModule } from './setupstoragenew/setupstoragenew.module';
import { ThreePlModule } from './three-pl/three-pl.module';
import { UsermanagementModule } from './usermanagement/usermanagement.module';
import { ManufacturingModule } from './manufacturing/manufacturing.module';
import { MasternewModule } from './Masters -1/masternew/masternew.module';
import { UsermanModule } from './userman/userman.module';
import { OtherSetupModule } from './other-setup/other-setup.module';
import { MastersStorageNewModule } from './Masters -1/masters-storage-new/masters-storage-new.module';
import { DeliveryManagementModule } from './delivery-management/delivery-management.module';
import { DeliveryModule } from './Masters -1/delivery/delivery.module';
import { MiddlewareModule } from './middleware/middleware.module';


const routes: Routes = [{
  path: '',
  component: HomeComponent,
  children: [
    {
      path: 'dashboard',
      loadChildren: () => import('./dashboard/dashboard.module').then(m => DashboardModule)
    },
    {
      path: 'organisationsetup',
      loadChildren: () => import('./setup-organisation/setup-organisation.module').then(m => SetupOrganisationModule)
    },
    {
      path: 'otherSetup',
      loadChildren: () => import('./other-setup/other-setup.module').then(m => OtherSetupModule)
    },
    {
      path: 'inbound',
      loadChildren: () => import('./Inbound/inbound.module').then(m => InboundModule)
  
    },
    {
      path: 'delivery',
      loadChildren: () => import('./Masters -1/delivery/delivery.module').then(m => DeliveryModule)
    },
    {
      path: 'other-masters',
      loadChildren: () => import('./Masters -1/other-masters/other-masters.module').then(m => OtherMastersModule)
    },
    {
      path: 'outbound',
      loadChildren: () => import('./Outbound/outbound.module').then(m => OutboundModule)
  
    },
    {
      path: 'make&change',
      loadChildren: () => import('./make&change/makeandchange.module').then(m => MakeandchangeModule)
  
    },
    {
      path: 'cycle-count',
      loadChildren: () => import('./cycle-count/cycle-count.module').then(m => CycleCountModule)
  
    },
    {
      path: 'masternew',
      loadChildren: () => import('./Masters -1/masternew/masternew.module').then(m => MasternewModule)
    },
    {
      path: 'userman',
      loadChildren: () => import('./userman/userman.module').then(m => UsermanModule)
    },
    {
      path: 'orderStatus',
      loadChildren: () => import('./daily-order-status/daily-order-status.module').then(m => DailyOrderStatusModule)
    },
    {
      path: 'reports',
      loadChildren: () => import('./reports/reports.module').then(m => ReportsModule)
    },
    {
      path: 'mastersStorageNew',
      loadChildren: () => import('./Masters -1/masters-storage-new/masters-storage-new.module').then(m => MastersStorageNewModule)
    },
    {
      path: 'productsetup',
      loadChildren: () => import('./setup-product-new/setup-product-new.module').then(m => SetupProductNewModule)
    },
    {
      path: 'productstorage',
      loadChildren: () => import('./setupstoragenew/setupstoragenew.module').then(m => SetupstoragenewModule)
    },
    {
      path: 'delivery',
      loadChildren: () => import('./delivery-management/delivery-management.module').then(m => DeliveryManagementModule)
    },
    {
      path: 'middleware',
      loadChildren: () => import('./middleware/middleware.module').then(m => MiddlewareModule)
    }
  
    
  ]
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MainModuleRoutingModule { }
