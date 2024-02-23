import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class InboundConfirmationService {



  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'inboundheader';  methodName1 = 'inboundline';
  url = this.apiName + this.methodName;
  url1 = this.apiName + this.methodName1;

  search(obj: any) {
    return this.http.post<any>(this.url + '/findInboundHeader/v2', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/inboundHeader', obj);
  }
  searchLine(obj: any) {
    return this.http.post<any>(this.url1 + '/findInboundLine', obj);
  }
  searchLinev2(obj: any) {
    return this.http.post<any>(this.url1 + '/v2/findInboundLine', obj);
  }
  searchLinespark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/inboundline', obj);
  }
  confirm(warehouseId: any, preInboundNo: any, refDocNumber: any) {
    return this.http.patch<any>(this.url + '/confirmIndividual?preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId, {});
  }
  confirmV2(warehouseId: any, preInboundNo: any, refDocNumber: any,plantId:any,companyCode:any,languageId:any) {
    return this.http.patch<any>(this.url + '/v2/confirmIndividual?preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId +'&plantId='+plantId+'&companyCode='+companyCode+'&languageId='+languageId, {});
  }
  Get(warehouseId: any, preInboundNo: any, refDocNumber: any, companyCode: any, languageId: any, plantId: any) {
    return this.http.get<any>(this.url + '/v2/' + refDocNumber + '?preInboundNo=' + preInboundNo + '&warehouseId=' + warehouseId + '&companyCode=' + companyCode + '&languageId=' + languageId + '&plantId=' + plantId, {});
  }
  Get1(warehouseId: any, preInboundNo: any, refDocNumber: any, companyCode: any, languageId: any, plantId: any,itemCode:any,lineNo:any) {
    return this.http.get<any>(this.url1 + '/v2/' +lineNo+'?refDocNumber='+refDocNumber + '&preInboundNo=' + preInboundNo + '&warehouseId=' + warehouseId + '&companyCode=' + companyCode + '&languageId=' + languageId + '&plantId=' + plantId+'&itemCode='+itemCode, {});
  }

  replaceASN(warehouseId: any, preInboundNo: any, asnNumber: any, refDocNumber: any) {
    return this.http.get<any>(this.url + '/replaceASN/v2?asnNumber=' + asnNumber + '&preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId, {});
  }

  getInboundFormPdf(asnNumber) {
    return this.http.get<any>(this.apiName + 'reports/receiptConfirmation' + `?asnNumber=${asnNumber}&reportFormat='pdf'`);
  }
  updateinboundHeader(obj:any,refDocNumber: any, warehouseId: any,) {
    return this.http.patch<any>(this.url + '/' + refDocNumber  + '?warehouseId=' + warehouseId,obj);
  }
  updateinboundHeaderV2(obj:any,refDocNumber: any, warehouseId: any,companyCode:any,plantId:any,languageId:any,preInboundNo:any) {
    return this.http.patch<any>(this.url + '/v2/' + refDocNumber  + '?warehouseId=' + warehouseId+'&companyCode='+companyCode+'&plantId='+plantId+'&languageId='+languageId+'&preInboundNo='+preInboundNo,obj);
  }
  updateinboundlineV2(obj:any,refDocNumber: any, warehouseId: any,companyCode:any,plantId:any,languageId:any,preInboundNo:any,lineNo:any,itemCode:any) {
    return this.http.patch<any>(this.url1 + '/v2' + '?refDocNumber='+refDocNumber  + '&warehouseId=' + warehouseId+'&companyCode='+companyCode+'&plantId='+plantId+'&languageId='+languageId+'&preInboundNo='+preInboundNo+'&lineNo='+lineNo+'&itemCode='+itemCode,obj);
  }
}
