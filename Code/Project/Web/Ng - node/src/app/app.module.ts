import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from './shared/shared.module';
import { LocationStrategy, HashLocationStrategy, CommonModule, PathLocationStrategy } from '@angular/common';
import { AuthGuard } from './core/Auth/auth.guard';
import { CommonInterceptor } from './core/common.interceptors';
import { AuthService } from './core/core';
import { ErrorInterceptor } from './core/error.interceptor';
import { NgxSpinnerModule } from 'ngx-spinner';
import { ToastrModule } from 'ngx-toastr';
import { DialogExampleComponent } from './common-field/innerheader/dialog-example/dialog-example.component';


@NgModule({
  declarations: [
    AppComponent,
    DialogExampleComponent,
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    SharedModule,
    // CommonFieldModule,
    // ToastrModule added
    NgxSpinnerModule,
    BrowserModule,
    ReactiveFormsModule,
    // MatDialogModule,
    // AppRoutingModule,
    // BrowserAnimationsModule,
    NgbModule,
    ToastrModule.forRoot({
      timeOut: 5000,
      positionClass: 'toast-bottom-right',
      preventDuplicates: true,
      progressBar: true
    }),
  ],
  providers: [AuthGuard,
    AuthService,
    { provide: LocationStrategy, useClass: PathLocationStrategy },
    { provide: HTTP_INTERCEPTORS, useClass: CommonInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
  entryComponents: [DialogExampleComponent],
})
export class AppModule { }
