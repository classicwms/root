import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsermanagementRoutingModule } from './usermanagement-routing.module';
import { UserprofileComponent } from './userprofile/userprofile.component';
import { UserroleComponent } from './userrole/userrole.component';
import { HhtuserComponent } from './hhtuser/hhtuser.component';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { UserprofileNewComponent } from './userprofile/userprofile-new/userprofile-new.component';
import { HhtuserNewComponent } from './hhtuser/hhtuser-new/hhtuser-new.component';
import { UserroleNewComponent } from './userrole/userrole-new/userrole-new.component';


@NgModule({
  declarations: [
    UserprofileComponent,
    UserroleComponent,
    HhtuserComponent,
    UserprofileNewComponent,
    HhtuserNewComponent,
    UserroleNewComponent
  ],
  imports: [
    CommonModule,
    UsermanagementRoutingModule,
    SharedModule,
    CommonFieldModule,
  ],
  exports:[
    UserprofileComponent,
    UserroleComponent,
    HhtuserComponent
  ]
})
export class UsermanagementModule { }
