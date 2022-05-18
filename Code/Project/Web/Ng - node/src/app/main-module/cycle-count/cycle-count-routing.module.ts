import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PhysicalCreateComponent } from './physical-inventory/physical-create/physical-create.component';
import { PhysicalMainComponent } from './physical-inventory/physical-main/physical-main.component';
import { PrepetualAssignComponent } from './prepetual-count/prepetual-assign/prepetual-assign.component';
import { PrepetualConfirmComponent } from './prepetual-count/prepetual-confirm/prepetual-confirm.component';
import { PrepetualCoutingComponent } from './prepetual-count/prepetual-couting/prepetual-couting.component';
import { PrepetualMainComponent } from './prepetual-count/prepetual-main/prepetual-main.component';

const routes: Routes = [
  {
    path: 'Prepetual-main',
    component: PrepetualMainComponent
  },
  {
    path: 'Prepetual-confirm',
    component: PrepetualConfirmComponent
  },
  {
    path: 'physical-main',
    component: PhysicalMainComponent
  },
  {
    path: 'physical-create',
    component: PhysicalCreateComponent
  },
  
  
  {
    path: '',
    redirectTo: 'Prepetual-main'
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CycleCountRoutingModule { }
