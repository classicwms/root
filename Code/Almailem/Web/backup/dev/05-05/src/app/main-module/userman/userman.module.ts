import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsermanRoutingModule } from './userman-routing.module';
import { UsermanComponent } from './userman/userman.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { UserprofileComponent } from './userprofile/userprofile.component';
import { UserprofileNewComponent } from './userprofile/userprofile-new/userprofile-new.component';
import { HhtuserComponent } from './hhtuser/hhtuser.component';
import { HhtuserNewComponent } from './hhtuser/hhtuser-new/hhtuser-new.component';
import { UserroleComponent } from './userrole/userrole.component';
import { OtherMastersModule } from '../Masters -1/other-masters/other-masters.module';


@NgModule({
  declarations: [
    UsermanComponent,
    UserprofileComponent,
    UserprofileNewComponent,
    HhtuserComponent,
    HhtuserNewComponent,
    UserroleComponent
  ],
  imports: [
    CommonModule,
    UsermanRoutingModule,
    SharedModule,
    CommonFieldModule,
    OtherMastersModule
  ]
})
export class UsermanModule { }
