import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthService } from './core';

@Injectable()
export class CommonInterceptor implements HttpInterceptor {
  cur: any;
  url = environment.apiURL;
  constructor(private authService: AuthService) { }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq;

    const bearerToken = this.authService.getToken(req.url.split('/')[1]);//localStorage.getItem('token');
    if (bearerToken) {
      authReq = req.clone({
        // setHeaders: { MyHeader: `${regcompany}` },
        // setHeaders: { Authorization: `${bearerToken}` },
        url: `${this.url}${req.url}`,
        params: new HttpParams().set("authToken", bearerToken)
      });
    }
    else {
      authReq = req.clone({
        //setHeaders: { Authorization: 'Basic ' + btoa(environment.OAUTH_CLIENT + ':' + environment.OAUTH_SECRET) },

        // headers: req.headers.set('Content-Type', 'text/plain').set('Accept', 'text/plain'),
        url: `${this.url}${req.url}`
      });
    }

    // if (req.url.includes('1.0/suggest-bin')) {
    //   authReq = req.clone({
    //     setHeaders: { Authorization: `${bearerToken}` },
    //     url: `${this.binUrl}${req.url}`
    //   });
    // }
    // else if (req.url.includes('report_wcf')) {
    //   authReq = req.clone({
    //     headers: req.headers.set('Content-Type', 'application/json'),

    //     url: `${this.reporturl}${req.url}`
    //   });
    // }
    return next.handle(authReq);
  }
}
