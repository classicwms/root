import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {

  constructor(private http: HttpClient, private auth: AuthService) { }
  apiName = '/wms-transaction-service/';

  Create(obj: any) {
    return this.http.post<any>('/wms-transaction-service/deliveryheader', obj);
  }
  CreateLine(obj: any) {
    return this.http.post<any>('/wms-transaction-service/deliveryline', obj);
  }

  searchLinev2(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/v2/findOutboundLine', obj);
  }
  searchDeliveryLine(obj: any) {
    return this.http.post<any>('/wms-transaction-service/deliveryline/findDeliveryLine', obj);
  }
  search1(obj: any) {
    return this.http.post<any>('/wms-masters-service/drivervehicleassignment/findDriverVehicleAssignment', obj);
  }
  updatedelivery(obj: any, deliveryNo: any, companyCodeId:any,plantId:any,warehouseId:any,languageId:any) {
    return this.http.patch<any>('/wms-transaction-service/deliveryheader/' + deliveryNo + '?companyCodeId='+companyCodeId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&languageId='+languageId, obj );
  }
  updatedeliveryLine(obj: any) {
    return this.http.patch<any>('/wms-transaction-service/deliveryline', obj );
  }
  searchV2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/outboundheader/v2/findOutboundHeader', obj);
  }
  searchdleiveryheader(obj: any) {
    return this.http.post<any>('/wms-transaction-service/deliveryheader/findDeliveryHeader', obj);
  }

  Delete(line: any) {
    return this.http.delete<any>(`/wms-transaction-service/deliveryline/` +  line.deliveryNo +'?warehouseId=' + this.auth.warehouseId +'&languageId=' + this.auth.languageId+ '&plantId=' + this.auth.plantId +'&companyCodeId=' + this.auth.companyId
    +'&invoiceNumber=' + line.invoiceNumber +'&itemCode=' + line.itemCode +'&lineNumber=' + line.lineNumber +'&refDocNumber=' + line.refDocNumber);
  }


  
  download(fileName: any, location: any): Promise<File> {
    return this.http
      .get<any>(`/wms-idmaster-service/doc-storage/download?fileName=${fileName}&location=${location}`, {
        responseType: 'blob' as 'json',
      })
      .toPromise();
  }
}
