import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class WarehouseService {

  constructor(private apiService: ApiService, private configService: ConfigService) { }

  saveWarehouseDetails(data: any): Observable<any> {
    return this.apiService.post(this.configService.warehouse_url, data);
  }

  getWarehouseList() {
    return this.apiService.get(this.configService.warehouse_url);
  }

  getWarehouseDetails(warehouseId: string, warehouseTypeId: number, modeOfImplementation: string) {
    return this.apiService.get(this.configService.warehouse_url + '/' + warehouseId + '?modeOfImplementation=' + modeOfImplementation + '&warehouseTypeId=' + warehouseTypeId);
  }

  updateWarehouseDetails(data: any): Observable<any> {
    return this.apiService.patchWithBody(this.configService.warehouse_url + '/' + data.warehouseId, data);
  }
}
