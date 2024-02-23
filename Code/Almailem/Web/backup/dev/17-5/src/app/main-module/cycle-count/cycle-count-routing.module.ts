import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PhysicalAssignComponent } from './physical-inventory/physical-assign/physical-assign.component';
import { PhysicalCreateComponent } from './physical-inventory/physical-create/physical-create.component';
import { PhysicalMainComponent } from './physical-inventory/physical-main/physical-main.component';
import { VariantAnnualComponent } from './physical-inventory/variant-annual/variant-annual.component';
import { VarianteditAnnualComponent } from './physical-inventory/variant-annual/variantedit-annual/variantedit-annual.component';
import { PrepetualConfirmComponent } from './prepetual-count/prepetual-confirm/prepetual-confirm.component';
import { PrepetualMainComponent } from './prepetual-count/prepetual-main/prepetual-main.component';
import { VariantAnalysisComponent } from './variant-analysis/variant-analysis.component';
import { VariantEditComponent } from './variant-analysis/variant-edit/variant-edit.component';
import { PerpetualVarianceConfirmComponent } from './prepetual-count/perpetual-variance-confirm/perpetual-variance-confirm.component';
import { PerpetualVarianceComponent } from './prepetual-count/perpetual-variance/perpetual-variance.component';

const routes: Routes = [
  {
    path: 'Prepetual-main/:code',
    component: PrepetualMainComponent
  },
  {
    path: 'varianceConfirm/:code',
    component: PerpetualVarianceConfirmComponent
  },
  {
    path: 'varianceConfirm',
    component: PerpetualVarianceComponent
  },
  {
    path: 'Prepetual-confirm/:code',
    component: PrepetualConfirmComponent
  },
  {
    path: 'physical-main',
    component: PhysicalMainComponent
  },
  {
    path: 'physical-create/:data',
    component: PhysicalCreateComponent
  },
  {
    path: 'variant-analysis',
    component: VariantAnalysisComponent
  },
  {
    path: 'variant-edit',
    component: VariantEditComponent
  },

  {
    path: 'variant-analysis-annual',
    component: VariantAnnualComponent
  },
  {
    path: 'variant-annual-edit/:data',
    component: VarianteditAnnualComponent
  },
  {
    path: 'periodic-count-confirm/:data',
    component: PhysicalAssignComponent
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
