import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StoragebintypeNewComponent } from './storagebintype/storagebintype-new/storagebintype-new.component';
import { StoragebintypeComponent } from './storagebintype/storagebintype.component';
import { StorageclassNewComponent } from './storageclass/storageclass-new/storageclass-new.component';
import { StorageclassComponent } from './storageclass/storageclass.component';
import { StoragetypeNewComponent } from './storagetype/storagetype-new/storagetype-new.component';
import { StoragetypeComponent } from './storagetype/storagetype.component';
import { StratergyComponent } from './stratergy/stratergy.component';

const routes: Routes = [{
  path: 'storageclass',
  component: StorageclassComponent
},
{
  path: 'storageclassNew/:code',
  component: StorageclassNewComponent
},
{
  path: 'storagetype',
  component: StoragetypeComponent
},
{
  path: 'storagetypeNew/:code',
  component: StoragetypeNewComponent
},
{
  path: 'storagebintype',
  component: StoragebintypeComponent
},
{
  path: 'storagebintypeNew/:code',
  component: StoragebintypeNewComponent
},
{
  path: 'stratergy',
  component: StratergyComponent
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SetupstoragenewRoutingModule { }
