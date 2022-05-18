import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OutboundRoutingModule } from './outbound-routing.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';


@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    OutboundRoutingModule,
    SharedModule,
    CommonFieldModule,
  ]
})
export class OutboundModule { }
