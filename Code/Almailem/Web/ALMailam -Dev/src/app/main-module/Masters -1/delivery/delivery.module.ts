import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DeliveryRoutingModule } from './delivery-routing.module';
import { DriverComponent } from './driver/driver.component';

import { DeliveryTabComponent } from './delivery-tab/delivery-tab.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { DriverNewComponent } from './driver/driver-new/driver-new.component';
import { VehicleComponent } from './vehicle/vehicle.component';
import { VehicleNewComponent } from './vehicle/vehicle-new/vehicle-new.component';
import { RouteComponent } from './route/route.component';
import { RouteNewComponent } from './route/route-new/route-new.component';
import { DriverassignComponent } from './driverassign/driverassign.component';
import { DriverassignNewComponent } from './driverassign/driverassign-new/driverassign-new.component';


@NgModule({
  declarations: [
    DriverComponent,
    DeliveryTabComponent,
    DriverNewComponent,
    VehicleComponent,
    VehicleNewComponent,
    RouteComponent,
    RouteNewComponent,
    DriverassignComponent,
    DriverassignNewComponent
  ],
  imports: [
    CommonModule,
    DeliveryRoutingModule,
    SharedModule,
  ]
})
export class DeliveryModule { }
