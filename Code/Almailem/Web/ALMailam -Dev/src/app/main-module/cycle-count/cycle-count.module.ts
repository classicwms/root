import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CycleCountRoutingModule } from './cycle-count-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { VariantAnalysisComponent } from './variant-analysis/variant-analysis.component';
import { VariantEditComponent } from './variant-analysis/variant-edit/variant-edit.component';
import { StockAdjustmentComponent } from './Stock-Adjustment/stock-adjustment/stock-adjustment.component';
import { StockadjTabComponent } from './Stock-Adjustment/stockadj-tab/stockadj-tab.component';
import { StockAdjustmentEditComponent } from './Stock-Adjustment/stock-adjustment-edit/stock-adjustment-edit.component';


@NgModule({
  declarations: [
    VariantAnalysisComponent,
    VariantEditComponent,
    StockAdjustmentComponent,
    StockadjTabComponent,
    StockAdjustmentEditComponent,
  ],
  imports: [
    CommonModule,
    CycleCountRoutingModule,
    SharedModule,
    CommonFieldModule,
  ]
})
export class CycleCountModule { }
