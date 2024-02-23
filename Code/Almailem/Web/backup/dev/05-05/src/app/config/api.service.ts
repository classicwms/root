import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private http: HttpClient) { }

  private formatErrors(httpresponse: any) {
    return throwError(httpresponse);
  }

  get(path: string, params: HttpParams = new HttpParams(), headers: HttpHeaders = new HttpHeaders()): Observable<any> {

    return this.http.get(`${path}`, {
      params: params,
      headers: headers
    })
      .pipe(catchError(this.formatErrors));
  }

  delete(path: any): Observable<any> {
    return this.http.delete(`${path}`
    )
      .pipe(catchError(this.formatErrors));
  }

  post(path: string, body: Object = {}): Observable<any> {
    return this.http.post(`${path}`,
      JSON.stringify(body)
    )
      .pipe(catchError(this.formatErrors));
  }

  put(path: string, body: Object = {}): Observable<any> {
    return this.http.put(`${path}`,
      JSON.stringify(body)
    )
      .pipe(catchError(this.formatErrors));
  }

  patch(path: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.patch(`${path}`, null
    )
      .pipe(catchError(this.formatErrors));
  }

  patchWithBody(path: string, body: Object = {}, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.patch(`${path}`, JSON.stringify(body)
    )
      .pipe(catchError(this.formatErrors));
  }
}
