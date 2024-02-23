import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HhtuserComponent } from './hhtuser/hhtuser.component';
import { UserprofileComponent } from './userprofile/userprofile.component';
import { UserroleNewComponent } from './userrole/userrole-new/userrole-new.component';
import { UserroleComponent } from './userrole/userrole.component';

const routes: Routes = [
  {
    path: 'user-role',
    component: UserroleComponent
  },
  {
    path: 'userrole-new',
    component: UserroleNewComponent
  },
  {
    path: 'user-profile',
    component: UserprofileComponent
  },
  {
    path: 'hht-user',
    component: HhtuserComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsermanagementRoutingModule { }
