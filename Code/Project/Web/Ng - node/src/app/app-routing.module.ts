import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/Auth/auth.guard';
import { InboundModule } from './main-module/Inbound/inbound.module';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
    pathMatch: 'full'
  },
  {
    path: 'main',
    loadChildren: () => import('./main-module/main-module.module').then(m => m.MainModuleModule),
    canActivate: [AuthGuard]

  },
 
  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
