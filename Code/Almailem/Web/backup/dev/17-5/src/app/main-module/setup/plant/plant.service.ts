import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class PlantService {

  constructor(private apiService: ApiService, private configService: ConfigService) { }

  savePlantDetails(data: any): Observable<any> {
    return this.apiService.post(this.configService.plant_url, data);
  }

  getPlantDetails(plantId: string) {
    return this.apiService.get(this.configService.plant_url + '/' + plantId);
  }

  updatePlantDetails(data: any): Observable<any> {
    return this.apiService.patchWithBody(this.configService.plant_url + '/' + data.plantId, data);
  }
}
