import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthService } from './core';

@Injectable()
export class CommonInterceptor implements HttpInterceptor {
  cur: any;
  constructor(private authService: AuthService) { }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq;
    let url = environment.apiURL;
    const bearerToken = this.authService.getToken(req.url.split('/')[1]);//sessionStorage.getItem('token');
    ;
    // if (req.url.includes('mnr-management-service'))
    //   url = environment.apiURL_management_service;
    if (bearerToken) {

      // if (req.method != 'get') {
      // if (req.url.includes('mnr-management-service'))
      //   authReq = req.clone({
      //     url: `${url}${req.url}`,
      //     setHeaders: { Authorization: 'Bearer ' + `${bearerToken}` },
      //   });
      // else
      // authReq = req.clone({
      //   url: `${url}${req.url}`,
      //   params: new HttpParams().set("authToken", bearerToken)
      // });
      // }
      // else {
      //   // if (req.url.includes('mnr-management-service'))
      //   //   authReq = req.clone({
      //   //     url: `${url}${req.url}`, params: new HttpParams().set('loginUserID', this.authService.userID),
      //   //     setHeaders: { Authorization: 'Bearer ' + `${bearerToken}` },
      //   //   });
      //   // else
      //   authReq = req.clone({
      //     url: `${url}${req.url}`,
      //     params: new HttpParams().set("authToken", bearerToken).set('loginUserID', this.authService.userID)
      //   });
      //}

      // else
      // if (req.method != 'get')
      //   authReq = req.clone({
      //     url: `${this.url}${req.url}`,
      //     params: new HttpParams().set("authToken", bearerToken).set('loginUserID', this.authService.userID)
      //   });
      // else
      //   authReq = req.clone({
      //     url: `${this.url}${req.url}`,
      //     params: new HttpParams().set("authToken", bearerToken)
      //   });

      if (req.url.split('/')[1] == 'wms-idmaster-service' && req.url.split('/')[2] == 'company' && req.url.split('/').length == 4 && req.method == 'DELETE') {
        authReq = req.clone({
          url: `${url}${req.url}`,
          params: new HttpParams().set("authToken", bearerToken).set('loginUserID', this.authService.userID).set('companyId', req.url.split('/')[3])
        });

      }
      if (req.url.split('/')[1] == 'wms-idmaster-service' && req.url.split('/')[2] == "doc-storage" &&  req.method == 'POST') {
        authReq = req.clone({
          url: `${url}${req.url}`,
         params: new HttpParams()
        });

      }

      else if (req.url.split('/')[1] == 'wms-idmaster-service' && req.url.split('/')[2] == 'company' && req.url.split('/').length == 4 && req.method == 'GET') {
        console.log('ddd')
        authReq = req.clone({
          url: `${url}${req.url}`,
          params: new HttpParams().set("authToken", bearerToken).set('companyId', req.url.split('/')[3])
        });

      }
      else {
        authReq = req.clone({
          headers: req.headers.set('Content-Type', 'application/json').set('Accept', 'application/json'),
          url: `${url}${req.url}`,
          params: new HttpParams().set("authToken", bearerToken).set('loginUserID', this.authService.userID)
        });
        // if (req.method != 'GET')
        // {
        //   if(req.method == 'PATCH')
        //   {
        //     debugger;
        //     authReq = req.clone({
        //       headers: req.headers.set('Content-Type', 'application/json').set('Accept', 'application/json'),
        //       url: `${url}${req.url}`,
        //       params: req.params.append("authToken", bearerToken).append('loginUserID', this.authService.userID)
        //     });
        //   }
        //   else
        //   {
        //   authReq = req.clone({
        //     headers: req.headers.set('Content-Type', 'application/json').set('Accept', 'application/json'),
        //     url: `${url}${req.url}`,
        //     params: new HttpParams().set("authToken", bearerToken).set('loginUserID', this.authService.userID)
        //   });
        // }
        // }
        // else
        // {
        //   authReq = req.clone({
        //     headers: req.headers.set('Content-Type', 'application/json').set('Accept', 'application/json'),
        //     url: `${url}${req.url}`,
        //     params: new HttpParams().set("authToken", bearerToken)
        //   });
        // }

      }
    }
    else {
      authReq = req.clone({
        headers: req.headers.set('Content-Type', 'application/json').set('Accept', 'application/json'),
        url: `${url}${req.url}`
      });
      // else
      // {
      //   if(req.url.split('/')[1] == 'wms-enterprise-service' && req.method == 'POST')
      //   {
      //     let user = JSON.parse(sessionStorage.getItem("user") || '{}');
      //     authReq = req.clone({
      //       // setHeaders: { MyHeader: `${regcompany}` },
      //       // setHeaders: { Authorization: `${bearerToken}` },
      //       headers: req.headers.set('Content-Type', 'application/json').set('Accept', 'application/json'),
      //       url: `${this.url}${req.url}`,
      //       params: new HttpParams().set("authToken", bearerToken ? bearerToken : '').append("loginUserID", user.username ? user.username : '')
      //     });
      //   }
      //   else
      //   {
      //     authReq = req.clone({
      //       // setHeaders: { MyHeader: `${regcompany}` },
      //       // setHeaders: { Authorization: `${bearerToken}` },
      //       headers: req.headers.set('Content-Type', 'application/json').set('Accept', 'application/json'),
      //       url: `${this.url}${req.url}`,
      //       params: new HttpParams().set("authToken", bearerToken ? bearerToken : '')
      //     });
      //   }

    }
    return next.handle(authReq);
  }
}
