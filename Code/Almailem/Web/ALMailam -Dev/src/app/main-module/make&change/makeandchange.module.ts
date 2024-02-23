import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MakeandchangeRoutingModule } from './makeandchange-routing.module';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';


@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    MakeandchangeRoutingModule,
     NgMultiSelectDropDownModule.forRoot(),
     AngularMultiSelectModule,
  ]
})
export class MakeandchangeModule { }
