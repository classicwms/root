import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BarcodeComponent } from './common-field/barcode/barcode.component';
import { AuthGuard } from './core/Auth/auth.guard';
import { InboundModule } from './main-module/Inbound/inbound.module';
import { BarcodeNewComponent } from './common-field/barcode-new/barcode-new.component';

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
  {
    path: 'barcode',
    component: BarcodeComponent
  },
  {
    path: 'barcodeNew',
    component: BarcodeNewComponent
  },
  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
