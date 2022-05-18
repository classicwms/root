import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OrderManagementRoutingModule } from './order-management-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrdermanagementMainComponent } from './ordermanagement-main/ordermanagement-main.component';
import { ReallocationComponent } from './reallocation/reallocation.component';
import { PickupCreationComponent } from './pickup-creation/pickup-creation.component';
import { ProposedLocationsComponent } from './pickup-creation/proposed-locations/proposed-locations.component';
import { OrderTabComponent } from './order-tab/order-tab.component';
import { ReallocateComponent } from './ordermanagement-main/reallocate/reallocate.component';
import { BinLocationComponent } from './ordermanagement-main/bin-location/bin-location.component';


@NgModule({
  declarations: [
    OrdermanagementMainComponent,
    ReallocationComponent,
    PickupCreationComponent,
    ProposedLocationsComponent,
    OrderTabComponent,
    ReallocateComponent,
    BinLocationComponent
  ],
  imports: [
    CommonModule,
    OrderManagementRoutingModule,
    SharedModule,
    CommonFieldModule
  ],
  exports: [
    OrdermanagementMainComponent,
    ReallocationComponent,
    PickupCreationComponent,
    ProposedLocationsComponent,
    OrderTabComponent
  ]
})
export class OrderManagementModule { }
