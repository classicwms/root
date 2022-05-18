import { AuthService } from './Auth/auth.service';
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpParams
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
    // return next.handle(request).pipe(
    //   catchError(err => {
    //     if (err.status === 401) {
    //       // auto logout if 401 response returned from api
    //       this.authenticationService.logout();
    //       // location.reload(true);
    //     }

    //     // const error = err.error.message || err.statusText;
    //     // const error = err.error.result.message || err.statusText;
    //     return throwError(err.error);
    //   })
    // );
    return next.handle(request).pipe(catchError(error => {
      if (error.status === 401 || error.status == 400) {
        return this.handle401Error(request, next, error);
      }
      // this.auth.logout();
      return throwError(error);
    }));
  }



  private handle401Error(request: HttpRequest<any>, next: HttpHandler, error: any) {

    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);
    ;
    const token = this.auth.token;
    const wmsApiName = request.url.split('/')[4];
    console.log(wmsApiName);
    return this.auth.refreshToken(token, wmsApiName).pipe(
      switchMap((token: any) => {

        this.auth.saveToken(token.access_token, wmsApiName);
        this.refreshTokenSubject.next(token.access_token);

        return next.handle(this.addTokenHeader(request, token.access_token));
      }),
      catchError((err) => {

        //    this.auth.logout();      //mugilan
        return throwError(err);
      })
    );

    // }
    // else
    //   return throwError(error);
    //   if (token)
    //     return this.auth.refreshToken(token).pipe(
    //       switchMap((token: any) => {
    //         this.isRefreshing = false;


    //         this.refreshTokenSubject.next(token.accessToken);

    //         return next.handle(this.addTokenHeader(request, token.accessToken));
    //       }),
    //       catchError((err) => {
    //         this.isRefreshing = false;

    //         this.auth.logout();
    //         return throwError(err);
    //       })
    //     );


    // return this.refreshTokenSubject.pipe(
    //   filter(token => token !== null),
    //   take(1),
    //   switchMap((token) => next.handle(this.addTokenHeader(request, token)))
    // );

  }
  private addTokenHeader(request: HttpRequest<any>, token: string) {
    /* for Spring Boot back-end */
    // return request.clone({ headers: request.headers.set(TOKEN_HEADER_KEY, 'Bearer ' + token) });

    /* for Node.js Express back-end */
    return request.clone({ params: new HttpParams().append("authToken", token) });
  }

}


