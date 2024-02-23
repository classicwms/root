import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private http: HttpClient, private apiService: ApiService, private configService: ConfigService, private auth: AuthService) { }

  getAllAwaitingASN(warehouseId: any) {
    return this.apiService.get(`/wms-transaction-service/reports/dashboard/awaiting-asn?warehouseId=${warehouseId}`);
  }
  getAllBinStatus(warehouseId: any) {
    return this.apiService.get(`/wms-transaction-service/reports/dashboard/bin-status?warehouseId=${warehouseId}`);
  }
  getAllContainerReceived(warehouseId: any) {
    return this.apiService.get(`/wms-transaction-service/reports/dashboard/container-received?warehouseId=${warehouseId}`);
  }

  getAllItemReceived(warehouseId: any) {
    return this.apiService.get(`/wms-transaction-service/reports/dashboard/item-received?warehouseId=${warehouseId}`);
  }

  getAllNormalCount(warehouseId: any) {
    return this.apiService.get(`/wms-transaction-service/reports/dashboard/normal-count?warehouseId=${warehouseId}`);
  }

  getAllShippedLine(warehouseId: any) {
    return this.apiService.get(`/wms-transaction-service/reports/dashboard/shipped-line?warehouseId=${warehouseId}`);
  }

  
  getAllSpecialCount(warehouseId: any) {
    return this.apiService.get(`/wms-transaction-service/reports/dashboard/special-count?warehouseId=${warehouseId}`);
  }

  dashboardNew(warehouseId: any) {
    return this.apiService.get(`/wms-transaction-service/reports/dashboard/get-count?warehouseId=${warehouseId}`);
  }

  fastSlow(obj: any) {
    return this.http.post<any>('/wms-transaction-service/reports/dashboard/get-fast-slow-moving', obj);
  }
}
