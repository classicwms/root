import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class PlantService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/plantid');
  }
  Get(code: string,companyCodeId: any,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/plantid/' +code+'?companyCodeId=' + companyCodeId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/plantid', obj);
  }
  Update(obj: any,code: any,companyCodeId: any,languageId:any) {
    return this.http.patch<any>('/wms-idmaster-service/plantid/' + code +'?companyCodeId=' + companyCodeId+'&languageId='+languageId, obj);
  }
  Delete(plantId: any,companyCodeId: any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/plantid/' + plantId +'?companyCodeId=' + companyCodeId+'&languageId='+languageId );
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/plantid/findplantid', obj);
  }
}
