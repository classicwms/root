import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class ItemReceiptService {



  constructor(private http: HttpClient, private auth: AuthService,) { }

  apiName = '/wms-transaction-service/';
  methodName = 'grheader';
  methodName1 = 'grline';
  url = this.apiName + this.methodName;
  url1 = this.apiName + this.methodName1;

  urlline = this.apiName + 'grline';
  Getall() {
    return this.http.get<any>(this.url + '/containerReceiptNo');
  }
  Get(goodsReceiptNo: any, caseCode: any, palletCode: any, preInboundNo: any, refDocNumber: any, stagingNo: any, warehouseId: any) {
    return this.http.get<any>(this.url + '/' + goodsReceiptNo + '/v2' + '?caseCode=' + caseCode + '&palletCode=' + palletCode + '&preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&stagingNo=' + stagingNo + '&warehouseId=' + warehouseId);
  }
  updateGrHeader(obj:any,goodsReceiptNo: any, caseCode: any, palletCode: any, preInboundNo: any, refDocNumber: any, stagingNo: any, warehouseId: any,companyCode:any,plantId:any,languageId:any) {
    return this.http.patch<any>(this.url + '/' + goodsReceiptNo + '/v2' + '?caseCode=' + caseCode + '&palletCode=' + palletCode + '&preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&stagingNo=' + stagingNo + '&warehouseId=' + warehouseId+'&companyCode='+companyCode+'&plantId='+plantId+'&languageId='+languageId,obj);
  }
  DeleteLine(obj: any) {
    return this.http.delete<any>(this.apiName + 'stagingline/' + obj.lineNo  + '/v2' + '?caseCode=' + obj.caseCode + '&stagingNo=' + obj.stagingNo + '&itemCode=' + obj.itemCode + '&palletCode=' + obj.palletCode + '&preInboundNo=' + obj.preInboundNo + '&refDocNumber=' + obj.refDocNumber + '&warehouseId=' + obj.warehouseId);
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findGrHeader/v2', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/grHeader', obj);
  }
  searchline(obj: any) {
    return this.http.post<any>(this.apiName + 'grline/findGrLine/v2', obj);
  }
  searchlineSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/grline', obj);
  }

  CreateLine(obj: any) {
    return this.http.post<any>(this.urlline + '/v2', obj);
  }


  getheVaidate(heno: any,companyCodeId:any,plantId:any,languageId:any) {
   return this.http.get<any>('/wms-masters-service/handlingequipment/' + heno + '/v2/barCode?warehouseId=' + this.auth.warehouseId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId);
   //return this.http.get<any>('/wms-masters-service/handlingequipment/' + heno + '/barCode?warehouseId=' + 110);
  }
  packBarcode(acceptedQty: any, damageQty: any,  warehouseId: any) {
    return this.http.get<any>(this.urlline + '/packBarcode?acceptQty=' + acceptedQty + '&damageQty=' + damageQty + '&warehouseId=' + warehouseId);
  }

  packBarcodeV2(acceptedQty: any, damageQty: any,  warehouseId: any,  companyCode: any,  plantId: any,  languageId: any) {
    return this.http.get<any>(this.urlline + '/packBarcode/v2?acceptQty=' + acceptedQty + '&damageQty=' + damageQty + '&warehouseId=' + warehouseId + '&companyCodeId=' + companyCode + '&plantId=' + plantId + '&languageId=' + languageId);
  }

  deleteGRHeader(obj: any) {
    return this.http.delete<any>(this.url + '/'  + obj.goodsReceiptNo + '/v2' + '?caseCode=' + obj.caseCode + '&palletCode=' + obj.palletCode + '&preInboundNo=' + obj.preInboundNo + '&refDocNumber=' + obj.refDocNumber + '&stagingNo=' + obj.stagingNo + '&warehouseId=' + obj.warehouseId  + '&companyCode=' + obj.companyCode + '&plantId=' + obj.plantId + '&languageId=' + obj.languageId);
  }
  deleteGRLine(obj: any) {
    return this.http.delete<any>(this.url1 + '/'  + obj.lineNo + '/v2' + '?caseCode=' + obj.caseCode + '&goodsReceiptNo=' + obj.goodsReceiptNo + '&itemCode=' + obj.itemCode + '&packBarcodes=' + obj.packBarcodes + '&palletCode=' + obj.palletCode +  '&preInboundNo=' + obj.preInboundNo + '&refDocNumber=' + obj.refDocNumber + '&warehouseId=' + obj.warehouseId + '&companyCode=' + obj.companyCode + '&plantId=' + obj.plantId + '&languageId=' + obj.languageId);
  }
}
