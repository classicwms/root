import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class BasicdataService {
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `imbasicdata1`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,uomId:any,itemCode:any,manufacturerPartNo:any) {
    return this.http.get<any>(this.apiName + `imbasicdata1/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&uomId='+uomId+'&itemCode='+itemCode+'&manufacturerPartNo='+manufacturerPartNo);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `imbasicdata1`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,uomId:any,itemCode:any,manufacturerPartNo:any) {
    return this.http.patch<any>(this.apiName + `imbasicdata1/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&uomId='+uomId+'&itemCode='+itemCode+'&manufacturerPartNo='+manufacturerPartNo, obj);
  }
  Delete(warehouseId:any,languageId:any,plantId:any,uomId:any,companyCodeId:any,itemCode:any,manufacturerPartNo:any) {
    return this.http.delete<any>(this.apiName + `imbasicdata1`+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&uomId='+uomId+'&companyCodeId='+companyCodeId+'&itemCode='+itemCode+'&manufacturerPartNo='+manufacturerPartNo);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'imbasicdata1/findImBasicData1', obj);
  }
  search1(obj: any) {
    return this.http.post<any>(this.apiName + 'imbasicdata1/findImBasicData1New', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/imbasicdata1/test/v2', obj);
  }
}