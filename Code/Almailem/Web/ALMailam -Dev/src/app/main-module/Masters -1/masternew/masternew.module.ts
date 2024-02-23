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
import { BasicdataNewComponent } from './basicdata/basicdata-new/basicdata-new.component';
import { BinlocationComponent } from './binlocation/binlocation.component';
import { BinloactionNewComponent } from './binlocation/binloaction-new/binloaction-new.component';
import { Basicdata2Component } from './basicdata2/basicdata2.component';
import { Basicdata2NewComponent } from './basicdata2/basicdata2-new/basicdata2-new.component';
import { AltpartComponent } from './altpart/altpart.component';
import { AltpartNewComponent } from './altpart/altpart-new/altpart-new.component';
import { ImcapacityComponent } from './imcapacity/imcapacity.component';
import { ImcapacityNewComponent } from './imcapacity/imcapacity-new/imcapacity-new.component';
import { ImvariantComponent } from './imvariant/imvariant.component';
import { ImvariantNewComponent } from './imvariant/imvariant-new/imvariant-new.component';
import { StratergiesComponent } from './stratergies/stratergies.component';
import { StratergiesNewComponent } from './stratergies/stratergies-new/stratergies-new.component';
import { ImpalletizationComponent } from './impalletization/impalletization.component';
import { ImpalletizationNewComponent } from './impalletization/impalletization-new/impalletization-new.component';
import { ImbatchserialComponent } from './imbatchserial/imbatchserial.component';
import { ImbatchserialNewComponent } from './imbatchserial/imbatchserial-new/imbatchserial-new.component';
import { ImpackingComponent } from './impacking/impacking.component';
import { ImpackingNewComponent } from './impacking/impacking-new/impacking-new.component';
import { AddLinesComponent } from './partner/partner-new/add-lines/add-lines.component';
import { AddlinesComponent } from './imvariant/addlines/addlines.component';
import { PalletaddlinesComponent } from './impalletization/palletaddlines/palletaddlines.component';


@NgModule({
  declarations: [
    MasternewComponent,
    BasicdataComponent,
    PartnerComponent,
    PartnerNewComponent,
    AltuomComponent,
    AltuomNewComponent,
    BasicdataNewComponent,
    BinlocationComponent,
    BinloactionNewComponent,
    Basicdata2Component,
    Basicdata2NewComponent,
    AltpartComponent,
    AltpartNewComponent,
    ImcapacityComponent,
    ImcapacityNewComponent,
    ImvariantComponent,
    ImvariantNewComponent,
    StratergiesComponent,
    StratergiesNewComponent,
    ImpalletizationComponent,
    ImpalletizationNewComponent,
    ImbatchserialComponent,
    ImbatchserialNewComponent,
    ImpackingComponent,
    ImpackingNewComponent,
    AddLinesComponent,
    AddlinesComponent,
    PalletaddlinesComponent
  ],
  imports: [
    CommonModule,
    MasternewRoutingModule,
    SharedModule,
  ]
})
export class MasternewModule { }
