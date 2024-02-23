import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class SubmovementtypeidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/submovementtypeid');
  }
  Get(code: string, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,movementTypeId:any) {
    return this.http.get<any>('/wms-idmaster-service/submovementtypeid/' + code +'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&movementTypeId='+movementTypeId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/submovementtypeid', obj);
  }
  Update(obj: any, code: any, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,movementTypeId:any,) {
    return this.http.patch<any>('/wms-idmaster-service/submovementtypeid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&movementTypeId='+movementTypeId, obj);
  }
  Delete(subMovementTypeId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,movementTypeId:any) {
    return this.http.delete<any>('/wms-idmaster-service/submovementtypeid/' + subMovementTypeId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&movementTypeId='+movementTypeId );
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/submovementtypeid/findSubMovementTypeId', obj);
}
}




