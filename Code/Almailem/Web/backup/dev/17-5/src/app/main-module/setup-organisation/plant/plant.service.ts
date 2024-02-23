import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class PlantService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `plant`);
  }
  Get(code: string,companyId:any,languageId:any) {
    return this.http.get<any>(this.apiName + `plant/` + code+'?companyId='+companyId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `plant`, obj);
  }
  Update(obj: any, code: any,companyId:any,languageId:any) {
    return this.http.patch<any>(this.apiName + `plant/` + code+'?companyId='+companyId+'&languageId='+languageId, obj);
  }
  Delete(plantId: any,companyId:any,languageId:any) {
    return this.http.delete<any>(this.apiName + `plant/` + plantId+'?companyId='+companyId+'&languageId='+languageId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'plant/findPlant', obj);
  }
}
