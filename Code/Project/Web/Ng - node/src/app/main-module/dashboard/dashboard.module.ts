import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { CardComponent } from './card/card.component';
import { OtherMastersModule } from '../Masters -1/other-masters/other-masters.module';
import { PrepetualCountModule } from '../cycle-count/prepetual-count/prepetual-count.module';
import { HighchartsChartModule } from 'highcharts-angular';
import { PiechartComponent } from './dashboard/piechart/piechart.component';
import { LineChartComponent } from './dashboard/line-chart/line-chart.component';
import { StackChartComponent } from './dashboard/stack-chart/stack-chart.component';
import { StackBarComponent } from './dashboard/stack-bar/stack-bar.component';
import { BarChartComponent } from './dashboard/bar-chart/bar-chart.component';

@NgModule({
  declarations: [
    DashboardComponent,
    CardComponent,
    PiechartComponent,
    LineChartComponent,
    StackChartComponent,
    StackBarComponent,
    BarChartComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    DashboardRoutingModule,
    HighchartsChartModule,
    OtherMastersModule,
    PrepetualCountModule,
    
  ],
  exports:[]
})
export class DashboardModule { }
