import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReversalOutboundRoutingModule } from './reversal-outbound-routing.module';
import { ReversaloutboundMainComponent } from './reversaloutbound-main/reversaloutbound-main.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';


@NgModule({
  declarations: [
    ReversaloutboundMainComponent
  ],
  imports: [
    CommonModule,
    ReversalOutboundRoutingModule,
    SharedModule,
    CommonFieldModule,
  ],
  exports:[
    ReversaloutboundMainComponent
  ]
})
export class ReversalOutboundModule { }
