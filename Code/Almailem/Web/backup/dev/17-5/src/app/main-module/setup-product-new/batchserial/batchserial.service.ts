import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class BatchserialService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `batchserial`);
  }
  Get(code: string) {
    return this.http.get<any>(this.apiName + `batchserial/`+ code);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `batchserial`, obj);
  }
  Update(obj: any, code: any) {
    return this.http.patch<any>(this.apiName + `batchserial/` + code, obj);
  }
  Delete(storageMethod: any) {
    return this.http.delete<any>(this.apiName + `batchserial/`+storageMethod);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'batchserial/findBatchSerial', obj);
  }
}
