import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PreinboundService {



  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'preinboundheader';
  methodName4='containerreceipt';
  methodName1='preinboundline';
  methodName2='stagingline'
  url = this.apiName + this.methodName;
  url2=this.apiName+this.methodName1;
  url3=this.apiName+this.methodName2;
  url4=this.apiName+this.methodName4;
  Getall() {
    return this.http.get<any>(this.url + '/v2');
  }
  Get(preInboundNo: any, warehouseId: any, companyCode, plantId, languageId) {
    return this.http.get<any>(this.url + '/' + preInboundNo + '/v2' + '?warehouseId=' + warehouseId + '&companyCode=' + companyCode + '&plantId=' + plantId + '&languageId=' + languageId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.url + '/v2', obj);
  }
  Update(obj: any, preInboundNo: any, warehouseId: any) {
    return this.http.patch<any>(this.url + '/' + preInboundNo + '/v2' + '?warehouseId=' + warehouseId, obj);
  }
  UpdateSalesInvoice(warehouseId:any,plantId:any,companyCode:any,languageId:any,oldInvoiceNumber:any,newInvoiceNumber:any) {
    return this.http.get<any>('/wms-transaction-service/SalesInvoice/replace'+'?warehouseId='+warehouseId+'&plantId='+plantId +'&companyCode='+companyCode+'&languageId='+languageId+'&oldInvoiceNumber='+oldInvoiceNumber+'&newInvoiceNumber='+newInvoiceNumber,);
  }
  Update1(obj: any, preInboundNo: any, warehouseId: any, companyCode, plantId, languageId) {
    return this.http.patch<any>(this.url + '/' + preInboundNo + '/v2' + '?warehouseId=' + warehouseId+'&companyCode=' + companyCode + '&plantId=' + plantId + '&languageId=' + languageId, obj);
  }
  Updateline(obj: any, preInboundNo: any, warehouseId: any, companyCode, plantId, languageId,itemCode,lineNo,refDocNumber) {
    return this.http.patch<any>(this.url2 + '/' + preInboundNo + '/V2' + '?warehouseId=' + warehouseId+'&companyCode=' + companyCode + '&plantId=' + plantId + '&languageId=' + languageId+'&itemCode='+itemCode+'&lineNo='+lineNo+'&refDocNumber='+refDocNumber, obj);
  }
  Updatestagingline(obj: any, lineNo: any, warehouseId: any, companyCode, plantId, languageId,itemCode,preInboundNo,refDocNumber,stagingNo,caseCode,palletCode) {
    return this.http.patch<any>(this.url3 + '/' + lineNo + '/v2' + '?warehouseId=' + warehouseId+'&companyCode=' + companyCode + '&plantId=' + plantId + '&languageId=' + languageId+'&itemCode='+itemCode+'&preInboundNo='+preInboundNo+'&refDocNumber='+refDocNumber+'&stagingNo='+stagingNo+'&caseCode='+caseCode+'&palletCode='+palletCode, obj);
  }
  Delete(preInboundNo: any, warehouseId: any) {
    return this.http.delete<any>(this.url + '/' + preInboundNo  + '/v2' + '?warehouseId=' + warehouseId);
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findPreInboundHeader/v2', obj);
  }
  searchInboundHeader(obj: any) {
    return this.http.post<any>('/wms-transaction-service/findInboundOrderV2', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/preinboundheader', obj);
  }
  searchContainer(obj:any){
    return this.http.post<any>(this.url4 + '/findContainerReceipt/v2', obj);
  }
  Getline(preInboundNo: any) {
    return this.http.get<any>(this.apiName + 'preinboundline/' + preInboundNo + '/v2');
  }

  processASN(obj: any,) {
    return this.http.post<any>(this.url + '/processASN', obj);
    //return this.http.get<any>(this.url + '/' + preInboundNo + '/processASN?containerNo=' + containerNo + '&invoiceNo=' + invoiceNo);
  }

  createbom(itemCode: any, lineNo: any, preInboundNo: any, refDocNumber: any, warehouseId: any) {
    return this.http.post<any>(this.apiName + 'preinboundline/bom?itemCode=' + itemCode + '&lineNo=' + lineNo + '&preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId, {});
  }
  Getcontainer() {
    return this.http.get<any>('/wms-transaction-service/containerreceipt');
  }
  searchLine(obj:any) {
    return this.http.post<any>('/wms-transaction-service/preinboundline/v2/findPreInboundLine',obj);
  }

  
  createAsnOrder(obj: any) {
    return this.http.post<any>('/wms-transaction-service/warehouse/inbound/asn', obj);
  }
  createAsnOrderV2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/warehouse/inbound/asn/v2', obj);
  }
}
