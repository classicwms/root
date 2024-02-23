import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserprofileComponent } from './userprofile/userprofile.component';
import { HhtuserComponent } from './hhtuser/hhtuser.component';
import { UserroleComponent } from './userrole/userrole.component';
import { UserRoleNewComponent } from './userrole/user-role-new/user-role-new.component';

const routes: Routes = [{
  path: 'user-profile',
  component: UserprofileComponent
},
{
  path: 'hhtuser/:code',
  component: HhtuserComponent
},
{
  path: 'userrole',
  component: UserroleComponent
},
{
  path: 'userrole-new/:code',
  component: UserRoleNewComponent
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsermanRoutingModule { }
