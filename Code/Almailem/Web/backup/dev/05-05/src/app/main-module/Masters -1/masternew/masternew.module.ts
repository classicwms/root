import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MasternewRoutingModule } from './masternew-routing.module';
import { MasternewComponent } from './masternew/masternew.component';
import { BasicdataComponent } from './basicdata/basicdata.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { PartnerComponent } from './partner/partner.component';
import { PartnerNewComponent } from './partner/partner-new/partner-new.component';
import { AltuomComponent } from './altuom/altuom.component';
import { AltuomNewComponent } from './altuom/altuom-new/altuom-new.component';


@NgModule({
  declarations: [
    MasternewComponent,
    BasicdataComponent,
    PartnerComponent,
    PartnerNewComponent,
    AltuomComponent,
    AltuomNewComponent
  ],
  imports: [
    CommonModule,
    MasternewRoutingModule,
    SharedModule,
  ]
})
export class MasternewModule { }
