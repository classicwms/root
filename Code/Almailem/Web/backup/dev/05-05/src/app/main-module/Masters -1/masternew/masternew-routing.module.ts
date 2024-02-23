import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BasicdataComponent } from './basicdata/basicdata.component';
import { PartnerComponent } from './partner/partner.component';
import { PartnerNewComponent } from './partner/partner-new/partner-new.component';
import { AltuomComponent } from './altuom/altuom.component';
import { AltuomNewComponent } from './altuom/altuom-new/altuom-new.component';

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
},];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasternewRoutingModule { }
