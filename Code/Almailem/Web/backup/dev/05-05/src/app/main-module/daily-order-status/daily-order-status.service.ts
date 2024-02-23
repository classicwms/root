import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class DailyOrderStatusService {
  Get(code: any, warehouseId: any, languageId: any, plantId: any, companyId: any, approvalLevel: any, approvalProcess: any, approverName: any, approverCode: any, designation: any) {
    throw new Error('Method not implemented.');
  }
  Update(arg0: any, code: any, warehouseId: any, languageId: any, plantId: any, companyId: any, approvalLevel: any, approvalProcess: any, approverName: any, approverCode: any, designation: any) {
    throw new Error('Method not implemented.');
  }
  Create(arg0: any) {
    throw new Error('Method not implemented.');
  }

  constructor(private http: HttpClient, private auth: AuthService) { }
  Getall() {
    return this.http.get<any>('/wms-transaction-service/orders/inbound/success');
  }
 
}
