import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class ApprovalidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/approvalid');
  }
  Get(code: string, warehouseId: string,languageId:any,plantId:any,companyCodeId:any,approvalLevel:any,approvalProcessId:any) {
    return this.http.get<any>('/wms-idmaster-service/approvalid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&approvalLevel='+approvalLevel+'&approvalProcessId='+approvalProcessId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/approvalid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,plantId:any,companyCodeId:any,approvalLevel:any,approvalProcessId:any) {
    return this.http.patch<any>('/wms-idmaster-service/approvalid/' + code+'?warehouseId='+ warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&approvalLevel='+approvalLevel+'&approvalProcessId='+approvalProcessId, obj);
  }
  Delete(approvalId: any,approvalLevel:any,approverProcessId:any,warehouseId: string,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.delete<any>('/wms-idmaster-service/approvalid/' + approvalId+'?approvalLevel='+approvalLevel+'&approverProcessId='+approverProcessId+'?warehouseId='+ warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/approvalid/findApprovalId', obj);
}
}


