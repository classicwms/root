import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BasicdataComponent } from './basicdata/basicdata.component';
import { PartnerComponent } from './partner/partner.component';
import { PartnerNewComponent } from './partner/partner-new/partner-new.component';
import { AltuomComponent } from './altuom/altuom.component';
import { AltuomNewComponent } from './altuom/altuom-new/altuom-new.component';
import { BasicdataNewComponent } from './basicdata/basicdata-new/basicdata-new.component';
import { BinlocationComponent } from './binlocation/binlocation.component';
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
import { ImbatchserialComponent } from './imbatchserial/imbatchserial.component';
import { ImpalletizationNewComponent } from './impalletization/impalletization-new/impalletization-new.component';
import { ImbatchserialNewComponent } from './imbatchserial/imbatchserial-new/imbatchserial-new.component';
import { ImpackingComponent } from './impacking/impacking.component';
import { ImpackingNewComponent } from './impacking/impacking-new/impacking-new.component';

const routes: Routes = [{
  path: 'basicdata',
  component: BasicdataComponent
},
{
  path: 'partner',
  component: PartnerComponent
},
{
  path: 'partnerNew/:code',
  component: PartnerNewComponent
},
{
  path: 'altuom',
  component: AltuomComponent
},
{
  path: 'altuomNew/:code',
  component: AltuomNewComponent
},
{
  path: 'basicdata',
  component: BasicdataComponent
},
{
  path: 'basicdataNew/:code',
  component: BasicdataNewComponent
},
{
  path: 'binlocation',
  component: BinlocationComponent
},
{
  path: 'basicdata2',
  component: Basicdata2Component
},
{
  path: 'basicdata2New/:code',
  component: Basicdata2NewComponent
},
{
  path: 'altpart',
  component: AltpartComponent
},
{
  path: 'altpartNew/:code',
  component: AltpartNewComponent
},
{
  path: 'imcapacity',
  component: ImcapacityComponent
},
{
  path: 'imcapacityNew/:code',
  component: ImcapacityNewComponent
},{
  path: 'imvariant',
  component: ImvariantComponent
},{
  path: 'imvariantNew/:code',
  component: ImvariantNewComponent
},
{
  path: 'startegy',
  component: StratergiesComponent
},
{
  path: 'startegyNew/:code',
  component: StratergiesNewComponent
},

{
  path: 'palletization',
  component: ImpalletizationComponent
},
{
  path: 'palletizationNew/:code',
  component: ImpalletizationNewComponent
},
{
  path: 'batchserial',
  component: ImbatchserialComponent
},
{
  path: 'batchserialNew/:code',
  component: ImbatchserialNewComponent
},{
  path: 'impacking',
  component: ImpackingComponent
},
{
  path: 'impackingNew/:code',
  component: ImpackingNewComponent
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasternewRoutingModule { }
