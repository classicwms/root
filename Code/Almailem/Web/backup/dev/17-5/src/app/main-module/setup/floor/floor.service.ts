import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class FloorService {

  constructor(private apiService: ApiService, private configService: ConfigService) { }

  saveFloorDetails(data: any): Observable<any> {
    return this.apiService.post(this.configService.floor_url, data);
  }

  getFloorDetails(floorId: any) {
    return this.apiService.get(this.configService.floor_url + '/' + floorId);
  }

  updateFloorDetails(data: any): Observable<any> {
    return this.apiService.patchWithBody(this.configService.floor_url + '/' + data.floorId + '?warehouseId=' + data.warehouseId, data);
  }
}
