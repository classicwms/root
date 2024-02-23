import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';
import { AuthService } from 'src/app/core/Auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class StockCountService {

  constructor(private http: HttpClient, private configService: ConfigService, private auth: AuthService) { }


  findStockAdjustment(data: any) {
    return this.http.post<any>('/wms-transaction-service/stockAdjustment/findStockAdjustment', data);      //20-06-23 streaming
  }

  GetStockAdjustment(stockAdjustmentKey: any, itemCode: any, manufacturerName: any, storageBin: any) {
    return this.http.get<any>('/wms-transaction-service/stockAdjustment/' + stockAdjustmentKey + '?itemCode=' + itemCode + '&manufacturerName=' + manufacturerName + '&storageBin=' + storageBin + '&companyCode=' + this.auth.companyId + '&languageId=' + this.auth.languageId + '&plantId=' + this.auth.plantId + '&warehouseId=' + this.auth.warehouseId);
  }

  updateStockAdjustment(stockAdjustmentKey: any, itemCode: any, manufacturerName: any, obj: any) {
    return this.http.patch<any>('/wms-transaction-service/stockAdjustment/'  + stockAdjustmentKey + '?itemCode=' + itemCode + '&manufacturerName=' + manufacturerName + '&companyCode=' + this.auth.companyId + '&languageId=' + this.auth.languageId + '&plantId=' + this.auth.plantId + '&warehouseId=' + this.auth.warehouseId, obj);
  }
  
}
