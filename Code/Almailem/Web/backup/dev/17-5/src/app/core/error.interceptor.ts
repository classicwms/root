import { AuthService } from './Auth/auth.service';
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpParams,
  HttpHeaders
} from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

const TOKEN_HEADER_KEY = 'Authorization';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) { }
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  private bearerToken: any;
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {

    return next.handle(request).pipe(catchError(error => {
      if (error.status === 401 || error.status == 400 || error.status == 415) {
        if (request.url.includes('/auth-token') || (error.error != null && error.error.error.includes("Order is not completely Processed"))) {
          return throwError(error);
        }
        return this.handle401Error(request, next, error);
      }
      return throwError(error);
    }));
  }



  private handle401Error(request: HttpRequest<any>, next: HttpHandler, error: any) {


    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);
    ;
    const token = this.auth.token;

    let apiName = "";
    // if (request.url.includes('mnr-management-service'))
    //   apiName = request.url.split('/')[5];
    // else 
    apiName = request.url.split('/')[4];

    return this.auth.refreshToken(apiName).pipe(
      switchMap((token: any) => {

        this.auth.saveToken(token.access_token, apiName);
        this.refreshTokenSubject.next(token.access_token);


        return next.handle(this.addTokenHeader(request, token.access_token));
      }),
      catchError((err) => {

        if (err.status == 401) {

          this.auth.logout();
        }
        return throwError(err);
      })
    );
  }
  private addTokenHeader(request: HttpRequest<any>, token: string) {
    // if (request.method == 'get')
    //   return request.clone({ params: new HttpParams().append("authToken", token) });
    // else/
    if (request.url.includes('/wms-idmaster-service/login'))
      return request.clone({ params: new HttpParams().append("authToken", token) });


      if(request.url.includes('/wms-idmaster-service/usermanagement/findUserManagement'))
      return request.clone({ params: new HttpParams().append("authToken", token) });
    // if (request.url.includes('mnr-management-service'))
    //   return request.clone({ headers: new HttpHeaders().append("Authorization", 'Bearer ' + `${token}`) });


    return request.clone({ params: new HttpParams().append("authToken", token).append('loginUserID', this.auth.userID) });
  }

}


