import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class UserprofileService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/usermanagement');
  }
  Get(code: string, warehouseId: any) {
    return this.http.get<any>('/wms-idmaster-service/usermanagement/' + code + '?warehouseId=' + warehouseId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/usermanagement', obj);
  }
  Update(obj: any, code: any, warehouseId: any) {
    return this.http.patch<any>('/wms-idmaster-service/usermanagement/' + code + '?warehouseId=' + warehouseId, obj);
  }
  Delete(userId: any,warehouseId: any) {
    return this.http.delete<any>('/wms-idmaster-service/usermanagement/' + userId + '?warehouseId=' + warehouseId );
  }
}

