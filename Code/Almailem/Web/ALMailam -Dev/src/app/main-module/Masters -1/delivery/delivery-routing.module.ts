import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DriverComponent } from './driver/driver.component';
import { VehicleComponent } from './vehicle/vehicle.component';
import { RouteComponent } from './route/route.component';
import { DriverNewComponent } from './driver/driver-new/driver-new.component';
import { VehicleNewComponent } from './vehicle/vehicle-new/vehicle-new.component';
import { RouteNewComponent } from './route/route-new/route-new.component';
import { DriverassignComponent } from './driverassign/driverassign.component';
import { DriverassignNewComponent } from './driverassign/driverassign-new/driverassign-new.component';


const routes: Routes = [ {
  path: 'driver',
  component: DriverComponent
}, 
 {
  path: 'vehicle',
  component: VehicleComponent
},
{
  path: 'route',
  component: RouteComponent
},
{
  path: 'driverNew/:code',
  component: DriverNewComponent
},
{
  path: 'vehicleNew/:code',
  component: VehicleNewComponent
},{
  path: 'routeNew/:code',
  component: RouteNewComponent
},{
  path: 'drivervehicleassign',
  component: DriverassignComponent
},
{
  path: 'drivervehicleassignNew/:code',
  component: DriverassignNewComponent
},];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DeliveryRoutingModule { }
