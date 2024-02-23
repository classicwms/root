import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SetupstoragenewRoutingModule } from './setupstoragenew-routing.module';
import { SetupstoragenewComponent } from './setupstoragenew/setupstoragenew.component';
import { StorageclassComponent } from './storageclass/storageclass.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { StorageclassNewComponent } from './storageclass/storageclass-new/storageclass-new.component';
import { StoragetypeComponent } from './storagetype/storagetype.component';
import { StoragetypeNewComponent } from './storagetype/storagetype-new/storagetype-new.component';
import { StoragebintypeComponent } from './storagebintype/storagebintype.component';
import { StoragebintypeNewComponent } from './storagebintype/storagebintype-new/storagebintype-new.component';
import { StoragebinTableComponent } from './storagebintype/storagebin-table/storagebin-table.component';
import { StratergyComponent } from './stratergy/stratergy.component';
import { StratergyNewComponent } from './stratergy/stratergy-new/stratergy-new.component';
import { AddLinesComponent } from './stratergy/stratergy-new/add-lines/add-lines.component';


@NgModule({
  declarations: [
    SetupstoragenewComponent,
    StorageclassComponent,
    StorageclassNewComponent,
    StoragetypeComponent,
    StoragetypeNewComponent,
    StoragebintypeComponent,
    StoragebintypeNewComponent,
    StoragebinTableComponent,
    StratergyComponent,
    StratergyNewComponent,
    AddLinesComponent
  ],
  imports: [
    CommonModule,
    SetupstoragenewRoutingModule,
    SharedModule,
  ]
})
export class SetupstoragenewModule { }
